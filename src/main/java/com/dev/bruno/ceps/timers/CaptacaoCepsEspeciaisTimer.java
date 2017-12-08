package com.dev.bruno.ceps.timers;

import java.util.Properties;

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
import com.dev.bruno.ceps.resources.Configurable;
import com.dev.bruno.ceps.services.CaptacaoCepsEspeciaisService;

@Singleton
@Startup
public class CaptacaoCepsEspeciaisTimer {

	public static final String INFO_PREFIX = "CaptacaoCepsEspeciaisTimer_";

	@Inject
	private CaptacaoCepsEspeciaisService service;

	@Inject
	private Properties properties;

	@Inject
	@Configurable("captacao.ativa")
	private Boolean captacaoAtiva;

	@Resource
	private TimerService timerService;

	@PostConstruct
	private void init() {
		if (!captacaoAtiva) {
			return;
		}

		for (CepUFEnum uf : CepUFEnum.values()) {
			TimerConfig timerConfig = new TimerConfig();
			timerConfig.setInfo(INFO_PREFIX + uf);
			timerConfig.setPersistent(false);

			String[] expressions = properties.getProperty("captacao.ceps-especiais." + uf).split("\\s");

			ScheduleExpression schedule = new ScheduleExpression();

			schedule.second(expressions[0]).minute(expressions[1]).hour(expressions[2]).dayOfWeek(expressions[5]);

			timerService.createCalendarTimer(schedule, timerConfig);
		}
	}

	@Timeout
	public void execute(Timer timer) {
		service.captarCepsEspeciais(timer);
	}
}