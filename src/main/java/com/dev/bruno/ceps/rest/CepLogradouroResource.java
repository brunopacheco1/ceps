package com.dev.bruno.ceps.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.dev.bruno.ceps.model.Cep;
import com.dev.bruno.ceps.model.CepLogradouro;
import com.dev.bruno.ceps.responses.ResultList;
import com.dev.bruno.ceps.service.AbstractService;
import com.dev.bruno.ceps.service.CepLogradouroService;

import io.swagger.annotations.Api;

@RequestScoped
@Path("logradouro")
@Api(tags="logradouro", value="Servicos consulta e persistencia relacionados a Logradouro")
public class CepLogradouroResource extends AbstractResource<CepLogradouro> {

	@Inject
	private CepLogradouroService service;
	
	@Override
	protected AbstractService<CepLogradouro> getService() {
		return service;
	}
	
	@GET
	@Path("/{id}/ceps")
	public ResultList<Cep> getCepsDeLogradouro(@PathParam("id") Long id) throws Exception {
		return service.getCeps(id);
	}
}