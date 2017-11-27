package com.dev.bruno.ceps.service;

import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.dev.bruno.ceps.dao.CepLocalidadeDAO;
import com.dev.bruno.ceps.model.CepLocalidade;
import com.dev.bruno.ceps.model.CepUF;

@Stateless
public class CaptacaoFaixasCepService {
	
	@Inject
	private Logger logger;
	
	@Inject
	private CepLocalidadeDAO cepLocalidadeDAO;

	public void captarFaixasCep() throws Exception {
		Integer limit = Integer.parseInt(CepsProperties.getInstance().getProperty("captacao.faixas-de-cep.limit"));

		for (CepLocalidade cepLocalidade : cepLocalidadeDAO.listarLocalidadesSemFaixaCep(limit)) {
			buscarFaixaCep(cepLocalidade);

			cepLocalidadeDAO.update(cepLocalidade);
		}
	}
	
	public void captarFaixasCep(Long cepLocalidadeId) throws Exception {
		CepLocalidade cepLocalidade = cepLocalidadeDAO.get(cepLocalidadeId);

		buscarFaixaCep(cepLocalidade);
		
		cepLocalidadeDAO.update(cepLocalidade);
	}

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
}
