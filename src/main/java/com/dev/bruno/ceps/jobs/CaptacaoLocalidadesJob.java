package com.dev.bruno.ceps.jobs;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.dev.bruno.ceps.service.CaptacaoLocalidadesService;
import com.dev.bruno.ceps.service.ServiceLocator;

public class CaptacaoLocalidadesJob implements Job {

	private CaptacaoLocalidadesService service = (CaptacaoLocalidadesService) ServiceLocator.getInstance().lookup(CaptacaoLocalidadesService.class);
	
	protected Logger logger = Logger.getLogger(this.getClass().getName());
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		if(service == null) {
			return;
		}
		
		Long time = System.currentTimeMillis();
		
		String uf = context.getJobDetail().getJobDataMap().getString("uf");
		
		logger.info(String.format("CAPTACAO DE LOCALIDADES PARA %s --> BEGIN", uf));
		
		try {
			service.captarLocalidades(uf);
		} catch(Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
		
		time = System.currentTimeMillis() - time;
		
		logger.info(String.format("CAPTACAO DE LOCALIDADES PARA %s --> END - Tempo total: %sms", uf, time));
	}
}