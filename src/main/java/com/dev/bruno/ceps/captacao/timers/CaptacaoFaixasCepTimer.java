package com.dev.bruno.ceps.captacao.timers;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import com.dev.bruno.ceps.captacao.services.AbstractCaptacaoService;
import com.dev.bruno.ceps.captacao.services.CaptacaoFaixasCepService;

@Singleton
@Startup
public class CaptacaoFaixasCepTimer extends AbstractCaptacaoTimer {

	public static final String INFO_PREFIX = "CaptacaoFaixasCepTimer";

	@Inject
	private CaptacaoFaixasCepService service;

	@Override
	protected Map<String, String> getExpressions() {
		Map<String, String> expressions = new HashMap<>();

		String info = INFO_PREFIX;

		String expression = properties.getProperty("captacao.faixas-de-cep.cron");

		expressions.put(info, expression);

		return expressions;
	}

	@Override
	protected AbstractCaptacaoService getTimerService() {
		return service;
	}
}