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
	private LocalidadeDAO localidadeDAO;

	@Inject
	private BairroDAO bairroDAO;

	@Override
	protected AbstractDAO<Bairro> getDAO() {
		return bairroDAO;
	}

	public BairroService() {
	}

	public BairroService(LocalidadeDAO localidadeDAO, BairroDAO bairroDAO, Validator validator) {
		this.bairroDAO = bairroDAO;
		this.localidadeDAO = localidadeDAO;
		this.validator = validator;
	}

	@Override
	protected void build(Bairro bairro) {
		Long localidadeId = bairro.getLocalidadeId();

		Localidade localidade = localidadeDAO.get(localidadeId);

		bairro.setLocalidade(localidade);

		String nome = bairro.getNome();

		if (nome != null) {
			bairro.setNomeNormalizado(StringUtils.normalizarNome(nome));
		}
	}

	public ResultList<Logradouro> getLogradouros(Long bairroId) {
		Bairro bairro = getDAO().get(bairroId);

		List<Logradouro> logradouros = bairro.getLogradouros();

		ResultList<Logradouro> result = new ResultList<>();

		result.setResult(logradouros);
		result.setLimit(logradouros.size());

		return result;
	}

	public ResultList<Cep> getCeps(Long bairroId) {
		Bairro bairro = getDAO().get(bairroId);

		List<Cep> ceps = bairro.getCeps();

		ResultList<Cep> result = new ResultList<>();

		result.setResult(ceps);
		result.setLimit(ceps.size());

		return result;
	}
}