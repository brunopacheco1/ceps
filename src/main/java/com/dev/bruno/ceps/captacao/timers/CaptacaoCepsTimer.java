package com.dev.bruno.ceps.captacao.timers;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import com.dev.bruno.ceps.captacao.services.AbstractCaptacaoService;
import com.dev.bruno.ceps.captacao.services.CaptacaoCepsService;

@Singleton
@Startup
public class CaptacaoCepsTimer extends AbstractCaptacaoTimer {

	public static final String INFO_PREFIX = "CaptacaoCepsTimer";

	@Inject
	private CaptacaoCepsService service;

	@Override
	protected Map<String, String> getExpressions() {
		Map<String, String> expressions = new HashMap<>();

		String info = INFO_PREFIX;

		String expression = properties.getProperty("captacao.ceps-de-logradouros.cron");

		expressions.put(info, expression);

		return expressions;
	}

	@Override
	protected AbstractCaptacaoService getTimerService() {
		return service;
	}
}