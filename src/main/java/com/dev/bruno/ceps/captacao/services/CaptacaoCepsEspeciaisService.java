package com.dev.bruno.ceps.captacao.services;

import java.time.LocalDate;
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
import javax.jms.Destination;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.dev.bruno.ceps.captacao.timers.CaptacaoCepsEspeciaisTimer;
import com.dev.bruno.ceps.dao.BairroDAO;
import com.dev.bruno.ceps.dao.CepDAO;
import com.dev.bruno.ceps.dao.LocalidadeDAO;
import com.dev.bruno.ceps.dao.LogradouroDAO;
import com.dev.bruno.ceps.model.Bairro;
import com.dev.bruno.ceps.model.Cep;
import com.dev.bruno.ceps.model.Localidade;
import com.dev.bruno.ceps.model.Logradouro;
import com.dev.bruno.ceps.model.TipoCepEnum;
import com.dev.bruno.ceps.model.UFEnum;
import com.dev.bruno.ceps.utils.StringUtils;

@Stateless
public class CaptacaoCepsEspeciaisService extends AbstractCaptacaoService {

	@Inject
	private Logger logger;

	@Inject
	private LocalidadeDAO localidadeDAO;

	@Inject
	private BairroDAO bairroDAO;

	@Inject
	private LogradouroDAO logradouroDAO;

	@Inject
	private CepDAO cepDAO;

	private Set<String> ceps = new HashSet<>();

	private Map<String, Logradouro> logradouros = new HashMap<>();

	private Map<String, Bairro> bairros = new HashMap<>();

	@Inject
	@JMSConnectionFactory("java:jboss/DefaultJMSConnectionFactory")
	private JMSContext context;

	@Resource(mappedName = "java:/jms/queue/ceps/CepsEspeciais")
	private Destination queue;

	@Resource
	private TimerService timerService;

	public void agendarCaptacaoCepsEspeciais(UFEnum uf) {
		String info = CaptacaoCepsEspeciaisTimer.INFO_PREFIX + uf + "_manualtimer";

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
	public void executarTimer(Timer timer) {
		Long time = System.currentTimeMillis();

		String info = (String) timer.getInfo();

		UFEnum uf = UFEnum.valueOf(info.split("_")[1]);

		logger.info(String.format("CAPTACAO DE CEPS ESPECIAIS PARA %s --> BEGIN", uf));

		try {
			captarCepsEspeciaisPorUF(uf);
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}

		time = System.currentTimeMillis() - time;

		logger.info(String.format("CAPTACAO DE CEPS ESPECIAIS PARA %s --> END - Tempo total: %sms", uf, time));
	}

	private void captarCepsEspeciaisPorUF(UFEnum uf) {
		for (Long localidadeId : localidadeDAO.listarLocalidadesIdsPorUF(uf)) {
			context.createProducer().send(queue, localidadeId);
		}
	}

	public void captarCepsEspeciaisPorLocalidade(Long localidadeId) {
		Localidade localidade = localidadeDAO.get(localidadeId);

		if (localidade.getCaptacaoCepsEspeciais() != null
				&& !LocalDate.now().isAfter(localidade.getCaptacaoCepsEspeciais())) {
			return;
		}

		ceps = new HashSet<>();
		logradouros = new HashMap<>();
		bairros = new HashMap<>();

		Connection beginConnection = Jsoup
				.connect("http://www.buscacep.correios.com.br/sistemas/buscacep/buscaCepEndereco.cfm").timeout(360000);

		Response ufResponse = null;
		try {
			ufResponse = beginConnection.method(Method.GET).execute();
		} catch (Exception e) {
			logger.info(
					String.format("FALHA --> %s / %s", localidade.getNomeNormalizado(), localidade.getUf().getNome()));

			return;
		}

		Map<String, String> cookies = ufResponse.cookies();

		boolean result = false;

		result |= buscarCEPSEspeciais(cookies, localidade, "PRO", null, null, null);
		result |= buscarCEPSEspeciais(cookies, localidade, "CPC", null, null, null);
		result |= buscarCEPSEspeciais(cookies, localidade, "GRU", null, null, null);
		result |= buscarCEPSEspeciais(cookies, localidade, "UOP", null, null, null);

		if (result) {
			localidade.setCaptacaoCepsEspeciais(LocalDate.now());

			localidadeDAO.update(localidade);
		}
	}

	private boolean buscarCEPSEspeciais(Map<String, String> cookies, Localidade localidade, String tipoCep,
			String qtdrow, String pagini, String pagfim) {
		logger.info(String.format("CAPTACAO DE CEP ESPECIAL[%s] --> %s / %s", tipoCep, localidade.getNomeNormalizado(),
				localidade.getUf().getNome()));

		String nomeLocalidade = localidade.getNomeNormalizado() + "/" + localidade.getUf().getNome();

		Connection logradourosConnection = Jsoup
				.connect("http://www.buscacep.correios.com.br/sistemas/buscacep/resultadoBuscaCepEndereco.cfm")
				.timeout(360000);

		logradourosConnection.cookies(cookies);
		logradourosConnection.data("relaxation", nomeLocalidade);
		logradourosConnection.data("tipoCEP", tipoCep);
		logradourosConnection.data("semelhante", "N");

		if (qtdrow != null) {
			logradourosConnection.data("qtdrow", qtdrow);
		}

		if (pagini != null) {
			logradourosConnection.data("pagini", pagini);
		}

		if (pagfim != null) {
			logradourosConnection.data("pagfim", pagfim);
		}

		Document logradourosDocument = null;
		try {
			Response response = logradourosConnection.method(Method.POST).execute();
			cookies = response.cookies();
			logradourosDocument = Jsoup.parse(new String(response.bodyAsBytes(), CaptacaoCepsService.CORREIOS_CHARSET));
		} catch (Exception e) {
			logger.info(String.format("FALHA NA CAPTACAO DE CEP ESPECIAL[%s] --> %s / %s", tipoCep,
					localidade.getNomeNormalizado(), localidade.getUf().getNome()));
			return false;
		}

		if (logradourosDocument.select("table.tmptabela").isEmpty()) {
			return true;
		}

		for (Element tr : logradourosDocument.select("table.tmptabela tbody tr")) {
			if (tr.select("td").size() != 4 || tr.select("td").text().contains("Logradouro/Nome:")) {
				continue;
			}

			String nomeLogradouro = tr.select("td").get(0).html().replaceAll("&nbsp;", "");
			String nomeEspecial = null;

			String numeroCep = tr.select("td").get(3).text().replaceAll("\\D", "");

			String validarCidade = tr.select("td").get(2).text();

			if (!numeroCep.matches("^\\d{8}$") || ceps.contains(numeroCep)
					|| !nomeLocalidade.equals(StringUtils.normalizarNome(validarCidade))
					|| cepDAO.existeCep(numeroCep)) {
				continue;
			}

			String nomeBairro = tr.select("td").get(1).html().replaceAll("&nbsp;", "").trim();
			Bairro bairro = null;

			if (nomeBairro != null && !nomeBairro.isEmpty()) {
				if (bairros.containsKey(nomeBairro)) {
					bairro = bairros.get(nomeBairro);
				} else if (bairroDAO.existeBairro(localidade, nomeBairro)) {
					bairro = bairroDAO.buscarBairro(localidade, nomeBairro);

					bairros.put(nomeBairro, bairro);
				} else {
					bairro = new Bairro();
					bairro.setLocalidade(localidade);
					bairro.setNome(nomeBairro);
					bairro.setNomeNormalizado(StringUtils.normalizarNome(nomeBairro));

					bairroDAO.add(bairro);

					bairros.put(nomeBairro, bairro);
				}
			}

			if (nomeLogradouro.contains("javascript:detalhaCep")) {
				if (nomeLogradouro.contains("<br>")) {
					String[] slices = nomeLogradouro.split("<br>");
					nomeEspecial = StringUtils.normalizarNome(slices[slices.length - 1].replaceAll("<.*?>", "").trim());
					nomeLogradouro = slices[0].replaceAll("<.*?>", "").trim();
				} else {
					nomeEspecial = StringUtils.normalizarNome(nomeLogradouro.replaceAll("<.*?>", "").trim());
					nomeLogradouro = null;
				}
			} else {
				nomeLogradouro = nomeLogradouro.replaceAll("<.*?>", "").trim();
			}

			String complemento = null;

			if (nomeLogradouro != null && nomeLogradouro.contains(" - ")) {
				complemento = nomeLogradouro.substring(nomeLogradouro.indexOf(" - ") + 3).trim();
				nomeLogradouro = nomeLogradouro.substring(0, nomeLogradouro.indexOf(" - ")).trim();
			}

			Logradouro logradouro = null;

			if (nomeLogradouro != null && !nomeLogradouro.isEmpty()) {
				String chave = null;

				if (bairro != null) {
					chave = localidade.getId() + "-" + bairro.getId() + "-" + nomeLogradouro + "-" + complemento;
				} else {
					chave = localidade.getId() + "-null-" + nomeLogradouro + "-" + complemento;
				}

				if (logradouros.containsKey(chave)) {
					logradouro = logradouros.get(chave);
				} else if (logradouroDAO.existeLogradouro(localidade, bairro, nomeLogradouro, complemento)) {
					logradouro = logradouroDAO.buscarLogradouro(localidade, bairro, nomeLogradouro, complemento);

					logradouros.put(chave, logradouro);
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
			}

			Cep cep = new Cep();
			cep.setNumeroCep(numeroCep);
			cep.setLogradouro(logradouro);
			cep.setBairro(bairro);
			cep.setLocalidade(localidade);
			cep.setNomeEspecial(nomeEspecial);
			cep.setTipoCep(TipoCepEnum.valueOf(tipoCep));

			cepDAO.add(cep);

			ceps.add(numeroCep);
		}

		if (!logradourosDocument.select("form[name=Proxima]").isEmpty()) {
			qtdrow = logradourosDocument.select("form[name=Proxima] input[name=qtdrow]").attr("value");
			pagini = logradourosDocument.select("form[name=Proxima] input[name=pagini]").attr("value");
			pagfim = logradourosDocument.select("form[name=Proxima] input[name=pagfim]").attr("value");

			return buscarCEPSEspeciais(cookies, localidade, tipoCep, qtdrow, pagini, pagfim);
		}

		return true;
	}
}
