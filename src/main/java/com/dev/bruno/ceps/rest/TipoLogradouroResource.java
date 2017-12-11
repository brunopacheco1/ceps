package com.dev.bruno.ceps.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;

import com.dev.bruno.ceps.model.TipoLogradouro;
import com.dev.bruno.ceps.services.AbstractService;
import com.dev.bruno.ceps.services.TipoLogradouroService;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequestScoped
@Path("tipo-logradouro")
@Tag(name = "tipo-logradouro", description = "Servicos consulta e persistencia relacionados a Tipo de Logradouro")
@SecurityScheme(name = "api_key", type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.HEADER)
public class TipoLogradouroResource extends AbstractResource<TipoLogradouro> {

	@Inject
	private TipoLogradouroService service;

	@Override
	protected AbstractService<TipoLogradouro> getService() {
		return service;
	}
}