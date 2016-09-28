package com.dev.bruno.ceps.resource;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Path;

import com.dev.bruno.ceps.dto.CepTipoLogradouroDTO;
import com.dev.bruno.ceps.model.CepTipoLogradouro;
import com.dev.bruno.ceps.service.AbstractService;
import com.dev.bruno.ceps.service.CepTipoLogradouroService;

@Stateless
@Path("tipo-logradouro")
public class CepTipoLogradouroResource extends AbstractResource<CepTipoLogradouro, CepTipoLogradouroDTO> {

	@Inject
	private CepTipoLogradouroService service;
	
	@Override
	protected AbstractService<CepTipoLogradouro, CepTipoLogradouroDTO> getService() {
		return service;
	}
}