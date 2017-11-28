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

@RequestScoped
@Path("captacao")
@Produces(MediaType.APPLICATION_JSON)
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
	public GenericResponse captarLocalidades(@PathParam("uf") String uf) throws Exception {
		captacaoLocalidadesService.captarLocalidades(uf);

		return new GenericResponse(true);
	}

	@POST
	@Path("/uf/{uf}/bairros")
	public GenericResponse captarBairros(@PathParam("uf") String uf) throws Exception {
		captacaoBairrosService.captarBairros(uf);

		return new GenericResponse(true);
	}

	@POST
	@Path("/uf/{uf}/ceps-especiais")
	public GenericResponse captarCepsEspeciais(@PathParam("uf") String uf) throws Exception {
		captacaoCepsEspeciaisService.captarCepsEspeciais(uf);

		return new GenericResponse(true);
	}

	@POST
	@Path("/localidade/{id}/faixas-cep")
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
	@Path("/bairro/{id}/ceps")
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
