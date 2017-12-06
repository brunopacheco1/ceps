package com.dev.bruno.ceps.service;

import java.net.URLEncoder;
import java.time.LocalDate;
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
import com.dev.bruno.ceps.dao.CepLocalidadeDAO;
import com.dev.bruno.ceps.model.CepBairro;
import com.dev.bruno.ceps.model.CepLocalidade;
import com.dev.bruno.ceps.timers.CaptacaoBairrosTimer;
import com.dev.bruno.ceps.utils.StringUtils;

@Stateless
public class CaptacaoBairrosService {

	private Logger logger = Logger.getLogger(getClass().getName());

	@Inject
	private CepLocalidadeDAO cepLocalidadeDAO;

	@Inject
	private CepBairroDAO cepBairroDAO;

	@Inject
	@JMSConnectionFactory("java:jboss/DefaultJMSConnectionFactory")
	private JMSContext context;

	@Resource(mappedName = "java:/jms/queue/ceps/Bairros")
	private Destination queue;

	@Resource
	private TimerService timerService;

	public void agendarCaptacaoBairros(String uf) {
		String info = CaptacaoBairrosTimer.INFO_PREFIX + uf + "_manualtimer";

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
	public void captarBairros(Timer timer) {
		Long time = System.currentTimeMillis();

		String info = (String) timer.getInfo();

		String uf = info.split("_")[1];

		logger.info(String.format("CAPTACAO DE BAIRROS PARA %s --> BEGIN", uf));

		try {
			captarBairros(uf);
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}

		time = System.currentTimeMillis() - time;

		logger.info(String.format("CAPTACAO DE BAIRROS PARA %s --> END - Tempo total: %sms", uf, time));
	}

	private void captarBairros(String uf) throws Exception {
		for (Long cepLocalidadeId : cepLocalidadeDAO.listarLocalidadesIdsPorUF(uf)) {
			context.createProducer().send(queue, cepLocalidadeId);
		}
	}

	public void captarBairros(Long cepLocalidadeId) throws Exception {
		CepLocalidade cepLocalidade = cepLocalidadeDAO.get(cepLocalidadeId);

		buscarBairros(cepLocalidade);
	}

	private void buscarBairros(CepLocalidade cepLocalidade) throws Exception {
		if (cepLocalidade.getCaptacaoBairros() != null
				&& !LocalDate.now().isAfter(cepLocalidade.getCaptacaoBairros())) {
			return;
		}

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
		} catch (Exception e) {
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

		if (localidadeDocument.toString().contains("ACESSO NEGADO")) {
			logger.info(String.format("FALHA[ACESSO NEGADO] --> %s / %s", cepLocalidade.getNomeNormalizado(),
					cepLocalidade.getCepUF().getUf()));
			return;
		}

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

			if (localidadeLetterDocument.toString().contains("ACESSO NEGADO")) {
				logger.info(String.format("FALHA[ACESSO NEGADO] --> %s / %s", cepLocalidade.getNomeNormalizado(),
						cepLocalidade.getCepUF().getUf()));
				break;
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

		cepLocalidade.setCaptacaoBairros(LocalDate.now());

		cepLocalidadeDAO.update(cepLocalidade);
	}
}