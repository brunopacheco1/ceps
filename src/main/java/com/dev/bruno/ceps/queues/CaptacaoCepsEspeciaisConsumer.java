package com.dev.bruno.ceps.queues;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;

import com.dev.bruno.ceps.services.CaptacaoCepsEspeciaisService;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/queue/ceps/CepsEspeciais"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue") })
public class CaptacaoCepsEspeciaisConsumer implements MessageListener {

	@Inject
	private Logger logger;

	@Inject
	private CaptacaoCepsEspeciaisService service;

	@Override
	public void onMessage(Message message) {
		try {
			Long cepLocalidadeId = message.getBody(Long.class);

			service.captarCepsEspeciaisPorLocalidade(cepLocalidadeId);
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}
}