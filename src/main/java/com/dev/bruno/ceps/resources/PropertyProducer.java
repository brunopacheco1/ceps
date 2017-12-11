package com.dev.bruno.ceps.resources;

import java.io.InputStream;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import com.dev.bruno.ceps.exceptions.PropertiesFileException;

public class PropertyProducer {

	private Properties properties;

	@Produces
	public String produceString(final InjectionPoint ip) {
		return this.properties.getProperty(getKey(ip));
	}

	@Produces
	public Integer produceInteger(final InjectionPoint ip) {
		return Integer.valueOf(this.properties.getProperty(getKey(ip)));
	}

	@Produces
	public Properties produceProperties(final InjectionPoint ip) {
		return this.properties;
	}

	@Produces
	public Boolean produceBoolean(final InjectionPoint ip) {
		return Boolean.valueOf(this.properties.getProperty(getKey(ip)));
	}

	private String getKey(final InjectionPoint ip) {
		return (ip.getAnnotated().isAnnotationPresent(Configurable.class)
				&& !ip.getAnnotated().getAnnotation(Configurable.class).value().isEmpty())
						? ip.getAnnotated().getAnnotation(Configurable.class).value()
						: ip.getMember().getName();
	}

	@PostConstruct
	public void init() {
		this.properties = new Properties();
		final InputStream stream = PropertyProducer.class.getResourceAsStream("/META-INF/application.properties");

		try {
			this.properties.load(stream);
		} catch (Exception e) {
			throw new PropertiesFileException("Configuration could not be loaded!");
		}
	}
}