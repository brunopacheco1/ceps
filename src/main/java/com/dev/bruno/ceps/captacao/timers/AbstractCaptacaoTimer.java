package com.dev.bruno.ceps.captacao.timers;

import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ScheduleExpression;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;

import com.dev.bruno.ceps.captacao.services.AbstractCaptacaoService;
import com.dev.bruno.ceps.resources.Configurable;

public abstract class AbstractCaptacaoTimer {

	@Inject
	@Configurable("captacao.ativa")
	private Boolean captacaoAtiva;

	@Resource
	private TimerService timerService;
	
	@Inject
	protected Properties properties;

	@PostConstruct
	private void init() {
		if (!captacaoAtiva) {
			return;
		}
		
		Map<String, String> expressions = getExpressions();

		for (String info : expressions.keySet()) {
			TimerConfig timerConfig = new TimerConfig();
			timerConfig.setInfo(info);
			timerConfig.setPersistent(false);

			String[] expression = expressions.get(info).split("\\s");

			ScheduleExpression schedule = new ScheduleExpression();
			schedule.second(expression[0]).minute(expression[1]).hour(expression[2]).dayOfWeek(expression[5]);

			timerService.createCalendarTimer(schedule, timerConfig);
		}
	}

	protected abstract Map<String, String> getExpressions();

	protected abstract AbstractCaptacaoService getTimerService();
	
	@Timeout
	private void execute(Timer timer) {
		getTimerService().executarTimer(timer);
	}
}