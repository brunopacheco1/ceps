package com.dev.bruno.ceps.exceptions;

import javax.ws.rs.core.Response.Status;

public abstract class GenericException extends Exception {

	private static final long serialVersionUID = 4166361747357456492L;
	
	protected Integer status = Status.BAD_REQUEST.getStatusCode(); 

	public GenericException(String msg) {
		super(msg);
	}
	
	public GenericException(String msg, Integer status) {
		super(msg);
		this.status = status;
	}

	public int getStatus() {
		return status;
	}
}