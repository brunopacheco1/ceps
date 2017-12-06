package com.dev.bruno.ceps.exceptions;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.dev.bruno.ceps.responses.GenericResponse;

@Provider
public class ThrowableMapper implements ExceptionMapper<Throwable> {

	private Logger logger = Logger.getLogger(getClass().getName());

	@Override
	public Response toResponse(Throwable t) {
		GenericResponse response = new GenericResponse("Erro não esperado.");

		logger.log(Level.SEVERE, t.getMessage(), t);

		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(response).type(MediaType.APPLICATION_JSON).build();
	}
}