package com.dev.bruno.ceps.service;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Validator;

import com.dev.bruno.ceps.dao.AbstractDAO;
import com.dev.bruno.ceps.dao.CepTipoLogradouroDAO;
import com.dev.bruno.ceps.model.CepTipoLogradouro;

@Stateless
public class CepTipoLogradouroService extends AbstractService<CepTipoLogradouro> {

	@Inject
	private CepTipoLogradouroDAO cepTipoLogradouroDAO;

	public CepTipoLogradouroService() {
	}

	public CepTipoLogradouroService(CepTipoLogradouroDAO cepTipoLogradouroDAO, Validator validator) {
		this.cepTipoLogradouroDAO = cepTipoLogradouroDAO;
		this.validator = validator;
	}

	@Override
	protected AbstractDAO<CepTipoLogradouro> getDAO() {
		return cepTipoLogradouroDAO;
	}

	@Override
	protected void build(CepTipoLogradouro entity) {
	}
}