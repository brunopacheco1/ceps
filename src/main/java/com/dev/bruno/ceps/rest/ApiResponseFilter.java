package com.dev.bruno.ceps.rest;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

@Provider
public class ApiResponseFilter implements ContainerResponseFilter {

	@Override
	public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext)
			throws IOException {

		MediaType contentType = responseContext.getMediaType();
		if (contentType != null && !contentType.toString().contains("charset")) {
			responseContext.getHeaders().putSingle("Content-Type", contentType + ";charset=utf-8");
		}

		responseContext.getHeaders().putSingle("Access-Control-Allow-Origin", "*");
		responseContext.getHeaders().putSingle("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
		responseContext.getHeaders().putSingle("Access-Control-Allow-Headers",
				"Content-Type, access_token, Authorization");
	}
}