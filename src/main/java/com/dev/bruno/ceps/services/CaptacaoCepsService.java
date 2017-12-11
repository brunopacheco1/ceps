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
	private BairroDAO cepBairroDAO;

	@Inject
	private LogradouroDAO cepLogradouroDAO;

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
		for (Bairro cepBairro : cepBairroDAO.listarBairrosNaoProcessados(limiteCaptacaoLogradouro)) {
			captarCepsDeBairro(cepBairro);
		}
	}

	public void captarCeps(Long cepBairroId) {
		Bairro cepBairro = cepBairroDAO.get(cepBairroId);

		captarCepsDeBairro(cepBairro);
	}

	private void captarCepsDeBairro(Bairro cepBairro) {
		ceps = new HashSet<>();
		logradouros = new HashMap<>();

		Connection beginConnection = Jsoup
				.connect("http://www.buscacep.correios.com.br/sistemas/buscacep/BuscaLogBairro.cfm").timeout(360000);

		Response ufResponse = null;
		try {
			ufResponse = beginConnection.method(Method.GET).execute();
		} catch (Exception e) {
			logger.info(String.format("FALHA NA CAPTACAO DE CEPS DE LOGRADOUROS --> %s - %s / %s",
					cepBairro.getNomeNormalizado(), cepBairro.getCepLocalidade().getNomeNormalizado(),
					cepBairro.getCepLocalidade().getCepUF().getUf()));

			return;
		}

		Map<String, String> cookies = ufResponse.cookies();

		buscarCeps(cookies, cepBairro.getCepLocalidade(), cepBairro, cepBairro.getCepLocalidade().getCepUF().getUf(),
				null, null, null);

		cepBairro.setUltimoProcessamento(LocalDateTime.now());

		cepBairroDAO.update(cepBairro);
	}

	private void buscarCeps(Map<String, String> cookies, Localidade cepLocalidade, Bairro cepBairro, String uf,
			String qtdrow, String pagini, String pagfim) {
		logger.info(String.format("CAPTACAO DE CEPS DE LOGRADOUROS[%s,%s/%s] --> %s - %s / %s", qtdrow, pagini, pagfim,
				cepBairro.getNomeNormalizado(), cepBairro.getCepLocalidade().getNomeNormalizado(),
				cepBairro.getCepLocalidade().getCepUF().getUf()));

		Connection logradourosConnection = Jsoup
				.connect("http://www.buscacep.correios.com.br/sistemas/buscacep/resultadoBuscaLogBairro.cfm")
				.timeout(360000);

		logradourosConnection.cookies(cookies);
		logradourosConnection.data("UF", uf);
		logradourosConnection.data("Localidade",
				cepLocalidade.getDistrito() != null ? cepLocalidade.getDistrito() : cepLocalidade.getNome());
		logradourosConnection.data("Bairro", cepBairro.getNome());

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
					cepBairro.getNomeNormalizado(), cepBairro.getCepLocalidade().getNomeNormalizado(),
					cepBairro.getCepLocalidade().getCepUF().getUf()));

			return;
		}

		if (logradourosDocument.select("table.tmptabela").isEmpty()) {
			return;
		}

		for (Element tr : logradourosDocument.select("table.tmptabela tbody tr")) {
			if (tr.select("td").size() != 4 || tr.select("td").text().contains("Logradouro/Nome:")) {
				continue;
			}

			String logradouro = tr.select("td").get(0).html().replaceAll("&nbsp;", "");
			String nomeEspecial = null;

			String cep = tr.select("td").get(3).text().replaceAll("\\D", "");

			String validarBairro = tr.select("td").get(1).text();

			if (!cep.matches("^\\d{8}$") || ceps.contains(cep)
					|| !cepBairro.getNomeNormalizado().equals(StringUtils.normalizarNome(validarBairro))
					|| cepDAO.existsByCEP(cep)) {
				continue;
			}

			if (logradouro.contains("javascript:detalhaCep")) {
				nomeEspecial = StringUtils
						.normalizarNome(logradouro.split("<br><br>")[1].replaceAll("<.*?>", "").trim());
				logradouro = logradouro.split("<br><br>")[0].replaceAll("<.*?>", "").trim();
			} else {
				logradouro = logradouro.replaceAll("<.*?>", "").trim();
			}

			String complemento = null;

			if (logradouro.contains(" - ")) {
				complemento = logradouro.substring(logradouro.indexOf(" - ") + 3).trim();
				logradouro = logradouro.substring(0, logradouro.indexOf(" - ")).trim();
			}

			Logradouro cepLogradouro = null;

			String chave = cepLocalidade.getId() + "-" + cepBairro.getId() + "-" + logradouro + "-" + complemento;

			if (logradouros.containsKey(chave)) {
				cepLogradouro = logradouros.get(chave);
			} else {
				cepLogradouro = new Logradouro();
				cepLogradouro.setCepLocalidade(cepLocalidade);
				cepLogradouro.setCepBairro(cepBairro);
				cepLogradouro.setNome(logradouro);
				cepLogradouro.setComplemento(complemento);
				cepLogradouro.setNomeNormalizado(StringUtils.normalizarNome(logradouro));

				cepLogradouroDAO.add(cepLogradouro);

				logradouros.put(chave, cepLogradouro);
			}

			Cep cepObj = new Cep();
			cepObj.setNumeroCep(cep);
			cepObj.setCepLogradouro(cepLogradouro);
			cepObj.setCepBairro(cepBairro);
			cepObj.setCepLocalidade(cepLocalidade);
			cepObj.setNomeEspecial(nomeEspecial);

			Long cepSufixo = Long.parseLong(cep.substring(5));

			if (cepSufixo <= 899) {
				cepObj.setTipoCep(TipoCepEnum.LOG);
			} else if (cepSufixo <= 959) {
				cepObj.setTipoCep(TipoCepEnum.GRU);
			} else if (cepSufixo <= 969) {
				cepObj.setTipoCep(TipoCepEnum.PRO);
			} else if (cepSufixo <= 989) {
				cepObj.setTipoCep(TipoCepEnum.UOP);
			} else if (cepSufixo <= 998) {
				cepObj.setTipoCep(TipoCepEnum.CPC);
			} else {
				cepObj.setTipoCep(TipoCepEnum.UOP);
			}

			cepDAO.add(cepObj);

			ceps.add(cep);
		}

		if (!logradourosDocument.select("form[name=Proxima]").isEmpty()) {
			qtdrow = logradourosDocument.select("form[name=Proxima] input[name=qtdrow]").attr("value");
			pagini = logradourosDocument.select("form[name=Proxima] input[name=pagini]").attr("value");
			pagfim = logradourosDocument.select("form[name=Proxima] input[name=pagfim]").attr("value");

			buscarCeps(cookies, cepLocalidade, cepBairro, uf, qtdrow, pagini, pagfim);
		}
	}
}
