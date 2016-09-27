package com.dev.bruno.ceps.resource;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Path;

import com.dev.bruno.ceps.dto.CepBairroDTO;
import com.dev.bruno.ceps.model.CepBairro;
import com.dev.bruno.ceps.service.AbstractService;
import com.dev.bruno.ceps.service.CepBairroService;

@Stateless
@Path("bairro")
public class CepBairroResource extends AbstractResource<CepBairro, CepBairroDTO> {

	@EJB
	private CepBairroService service;
	
	@Override
	protected AbstractService<CepBairro, CepBairroDTO> getService() {
		return service;
	}
}