package com.dev.bruno.ceps.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.dev.bruno.ceps.model.Cep;
import com.dev.bruno.ceps.model.CepBairro;
import com.dev.bruno.ceps.model.CepLocalidade;
import com.dev.bruno.ceps.model.CepLogradouro;
import com.dev.bruno.ceps.responses.ResultList;
import com.dev.bruno.ceps.service.AbstractService;
import com.dev.bruno.ceps.service.CepLocalidadeService;

import io.swagger.annotations.Api;

@RequestScoped
@Path("localidade")
@Api(tags="localidade", value="Servicos consulta e persistencia relacionados a Localidade")
public class CepLocalidadeResource extends AbstractResource<CepLocalidade> {

	@Inject
	private CepLocalidadeService service;
	
	@Override
	protected AbstractService<CepLocalidade> getService() {
		return service;
	}
	
	@GET
	@Path("/{id}/bairros")
	public ResultList<CepBairro> getBairrosDeLocalidade(@PathParam("id") Long id) throws Exception {
		return service.getBairros(id);
	}

	@GET
	@Path("/{id}/logradouros")
	public ResultList<CepLogradouro> getLogradourosDeLocalidade(@PathParam("id") Long id) throws Exception {
		return service.getLogradouros(id);
	}

	@GET
	@Path("/{id}/ceps")
	public ResultList<Cep> getCepsDeLocalidade(@PathParam("id") Long id) throws Exception {
		return service.getCeps(id);
	}
}