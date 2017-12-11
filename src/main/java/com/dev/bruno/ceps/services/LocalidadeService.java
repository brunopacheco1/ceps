package com.dev.bruno.ceps.services;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Validator;

import com.dev.bruno.ceps.dao.AbstractDAO;
import com.dev.bruno.ceps.dao.LocalidadeDAO;
import com.dev.bruno.ceps.dao.UFDAO;
import com.dev.bruno.ceps.model.Cep;
import com.dev.bruno.ceps.model.Bairro;
import com.dev.bruno.ceps.model.Localidade;
import com.dev.bruno.ceps.model.Logradouro;
import com.dev.bruno.ceps.model.UF;
import com.dev.bruno.ceps.responses.ResultList;
import com.dev.bruno.ceps.utils.StringUtils;

@Stateless
public class LocalidadeService extends AbstractService<Localidade> {

	@Inject
	private LocalidadeDAO localidadeDAO;

	@Inject
	private UFDAO ufDAO;
	
	public LocalidadeService(){}

	public LocalidadeService(LocalidadeDAO localidadeDAO, UFDAO ufDAO, Validator validator) {
		super();
		this.localidadeDAO = localidadeDAO;
		this.ufDAO = ufDAO;
		this.validator = validator;
	}

	@Override
	protected AbstractDAO<Localidade> getDAO() {
		return localidadeDAO;
	}

	@Override
	protected void build(Localidade localidade) {
		Long ufId = localidade.getUfId();

		UF uf = ufDAO.get(ufId);

		localidade.setUf(uf);

		String distrito = localidade.getDistrito();

		String nome = localidade.getNome();

		if (nome != null) {
			if (distrito == null) {
				localidade.setNomeNormalizado(StringUtils.normalizarNome(nome));
			} else {
				localidade.setNomeNormalizado(StringUtils.normalizarNome(distrito + " (" + nome + ")"));
			}
		}
	}

	public ResultList<Bairro> getBairros(Long localidadeId) {
		Localidade localidade = getDAO().get(localidadeId);

		List<Bairro> bairros = localidade.getBairros();

		ResultList<Bairro> result = new ResultList<>();

		result.setResult(bairros);
		result.setLimit(bairros.size());

		return result;
	}

	public ResultList<Logradouro> getLogradouros(Long localidadeId) {
		Localidade localidade = getDAO().get(localidadeId);

		List<Logradouro> logradouros = localidade.getLogradouros();

		ResultList<Logradouro> result = new ResultList<>();

		result.setResult(logradouros);
		result.setLimit(logradouros.size());

		return result;
	}

	public ResultList<Cep> getCeps(Long localidadeId) {
		Localidade localidade = getDAO().get(localidadeId);

		List<Cep> ceps = localidade.getCeps();

		ResultList<Cep> result = new ResultList<>();

		result.setResult(ceps);
		result.setLimit(ceps.size());

		return result;
	}
}