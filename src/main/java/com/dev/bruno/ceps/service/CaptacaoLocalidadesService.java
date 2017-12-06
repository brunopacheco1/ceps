package com.dev.bruno.ceps.service;

import java.util.Date;
import java.util.Map;
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
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.dev.bruno.ceps.dao.CepDAO;
import com.dev.bruno.ceps.dao.CepLocalidadeDAO;
import com.dev.bruno.ceps.dao.CepUFDAO;
import com.dev.bruno.ceps.model.Cep;
import com.dev.bruno.ceps.model.CepLocalidade;
import com.dev.bruno.ceps.model.CepTipo;
import com.dev.bruno.ceps.model.CepUF;
import com.dev.bruno.ceps.timers.CaptacaoLocalidadesTimer;
import com.dev.bruno.ceps.utils.StringUtils;

@Stateless
public class CaptacaoLocalidadesService {

	@Inject
	private CepUFDAO cepUFDAO;

	@Inject
	private CepLocalidadeDAO cepLocalidadeDAO;

	@Inject
	private CepDAO cepDAO;
	
	private Logger logger = Logger.getLogger(getClass().getName());

	@Resource
	private TimerService timerService;

	public void agendarCaptacaoLocalidades(String uf) throws Exception {
		String info = CaptacaoLocalidadesTimer.INFO_PREFIX + uf + "_manualtimer";

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
	public void captarLocalidades(Timer timer) {
		Long time = System.currentTimeMillis();

		String info = (String) timer.getInfo();

		String uf = info.split("_")[1];

		logger.info(String.format("CAPTACAO DE LOCALIDADES PARA %s --> BEGIN", uf));

		try {
			captarLocalidades(uf);
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}

		time = System.currentTimeMillis() - time;

		logger.info(String.format("CAPTACAO DE LOCALIDADES PARA %s --> END - Tempo total: %sms", uf, time));
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
					cepLocalidade.setNomeNormalizado(StringUtils.normalizarNome(localidade));
				} else {
					cepLocalidade.setNomeNormalizado(StringUtils.normalizarNome(distrito + " (" + localidade + ")"));
				}
				cepLocalidade.setDistrito(distrito);
				cepLocalidade.setCepUF(cepUF);

				cep = cep.replaceAll("\\D", "");

				Cep cepObj = null;
				if (cep.matches("^\\d{8}$")) {
					cepObj = new Cep();
					cepObj.setTipoCep(CepTipo.UNI);
					cepObj.setCep(cep);
					cepObj.setCepLocalidade(cepLocalidade);
				}

				localidadeQuery = localidadeQuery.substring(localidadeQuery.indexOf(",\"") + 2,
						localidadeQuery.lastIndexOf("\""));

				cepLocalidadeDAO.add(cepLocalidade);

				if (cepObj != null) {
					cepDAO.add(cepObj);
				}
			}
		}
	}
}