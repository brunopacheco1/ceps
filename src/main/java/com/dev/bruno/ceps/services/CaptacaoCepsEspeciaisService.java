package com.dev.bruno.ceps.services;

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

import com.dev.bruno.ceps.dao.BairroDAO;
import com.dev.bruno.ceps.dao.CepDAO;
import com.dev.bruno.ceps.dao.LocalidadeDAO;
import com.dev.bruno.ceps.dao.LogradouroDAO;
import com.dev.bruno.ceps.model.Bairro;
import com.dev.bruno.ceps.model.Cep;
import com.dev.bruno.ceps.model.Localidade;
import com.dev.bruno.ceps.model.Logradouro;
import com.dev.bruno.ceps.model.TipoCepEnum;
import com.dev.bruno.ceps.timers.CaptacaoCepsEspeciaisTimer;
import com.dev.bruno.ceps.utils.StringUtils;

@Stateless
public class CaptacaoCepsEspeciaisService {
	
	@Inject
	private Logger logger;

	@Inject
	private LocalidadeDAO cepLocalidadeDAO;

	@Inject
	private BairroDAO cepBairroDAO;

	@Inject
	private LogradouroDAO cepLogradouroDAO;

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

	public void agendarCaptacaoCepsEspeciais(String uf) {
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
	public void captarCepsEspeciais(Timer timer) {
		Long time = System.currentTimeMillis();

		String info = (String) timer.getInfo();

		String uf = info.split("_")[1];

		logger.info(String.format("CAPTACAO DE CEPS ESPECIAIS PARA %s --> BEGIN", uf));

		try {
			captarCepsEspeciaisByUF(uf);
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}

		time = System.currentTimeMillis() - time;

		logger.info(String.format("CAPTACAO DE CEPS ESPECIAIS PARA %s --> END - Tempo total: %sms", uf, time));
	}

	private void captarCepsEspeciaisByUF(String uf) {
		for (Long cepLocalidadeId : cepLocalidadeDAO.listarLocalidadesIdsPorUF(uf)) {
			context.createProducer().send(queue, cepLocalidadeId);
		}
	}

	public void captarCepsEspeciaisByCepLocalidadeId(Long cepLocalidadeId) {
		Localidade cepLocalidade = cepLocalidadeDAO.get(cepLocalidadeId);

		captarCepsEspeciaisByCepLocalidade(cepLocalidade);
	}

	private void captarCepsEspeciaisByCepLocalidade(Localidade cepLocalidade) {
		if (cepLocalidade.getCaptacaoCepsEspeciais() != null
				&& !LocalDate.now().isAfter(cepLocalidade.getCaptacaoCepsEspeciais())) {
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
			logger.info(String.format("FALHA --> %s / %s", cepLocalidade.getNomeNormalizado(),
					cepLocalidade.getCepUF().getUf()));

			return;
		}

		Map<String, String> cookies = ufResponse.cookies();

		boolean result = false;

		result |= buscarCEPSEspeciais(cookies, cepLocalidade, "PRO", null, null, null);
		result |= buscarCEPSEspeciais(cookies, cepLocalidade, "CPC", null, null, null);
		result |= buscarCEPSEspeciais(cookies, cepLocalidade, "GRU", null, null, null);
		result |= buscarCEPSEspeciais(cookies, cepLocalidade, "UOP", null, null, null);

		if (result) {
			cepLocalidade.setCaptacaoCepsEspeciais(LocalDate.now());

			cepLocalidadeDAO.update(cepLocalidade);
		}
	}

	private boolean buscarCEPSEspeciais(Map<String, String> cookies, Localidade cepLocalidade, String tipoCep,
			String qtdrow, String pagini, String pagfim) {
		logger.info(String.format("CAPTACAO DE CEP ESPECIAL[%s] --> %s / %s", tipoCep,
				cepLocalidade.getNomeNormalizado(), cepLocalidade.getCepUF().getUf()));

		String localidade = cepLocalidade.getNomeNormalizado() + "/" + cepLocalidade.getCepUF().getUf();

		Connection logradourosConnection = Jsoup
				.connect("http://www.buscacep.correios.com.br/sistemas/buscacep/resultadoBuscaCepEndereco.cfm")
				.timeout(360000);

		logradourosConnection.cookies(cookies);
		logradourosConnection.data("relaxation", localidade);
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
					cepLocalidade.getNomeNormalizado(), cepLocalidade.getCepUF().getUf()));
			return false;
		}

		if (logradourosDocument.select("table.tmptabela").isEmpty()) {
			return true;
		}

		for (Element tr : logradourosDocument.select("table.tmptabela tbody tr")) {
			if (tr.select("td").size() != 4 || tr.select("td").text().contains("Logradouro/Nome:")) {
				continue;
			}

			String logradouro = tr.select("td").get(0).html().replaceAll("&nbsp;", "");
			String nomeEspecial = null;

			String cep = tr.select("td").get(3).text().replaceAll("\\D", "");

			String validarCidade = tr.select("td").get(2).text();

			if (!cep.matches("^\\d{8}$") || ceps.contains(cep)
					|| !localidade.equals(StringUtils.normalizarNome(validarCidade)) || cepDAO.existsByCEP(cep)) {
				continue;
			}

			String bairro = tr.select("td").get(1).html().replaceAll("&nbsp;", "").trim();
			Bairro cepBairro = null;

			if (bairro != null && !bairro.isEmpty()) {
				if (bairros.containsKey(bairro)) {
					cepBairro = bairros.get(bairro);
				} else if (cepBairroDAO.existsByNomeLocalidade(cepLocalidade, bairro)) {
					cepBairro = cepBairroDAO.buscarByNomeLocalidade(cepLocalidade, bairro);

					bairros.put(bairro, cepBairro);
				} else {
					cepBairro = new Bairro();
					cepBairro.setCepLocalidade(cepLocalidade);
					cepBairro.setNome(bairro);
					cepBairro.setNomeNormalizado(StringUtils.normalizarNome(bairro));

					cepBairroDAO.add(cepBairro);

					bairros.put(bairro, cepBairro);
				}
			}

			if (logradouro.contains("javascript:detalhaCep")) {
				if (logradouro.contains("<br>")) {
					String[] slices = logradouro.split("<br>");
					nomeEspecial = StringUtils.normalizarNome(slices[slices.length - 1].replaceAll("<.*?>", "").trim());
					logradouro = slices[0].replaceAll("<.*?>", "").trim();
				} else {
					nomeEspecial = StringUtils.normalizarNome(logradouro.replaceAll("<.*?>", "").trim());
					logradouro = null;
				}
			} else {
				logradouro = logradouro.replaceAll("<.*?>", "").trim();
			}

			String complemento = null;

			if (logradouro != null && logradouro.contains(" - ")) {
				complemento = logradouro.substring(logradouro.indexOf(" - ") + 3).trim();
				logradouro = logradouro.substring(0, logradouro.indexOf(" - ")).trim();
			}

			Logradouro cepLogradouro = null;

			if (logradouro != null && !logradouro.isEmpty()) {
				String chave = null;

				if (cepBairro != null) {
					chave = cepLocalidade.getId() + "-" + cepBairro.getId() + "-" + logradouro + "-" + complemento;
				} else {
					chave = cepLocalidade.getId() + "-null-" + logradouro + "-" + complemento;
				}

				if (logradouros.containsKey(chave)) {
					cepLogradouro = logradouros.get(chave);
				} else if (cepLogradouroDAO.existByLocalidadeBairroEnderecoComplemento(cepLocalidade, cepBairro,
						logradouro, complemento)) {
					cepLogradouro = cepLogradouroDAO.buscarByLocalidadeBairroEnderecoComplemento(cepLocalidade,
							cepBairro, logradouro, complemento);

					logradouros.put(chave, cepLogradouro);
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
			}

			Cep cepObj = new Cep();
			cepObj.setNumeroCep(cep);
			cepObj.setCepLogradouro(cepLogradouro);
			cepObj.setCepBairro(cepBairro);
			cepObj.setCepLocalidade(cepLocalidade);
			cepObj.setNomeEspecial(nomeEspecial);
			cepObj.setTipoCep(TipoCepEnum.valueOf(tipoCep));

			cepDAO.add(cepObj);

			ceps.add(cep);
		}

		if (!logradourosDocument.select("form[name=Proxima]").isEmpty()) {
			qtdrow = logradourosDocument.select("form[name=Proxima] input[name=qtdrow]").attr("value");
			pagini = logradourosDocument.select("form[name=Proxima] input[name=pagini]").attr("value");
			pagfim = logradourosDocument.select("form[name=Proxima] input[name=pagfim]").attr("value");

			return buscarCEPSEspeciais(cookies, cepLocalidade, tipoCep, qtdrow, pagini, pagfim);
		}

		return true;
	}
}
