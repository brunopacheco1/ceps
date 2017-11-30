package com.dev.bruno.ceps.timers;

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

	public static final String INFO_PREFIX = "CaptacaoCepsTimer";

	@Inject
	private CaptacaoCepsService service;

	@Inject
	private CepsProperties properties;

	@Resource
	private TimerService timerService;

	@PostConstruct
	private void init() {
		Boolean ativa = Boolean.parseBoolean(properties.getProperty("captacao.ativa"));

		if (!ativa) {
			return;
		}

		TimerConfig timerConfig = new TimerConfig();
		timerConfig.setInfo(INFO_PREFIX);
		timerConfig.setPersistent(false);

		String[] expressions = properties.getProperty("captacao.ceps-de-logradouros.cron").split("\\s");

		ScheduleExpression schedule = new ScheduleExpression();
		schedule.second(expressions[0]).minute(expressions[1]).hour(expressions[2]).dayOfWeek(expressions[5]);

		timerService.createCalendarTimer(schedule, timerConfig);
	}

	@Timeout
	public void execute(Timer timer) {
		service.captarCeps(timer);
	}
}