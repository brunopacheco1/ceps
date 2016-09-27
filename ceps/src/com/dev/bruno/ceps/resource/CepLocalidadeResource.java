package com.dev.bruno.ceps.resource;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Path;

import com.dev.bruno.ceps.dto.CepLocalidadeDTO;
import com.dev.bruno.ceps.model.CepLocalidade;
import com.dev.bruno.ceps.service.AbstractService;
import com.dev.bruno.ceps.service.CepLocalidadeService;

@Stateless
@Path("localidade")
public class CepLocalidadeResource extends AbstractResource<CepLocalidade, CepLocalidadeDTO> {

	@EJB
	private CepLocalidadeService service;
	
	@Override
	protected AbstractService<CepLocalidade, CepLocalidadeDTO> getService() {
		return service;
	}
}