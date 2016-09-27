package com.dev.bruno.ceps.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CepsProperties {

	private CepsProperties() {
		try {
			InputStream inputStream = this.getClass().getResourceAsStream("/scheduler.properties");
			
			load(inputStream);
			
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static CepsProperties instance;
	private boolean loaded = false;
	private Properties properties = new Properties();

	private void load(InputStream stream) throws IOException {
		if(loaded) {
			return;
		}
		
		properties.load(stream);
		loaded = true;
	}
	
	static {
		instance = new CepsProperties();
	}
	
	public static CepsProperties getInstance() {
		return instance;
	}

	public String getProperty(String key) {
		if(!loaded) {
			return null;
		}
		
		return properties.getProperty(key);
	}

	public boolean isLoaded() {
		return loaded;
	}
}