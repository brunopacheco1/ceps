package com.dev.bruno.ceps.captacao.services;

import javax.ejb.Timer;

public abstract class AbstractCaptacaoService {

	public abstract void executarTimer(Timer timer);
}