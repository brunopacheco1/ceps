package com.dev.bruno.ceps.captacao.timers;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import com.dev.bruno.ceps.captacao.services.AbstractCaptacaoService;
import com.dev.bruno.ceps.captacao.services.CaptacaoBairrosService;
import com.dev.bruno.ceps.model.UFEnum;

@Singleton
@Startup
public class CaptacaoBairrosTimer extends AbstractCaptacaoTimer {

	public static final String INFO_PREFIX = "CaptacaoBairrosTimer_";

	@Inject
	private CaptacaoBairrosService service;

	@Override
	protected Map<String, String> getExpressions() {
		Map<String, String> expressions = new HashMap<>();

		for (UFEnum uf : UFEnum.values()) {
			String info = INFO_PREFIX + uf;

			String expression = properties.getProperty("captacao.bairros." + uf);

			expressions.put(info, expression);
		}

		return expressions;
	}

	@Override
	protected AbstractCaptacaoService getTimerService() {
		return service;
	}
}