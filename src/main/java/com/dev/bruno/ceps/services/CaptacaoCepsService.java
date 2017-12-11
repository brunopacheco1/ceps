package com.dev.bruno.ceps.services;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.dev.bruno.ceps.dao.BairroDAO;
import com.dev.bruno.ceps.dao.CepDAO;
import com.dev.bruno.ceps.dao.LogradouroDAO;
import com.dev.bruno.ceps.model.Bairro;
import com.dev.bruno.ceps.model.Cep;
import com.dev.bruno.ceps.model.Localidade;
import com.dev.bruno.ceps.model.Logradouro;
import com.dev.bruno.ceps.model.TipoCepEnum;
import com.dev.bruno.ceps.resources.Configurable;
import com.dev.bruno.ceps.timers.CaptacaoCepsTimer;
import com.dev.bruno.ceps.utils.StringUtils;

@Stateless
public class CaptacaoCepsService {

	public static final String CORREIOS_CHARSET = "ISO-8859-1";

	@Inject
	private Logger logger;

	@Inject
	@Configurable("captacao.ceps-de-logradouros.limit")
	private Integer limiteCaptacaoLogradouro;

	@Inject
	private BairroDAO bairroDAO;

	@Inject
	private LogradouroDAO logradouroDAO;

	@Inject
	private CepDAO cepDAO;

	@Resource
	private TimerService timerService;

	private Set<String> ceps = new HashSet<>();

	private Map<String, Logradouro> logradouros = new HashMap<>();

	public void agendarCaptacaoCeps() {
		String info = CaptacaoCepsTimer.INFO_PREFIX + "_manualtimer";

		long count = timerService.getTimers().stream().filter(timer -> timer.getInfo().toString().equals(info)).count();

		if (count > 0) {
			return;
		}

		TimerConfig timerConfig = new TimerConfig();
		timerConfig.setInfo(info);
		timerConfig.setPersistent(false);

		Date expiration = new Date();

		timerService.createSingleActionTimer(expiration, timerConfig);
	}

	@Timeout
	public void captarCeps(Timer timer) {
		Long time = System.currentTimeMillis();

		logger.info(String.format("CAPTACAO DE CEPS DE LOGRADOUROS --> BEGIN"));

		try {
			captarCeps();
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}

		time = System.currentTimeMillis() - time;

		logger.info(String.format("CAPTACAO DE CEPS DE LOGRADOUROS --> END - Tempo total: %sms", time));
	}

	private void captarCeps() {
		for (Bairro bairro : bairroDAO.listarBairrosNaoProcessados(limiteCaptacaoLogradouro)) {
			captarCepsPorBairro(bairro);
		}
	}

	public void captarCeps(Long bairroId) {
		Bairro bairro = bairroDAO.get(bairroId);

		captarCepsPorBairro(bairro);
	}

	private void captarCepsPorBairro(Bairro bairro) {
		ceps = new HashSet<>();
		logradouros = new HashMap<>();

		Connection beginConnection = Jsoup
				.connect("http://www.buscacep.correios.com.br/sistemas/buscacep/BuscaLogBairro.cfm").timeout(360000);

		Response ufResponse = null;
		try {
			ufResponse = beginConnection.method(Method.GET).execute();
		} catch (Exception e) {
			logger.info(String.format("FALHA NA CAPTACAO DE CEPS DE LOGRADOUROS --> %s - %s / %s",
					bairro.getNomeNormalizado(), bairro.getLocalidade().getNomeNormalizado(),
					bairro.getLocalidade().getUf().getNome()));

			return;
		}

		Map<String, String> cookies = ufResponse.cookies();

		buscarCeps(cookies, bairro.getLocalidade(), bairro, bairro.getLocalidade().getUf().getNome(),
				null, null, null);

		bairro.setDataUltimoProcessamento(LocalDateTime.now());

		bairroDAO.update(bairro);
	}

	private void buscarCeps(Map<String, String> cookies, Localidade localidade, Bairro bairro, String uf,
			String qtdrow, String pagini, String pagfim) {
		logger.info(String.format("CAPTACAO DE CEPS DE LOGRADOUROS[%s,%s/%s] --> %s - %s / %s", qtdrow, pagini, pagfim,
				bairro.getNomeNormalizado(), bairro.getLocalidade().getNomeNormalizado(),
				bairro.getLocalidade().getUf().getNome()));

		Connection logradourosConnection = Jsoup
				.connect("http://www.buscacep.correios.com.br/sistemas/buscacep/resultadoBuscaLogBairro.cfm")
				.timeout(360000);

		logradourosConnection.cookies(cookies);
		logradourosConnection.data("UF", uf);
		logradourosConnection.data("Localidade",
				localidade.getDistrito() != null ? localidade.getDistrito() : localidade.getNome());
		logradourosConnection.data("Bairro", bairro.getNome());

		if (qtdrow != null) {
			logradourosConnection.data("qtdrow", qtdrow);
		}

		if (pagini != null) {
			logradourosConnection.data("pagini", pagini);
		}

		if (pagfim != null) {
			logradourosConnection.data("pagfim", pagfim);
		}

		Response response = null;
		Document logradourosDocument = null;
		try {
			response = logradourosConnection.method(Method.POST).execute();

			cookies = response.cookies();

			logradourosDocument = Jsoup.parse(new String(response.bodyAsBytes(), CORREIOS_CHARSET));
		} catch (Exception e) {
			logger.info(String.format("FALHA NA CAPTACAO DE CEPS DE LOGRADOUROS --> %s - %s / %s",
					bairro.getNomeNormalizado(), bairro.getLocalidade().getNomeNormalizado(),
					bairro.getLocalidade().getUf().getNome()));

			return;
		}

		if (logradourosDocument.select("table.tmptabela").isEmpty()) {
			return;
		}

		for (Element tr : logradourosDocument.select("table.tmptabela tbody tr")) {
			if (tr.select("td").size() != 4 || tr.select("td").text().contains("Logradouro/Nome:")) {
				continue;
			}

			String nomeLogradouro = tr.select("td").get(0).html().replaceAll("&nbsp;", "");
			String nomeEspecial = null;

			String numeroCep = tr.select("td").get(3).text().replaceAll("\\D", "");

			String validarBairro = tr.select("td").get(1).text();

			if (!numeroCep.matches("^\\d{8}$") || ceps.contains(numeroCep)
					|| !bairro.getNomeNormalizado().equals(StringUtils.normalizarNome(validarBairro))
					|| cepDAO.existsByCEP(numeroCep)) {
				continue;
			}

			if (nomeLogradouro.contains("javascript:detalhaCep")) {
				nomeEspecial = StringUtils
						.normalizarNome(nomeLogradouro.split("<br><br>")[1].replaceAll("<.*?>", "").trim());
				nomeLogradouro = nomeLogradouro.split("<br><br>")[0].replaceAll("<.*?>", "").trim();
			} else {
				nomeLogradouro = nomeLogradouro.replaceAll("<.*?>", "").trim();
			}

			String complemento = null;

			if (nomeLogradouro.contains(" - ")) {
				complemento = nomeLogradouro.substring(nomeLogradouro.indexOf(" - ") + 3).trim();
				nomeLogradouro = nomeLogradouro.substring(0, nomeLogradouro.indexOf(" - ")).trim();
			}

			Logradouro logradouro = null;

			String chave = localidade.getId() + "-" + bairro.getId() + "-" + nomeLogradouro + "-" + complemento;

			if (logradouros.containsKey(chave)) {
				logradouro = logradouros.get(chave);
			} else {
				logradouro = new Logradouro();
				logradouro.setLocalidade(localidade);
				logradouro.setBairro(bairro);
				logradouro.setNome(nomeLogradouro);
				logradouro.setComplemento(complemento);
				logradouro.setNomeNormalizado(StringUtils.normalizarNome(nomeLogradouro));

				logradouroDAO.add(logradouro);

				logradouros.put(chave, logradouro);
			}

			Cep cep = new Cep();
			cep.setNumeroCep(numeroCep);
			cep.setLogradouro(logradouro);
			cep.setBairro(bairro);
			cep.setLocalidade(localidade);
			cep.setNomeEspecial(nomeEspecial);
			cep.setTipoCep(TipoCepEnum.getTipoPorCEP(numeroCep));
			

			cepDAO.add(cep);

			ceps.add(numeroCep);
		}

		if (!logradourosDocument.select("form[name=Proxima]").isEmpty()) {
			qtdrow = logradourosDocument.select("form[name=Proxima] input[name=qtdrow]").attr("value");
			pagini = logradourosDocument.select("form[name=Proxima] input[name=pagini]").attr("value");
			pagfim = logradourosDocument.select("form[name=Proxima] input[name=pagfim]").attr("value");

			buscarCeps(cookies, localidade, bairro, uf, qtdrow, pagini, pagfim);
		}
	}
}
