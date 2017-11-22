package com.dev.bruno.ceps.service;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.dev.bruno.ceps.dao.AbstractDAO;
import com.dev.bruno.ceps.dao.CepBairroDAO;
import com.dev.bruno.ceps.dao.CepLocalidadeDAO;
import com.dev.bruno.ceps.dao.CepLogradouroDAO;
import com.dev.bruno.ceps.dao.CepTipoLogradouroDAO;
import com.dev.bruno.ceps.model.CepLogradouro;

@Stateless
public class CepLogradouroService extends AbstractService<CepLogradouro> {

	@Inject
	private CepLocalidadeDAO cepLocalidadeDAO;
	
	@Inject
	private CepBairroDAO cepBairroDAO;
	
	@Inject
	private CepLogradouroDAO cepLogradouroDAO;
	
	@Inject
	private CepTipoLogradouroDAO cepTipoLogradouroDAO;
	
	@Override
	protected AbstractDAO<CepLogradouro> getDAO() {
		return cepLogradouroDAO;
	}
}