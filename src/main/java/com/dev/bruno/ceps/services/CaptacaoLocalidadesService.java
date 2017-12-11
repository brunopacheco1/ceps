package com.dev.bruno.ceps.services;

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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.dev.bruno.ceps.dao.CepDAO;
import com.dev.bruno.ceps.dao.LocalidadeDAO;
import com.dev.bruno.ceps.dao.UFDAO;
import com.dev.bruno.ceps.model.Cep;
import com.dev.bruno.ceps.model.Localidade;
import com.dev.bruno.ceps.model.TipoCepEnum;
import com.dev.bruno.ceps.model.UF;
import com.dev.bruno.ceps.timers.CaptacaoLocalidadesTimer;
import com.dev.bruno.ceps.utils.StringUtils;

@Stateless
public class CaptacaoLocalidadesService {

	@Inject
	private UFDAO ufDAO;

	@Inject
	private LocalidadeDAO localidadeDAO;

	@Inject
	private CepDAO cepDAO;

	@Inject
	private Logger logger;

	@Resource
	private TimerService timerService;

	public void agendarCaptacaoLocalidades(String uf) {
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
			captarLocalidadesPorUF(uf);
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}

		time = System.currentTimeMillis() - time;

		logger.info(String.format("CAPTACAO DE LOCALIDADES PARA %s --> END - Tempo total: %sms", uf, time));
	}

	public void captarLocalidadesPorUF(String nomeUf) {
		UF uf = ufDAO.buscarPorUF(nomeUf);

		Connection ufConnection = Jsoup.connect(
				"http://www.buscacep.correios.com.br/sistemas/buscacep/consultaLocalidade.cfm?mostrar=1&UF=" + nomeUf)
				.timeout(360000);

		Response ufResponse = null;

		try {
			ufResponse = ufConnection.method(Method.GET).execute();
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			return;
		}

		Map<String, String> cookies = ufResponse.cookies();

		Document ufDocument = Jsoup.parse(ufResponse.body());

		for (Element ufA : ufDocument.select("a[name=Letra]")) {
			String ufLetra = ufA.html().replaceAll("&nbsp;", "");

			Connection letterConnection = Jsoup
					.connect("http://www.buscacep.correios.com.br/sistemas/buscacep/consultaLocalidade.cfm?mostrar=2")
					.timeout(360000);
			letterConnection.cookies(cookies);
			letterConnection.data("UF", nomeUf);
			letterConnection.data("Letra", ufLetra);

			Document letterDocument = null;
			try {
				letterDocument = Jsoup.parse(new String(letterConnection.method(Method.POST).execute().bodyAsBytes(),
						CaptacaoCepsService.CORREIOS_CHARSET));
			} catch (Exception e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
				continue;
			}

			for (Element ufTr : letterDocument.select("tr")) {
				String localidadeQuery = ufTr.select("td").get(0).select("input").attr("onclick");
				String nomeLocalidade = ufTr.select("td").get(1).html().replaceAll("&nbsp;", "").replaceAll("<.*?>", "")
						.trim();
				String nomeDistrito = null;

				String numeroCep = ufTr.select("td").get(2).text();

				if (nomeLocalidade.equals("Localidade:")) {
					continue;
				}

				if (nomeLocalidade.matches("^.+\\s\\(.+\\)\\/\\D{2}$")) {
					nomeDistrito = nomeLocalidade.substring(0, nomeLocalidade.indexOf("(")).trim();
					nomeLocalidade = nomeLocalidade.substring(nomeLocalidade.indexOf("(") + 1, nomeLocalidade.indexOf(")")).trim();
				}

				nomeLocalidade = nomeLocalidade.split("\\/")[0].trim();

				if (numeroCep.contains("Localidade subordinada")) {
					continue;
				}

				if (localidadeDAO.existePorNomeDistrito(nomeUf, nomeLocalidade, nomeDistrito)) {
					continue;
				}

				Localidade localidade = new Localidade();

				localidade.setNome(nomeLocalidade);

				if (nomeDistrito == null) {
					localidade.setNomeNormalizado(StringUtils.normalizarNome(nomeLocalidade));
				} else {
					localidade.setNomeNormalizado(StringUtils.normalizarNome(nomeDistrito + " (" + nomeLocalidade + ")"));
				}
				localidade.setDistrito(nomeDistrito);
				localidade.setUf(uf);

				numeroCep = numeroCep.replaceAll("\\D", "");

				Cep cep = null;
				if (numeroCep.matches("^\\d{8}$")) {
					cep = new Cep();
					cep.setTipoCep(TipoCepEnum.UNI);
					cep.setNumeroCep(numeroCep);
					cep.setLocalidade(localidade);
				}

				localidadeQuery = localidadeQuery.substring(localidadeQuery.indexOf(",\"") + 2,
						localidadeQuery.lastIndexOf("\""));

				localidadeDAO.add(localidade);

				if (cep != null) {
					cepDAO.add(cep);
				}
			}
		}
	}
}