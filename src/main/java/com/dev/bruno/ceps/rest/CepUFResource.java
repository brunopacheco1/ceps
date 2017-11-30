package com.dev.bruno.ceps.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.dev.bruno.ceps.model.CepLocalidade;
import com.dev.bruno.ceps.model.CepUF;
import com.dev.bruno.ceps.responses.ResultList;
import com.dev.bruno.ceps.service.AbstractService;
import com.dev.bruno.ceps.service.CepUFService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RequestScoped
@Path("uf")
@Api(tags = "uf", value = "Servicos consulta e persistencia relacionados a UF", authorizations = @Authorization(value = "api_key", scopes = {}))
public class CepUFResource extends AbstractResource<CepUF> {

	@Inject
	private CepUFService service;

	@Override
	protected AbstractService<CepUF> getService() {
		return service;
	}

	@GET
	@Path("/{id:\\d+}/localidades")
	@ApiOperation(value = "Busca de Localidades por UF")
	public ResultList<CepLocalidade> getLocalidadesDeUF(@PathParam("id") Long id) throws Exception {
		return service.getLocalidades(id);
	}
}