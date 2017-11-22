package com.dev.bruno.ceps.model;

import java.io.Serializable;

public abstract class AbstractModel implements Serializable {

	private static final long serialVersionUID = 6435974476117737767L;

	public abstract Long getId();
	
	public abstract void setId(Long id);
}