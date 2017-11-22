package com.dev.bruno.ceps.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;

import com.dev.bruno.ceps.model.CepLogradouro;
import com.dev.bruno.ceps.service.AbstractService;
import com.dev.bruno.ceps.service.CepLogradouroService;

@RequestScoped
@Path("logradouro")
public class CepLogradouroResource extends AbstractResource<CepLogradouro> {

	@Inject
	private CepLogradouroService service;
	
	@Override
	protected AbstractService<CepLogradouro> getService() {
		return service;
	}
}