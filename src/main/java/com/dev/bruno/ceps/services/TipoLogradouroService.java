package com.dev.bruno.ceps.services;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Validator;

import com.dev.bruno.ceps.dao.AbstractDAO;
import com.dev.bruno.ceps.dao.TipoLogradouroDAO;
import com.dev.bruno.ceps.model.TipoLogradouro;

@Stateless
public class TipoLogradouroService extends AbstractService<TipoLogradouro> {

	@Inject
	private TipoLogradouroDAO cepTipoLogradouroDAO;

	public TipoLogradouroService() {
	}

	public TipoLogradouroService(TipoLogradouroDAO cepTipoLogradouroDAO, Validator validator) {
		this.cepTipoLogradouroDAO = cepTipoLogradouroDAO;
		this.validator = validator;
	}

	@Override
	protected AbstractDAO<TipoLogradouro> getDAO() {
		return cepTipoLogradouroDAO;
	}

	@Override
	protected void build(TipoLogradouro entity) {
		//tipo de logradouro nao possui dependencias a serem contruidas
	}
}