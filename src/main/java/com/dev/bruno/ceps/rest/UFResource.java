package com.dev.bruno.ceps.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.dev.bruno.ceps.model.Localidade;
import com.dev.bruno.ceps.model.UF;
import com.dev.bruno.ceps.responses.ResultList;
import com.dev.bruno.ceps.services.AbstractService;
import com.dev.bruno.ceps.services.UFService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequestScoped
@Path("uf")
@Tag(name = "uf", description = "Servicos consulta e persistencia relacionados a UF")
@SecurityScheme(name = "api_key", type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.HEADER)
public class UFResource extends AbstractResource<UF> {

	@Inject
	private UFService service;

	@Override
	protected AbstractService<UF> getService() {
		return service;
	}

	@GET
	@Path("/{id:\\d+}/localidades")
	@Operation(description = "Busca de Localidades por UF")
	@SecurityRequirement(name = "api_key")
	public ResultList<Localidade> getLocalidadesDeUF(@PathParam("id") Long id) {
		return service.getLocalidades(id);
	}
}