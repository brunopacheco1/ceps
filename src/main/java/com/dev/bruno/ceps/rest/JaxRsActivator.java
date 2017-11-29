package com.dev.bruno.ceps.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import io.swagger.jaxrs.config.BeanConfig;

@ApplicationPath("/v1")
public class JaxRsActivator extends Application {

	public JaxRsActivator() {
		BeanConfig beanConfig = new BeanConfig();
		beanConfig.setVersion("1.0");
		beanConfig.setSchemes(new String[] { "http" });
		beanConfig.setHost("echo-api.endpoints.sentimentalizer-169016.cloud.goog");
		beanConfig.setBasePath("/ceps/v1");
		beanConfig.setResourcePackage("com.dev.bruno.ceps.rest");
		beanConfig.setDescription("API de CEPs desenvolvida com o intuido de simplificar a consulta de\r\n"
				+ "    CEPs do Brasil, tendo em vista que o proprio Correios nao possui API de integracao\r\n"
				+ "    simplificada e sem custo.");
		beanConfig.setTitle("API de CEPs");
		beanConfig.setScan(true);

		// x-google-endpoints:
		// - name: echo-api.endpoints.sentimentalizer-169016.cloud.goog
		// target: 198.162.0.1
	}
}