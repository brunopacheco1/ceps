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
public class CepLocalidadeResource extends AbstractResource<CepLocalidade> {

	@Inject
	private CepLocalidadeService service;

	@Override
	protected AbstractService<CepLocalidade> getService() {
		return service;
	}

	@GET
	@Path("/{id:\\d+}/bairros")
	@Operation(description = "Busca de Bairros por Localidade")
	@SecurityRequirement(name = "api_key")
	public ResultList<CepBairro> getBairrosDeLocalidade(@PathParam("id") Long id) throws Exception {
		return service.getBairros(id);
	}

	@GET
	@Path("/{id:\\d+}/logradouros")
	@Operation(description = "Busca de Logradouros por Localidade")
	@SecurityRequirement(name = "api_key")
	public ResultList<CepLogradouro> getLogradourosDeLocalidade(@PathParam("id") Long id) throws Exception {
		return service.getLogradouros(id);
	}

	@GET
	@Path("/{id:\\d+}/ceps")
	@Operation(description = "Busca de CEPs por Localidade")
	@SecurityRequirement(name = "api_key")
	public ResultList<Cep> getCepsDeLocalidade(@PathParam("id") Long id) throws Exception {
		return service.getCeps(id);
	}
}