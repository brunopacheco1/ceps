package com.dev.bruno.ceps.resource;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Path;

import com.dev.bruno.ceps.dto.CepUFDTO;
import com.dev.bruno.ceps.model.CepUF;
import com.dev.bruno.ceps.service.AbstractService;
import com.dev.bruno.ceps.service.CepUFService;

@Stateless
@Path("uf")
public class CepUFResource extends AbstractResource<CepUF, CepUFDTO> {

	@Inject
	private CepUFService service;
	
	@Override
	protected AbstractService<CepUF, CepUFDTO> getService() {
		return service;
	}
}