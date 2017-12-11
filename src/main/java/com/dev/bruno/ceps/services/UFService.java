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
	private UFDAO ufDAO;

	@Override
	protected AbstractDAO<UF> getDAO() {
		return ufDAO;
	}

	public UFService() {
	}

	public UFService(UFDAO ufDAO, Validator validator) {
		this.ufDAO = ufDAO;
		this.validator = validator;
	}

	@Override
	protected void build(UF uf) {
		// UF nao possui dependencias a serem contruidas
	}

	public ResultList<Localidade> getLocalidades(Long ufId) {
		UF uf = getDAO().get(ufId);

		List<Localidade> localidades = uf.getLocalidades();

		ResultList<Localidade> result = new ResultList<>();

		result.setResult(localidades);
		result.setLimit(localidades.size());

		return result;
	}
}