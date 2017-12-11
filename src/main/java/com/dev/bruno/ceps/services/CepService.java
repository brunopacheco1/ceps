package com.dev.bruno.ceps.services;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Validator;

import com.dev.bruno.ceps.dao.AbstractDAO;
import com.dev.bruno.ceps.dao.BairroDAO;
import com.dev.bruno.ceps.dao.CepDAO;
import com.dev.bruno.ceps.dao.LocalidadeDAO;
import com.dev.bruno.ceps.dao.LogradouroDAO;
import com.dev.bruno.ceps.model.Bairro;
import com.dev.bruno.ceps.model.Cep;
import com.dev.bruno.ceps.model.Localidade;
import com.dev.bruno.ceps.model.Logradouro;
import com.dev.bruno.ceps.model.TipoCepEnum;
import com.dev.bruno.ceps.responses.ResultList;

@Stateless
public class CepService extends AbstractService<Cep> {

	@Inject
	private CepDAO cepDAO;

	@Inject
	private LocalidadeDAO cepLocalidadeDAO;

	@Inject
	private BairroDAO cepBairroDAO;

	@Inject
	private LogradouroDAO cepLogradouroDAO;

	public CepService() {
	}

	public CepService(CepDAO cepDAO, LocalidadeDAO cepLocalidadeDAO, BairroDAO cepBairroDAO,
			LogradouroDAO cepLogradouroDAO, Validator validator) {
		super();
		this.cepDAO = cepDAO;
		this.cepLocalidadeDAO = cepLocalidadeDAO;
		this.cepBairroDAO = cepBairroDAO;
		this.cepLogradouroDAO = cepLogradouroDAO;
		this.validator = validator;
	}

	@Override
	protected AbstractDAO<Cep> getDAO() {
		return cepDAO;
	}

	@Override
	protected void build(Cep entity) {
		Localidade localidade = null;

		Bairro bairro = null;

		Logradouro logradouro = null;

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

	public ResultList<Cep> getCeps(TipoCepEnum cepTipo, Integer start, Integer limit, String order, String dir) {

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