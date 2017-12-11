package com.dev.bruno.ceps.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.dev.bruno.ceps.model.Cep;
import com.dev.bruno.ceps.model.Bairro;
import com.dev.bruno.ceps.model.Logradouro;
import com.dev.bruno.ceps.responses.ResultList;
import com.dev.bruno.ceps.services.AbstractService;
import com.dev.bruno.ceps.services.BairroService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequestScoped
@Path("bairro")
@Tag(name = "bairro", description = "Servicos consulta e persistencia relacionados a Bairro")
@SecurityScheme(name = "api_key", type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.HEADER)
public class BairroResource extends AbstractResource<Bairro> {

	@Inject
	private BairroService service;

	@Override
	protected AbstractService<Bairro> getService() {
		return service;
	}

	@GET
	@Path("/{id:\\d+}/logradouros")
	@Operation(description = "Busca de Logradouros por Bairro")
	@SecurityRequirement(name = "api_key")
	public ResultList<Logradouro> getLogradourosDeBairro(@PathParam("id") Long id) throws Exception {
		return service.getLogradouros(id);
	}

	@GET
	@Path("/{id:\\d+}/ceps")
	@Operation(description = "Busca de CEPs por Bairro")
	@SecurityRequirement(name = "api_key")
	public ResultList<Cep> getCepsDeBairro(@PathParam("id") Long id) throws Exception {
		return service.getCeps(id);
	}
}