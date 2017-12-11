package com.dev.bruno.ceps.captacao.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.dev.bruno.ceps.captacao.services.CaptacaoBairrosService;
import com.dev.bruno.ceps.captacao.services.CaptacaoCepsEspeciaisService;
import com.dev.bruno.ceps.captacao.services.CaptacaoCepsService;
import com.dev.bruno.ceps.captacao.services.CaptacaoFaixasCepService;
import com.dev.bruno.ceps.captacao.services.CaptacaoLocalidadesService;
import com.dev.bruno.ceps.model.UFEnum;
import com.dev.bruno.ceps.responses.GenericResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequestScoped
@Path("captacao")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "captacao", description = "Servicos de Captacao")
@SecurityScheme(name = "api_key", type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.HEADER)
public class CaptacaoResource {

	@Inject
	private CaptacaoCepsService captacaoCepsService;

	@Inject
	private CaptacaoBairrosService captacaoBairrosService;

	@Inject
	private CaptacaoLocalidadesService captacaoLocalidadesService;

	@Inject
	private CaptacaoCepsEspeciaisService captacaoCepsEspeciaisService;

	@Inject
	private CaptacaoFaixasCepService captacaoFaixasCepsService;

	@POST
	@Path("/uf/{uf}/localidades")
	@Operation(description = "Captacao de Localidade")
	public GenericResponse captarLocalidades(@PathParam("uf") UFEnum uf) {
		captacaoLocalidadesService.agendarCaptacaoLocalidades(uf);

		return new GenericResponse(true);
	}

	@POST
	@Path("/uf/{uf}/bairros")
	@Operation(description = "Captacao de Bairros")
	@SecurityRequirement(name = "api_key")
	public GenericResponse captarBairros(@PathParam("uf") UFEnum uf) {
		captacaoBairrosService.agendarCaptacaoBairros(uf);

		return new GenericResponse(true);
	}

	@POST
	@Path("/uf/{uf}/ceps-especiais")
	@Operation(description = "Captacao de CEPs Especiais")
	@SecurityRequirement(name = "api_key")
	public GenericResponse captarCepsEspeciaisByUF(@PathParam("uf") UFEnum uf) {
		captacaoCepsEspeciaisService.agendarCaptacaoCepsEspeciais(uf);

		return new GenericResponse(true);
	}

	@POST
	@Path("/localidades/faixas-cep")
	@Operation(description = "Captacao de Faixas de CEPs")
	@SecurityRequirement(name = "api_key")
	public GenericResponse captarCepsEspeciais() {
		captacaoFaixasCepsService.agendarCaptacaoFaixasCep();

		return new GenericResponse(true);
	}

	@POST
	@Path("/bairros/ceps")
	@Operation(description = "Captacao de CEPs")
	@SecurityRequirement(name = "api_key")
	public GenericResponse captarCeps() {
		captacaoCepsService.agendarCaptacaoCeps();

		return new GenericResponse(true);
	}
}
