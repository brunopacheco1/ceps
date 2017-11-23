package com.dev.bruno.ceps.service;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.dev.bruno.ceps.dao.AbstractDAO;
import com.dev.bruno.ceps.dao.CepBairroDAO;
import com.dev.bruno.ceps.dao.CepDAO;
import com.dev.bruno.ceps.dao.CepLocalidadeDAO;
import com.dev.bruno.ceps.dao.CepLogradouroDAO;
import com.dev.bruno.ceps.model.Cep;
import com.dev.bruno.ceps.model.CepBairro;
import com.dev.bruno.ceps.model.CepLocalidade;
import com.dev.bruno.ceps.model.CepLogradouro;
import com.dev.bruno.ceps.model.CepTipo;
import com.dev.bruno.ceps.responses.ResultList;
import com.dev.bruno.ceps.utils.StringUtils;

@Stateless
public class CepBairroService extends AbstractService<CepBairro> {

	private Set<String> ceps = new HashSet<>();

	private Map<String, CepLogradouro> logradouros = new HashMap<>();

	@Inject
	private CepLocalidadeDAO cepLocalidadeDAO;

	@Inject
	private CepBairroDAO cepBairroDAO;

	@Inject
	private CepLogradouroDAO cepLogradouroDAO;

	@Inject
	private CepDAO cepDAO;

	@Override
	protected AbstractDAO<CepBairro> getDAO() {
		return cepBairroDAO;
	}

	@Override
	protected void build(CepBairro entity) throws Exception {
		Long cepLocalidadeId = entity.getCepLocalidadeId();

		CepLocalidade localidade = cepLocalidadeDAO.get(cepLocalidadeId);

		entity.setCepLocalidade(localidade);

		String nome = entity.getNome();

		if (nome != null) {
			entity.setNomeNormalizado(StringUtils.normalizarNome(nome));
		}
	}

	public void captarCeps() throws Exception {
		Integer limit = Integer
				.parseInt(CepsProperties.getInstance().getProperty("captacao.ceps-de-logradouros.limit"));

		for (CepBairro cepBairro : cepBairroDAO.listarBairrosNaoProcessados(limit)) {
			captarCepsDeBairro(cepBairro);
		}
	}

	private void captarCepsDeBairro(CepBairro cepBairro) throws Exception {
		ceps = new HashSet<>();
		logradouros = new HashMap<>();

		Connection beginConnection = Jsoup
				.connect("http://www.buscacep.correios.com.br/sistemas/buscacep/BuscaLogBairro.cfm").timeout(360000);

		Response ufResponse = null;
		try {
			ufResponse = beginConnection.method(Method.GET).execute();
		} catch (HttpStatusException e) {
			Thread.sleep(5000l);
			try {
				ufResponse = beginConnection.method(Method.GET).execute();
			} catch (HttpStatusException e1) {
				logger.info(String.format("FALHA NA CAPTACAO DE CEPS DE LOGRADOUROS --> %s - %s / %s",
						cepBairro.getNomeNormalizado(), cepBairro.getCepLocalidade().getNomeNormalizado(),
						cepBairro.getCepLocalidade().getCepUF().getUf()));

				return;
			}
		}

		Map<String, String> cookies = ufResponse.cookies();

		buscarCeps(cookies, cepBairro.getCepLocalidade(), cepBairro, cepBairro.getCepLocalidade().getCepUF().getUf(),
				null, null, null);

		cepBairro.setUltimoProcessamento(new Date());

		cepBairroDAO.update(cepBairro);
	}

	private void buscarCeps(Map<String, String> cookies, CepLocalidade cepLocalidade, CepBairro cepBairro, String uf,
			String qtdrow, String pagini, String pagfim) throws Exception {
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
		try {
			response = logradourosConnection.method(Method.POST).execute();
		} catch (HttpStatusException e) {
			Thread.sleep(5000l);
			try {
				response = logradourosConnection.method(Method.POST).execute();
			} catch (HttpStatusException e1) {
				logger.info(String.format("FALHA NA CAPTACAO DE CEPS DE LOGRADOUROS --> %s - %s / %s",
						cepBairro.getNomeNormalizado(), cepBairro.getCepLocalidade().getNomeNormalizado(),
						cepBairro.getCepLocalidade().getCepUF().getUf()));

				return;
			}
		}

		cookies = response.cookies();

		Document logradourosDocument = Jsoup.parse(new String(response.bodyAsBytes(), "ISO-8859-1"));

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

			CepLogradouro cepLogradouro = null;

			String chave = cepLocalidade.getId() + "-" + cepBairro.getId() + "-" + logradouro + "-" + complemento;

			if (logradouros.containsKey(chave)) {
				cepLogradouro = logradouros.get(chave);
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

			Cep cepObj = new Cep();
			cepObj.setCep(cep);
			cepObj.setCepLogradouro(cepLogradouro);
			cepObj.setCepBairro(cepBairro);
			cepObj.setCepLocalidade(cepLocalidade);
			cepObj.setNomeEspecial(nomeEspecial);

			Long cepSufixo = Long.parseLong(cep.substring(5));

			if (cepSufixo <= 899) {
				cepObj.setTipoCep(CepTipo.LOG);
			} else if (cepSufixo <= 959) {
				cepObj.setTipoCep(CepTipo.GRU);
			} else if (cepSufixo <= 969) {
				cepObj.setTipoCep(CepTipo.PRO);
			} else if (cepSufixo <= 989) {
				cepObj.setTipoCep(CepTipo.UOP);
			} else if (cepSufixo <= 998) {
				cepObj.setTipoCep(CepTipo.CPC);
			} else {
				cepObj.setTipoCep(CepTipo.UOP);
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

	public ResultList<CepLogradouro> getLogradouros(Long cepBairroId) throws Exception {
		CepBairro bairro = getDAO().get(cepBairroId);

		List<CepLogradouro> entities = bairro.getLogradouros();

		ResultList<CepLogradouro> result = new ResultList<>();

		result.setResultSize((long) entities.size());
		result.setTotalSize((long) entities.size());
		result.setResult(entities);
		result.setLimit(entities.size());
		result.setOrder("id");
		result.setStart(0);

		return result;
	}

	public ResultList<Cep> getCeps(Long cepBairroId) throws Exception {
		CepBairro bairro = getDAO().get(cepBairroId);

		List<Cep> entities = bairro.getCeps();

		ResultList<Cep> result = new ResultList<>();

		result.setResultSize((long) entities.size());
		result.setTotalSize((long) entities.size());
		result.setResult(entities);
		result.setLimit(entities.size());
		result.setOrder("id");
		result.setStart(0);

		return result;
	}
}