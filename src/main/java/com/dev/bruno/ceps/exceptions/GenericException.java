package com.dev.bruno.ceps.exceptions;

import javax.ws.rs.core.Response.Status;

public abstract class GenericException extends RuntimeException {

	private static final long serialVersionUID = 4166361747357456492L;

	protected final Integer status = Status.BAD_REQUEST.getStatusCode();

	public GenericException(String msg) {
		super(msg);
	}

	public int getStatus() {
		return status;
	}
}