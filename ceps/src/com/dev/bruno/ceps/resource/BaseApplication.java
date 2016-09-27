package com.dev.bruno.ceps.resource;

import java.util.Collections;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("api")
public class BaseApplication extends Application {

	@Override
	public Set<Class<?>> getClasses() {
		return Collections.emptySet();
	}
}