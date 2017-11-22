package com.dev.bruno.ceps.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.dev.bruno.ceps.model.CepLocalidade;
import com.dev.bruno.ceps.model.CepUF;
import com.dev.bruno.ceps.responses.ResultList;
import com.dev.bruno.ceps.service.AbstractService;
import com.dev.bruno.ceps.service.CepUFService;

@RequestScoped
@Path("uf")
public class CepUFResource extends AbstractResource<CepUF> {

	@Inject
	private CepUFService service;

	@Override
	protected AbstractService<CepUF> getService() {
		return service;
	}

	@GET
	@Path("/{id}/localidades")
	public ResultList<CepLocalidade> getLocalidades(@PathParam("id") Long id) throws Exception {
		return service.getLocalidades(id);
	}
}