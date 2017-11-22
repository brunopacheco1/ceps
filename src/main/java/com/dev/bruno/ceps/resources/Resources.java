package com.dev.bruno.ceps.resources;

import java.util.logging.Logger;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class Resources {
	
	@Produces
    @PersistenceContext
    private EntityManager em;

	@Produces
	public Logger exposeLogger(InjectionPoint ip) {
		String clazzName = ip.getMember().getDeclaringClass().getName();
		
		return Logger.getLogger(clazzName);
	}
}