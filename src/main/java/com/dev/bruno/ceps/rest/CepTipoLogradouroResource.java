package com.dev.bruno.ceps.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;

import com.dev.bruno.ceps.model.CepTipoLogradouro;
import com.dev.bruno.ceps.service.AbstractService;
import com.dev.bruno.ceps.service.CepTipoLogradouroService;

import io.swagger.annotations.Api;

@RequestScoped
@Path("tipo-logradouro")
@Api(tags="tipo-logradouro", value="Servicos consulta e persistencia relacionados a Tipo de Logradouro")
public class CepTipoLogradouroResource extends AbstractResource<CepTipoLogradouro> {

	@Inject
	private CepTipoLogradouroService service;
	
	@Override
	protected AbstractService<CepTipoLogradouro> getService() {
		return service;
	}
}