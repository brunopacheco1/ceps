package com.dev.bruno.ceps.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;

import com.dev.bruno.ceps.model.CepLocalidade;
import com.dev.bruno.ceps.service.AbstractService;
import com.dev.bruno.ceps.service.CepLocalidadeService;

@RequestScoped
@Path("localidade")
public class CepLocalidadeResource extends AbstractResource<CepLocalidade> {

	@Inject
	private CepLocalidadeService service;
	
	@Override
	protected AbstractService<CepLocalidade> getService() {
		return service;
	}
}