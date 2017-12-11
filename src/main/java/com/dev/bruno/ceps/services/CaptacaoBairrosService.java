package com.dev.bruno.ceps.services;

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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.dev.bruno.ceps.dao.BairroDAO;
import com.dev.bruno.ceps.dao.LocalidadeDAO;
import com.dev.bruno.ceps.model.Bairro;
import com.dev.bruno.ceps.model.Localidade;
import com.dev.bruno.ceps.model.UFEnum;
import com.dev.bruno.ceps.timers.CaptacaoBairrosTimer;
import com.dev.bruno.ceps.utils.StringUtils;

@Stateless
public class CaptacaoBairrosService {

	@Inject
	private Logger logger;

	@Inject
	private LocalidadeDAO localidadeDAO;

	@Inject
	private BairroDAO bairroDAO;

	@Inject
	@JMSConnectionFactory("java:jboss/DefaultJMSConnectionFactory")
	private JMSContext context;

	@Resource(mappedName = "java:/jms/queue/ceps/Bairros")
	private Destination queue;

	@Resource
	private TimerService timerService;

	public void agendarCaptacaoBairros(UFEnum uf) {
		String info = CaptacaoBairrosTimer.INFO_PREFIX + uf.name() + "_manualtimer";

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
	public void executarCaptacaoBairros(Timer timer) {
		Long time = System.currentTimeMillis();

		String info = (String) timer.getInfo();

		UFEnum uf = UFEnum.valueOf(info.split("_")[1]);

		logger.info(String.format("CAPTACAO DE BAIRROS PARA %s --> BEGIN", uf));

		try {
			captarBairrosPorUF(uf);
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}

		time = System.currentTimeMillis() - time;

		logger.info(String.format("CAPTACAO DE BAIRROS PARA %s --> END - Tempo total: %sms", uf, time));
	}

	private void captarBairrosPorUF(UFEnum uf) {
		for (Long localidadeId : localidadeDAO.listarLocalidadesIdsPorUF(uf)) {
			context.createProducer().send(queue, localidadeId);
		}
	}

	public void captarBairrosPorLocalidade(Long localidadeId) {
		Localidade localidade = localidadeDAO.get(localidadeId);
		
		LocalDate now = LocalDate.now();
		LocalDate toCheck = now;

		if (localidade.getCaptacaoBairros() != null) {
			toCheck = localidade.getCaptacaoBairros();
		}

		if (!now.isAfter(toCheck)) {
			return;
		}

		logger.info(String.format("CAPTACAO DE BAIRRO --> %s / %s", localidade.getNomeNormalizado(),
				localidade.getUf().getNome()));

		String uf = localidade.getUf().getNome();

		String localidadeQuery = localidade.getNome();
		if (localidade.getDistrito() != null) {
			localidadeQuery = localidade.getDistrito();
		}

		Map<String, String> localidadeCookies = null;

		Document localidadeDocument = null;
		try {
			Connection localidadeConnection = Jsoup.connect(
					"http://www.buscacep.correios.com.br/sistemas/buscacep/consultaBairro.cfm?mostrar=1&UF=" + uf
							+ "&Localidade=" + URLEncoder.encode(localidadeQuery, CaptacaoCepsService.CORREIOS_CHARSET))
					.timeout(360000);

			Response localidadeResponse = localidadeConnection.method(Method.GET).execute();

			localidadeCookies = localidadeResponse.cookies();

			localidadeDocument = Jsoup
					.parse(new String(localidadeResponse.bodyAsBytes(), CaptacaoCepsService.CORREIOS_CHARSET));
		} catch (Exception e) {
			logger.info(
					String.format("FALHA --> %s / %s", localidade.getNomeNormalizado(), localidade.getUf().getNome()));

			return;
		}

		if (localidadeDocument.toString().contains("ACESSO NEGADO")) {
			logger.info(String.format("FALHA[ACESSO NEGADO] --> %s / %s", localidade.getNomeNormalizado(),
					localidade.getUf().getNome()));
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
				Response response = localidadeLetterConnection.method(Method.POST).execute();
				String stringBody = new String(response.bodyAsBytes(), CaptacaoCepsService.CORREIOS_CHARSET);

				if (stringBody.contains("ACESSO NEGADO")) {
					logger.info(String.format("FALHA[ACESSO NEGADO] --> %s / %s", localidade.getNomeNormalizado(),
							localidade.getUf().getNome()));
					break;
				}

				localidadeLetterDocument = Jsoup.parse(stringBody);
			} catch (Exception e) {
				logger.info(String.format("FALHA --> %s / %s", localidade.getNomeNormalizado(),
						localidade.getUf().getNome()));

				continue;
			}

			for (Element localidadeTr : localidadeLetterDocument.select("tr")) {
				String nomeBairro = localidadeTr.select("td").get(1).html().replaceAll("&nbsp;", "")
						.replaceAll("<.*?>", "").trim();

				String nomeLocalidade = StringUtils.normalizarNome(localidadeTr.select("td").get(2).html()
						.replaceAll("&nbsp;", "").replaceAll("<.*?>", "").trim());

				if (nomeLocalidade.contains("/")) {
					nomeLocalidade = nomeLocalidade.substring(0, nomeLocalidade.lastIndexOf("/")).trim();
				}

				if (nomeBairro.equals("Bairro/Distrito:") || !localidade.getNomeNormalizado().equals(nomeLocalidade)) {
					continue;
				}

				if (bairroDAO.existeBairro(localidade, nomeBairro)) {
					continue;
				}

				Bairro bairro = new Bairro();
				bairro.setLocalidade(localidade);
				bairro.setNome(nomeBairro);
				bairro.setNomeNormalizado(StringUtils.normalizarNome(nomeBairro));

				bairroDAO.add(bairro);
			}
		}

		localidade.setCaptacaoBairros(LocalDate.now());

		localidadeDAO.update(localidade);
	}
}