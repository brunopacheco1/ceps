package com.dev.bruno.ceps.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.dev.bruno.ceps.dao.CepUFDAO;
import com.dev.bruno.ceps.jobs.CaptacaoBairrosJob;
import com.dev.bruno.ceps.jobs.CaptacaoCepsEspeciaisJob;
import com.dev.bruno.ceps.jobs.CaptacaoCepsLogradourosJob;
import com.dev.bruno.ceps.jobs.CaptacaoFaixasCepJob;
import com.dev.bruno.ceps.jobs.CaptacaoLocalidadesJob;
import com.dev.bruno.ceps.model.CepUF;

@Singleton
@Startup
public class SchedulerService {
	
	private Scheduler scheduler;
	
	@Inject
	private CepUFDAO cepUFDAO;
	
	@PostConstruct
	private void scheduleAllJobs() {
		try {
			scheduler = new StdSchedulerFactory().getScheduler();
	    	scheduler.start();
	    	
	    	Boolean ativa = Boolean.parseBoolean(CepsProperties.getInstance().getProperty("captacao.ativa"));
	    	
	    	if(ativa) {
		    	scheduleCaptacaoLocalidades();
		    	scheduleCaptacaoFaixasCeps();
		    	scheduleCaptacaoCepsEspeciais();
		    	scheduleCaptacaoBairros();
		    	scheduleCaptacaoCepsLogradouros();
	    	}
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
	
	private void scheduleCaptacaoCepsEspeciais() throws SchedulerException {
		for(CepUF cepUF : cepUFDAO.list()) {
			String uf = cepUF.getUf();
			
			String basicJobName = uf + "-CaptacaoCepsEspeciais";
			
			String cron = CepsProperties.getInstance().getProperty("captacao.ceps-especiais." + uf);
			
			Map<String, String> data = new HashMap<String, String>();
			data.put("uf", uf);
			
			scheduleJob(basicJobName, cron, CaptacaoCepsEspeciaisJob.class, data);
		}
	}
	
	private void scheduleCaptacaoBairros() throws SchedulerException {
		for(CepUF cepUF : cepUFDAO.list()) {
			String uf = cepUF.getUf();
			
			String basicJobName = uf + "-CaptacaoBairros";
			
			String cron = CepsProperties.getInstance().getProperty("captacao.bairros." + uf);
			
			Map<String, String> data = new HashMap<String, String>();
			data.put("uf", uf);
			
			scheduleJob(basicJobName, cron, CaptacaoBairrosJob.class, data);
		}
	}
	
	private void scheduleCaptacaoLocalidades() throws SchedulerException {
		for(CepUF cepUF : cepUFDAO.list()) {
			String uf = cepUF.getUf();
			
			String basicJobName = uf + "-CaptacaoLocalidades";
			
			String cron = CepsProperties.getInstance().getProperty("captacao.localidades." + uf);
			
			Map<String, String> data = new HashMap<String, String>();
			data.put("uf", uf);
			
			scheduleJob(basicJobName, cron, CaptacaoLocalidadesJob.class, data);
		}
	}
	
	private void scheduleCaptacaoFaixasCeps() throws SchedulerException {
		String basicJobName = "CaptacaoFaixasCep";
		
		String cron = CepsProperties.getInstance().getProperty("captacao.faixas-de-cep.cron");

		scheduleJob(basicJobName, cron, CaptacaoFaixasCepJob.class, null);
	}
	
	private void scheduleCaptacaoCepsLogradouros() throws SchedulerException {
		String basicJobName = "CaptacaoCepsLogradouros";
		
		String cron = CepsProperties.getInstance().getProperty("captacao.ceps-de-logradouros.cron");

		scheduleJob(basicJobName, cron, CaptacaoCepsLogradourosJob.class, null);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void scheduleJob(String basicJobName, String cron, Class<? extends Job> jobClass, Map<String, String> data) throws SchedulerException {
		JobBuilder jobBuilder = JobBuilder.newJob(jobClass).withIdentity(basicJobName + "-Job", basicJobName + "-Group");
		
		if(data != null) {
			for(String key : data.keySet()) {
				jobBuilder.usingJobData(key, data.get(key));
			}
		}
		
		JobDetail job = jobBuilder.build();
		
		TriggerBuilder triggerBuilder = TriggerBuilder.newTrigger().withIdentity(basicJobName + "-Trigger", basicJobName + "-Group");
		
		Trigger trigger = null;
		if(cron != null) {
			trigger = triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
		} else {
			trigger = triggerBuilder.startNow().build();
		}
		
		if(scheduler.checkExists(trigger.getKey())) {
			scheduler.rescheduleJob(trigger.getKey(), trigger);
		} else {
			scheduler.scheduleJob(job, trigger);
		}
	}
	
	@PreDestroy
	private void finish() {
		try {
			scheduler.shutdown(true);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
}