package com.dev.bruno.ceps.resource;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Path;

import com.dev.bruno.ceps.dto.CepLogradouroDTO;
import com.dev.bruno.ceps.model.CepLogradouro;
import com.dev.bruno.ceps.service.AbstractService;
import com.dev.bruno.ceps.service.CepLogradouroService;

@Stateless
@Path("logradouro")
public class CepLogradouroResource extends AbstractResource<CepLogradouro, CepLogradouroDTO> {

	@Inject
	private CepLogradouroService service;
	
	@Override
	protected AbstractService<CepLogradouro, CepLogradouroDTO> getService() {
		return service;
	}
}