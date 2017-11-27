package com.dev.bruno.ceps.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.dev.bruno.ceps.dao.CepBairroDAO;
import com.dev.bruno.ceps.dao.CepDAO;
import com.dev.bruno.ceps.dao.CepLocalidadeDAO;
import com.dev.bruno.ceps.dao.CepLogradouroDAO;
import com.dev.bruno.ceps.model.Cep;
import com.dev.bruno.ceps.model.CepBairro;
import com.dev.bruno.ceps.model.CepLocalidade;
import com.dev.bruno.ceps.model.CepLogradouro;
import com.dev.bruno.ceps.model.CepTipo;
import com.dev.bruno.ceps.utils.StringUtils;

@Stateless
public class CaptacaoCepsEspeciaisService {

	@Inject
	private Logger logger;

	@Inject
	private CepLocalidadeDAO cepLocalidadeDAO;

	@Inject
	private CepBairroDAO cepBairroDAO;

	@Inject
	private CepLogradouroDAO cepLogradouroDAO;

	@Inject
	private CepDAO cepDAO;

	private Set<String> ceps = new HashSet<>();

	private Map<String, CepLogradouro> logradouros = new HashMap<>();

	private Map<String, CepBairro> bairros = new HashMap<>();
	
	@Inject
	@JMSConnectionFactory("java:jboss/DefaultJMSConnectionFactory")
	private JMSContext context;
	
	@Resource(mappedName="java:/jms/queue/CepsEspeciais")
	private Destination queue;

	public void captarCepsEspeciais(String uf) throws Exception {
		for (Long cepLocalidadeId : cepLocalidadeDAO.listarLocalidadesIdsPorUF(uf)) {
			 context.createProducer().send(queue, cepLocalidadeId);
		}
	}

	public void captarCepsEspeciais(Long cepLocalidadeId) throws Exception {
		CepLocalidade cepLocalidade = cepLocalidadeDAO.get(cepLocalidadeId);

		buscarCepsEspeciais(cepLocalidade);
	}

	private void buscarCepsEspeciais(CepLocalidade cepLocalidade) throws Exception {
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
		} catch (HttpStatusException e) {
			Thread.sleep(5000l);
			try {
				ufResponse = beginConnection.method(Method.GET).execute();
			} catch (HttpStatusException e1) {
				logger.info(String.format("FALHA --> %s / %s", cepLocalidade.getNomeNormalizado(),
						cepLocalidade.getCepUF().getUf()));

				return;
			}
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

	private boolean buscarCEPSEspeciais(Map<String, String> cookies, CepLocalidade cepLocalidade, String tipoCep,
			String qtdrow, String pagini, String pagfim) throws Exception {
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

		Response response = null;

		try {
			response = logradourosConnection.method(Method.POST).execute();
		} catch (HttpStatusException e) {
			Thread.sleep(5000l);
			try {
				response = logradourosConnection.method(Method.POST).execute();
			} catch (HttpStatusException e1) {
				logger.info(String.format("FALHA NA CAPTACAO DE CEP ESPECIAL[%s] --> %s / %s", tipoCep,
						cepLocalidade.getNomeNormalizado(), cepLocalidade.getCepUF().getUf()));
				return false;
			}
		}

		cookies = response.cookies();

		Document logradourosDocument = Jsoup.parse(new String(response.bodyAsBytes(), "ISO-8859-1"));

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
			CepBairro cepBairro = null;

			if (bairro != null && !bairro.isEmpty()) {
				if (bairros.containsKey(bairro)) {
					cepBairro = bairros.get(bairro);
				} else if (cepBairroDAO.existsByNomeLocalidade(cepLocalidade, bairro)) {
					cepBairro = cepBairroDAO.buscarByNomeLocalidade(cepLocalidade, bairro);

					bairros.put(bairro, cepBairro);
				} else {
					cepBairro = new CepBairro();
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

			CepLogradouro cepLogradouro = null;

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
					cepLogradouro = new CepLogradouro();
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
			cepObj.setCep(cep);
			cepObj.setCepLogradouro(cepLogradouro);
			cepObj.setCepBairro(cepBairro);
			cepObj.setCepLocalidade(cepLocalidade);
			cepObj.setNomeEspecial(nomeEspecial);
			cepObj.setTipoCep(CepTipo.valueOf(tipoCep));

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
