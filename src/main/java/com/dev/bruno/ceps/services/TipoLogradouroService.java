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
	private TipoLogradouroDAO tipoLogradouroDAO;

	public TipoLogradouroService() {
	}

	public TipoLogradouroService(TipoLogradouroDAO tipoLogradouroDAO, Validator validator) {
		this.tipoLogradouroDAO = tipoLogradouroDAO;
		this.validator = validator;
	}

	@Override
	protected AbstractDAO<TipoLogradouro> getDAO() {
		return tipoLogradouroDAO;
	}

	@Override
	protected void build(TipoLogradouro tipoLogradouro) {
		// tipo de logradouro nao possui dependencias a serem contruidas
	}
}