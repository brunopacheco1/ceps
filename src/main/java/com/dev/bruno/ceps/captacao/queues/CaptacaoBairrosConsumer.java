package com.dev.bruno.ceps.captacao.queues;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;

import com.dev.bruno.ceps.captacao.services.CaptacaoBairrosService;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/queue/ceps/Bairros"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue") })
public class CaptacaoBairrosConsumer implements MessageListener {

	@Inject
	private Logger logger;

	@Inject
	private CaptacaoBairrosService service;

	@Override
	public void onMessage(Message message) {
		try {
			Long cepLocalidadeId = message.getBody(Long.class);

			service.captarBairrosPorLocalidade(cepLocalidadeId);
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}
}