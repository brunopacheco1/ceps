package com.dev.bruno.ceps.dto;

import java.io.Serializable;

public abstract class AbstractDTO implements Serializable {

	private static final long serialVersionUID = 6435974476117737767L;

	public abstract Long getId();
	
	public abstract void setId(Long id);
}