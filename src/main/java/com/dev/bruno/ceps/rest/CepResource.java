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
import com.dev.bruno.ceps.services.AbstractService;
import com.dev.bruno.ceps.services.CepService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequestScoped
@Path("cep")
@Tag(name = "cep", description = "Servicos consulta e persistencia relacionados a CEP")
@SecurityScheme(name = "api_key", type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.HEADER)
public class CepResource extends AbstractResource<Cep> {

	@Inject
	private CepService service;

	@Override
	protected AbstractService<Cep> getService() {
		return service;
	}

	@GET
	@Path("/{tipo:[A-Z]{3}}")
	@Operation(description = "Busca de CEPs por Tipo de CEP")
	@SecurityRequirement(name = "api_key")
	public ResultList<Cep> getCepsPorTipo(@PathParam("tipo") CepTipo cepTipo, @QueryParam("start") Integer start,
			@QueryParam("limit") Integer limit, @QueryParam("order") String order, @QueryParam("dir") String dir)
			throws Exception {
		return service.getCeps(cepTipo, start, limit, order, dir);
	}
}