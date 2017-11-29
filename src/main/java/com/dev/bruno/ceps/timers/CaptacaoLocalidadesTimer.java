package com.dev.bruno.ceps.timers;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;

import com.dev.bruno.ceps.model.CepUFEnum;
import com.dev.bruno.ceps.service.CaptacaoLocalidadesService;
import com.dev.bruno.ceps.service.CepsProperties;

@Singleton
@Startup
public class CaptacaoLocalidadesTimer {

	@Inject
	private CaptacaoLocalidadesService service;

	@Inject
	protected Logger logger;

	@Resource
	private TimerService timerService;

	@PostConstruct
	private void init() {
		Boolean ativa = Boolean.parseBoolean(CepsProperties.getInstance().getProperty("captacao.ativa"));

		if (!ativa) {
			return;
		}

		for (CepUFEnum uf : CepUFEnum.values()) {
			TimerConfig timerConfig = new TimerConfig();
			timerConfig.setInfo("CaptacaoLocalidadesTimer_" + uf);
			timerConfig.setPersistent(false);

			String[] expressions = CepsProperties.getInstance().getProperty("captacao.localidades." + uf).split("\\s");

			ScheduleExpression schedule = new ScheduleExpression();

			schedule.second(expressions[0]).minute(expressions[1]).hour(expressions[2]).dayOfWeek(expressions[5]);

			timerService.createCalendarTimer(schedule, timerConfig);
		}
	}

	@Timeout
	public void execute(Timer timer) {
		Long time = System.currentTimeMillis();

		String info = (String) timer.getInfo();

		String uf = info.split("_")[1];

		logger.info(String.format("CAPTACAO DE LOCALIDADES PARA %s --> BEGIN", uf));

		try {
			service.captarLocalidades(uf);
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}

		time = System.currentTimeMillis() - time;

		logger.info(String.format("CAPTACAO DE LOCALIDADES PARA %s --> END - Tempo total: %sms", uf, time));
	}
}