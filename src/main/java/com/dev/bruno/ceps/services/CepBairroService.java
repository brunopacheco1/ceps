package com.dev.bruno.ceps.services;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Validator;

import com.dev.bruno.ceps.dao.AbstractDAO;
import com.dev.bruno.ceps.dao.CepBairroDAO;
import com.dev.bruno.ceps.dao.CepLocalidadeDAO;
import com.dev.bruno.ceps.model.Cep;
import com.dev.bruno.ceps.model.CepBairro;
import com.dev.bruno.ceps.model.CepLocalidade;
import com.dev.bruno.ceps.model.CepLogradouro;
import com.dev.bruno.ceps.responses.ResultList;
import com.dev.bruno.ceps.utils.StringUtils;

@Stateless
public class CepBairroService extends AbstractService<CepBairro> {

	@Inject
	private CepLocalidadeDAO cepLocalidadeDAO;

	@Inject
	private CepBairroDAO cepBairroDAO;

	@Override
	protected AbstractDAO<CepBairro> getDAO() {
		return cepBairroDAO;
	}

	public CepBairroService() {
	}

	public CepBairroService(CepLocalidadeDAO cepLocalidadeDAO, CepBairroDAO cepBairroDAO, Validator validator) {
		this.cepBairroDAO = cepBairroDAO;
		this.cepLocalidadeDAO = cepLocalidadeDAO;
		this.validator = validator;
	}

	@Override
	protected void build(CepBairro entity) {
		Long cepLocalidadeId = entity.getCepLocalidadeId();

		CepLocalidade localidade = cepLocalidadeDAO.get(cepLocalidadeId);

		entity.setCepLocalidade(localidade);

		String nome = entity.getNome();

		if (nome != null) {
			entity.setNomeNormalizado(StringUtils.normalizarNome(nome));
		}
	}

	public ResultList<CepLogradouro> getLogradouros(Long cepBairroId) throws Exception {
		CepBairro bairro = getDAO().get(cepBairroId);

		List<CepLogradouro> entities = bairro.getLogradouros();

		ResultList<CepLogradouro> result = new ResultList<>();

		result.setResult(entities);
		result.setLimit(entities.size());

		return result;
	}

	public ResultList<Cep> getCeps(Long cepBairroId) throws Exception {
		CepBairro bairro = getDAO().get(cepBairroId);

		List<Cep> entities = bairro.getCeps();

		ResultList<Cep> result = new ResultList<>();

		result.setResult(entities);
		result.setLimit(entities.size());

		return result;
	}
}