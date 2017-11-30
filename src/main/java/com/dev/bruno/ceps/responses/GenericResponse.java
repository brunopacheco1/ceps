package com.dev.bruno.ceps.responses;

public class GenericResponse {

	protected Boolean success = false;
	
	protected String message;

	public GenericResponse(Boolean success, String message) {
		this.success = success;
		this.message = message;
	}
	
	public GenericResponse(Boolean success) {
		this.success = success;
	}
	
	public GenericResponse(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}
}