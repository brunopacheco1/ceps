package com.dev.bruno.ceps.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;

import com.dev.bruno.ceps.model.CepUF;
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
}