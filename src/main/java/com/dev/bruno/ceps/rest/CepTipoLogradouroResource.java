package com.dev.bruno.ceps.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;

import com.dev.bruno.ceps.model.CepTipoLogradouro;
import com.dev.bruno.ceps.services.AbstractService;
import com.dev.bruno.ceps.services.CepTipoLogradouroService;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequestScoped
@Path("tipo-logradouro")
@Tag(name = "tipo-logradouro", description = "Servicos consulta e persistencia relacionados a Tipo de Logradouro")
@SecurityScheme(name = "api_key", type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.HEADER)
public class CepTipoLogradouroResource extends AbstractResource<CepTipoLogradouro> {

	@Inject
	private CepTipoLogradouroService service;

	@Override
	protected AbstractService<CepTipoLogradouro> getService() {
		return service;
	}
}