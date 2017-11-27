package com.dev.bruno.ceps.jobs;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.dev.bruno.ceps.service.CaptacaoFaixasCepService;
import com.dev.bruno.ceps.service.ServiceLocator;

public class CaptacaoFaixasCepJob implements Job {

	private CaptacaoFaixasCepService service = (CaptacaoFaixasCepService) ServiceLocator.getInstance().lookup(CaptacaoFaixasCepService.class);
	
	protected Logger logger = Logger.getLogger(this.getClass().getName());
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		if(service == null) {
			return;
		}
		
		Long time = System.currentTimeMillis();
		
		logger.info(String.format("CAPTACAO DE FAIXAS DE CEP --> BEGIN"));
		
		try {
			service.captarFaixasCep();
		} catch(Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
		
		time = System.currentTimeMillis() - time;
		
		logger.info(String.format("CAPTACAO DE FAIXAS DE CEP --> END - Tempo total: %sms", time));
	}
}