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

import com.dev.bruno.ceps.service.CaptacaoCepsService;
import com.dev.bruno.ceps.service.CepsProperties;

@Singleton
@Startup
public class CaptacaoCepsTimer {

	@Inject
	private CaptacaoCepsService service;

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

		TimerConfig timerConfig = new TimerConfig();
		timerConfig.setInfo("CaptacaoCepsTimer");
		timerConfig.setPersistent(false);

		String[] expressions = CepsProperties.getInstance().getProperty("captacao.ceps-de-logradouros.cron")
				.split("\\s");

		ScheduleExpression schedule = new ScheduleExpression();
		schedule.second(expressions[0]).minute(expressions[1]).hour(expressions[2]).dayOfWeek(expressions[5]);

		timerService.createCalendarTimer(schedule, timerConfig);
	}

	@Timeout
	public void execute(Timer timer) {
		Long time = System.currentTimeMillis();

		logger.info(String.format("CAPTACAO DE CEPS DE LOGRADOUROS --> BEGIN"));

		try {
			service.captarCeps();
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}

		time = System.currentTimeMillis() - time;

		logger.info(String.format("CAPTACAO DE CEPS DE LOGRADOUROS --> END - Tempo total: %sms", time));
	}
}