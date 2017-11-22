package com.dev.bruno.ceps.service;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
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
import com.dev.bruno.ceps.dao.CepUFDAO;
import com.dev.bruno.ceps.model.Cep;
import com.dev.bruno.ceps.model.CepBairro;
import com.dev.bruno.ceps.model.CepLocalidade;
import com.dev.bruno.ceps.model.CepLogradouro;
import com.dev.bruno.ceps.model.CepUF;

@Stateless
public class CepLocalidadeService extends AbstractService<CepLocalidade> {

	private Set<String> ceps = new HashSet<>();

	private Map<String, CepLogradouro> logradouros = new HashMap<>();

	private Map<String, CepBairro> bairros = new HashMap<>();

	@Inject
	private CepLogradouroDAO cepLogradouroDAO;

	@Inject
	private CepLocalidadeDAO cepLocalidadeDAO;

	@Inject
	private CepUFDAO cepUFDAO;

	@Inject
	private CepDAO cepDAO;

	@Inject
	private CepBairroDAO cepBairroDAO;

	@Override
	protected AbstractDAO<CepLocalidade> getDAO() {
		return cepLocalidadeDAO;
	}

	public void captarFaixasCep() throws Exception {
		Integer limit = Integer.parseInt(CepsProperties.getInstance().getProperty("captacao.faixas-de-cep.limit"));

		for (CepLocalidade cepLocalidade : cepLocalidadeDAO.listarLocalidadesSemFaixaCep(limit)) {
			buscarFaixaCep(cepLocalidade);

			cepLocalidadeDAO.update(cepLocalidade);
		}
	}

	public void captarCepsEspeciais(String uf) throws Exception {
		for (CepLocalidade cepLocalidade : cepLocalidadeDAO.listarLocalidadesPorUF(uf)) {
			buscarCepsEspeciais(cepLocalidade);

			cepLocalidadeDAO.update(cepLocalidade);
		}
	}

	public void captarBairros(String uf) throws Exception {
		for (CepLocalidade cepLocalidade : cepLocalidadeDAO.listarLocalidadesPorUF(uf)) {
			buscarBairros(cepLocalidade);

			cepLocalidadeDAO.update(cepLocalidade);
		}
	}

	public void captarLocalidades(String uf) throws Exception {
		CepUF cepUF = cepUFDAO.buscarPorUF(uf);

		Connection ufConnection = Jsoup.connect(
				"http://www.buscacep.correios.com.br/sistemas/buscacep/consultaLocalidade.cfm?mostrar=1&UF=" + uf)
				.timeout(360000);

		Response ufResponse = null;

		try {
			ufResponse = ufConnection.method(Method.GET).execute();
		} catch (HttpStatusException e) {
			Thread.sleep(5000l);
			ufResponse = ufConnection.method(Method.GET).execute();
		}

		Map<String, String> cookies = ufResponse.cookies();

		Document ufDocument = Jsoup.parse(ufResponse.body());

		for (Element ufA : ufDocument.select("a[name=Letra]")) {
			String ufLetra = ufA.html().replaceAll("&nbsp;", "");

			Connection letterConnection = Jsoup
					.connect("http://www.buscacep.correios.com.br/sistemas/buscacep/consultaLocalidade.cfm?mostrar=2")
					.timeout(360000);
			letterConnection.cookies(cookies);
			letterConnection.data("UF", uf);
			letterConnection.data("Letra", ufLetra);

			Document letterDocument = null;
			try {
				letterDocument = Jsoup
						.parse(new String(letterConnection.method(Method.POST).execute().bodyAsBytes(), "ISO-8859-1"));
			} catch (HttpStatusException e) {
				Thread.sleep(5000l);
				letterDocument = Jsoup
						.parse(new String(letterConnection.method(Method.POST).execute().bodyAsBytes(), "ISO-8859-1"));
			}

			for (Element ufTr : letterDocument.select("tr")) {
				String localidadeQuery = ufTr.select("td").get(0).select("input").attr("onclick");
				String localidade = ufTr.select("td").get(1).html().replaceAll("&nbsp;", "").replaceAll("<.*?>", "")
						.trim();
				String distrito = null;

				String cep = ufTr.select("td").get(2).text();

				if (localidade.equals("Localidade:")) {
					continue;
				}

				if (localidade.matches("^.+\\s\\(.+\\)\\/\\D{2}$")) {
					distrito = localidade.substring(0, localidade.indexOf("(")).trim();
					localidade = localidade.substring(localidade.indexOf("(") + 1, localidade.indexOf(")")).trim();
				}

				localidade = localidade.split("\\/")[0].trim();

				if (cep.contains("Localidade subordinada")) {
					continue;
				}

				if (cepLocalidadeDAO.existsByNomeDistrito(uf, localidade, distrito)) {
					continue;
				}

				CepLocalidade cepLocalidade = new CepLocalidade();

				cepLocalidade.setNome(localidade);

				if (distrito == null) {
					cepLocalidade.setNomeNormalizado(normalizarNome(localidade));
				} else {
					cepLocalidade.setNomeNormalizado(normalizarNome(distrito + " (" + localidade + ")"));
				}
				cepLocalidade.setDistrito(distrito);
				cepLocalidade.setCepUF(cepUF);

				cep = cep.replaceAll("\\D", "");

				Cep cepObj = null;
				if (cep.matches("^\\d{8}$")) {
					cepObj = new Cep();
					cepObj.setTipoCep("UNI");
					cepObj.setCep(cep);
					cepObj.setCepLocalidade(cepLocalidade);
				}

				localidadeQuery = localidadeQuery.substring(localidadeQuery.indexOf(",\"") + 2,
						localidadeQuery.lastIndexOf("\""));

				// buscarFaixaCep(uf, localidadeQuery, cepUF, cepLocalidade);

				// cepUFDAO.update(cepUF);

				cepLocalidadeDAO.add(cepLocalidade);

				if (cepObj != null) {
					cepDAO.add(cepObj);
				}

				// buscarBairros(uf, localidadeQuery, cepLocalidade);

				// buscarCepsEspeciais(cepLocalidade);
			}
		}
	}

	private void buscarCepsEspeciais(CepLocalidade cepLocalidade) throws Exception {
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

		buscarCEPSEspeciais(cookies, cepLocalidade, "PRO", null, null, null);
		buscarCEPSEspeciais(cookies, cepLocalidade, "CPC", null, null, null);
		buscarCEPSEspeciais(cookies, cepLocalidade, "GRU", null, null, null);
		buscarCEPSEspeciais(cookies, cepLocalidade, "UOP", null, null, null);
	}

	private void buscarBairros(CepLocalidade cepLocalidade) throws Exception {
		logger.info(String.format("CAPTACAO DE BAIRRO --> %s / %s", cepLocalidade.getNomeNormalizado(),
				cepLocalidade.getCepUF().getUf()));

		String uf = cepLocalidade.getCepUF().getUf();

		String localidadeQuery = cepLocalidade.getDistrito() != null ? cepLocalidade.getDistrito()
				: cepLocalidade.getNome();

		Connection localidadeConnection = Jsoup
				.connect("http://www.buscacep.correios.com.br/sistemas/buscacep/consultaBairro.cfm?mostrar=1&UF=" + uf
						+ "&Localidade=" + URLEncoder.encode(localidadeQuery, "ISO-8859-1"))
				.timeout(360000);

		Response localidadeResponse = null;
		try {
			localidadeResponse = localidadeConnection.method(Method.GET).execute();
		} catch (HttpStatusException e) {
			Thread.sleep(5000l);
			try {
				localidadeResponse = localidadeConnection.method(Method.GET).execute();
			} catch (HttpStatusException e1) {
				logger.info(String.format("FALHA --> %s / %s", cepLocalidade.getNomeNormalizado(),
						cepLocalidade.getCepUF().getUf()));

				return;
			}
		}

		Map<String, String> localidadeCookies = localidadeResponse.cookies();

		Document localidadeDocument = Jsoup.parse(new String(localidadeResponse.bodyAsBytes(), "ISO-8859-1"));

		for (Element localidadeA : localidadeDocument.select("a[name=Letra]")) {
			String localidadeLetra = localidadeA.html().replaceAll("&nbsp;", "");

			Connection localidadeLetterConnection = Jsoup
					.connect("http://www.buscacep.correios.com.br/sistemas/buscacep/consultaBairro.cfm?mostrar=2")
					.timeout(360000);

			localidadeLetterConnection.cookies(localidadeCookies);
			localidadeLetterConnection.data("UF", uf);
			localidadeLetterConnection.data("Localidade", localidadeQuery);
			localidadeLetterConnection.data("Letra", localidadeLetra);

			Document localidadeLetterDocument = null;
			try {
				localidadeLetterDocument = Jsoup.parse(new String(
						localidadeLetterConnection.method(Method.POST).execute().bodyAsBytes(), "ISO-8859-1"));
			} catch (HttpStatusException e) {
				Thread.sleep(5000l);
				try {
					localidadeLetterDocument = Jsoup.parse(new String(
							localidadeLetterConnection.method(Method.POST).execute().bodyAsBytes(), "ISO-8859-1"));
				} catch (HttpStatusException e1) {
					logger.info(String.format("FALHA --> %s / %s", cepLocalidade.getNomeNormalizado(),
							cepLocalidade.getCepUF().getUf()));

					continue;
				}
			}

			for (Element localidadeTr : localidadeLetterDocument.select("tr")) {
				String bairro = localidadeTr.select("td").get(1).html().replaceAll("&nbsp;", "").replaceAll("<.*?>", "")
						.trim();

				String localidade = normalizarNome(localidadeTr.select("td").get(2).html().replaceAll("&nbsp;", "")
						.replaceAll("<.*?>", "").trim());

				if (localidade.contains("/")) {
					localidade = localidade.substring(0, localidade.lastIndexOf("/")).trim();
				}

				if (bairro.equals("Bairro/Distrito:") || !cepLocalidade.getNomeNormalizado().equals(localidade)) {
					continue;
				}

				if (cepBairroDAO.existsByNomeLocalidade(cepLocalidade, bairro)) {
					continue;
				}

				CepBairro cepBairro = new CepBairro();
				cepBairro.setCepLocalidade(cepLocalidade);
				cepBairro.setNome(bairro);
				cepBairro.setNomeNormalizado(normalizarNome(bairro));

				cepBairroDAO.add(cepBairro);
			}
		}

	}

	private String normalizarNome(String text) {
		if (text == null) {
			return null;
		}

		return java.text.Normalizer.normalize(text.toUpperCase().trim(), java.text.Normalizer.Form.NFD)
				.replaceAll("[^\\p{ASCII}]", "");
	}

	// private void buscarFaixaCep(String uf, String localidadeQuery, CepUF cepUF,
	// CepLocalidade cepLocalidade) throws Exception {
	private void buscarFaixaCep(CepLocalidade cepLocalidade) throws Exception {
		Random random = new Random();
		Integer secs = random.nextInt(9) + 1;
		Thread.sleep(secs * 1000);

		logger.info(String.format("CAPTACAO DE FAIXAS DE CEP (ESPERA %s sec) --> %s / %s", secs,
				cepLocalidade.getNomeNormalizado(), cepLocalidade.getCepUF().getUf()));

		String localidadeQuery = cepLocalidade.getDistrito() != null ? cepLocalidade.getDistrito()
				: cepLocalidade.getNome();
		CepUF cepUF = cepLocalidade.getCepUF();
		String uf = cepUF.getUf();
		if (cepLocalidade.getDistrito() != null) {
			return;
		}

		Connection cookieConnection = Jsoup
				.connect("http://www.buscacep.correios.com.br/sistemas/buscacep/buscaFaixaCep.cfm").timeout(360000);

		Response cookieResponse = null;
		try {
			cookieResponse = cookieConnection.method(Method.GET).execute();
		} catch (HttpStatusException e) {
			try {
				Thread.sleep(5000l);
				cookieResponse = cookieConnection.method(Method.GET).execute();
			} catch (HttpStatusException e1) {
				logger.info(String.format("FALHA --> %s / %s", cepLocalidade.getNomeNormalizado(),
						cepLocalidade.getCepUF().getUf()));
				return;
			}
		}

		Map<String, String> cookies = cookieResponse.cookies();

		Connection faixaConnection = Jsoup
				.connect("http://www.buscacep.correios.com.br/sistemas/buscacep/resultadoBuscaFaixaCEP.cfm")
				.timeout(360000);
		faixaConnection.cookies(cookies);
		faixaConnection.data("UF", uf);
		faixaConnection.data("Localidade", localidadeQuery);

		Document faixaDocument = null;
		try {
			faixaDocument = Jsoup
					.parse(new String(faixaConnection.method(Method.POST).execute().bodyAsBytes(), "ISO-8859-1"));
		} catch (HttpStatusException e) {
			try {
				Thread.sleep(5000l);
				faixaDocument = Jsoup
						.parse(new String(faixaConnection.method(Method.POST).execute().bodyAsBytes(), "ISO-8859-1"));
			} catch (HttpStatusException e1) {
				logger.info(String.format("FALHA --> %s / %s", cepLocalidade.getNomeNormalizado(),
						cepLocalidade.getCepUF().getUf()));
				return;
			}
		}

		if (faixaDocument == null || faixaDocument.select("table.tmptabela").isEmpty()) {
			return;
		}

		String faixaUf = null;
		String faixaLocalidade = null;

		for (Element tr : faixaDocument.select("table.tmptabela tr")) {
			if (tr.select("td").isEmpty()) {
				continue;
			}

			if (tr.select("td").get(0).text().equals(uf)) {
				faixaUf = tr.select("td").get(1).text();
			} else if (tr.select("td").get(0).text().equals(localidadeQuery)) {
				faixaLocalidade = tr.select("td").get(1).text();
			}
		}

		if (faixaUf != null && faixaLocalidade != null) {
			cepLocalidade.setFaixaCEP(faixaLocalidade);
		}
	}

	private void buscarCEPSEspeciais(Map<String, String> cookies, CepLocalidade cepLocalidade, String tipoCep,
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

			String validarCidade = tr.select("td").get(2).text();

			if (!cep.matches("^\\d{8}$") || ceps.contains(cep) || !localidade.equals(normalizarNome(validarCidade))
					|| cepDAO.existsByCEP(cep)) {
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
					cepBairro.setNomeNormalizado(normalizarNome(bairro));

					cepBairroDAO.add(cepBairro);

					bairros.put(bairro, cepBairro);
				}
			}

			if (logradouro.contains("javascript:detalhaCep")) {
				if (logradouro.contains("<br>")) {
					String[] slices = logradouro.split("<br>");
					nomeEspecial = normalizarNome(slices[slices.length - 1].replaceAll("<.*?>", "").trim());
					logradouro = slices[0].replaceAll("<.*?>", "").trim();
				} else {
					nomeEspecial = normalizarNome(logradouro.replaceAll("<.*?>", "").trim());
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
					cepLogradouro.setNomeNormalizado(normalizarNome(logradouro));

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
			cepObj.setTipoCep(tipoCep);

			cepDAO.add(cepObj);

			ceps.add(cep);
		}

		if (!logradourosDocument.select("form[name=Proxima]").isEmpty()) {
			qtdrow = logradourosDocument.select("form[name=Proxima] input[name=qtdrow]").attr("value");
			pagini = logradourosDocument.select("form[name=Proxima] input[name=pagini]").attr("value");
			pagfim = logradourosDocument.select("form[name=Proxima] input[name=pagfim]").attr("value");

			buscarCEPSEspeciais(cookies, cepLocalidade, tipoCep, qtdrow, pagini, pagfim);
		}
	}
}