package com.dev.bruno.ceps.queues;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;

import com.dev.bruno.ceps.service.CaptacaoCepsEspeciaisService;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/queue/ceps/CepsEspeciais"),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue") })
public class CaptacaoCepsEspeciaisConsumer implements MessageListener {

	@Resource
	private MessageDrivenContext mdc;

	@Inject
	private CaptacaoCepsEspeciaisService service;

	@Override
	public void onMessage(Message message) {
		try {
			Long cepLocalidadeId = message.getBody(Long.class);

			service.captarCepsEspeciais(cepLocalidadeId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}