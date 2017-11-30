package com.dev.bruno.ceps.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.dev.bruno.ceps.dao.AbstractDAO;
import com.dev.bruno.ceps.dao.CepBairroDAO;
import com.dev.bruno.ceps.dao.CepDAO;
import com.dev.bruno.ceps.dao.CepLocalidadeDAO;
import com.dev.bruno.ceps.dao.CepLogradouroDAO;
import com.dev.bruno.ceps.model.Cep;
import com.dev.bruno.ceps.model.CepBairro;
import com.dev.bruno.ceps.model.CepLocalidade;
import com.dev.bruno.ceps.model.CepLogradouro;
import com.dev.bruno.ceps.model.CepTipo;
import com.dev.bruno.ceps.responses.ResultList;

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

	@Override
	protected void build(Cep entity) throws Exception {
		CepLocalidade localidade = null;

		CepBairro bairro = null;

		CepLogradouro logradouro = null;

		Long cepLocalidadeId = entity.getCepLocalidadeId();

		Long cepBairroId = entity.getCepBairroId();

		Long cepLogradouroId = entity.getCepLogradouroId();

		if (cepLocalidadeId != null) {
			localidade = cepLocalidadeDAO.get(cepLocalidadeId);
		}

		if (cepBairroId != null) {
			bairro = cepBairroDAO.get(cepBairroId);
		}

		if (cepLogradouroId != null) {
			logradouro = cepLogradouroDAO.get(cepLogradouroId);
		}

		entity.setCepLocalidade(localidade);

		entity.setCepBairro(bairro);

		entity.setCepLogradouro(logradouro);
	}

	public ResultList<Cep> getCeps(CepTipo cepTipo, Integer start, Integer limit, String order, String dir)
			throws Exception {

		if (start == null) {
			start = 0;
		}

		if (limit == null) {
			limit = 100;
		}

		if (order == null) {
			order = "id";
		}

		if (dir == null) {
			dir = "asc";
		}

		List<Cep> entities = cepDAO.list(cepTipo, start, limit, order, dir);

		ResultList<Cep> result = new ResultList<>();

		result.setResult(entities);
		result.setLimit(limit);
		result.setOrder(order);
		result.setDir(dir);
		result.setStart(start);

		return result;
	}
}