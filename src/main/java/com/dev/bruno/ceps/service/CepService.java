package com.dev.bruno.ceps.service;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.dev.bruno.ceps.dao.AbstractDAO;
import com.dev.bruno.ceps.dao.CepBairroDAO;
import com.dev.bruno.ceps.dao.CepDAO;
import com.dev.bruno.ceps.dao.CepLocalidadeDAO;
import com.dev.bruno.ceps.dao.CepLogradouroDAO;
import com.dev.bruno.ceps.model.Cep;

@Stateless
public class CepService extends AbstractService<Cep> {

	@Inject
	private CepDAO cepDAO;
	
	@Inject
	private CepLocalidadeDAO cepLocalidadeDAO;
	
	@Inject
	private CepBairroDAO cepBairroDAO;
	
	@Inject
	private CepLogradouroDAO cepLogradouroDAO;
	
	@Override
	protected AbstractDAO<Cep> getDAO() {
		return cepDAO;
	}
}