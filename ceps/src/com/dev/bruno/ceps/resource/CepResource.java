package com.dev.bruno.ceps.resource;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Path;

import com.dev.bruno.ceps.dto.CepDTO;
import com.dev.bruno.ceps.model.Cep;
import com.dev.bruno.ceps.service.AbstractService;
import com.dev.bruno.ceps.service.CepService;

@Stateless
@Path("cep")
public class CepResource extends AbstractResource<Cep, CepDTO> {

	@EJB
	private CepService service;
	
	@Override
	protected AbstractService<Cep, CepDTO> getService() {
		return service;
	}
}