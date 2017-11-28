package com.dev.bruno.ceps.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;

import com.dev.bruno.ceps.model.Cep;
import com.dev.bruno.ceps.service.AbstractService;
import com.dev.bruno.ceps.service.CepService;

import io.swagger.annotations.Api;

@RequestScoped
@Path("cep")
@Api(tags="cep", value="Servicos consulta e persistencia relacionados a CEP")
public class CepResource extends AbstractResource<Cep> {

	@Inject
	private CepService service;
	
	@Override
	protected AbstractService<Cep> getService() {
		return service;
	}
}