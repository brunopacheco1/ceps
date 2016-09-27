package com.dev.bruno.ceps.jobs;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.dev.bruno.ceps.service.CepBairroService;
import com.dev.bruno.ceps.service.ServiceLocator;

public class CaptacaoCepsLogradourosJob implements Job {

	private CepBairroService service = (CepBairroService) ServiceLocator.getInstance().lookup(CepBairroService.class);
	
	protected Logger logger = Logger.getLogger(this.getClass().getName());
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		if(service == null) {
			return;
		}
		
		Long time = System.currentTimeMillis();
		
		logger.info(String.format("CAPTACAO DE CEPS DE LOGRADOUROS --> BEGIN"));
		
		try {
			service.captarCeps();
		} catch(Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
		
		time = System.currentTimeMillis() - time;
		
		logger.info(String.format("CAPTACAO DE CEPS DE LOGRADOUROS --> END - Tempo total: %sms", time));
	}
}