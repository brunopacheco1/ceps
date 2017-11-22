package com.dev.bruno.ceps.exceptions;


public class EntityExistsException extends GenericException {

	private static final long serialVersionUID = 6963057506068431542L;
	
	public EntityExistsException(String msg) {
		super(msg);
	}
}