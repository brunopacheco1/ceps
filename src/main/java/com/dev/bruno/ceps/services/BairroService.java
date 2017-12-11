package com.dev.bruno.ceps.services;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Validator;

import com.dev.bruno.ceps.dao.AbstractDAO;
import com.dev.bruno.ceps.dao.BairroDAO;
import com.dev.bruno.ceps.dao.LocalidadeDAO;
import com.dev.bruno.ceps.model.Bairro;
import com.dev.bruno.ceps.model.Cep;
import com.dev.bruno.ceps.model.Localidade;
import com.dev.bruno.ceps.model.Logradouro;
import com.dev.bruno.ceps.responses.ResultList;
import com.dev.bruno.ceps.utils.StringUtils;

@Stateless
public class BairroService extends AbstractService<Bairro> {

	@Inject
	private LocalidadeDAO cepLocalidadeDAO;

	@Inject
	private BairroDAO cepBairroDAO;

	@Override
	protected AbstractDAO<Bairro> getDAO() {
		return cepBairroDAO;
	}

	public BairroService() {
	}

	public BairroService(LocalidadeDAO cepLocalidadeDAO, BairroDAO cepBairroDAO, Validator validator) {
		this.cepBairroDAO = cepBairroDAO;
		this.cepLocalidadeDAO = cepLocalidadeDAO;
		this.validator = validator;
	}

	@Override
	protected void build(Bairro entity) {
		Long cepLocalidadeId = entity.getCepLocalidadeId();

		Localidade localidade = cepLocalidadeDAO.get(cepLocalidadeId);

		entity.setCepLocalidade(localidade);

		String nome = entity.getNome();

		if (nome != null) {
			entity.setNomeNormalizado(StringUtils.normalizarNome(nome));
		}
	}

	public ResultList<Logradouro> getLogradouros(Long cepBairroId) {
		Bairro bairro = getDAO().get(cepBairroId);

		List<Logradouro> entities = bairro.getLogradouros();

		ResultList<Logradouro> result = new ResultList<>();

		result.setResult(entities);
		result.setLimit(entities.size());

		return result;
	}

	public ResultList<Cep> getCeps(Long cepBairroId) {
		Bairro bairro = getDAO().get(cepBairroId);

		List<Cep> entities = bairro.getCeps();

		ResultList<Cep> result = new ResultList<>();

		result.setResult(entities);
		result.setLimit(entities.size());

		return result;
	}
}