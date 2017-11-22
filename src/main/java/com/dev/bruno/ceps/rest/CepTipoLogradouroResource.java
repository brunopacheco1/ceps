package com.dev.bruno.ceps.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;

import com.dev.bruno.ceps.model.CepTipoLogradouro;
import com.dev.bruno.ceps.service.AbstractService;
import com.dev.bruno.ceps.service.CepTipoLogradouroService;

@RequestScoped
@Path("tipo-logradouro")
public class CepTipoLogradouroResource extends AbstractResource<CepTipoLogradouro> {

	@Inject
	private CepTipoLogradouroService service;
	
	@Override
	protected AbstractService<CepTipoLogradouro> getService() {
		return service;
	}
}