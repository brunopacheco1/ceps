package com.dev.bruno.ceps.exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.dev.bruno.ceps.responses.GenericResponse;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<GenericException> {

	@Override
	public Response toResponse(GenericException t) {
		GenericResponse response = new GenericResponse(t.getMessage());
		
		return Response.status(t.getStatus()).entity(response).type(MediaType.APPLICATION_JSON).build();
	}
}