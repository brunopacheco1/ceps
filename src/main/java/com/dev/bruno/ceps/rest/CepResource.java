package com.dev.bruno.ceps.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import com.dev.bruno.ceps.model.Cep;
import com.dev.bruno.ceps.model.CepTipo;
import com.dev.bruno.ceps.responses.ResultList;
import com.dev.bruno.ceps.service.AbstractService;
import com.dev.bruno.ceps.service.CepService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RequestScoped
@Path("cep")
@Api(tags = "cep", value = "Servicos consulta e persistencia relacionados a CEP", authorizations = @Authorization(value = "api_key", scopes = {}))
public class CepResource extends AbstractResource<Cep> {

	@Inject
	private CepService service;

	@Override
	protected AbstractService<Cep> getService() {
		return service;
	}

	@GET
	@Path("/{tipo:[A-Z]{3}}")
	@ApiOperation(value = "Busca de CEPs por Tipo de CEP")
	public ResultList<Cep> getCepsPorTipo(@PathParam("tipo") CepTipo cepTipo, @QueryParam("start") Integer start,
			@QueryParam("limit") Integer limit, @QueryParam("order") String order, @QueryParam("dir") String dir)
			throws Exception {
		return service.getCeps(cepTipo, start, limit, order, dir);
	}
}