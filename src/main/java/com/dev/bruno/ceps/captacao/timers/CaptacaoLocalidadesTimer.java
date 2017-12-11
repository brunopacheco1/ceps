package com.dev.bruno.ceps.captacao.timers;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import com.dev.bruno.ceps.captacao.services.AbstractCaptacaoService;
import com.dev.bruno.ceps.captacao.services.CaptacaoLocalidadesService;
import com.dev.bruno.ceps.model.UFEnum;

@Singleton
@Startup
public class CaptacaoLocalidadesTimer extends AbstractCaptacaoTimer {

	public static final String INFO_PREFIX = "CaptacaoLocalidadesTimer_";

	@Inject
	private CaptacaoLocalidadesService service;

	@Override
	protected Map<String, String> getExpressions() {
		Map<String, String> expressions = new HashMap<>();

		for (UFEnum uf : UFEnum.values()) {
			String info = INFO_PREFIX + uf;

			String expression = properties.getProperty("captacao.localidades." + uf);

			expressions.put(info, expression);
		}

		return expressions;
	}

	@Override
	protected AbstractCaptacaoService getTimerService() {
		return service;
	}
}