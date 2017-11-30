package com.dev.bruno.ceps.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.dev.bruno.ceps.responses.GenericResponse;
import com.dev.bruno.ceps.service.CaptacaoBairrosService;
import com.dev.bruno.ceps.service.CaptacaoCepsEspeciaisService;
import com.dev.bruno.ceps.service.CaptacaoCepsService;
import com.dev.bruno.ceps.service.CaptacaoFaixasCepService;
import com.dev.bruno.ceps.service.CaptacaoLocalidadesService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RequestScoped
@Path("captacao")
@Produces(MediaType.APPLICATION_JSON)
@Api(tags = "captacao", value = "Servicos de Captacao", authorizations = @Authorization(value = "api_key", scopes = {}))
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
	@Path("/uf/{uf:[A-Z]{2}}/localidades")
	@ApiOperation(value = "Captacao de Localidade")
	public GenericResponse captarLocalidades(@PathParam("uf") String uf) throws Exception {
		captacaoLocalidadesService.agendarCaptacaoLocalidades(uf);

		return new GenericResponse(true);
	}

	@POST
	@Path("/uf/{uf:[A-Z]{2}}/bairros")
	@ApiOperation(value = "Captacao de Bairros")
	public GenericResponse captarBairros(@PathParam("uf") String uf) throws Exception {
		captacaoBairrosService.agendarCaptacaoBairros(uf);

		return new GenericResponse(true);
	}

	@POST
	@Path("/uf/{uf:[A-Z]{2}}/ceps-especiais")
	@ApiOperation(value = "Captacao de CEPs Especiais")
	public GenericResponse captarCepsEspeciaisByUF(@PathParam("uf") String uf) throws Exception {
		captacaoCepsEspeciaisService.agendarCaptacaoCepsEspeciais(uf);

		return new GenericResponse(true);
	}

	@POST
	@Path("/localidades/faixas-cep")
	@ApiOperation(value = "Captacao de Faixas de CEPs")
	public GenericResponse captarCepsEspeciais() throws Exception {
		captacaoFaixasCepsService.agendarCaptacaoFaixasCep();

		return new GenericResponse(true);
	}

	@POST
	@Path("/bairros/ceps")
	@ApiOperation(value = "Captacao de CEPs")
	public GenericResponse captarCeps() throws Exception {
		captacaoCepsService.agendarCaptacaoCeps();

		return new GenericResponse(true);
	}
}
