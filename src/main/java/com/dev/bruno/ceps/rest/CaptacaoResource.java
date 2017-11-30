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

@RequestScoped
@Path("captacao")
@Produces(MediaType.APPLICATION_JSON)
@Api(tags="captacao", value="Servicos de Captacao")
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
	public GenericResponse captarLocalidades(@PathParam("uf") String uf) throws Exception {
		captacaoLocalidadesService.captarLocalidades(uf);

		return new GenericResponse(true);
	}

	@POST
	@Path("/uf/{uf:[A-Z]{2}}/bairros")
	public GenericResponse captarBairros(@PathParam("uf") String uf) throws Exception {
		captacaoBairrosService.captarBairros(uf);

		return new GenericResponse(true);
	}

	@POST
	@Path("/uf/{uf:[A-Z]{2}}/ceps-especiais")
	public GenericResponse captarCepsEspeciaisByUF(@PathParam("uf") String uf) throws Exception {
		captacaoCepsEspeciaisService.captarCepsEspeciais(uf);

		return new GenericResponse(true);
	}

	@POST
	@Path("/localidade/{id:\\d+}/faixas-cep")
	public GenericResponse captarCepsEspeciaisById(@PathParam("id") Long cepLocalidadeId) throws Exception {
		captacaoFaixasCepsService.captarFaixasCep(cepLocalidadeId);

		return new GenericResponse(true);
	}

	@POST
	@Path("/localidades/faixas-cep")
	public GenericResponse captarCepsEspeciais() throws Exception {
		captacaoFaixasCepsService.captarFaixasCep();

		return new GenericResponse(true);
	}

	@POST
	@Path("/bairro/{id:\\d+}/ceps")
	public GenericResponse captarCepsById(@PathParam("id") Long cepBairroId) throws Exception {
		captacaoCepsService.captarCeps(cepBairroId);

		return new GenericResponse(true);
	}

	@POST
	@Path("/bairros/ceps")
	public GenericResponse captarCeps() throws Exception {
		captacaoCepsService.captarCeps();

		return new GenericResponse(true);
	}
}
