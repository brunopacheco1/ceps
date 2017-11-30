package com.dev.bruno.ceps.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.dev.bruno.ceps.model.Cep;
import com.dev.bruno.ceps.model.CepBairro;
import com.dev.bruno.ceps.model.CepLogradouro;
import com.dev.bruno.ceps.responses.ResultList;
import com.dev.bruno.ceps.service.AbstractService;
import com.dev.bruno.ceps.service.CepBairroService;

import io.swagger.annotations.Api;

@RequestScoped
@Path("bairro")
@Api(tags="bairro", value="Servicos consulta e persistencia relacionados a Bairro")
public class CepBairroResource extends AbstractResource<CepBairro> {

	@Inject
	private CepBairroService service;
	
	@Override
	protected AbstractService<CepBairro> getService() {
		return service;
	}
	
	@GET
	@Path("/{id:\\d+}/logradouros")
	public ResultList<CepLogradouro> getLogradourosDeBairro(@PathParam("id") Long id) throws Exception {
		return service.getLogradouros(id);
	}

	@GET
	@Path("/{id:\\d+}/ceps")
	public ResultList<Cep> getCepsDeBairro(@PathParam("id") Long id) throws Exception {
		return service.getCeps(id);
	}
}