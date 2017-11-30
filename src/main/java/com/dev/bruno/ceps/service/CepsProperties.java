package com.dev.bruno.ceps.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

@Singleton
public class CepsProperties {

	@PostConstruct
	private void init() {
		try {
			InputStream inputStream = this.getClass().getResourceAsStream("/scheduler.properties");

			properties.load(inputStream);

			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Properties properties = new Properties();

	public String getProperty(String key) {
		return properties.getProperty(key);
	}
}