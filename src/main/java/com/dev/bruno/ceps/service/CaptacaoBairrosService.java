package com.dev.bruno.ceps.service;

import java.net.URLEncoder;
import java.util.Map;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.dev.bruno.ceps.dao.CepBairroDAO;
import com.dev.bruno.ceps.dao.CepLocalidadeDAO;
import com.dev.bruno.ceps.model.CepBairro;
import com.dev.bruno.ceps.model.CepLocalidade;
import com.dev.bruno.ceps.utils.StringUtils;

@Stateless
public class CaptacaoBairrosService {
	
	@Inject
	protected Logger logger;
	
	@Inject
	private CepLocalidadeDAO cepLocalidadeDAO;

	@Inject
	private CepBairroDAO cepBairroDAO;

	public void captarBairros(String uf) throws Exception {
		for (CepLocalidade cepLocalidade : cepLocalidadeDAO.listarLocalidadesPorUF(uf)) {
			buscarBairros(cepLocalidade);

			cepLocalidadeDAO.update(cepLocalidade);
		}
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

				String localidade = StringUtils.normalizarNome(localidadeTr.select("td").get(2).html()
						.replaceAll("&nbsp;", "").replaceAll("<.*?>", "").trim());

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
				cepBairro.setNomeNormalizado(StringUtils.normalizarNome(bairro));

				cepBairroDAO.add(cepBairro);
			}
		}

	}
}