package com.dev.bruno.ceps.jobs;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.dev.bruno.ceps.service.CaptacaoCepsEspeciaisService;
import com.dev.bruno.ceps.service.ServiceLocator;

public class CaptacaoCepsEspeciaisJob implements Job {

	private CaptacaoCepsEspeciaisService service = (CaptacaoCepsEspeciaisService) ServiceLocator.getInstance().lookup(CaptacaoCepsEspeciaisService.class);
	
	protected Logger logger = Logger.getLogger(this.getClass().getName());
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		if(service == null) {
			return;
		}
		
		Long time = System.currentTimeMillis();
		
		String uf = context.getJobDetail().getJobDataMap().getString("uf");
		
		logger.info(String.format("CAPTACAO DE CEPS ESPECIAIS PARA %s --> BEGIN", uf));
		
		try {
			service.captarCepsEspeciais(uf);
		} catch(Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
		
		time = System.currentTimeMillis() - time;
		
		logger.info(String.format("CAPTACAO DE CEPS ESPECIAIS PARA %s --> END - Tempo total: %sms", uf, time));
	}
}