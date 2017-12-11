package com.dev.bruno.ceps.captacao.timers;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import com.dev.bruno.ceps.captacao.services.AbstractCaptacaoService;
import com.dev.bruno.ceps.captacao.services.CaptacaoCepsEspeciaisService;
import com.dev.bruno.ceps.model.UFEnum;

@Singleton
@Startup
public class CaptacaoCepsEspeciaisTimer extends AbstractCaptacaoTimer {

	public static final String INFO_PREFIX = "CaptacaoCepsEspeciaisTimer_";

	@Inject
	private CaptacaoCepsEspeciaisService service;

	@Override
	protected Map<String, String> getExpressions() {
		Map<String, String> expressions = new HashMap<>();

		for (UFEnum uf : UFEnum.values()) {
			String info = INFO_PREFIX + uf;

			String expression = properties.getProperty("captacao.ceps-especiais." + uf);

			expressions.put(info, expression);
		}

		return expressions;
	}

	@Override
	protected AbstractCaptacaoService getTimerService() {
		return service;
	}
}