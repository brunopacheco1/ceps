package com.dev.bruno.ceps.queues;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;

import com.dev.bruno.ceps.services.CaptacaoBairrosService;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/queue/ceps/Bairros"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue") })
public class CaptacaoBairrosConsumer implements MessageListener {

	@Inject
	private CaptacaoBairrosService service;

	@Override
	public void onMessage(Message message) {
		try {
			Long cepLocalidadeId = message.getBody(Long.class);

			service.captarBairros(cepLocalidadeId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}