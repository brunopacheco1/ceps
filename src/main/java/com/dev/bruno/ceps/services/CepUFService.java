package com.dev.bruno.ceps.services;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Validator;

import com.dev.bruno.ceps.dao.AbstractDAO;
import com.dev.bruno.ceps.dao.CepUFDAO;
import com.dev.bruno.ceps.model.CepLocalidade;
import com.dev.bruno.ceps.model.CepUF;
import com.dev.bruno.ceps.responses.ResultList;

@Stateless
public class CepUFService extends AbstractService<CepUF> {

	@Inject
	private CepUFDAO cepUFDAO;

	@Override
	protected AbstractDAO<CepUF> getDAO() {
		return cepUFDAO;
	}

	public CepUFService() {
	}

	public CepUFService(CepUFDAO cepUFDAO, Validator validator) {
		this.cepUFDAO = cepUFDAO;
		this.validator = validator;
	}

	@Override
	protected void build(CepUF entity) {
	}

	public ResultList<CepLocalidade> getLocalidades(Long cepUFId) throws Exception {
		CepUF uf = getDAO().get(cepUFId);

		List<CepLocalidade> entities = uf.getLocalidades();

		ResultList<CepLocalidade> result = new ResultList<>();

		result.setResult(entities);
		result.setLimit(entities.size());

		return result;
	}
}