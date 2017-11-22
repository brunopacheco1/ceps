package com.dev.bruno.ceps.service;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.dev.bruno.ceps.dao.AbstractDAO;
import com.dev.bruno.ceps.dao.CepUFDAO;
import com.dev.bruno.ceps.model.CepUF;

@Stateless
public class CepUFService extends AbstractService<CepUF> {

	@Inject
	private CepUFDAO cepUFDAO;
	
	@Override
	protected AbstractDAO<CepUF> getDAO() {
		return cepUFDAO;
	}
}