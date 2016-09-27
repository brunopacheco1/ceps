package com.dev.bruno.ceps.interceptor;

import java.util.logging.Logger;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

public class RequestTimeInterceptor {

	@AroundInvoke
	public Object checkTime(InvocationContext invocationContext) throws Exception {
		Logger logger = Logger.getLogger(invocationContext.getTarget().getClass().getName());
		
		Long time = System.currentTimeMillis();
		
		Object result = invocationContext.proceed();
		
		time = System.currentTimeMillis() - time;
		logger.info(String.format("%s() - %sms", invocationContext.getMethod().getName(), time));
		
		return result;
	}
}
