package com.dev.bruno.ceps.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.dev.bruno.ceps.model.Cep;
import com.dev.bruno.ceps.model.Bairro;
import com.dev.bruno.ceps.model.Localidade;
import com.dev.bruno.ceps.model.Logradouro;
import com.dev.bruno.ceps.responses.ResultList;
import com.dev.bruno.ceps.services.AbstractService;
import com.dev.bruno.ceps.services.LocalidadeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequestScoped
@Path("localidade")
@Tag(name = "localidade", description = "Servicos consulta e persistencia relacionados a Localidade")
@SecurityScheme(name = "api_key", type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.HEADER)
public class LocalidadeResource extends AbstractResource<Localidade> {

	@Inject
	private LocalidadeService service;

	@Override
	protected AbstractService<Localidade> getService() {
		return service;
	}

	@GET
	@Path("/{id:\\d+}/bairros")
	@Operation(description = "Busca de Bairros por Localidade")
	@SecurityRequirement(name = "api_key")
	public ResultList<Bairro> getBairrosDeLocalidade(@PathParam("id") Long id) {
		return service.getBairros(id);
	}

	@GET
	@Path("/{id:\\d+}/logradouros")
	@Operation(description = "Busca de Logradouros por Localidade")
	@SecurityRequirement(name = "api_key")
	public ResultList<Logradouro> getLogradourosDeLocalidade(@PathParam("id") Long id) {
		return service.getLogradouros(id);
	}

	@GET
	@Path("/{id:\\d+}/ceps")
	@Operation(description = "Busca de CEPs por Localidade")
	@SecurityRequirement(name = "api_key")
	public ResultList<Cep> getCepsDeLocalidade(@PathParam("id") Long id) {
		return service.getCeps(id);
	}
}