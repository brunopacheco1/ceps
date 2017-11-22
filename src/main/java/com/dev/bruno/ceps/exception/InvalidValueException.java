package com.dev.bruno.ceps.exception;

public class InvalidValueException extends GenericException {

	private static final long serialVersionUID = 6963057506068431542L;
	
	public InvalidValueException(String msg) {
		super(msg);
	}
}