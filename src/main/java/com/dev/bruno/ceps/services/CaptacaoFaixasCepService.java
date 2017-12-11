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

import com.dev.bruno.ceps.dao.LocalidadeDAO;
import com.dev.bruno.ceps.model.Localidade;
import com.dev.bruno.ceps.model.UF;
import com.dev.bruno.ceps.resources.Configurable;
import com.dev.bruno.ceps.timers.CaptacaoFaixasCepTimer;

@Stateless
public class CaptacaoFaixasCepService {

	@Inject
	private Logger logger;

	@Configurable("captacao.faixas-de-cep.limit")
	private Integer limiteCaptacaoFaixas;

	@Inject
	private LocalidadeDAO cepLocalidadeDAO;

	@Resource
	private TimerService timerService;

	public void agendarCaptacaoFaixasCep() {
		String info = CaptacaoFaixasCepTimer.INFO_PREFIX + "_manualtimer";

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
	public void captarFaixasCep(Timer timer) {
		Long time = System.currentTimeMillis();

		logger.info(String.format("CAPTACAO DE FAIXAS DE CEP --> BEGIN"));

		try {
			captarFaixasCep();
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}

		time = System.currentTimeMillis() - time;

		logger.info(String.format("CAPTACAO DE FAIXAS DE CEP --> END - Tempo total: %sms", time));
	}

	private void captarFaixasCep() throws Exception {
		for (Localidade cepLocalidade : cepLocalidadeDAO.listarLocalidadesSemFaixaCep(limiteCaptacaoFaixas)) {
			buscarFaixaCep(cepLocalidade);

			cepLocalidadeDAO.update(cepLocalidade);
		}
	}

	private void buscarFaixaCep(Localidade cepLocalidade) {
		logger.info(String.format("CAPTACAO DE FAIXAS DE CEP --> %s / %s",
				cepLocalidade.getNomeNormalizado(), cepLocalidade.getCepUF().getUf()));

		String localidadeQuery = cepLocalidade.getDistrito() != null ? cepLocalidade.getDistrito()
				: cepLocalidade.getNome();
		UF cepUF = cepLocalidade.getCepUF();
		String uf = cepUF.getUf();
		if (cepLocalidade.getDistrito() != null) {
			return;
		}

		Connection cookieConnection = Jsoup
				.connect("http://www.buscacep.correios.com.br/sistemas/buscacep/buscaFaixaCep.cfm").timeout(360000);

		Response cookieResponse = null;
		try {
			cookieResponse = cookieConnection.method(Method.GET).execute();
		} catch (Exception e) {
			logger.info(String.format("FALHA --> %s / %s", cepLocalidade.getNomeNormalizado(),
					cepLocalidade.getCepUF().getUf()));
			return;
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
					.parse(new String(faixaConnection.method(Method.POST).execute().bodyAsBytes(), CaptacaoCepsService.CORREIOS_CHARSET));
		} catch (Exception e) {
			logger.info(String.format("FALHA --> %s / %s", cepLocalidade.getNomeNormalizado(),
					cepLocalidade.getCepUF().getUf()));
			return;
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
