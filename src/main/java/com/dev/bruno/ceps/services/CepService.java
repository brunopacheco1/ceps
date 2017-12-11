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
	private LocalidadeDAO localidadeDAO;

	@Inject
	private BairroDAO bairroDAO;

	@Inject
	private LogradouroDAO logradouroDAO;

	public CepService() {
	}

	public CepService(CepDAO cepDAO, LocalidadeDAO localidadeDAO, BairroDAO bairroDAO, LogradouroDAO logradouroDAO,
			Validator validator) {
		super();
		this.cepDAO = cepDAO;
		this.localidadeDAO = localidadeDAO;
		this.bairroDAO = bairroDAO;
		this.logradouroDAO = logradouroDAO;
		this.validator = validator;
	}

	@Override
	protected AbstractDAO<Cep> getDAO() {
		return cepDAO;
	}

	@Override
	protected void build(Cep cep) {
		Localidade localidade = null;

		Bairro bairro = null;

		Logradouro logradouro = null;

		Long localidadeId = cep.getLocalidadeId();

		Long bairroId = cep.getBairroId();

		Long logradouroId = cep.getLogradouroId();

		if (localidadeId != null) {
			localidade = localidadeDAO.get(localidadeId);
		}

		if (bairroId != null) {
			bairro = bairroDAO.get(bairroId);
		}

		if (logradouroId != null) {
			logradouro = logradouroDAO.get(logradouroId);
		}
		
		String numeroCep = cep.getNumeroCep();
		
		cep.setTipoCep(TipoCepEnum.getTipoPorCEP(numeroCep));

		cep.setLocalidade(localidade);

		cep.setBairro(bairro);

		cep.setLogradouro(logradouro);
	}

	public ResultList<Cep> getCeps(TipoCepEnum tipo, Integer start, Integer limit, String order, String dir) {

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

		List<Cep> ceps = cepDAO.list(tipo, start, limit, order, dir);

		ResultList<Cep> result = new ResultList<>();

		result.setResult(ceps);
		result.setLimit(limit);
		result.setOrder(order);
		result.setDir(dir);
		result.setStart(start);

		return result;
	}
}