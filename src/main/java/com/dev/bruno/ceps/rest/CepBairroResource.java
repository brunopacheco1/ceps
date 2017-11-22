package com.dev.bruno.ceps.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;

import com.dev.bruno.ceps.model.CepBairro;
import com.dev.bruno.ceps.service.AbstractService;
import com.dev.bruno.ceps.service.CepBairroService;

@RequestScoped
@Path("bairro")
public class CepBairroResource extends AbstractResource<CepBairro> {

	@Inject
	private CepBairroService service;
	
	@Override
	protected AbstractService<CepBairro> getService() {
		return service;
	}
}