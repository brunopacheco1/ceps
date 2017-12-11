package com.dev.bruno.ceps.services;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Validator;

import com.dev.bruno.ceps.dao.AbstractDAO;
import com.dev.bruno.ceps.dao.UFDAO;
import com.dev.bruno.ceps.model.Localidade;
import com.dev.bruno.ceps.model.UF;
import com.dev.bruno.ceps.responses.ResultList;

@Stateless
public class UFService extends AbstractService<UF> {

	@Inject
	private UFDAO cepUFDAO;

	@Override
	protected AbstractDAO<UF> getDAO() {
		return cepUFDAO;
	}

	public UFService() {
	}

	public UFService(UFDAO cepUFDAO, Validator validator) {
		this.cepUFDAO = cepUFDAO;
		this.validator = validator;
	}

	@Override
	protected void build(UF entity) {
		//UF nao possui dependencias a serem contruidas
	}

	public ResultList<Localidade> getLocalidades(Long cepUFId) {
		UF uf = getDAO().get(cepUFId);

		List<Localidade> entities = uf.getLocalidades();

		ResultList<Localidade> result = new ResultList<>();

		result.setResult(entities);
		result.setLimit(entities.size());

		return result;
	}
}