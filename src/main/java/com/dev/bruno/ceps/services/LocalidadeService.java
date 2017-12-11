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
	private LocalidadeDAO cepLocalidadeDAO;

	@Inject
	private UFDAO cepUFDAO;
	
	public LocalidadeService(){}

	public LocalidadeService(LocalidadeDAO cepLocalidadeDAO, UFDAO cepUFDAO, Validator validator) {
		super();
		this.cepLocalidadeDAO = cepLocalidadeDAO;
		this.cepUFDAO = cepUFDAO;
		this.validator = validator;
	}

	@Override
	protected AbstractDAO<Localidade> getDAO() {
		return cepLocalidadeDAO;
	}

	@Override
	protected void build(Localidade entity) {
		Long ufId = entity.getCepUFId();

		UF uf = cepUFDAO.get(ufId);

		entity.setCepUF(uf);

		String distrito = entity.getDistrito();

		String nome = entity.getNome();

		if (nome != null) {
			if (distrito == null) {
				entity.setNomeNormalizado(StringUtils.normalizarNome(nome));
			} else {
				entity.setNomeNormalizado(StringUtils.normalizarNome(distrito + " (" + nome + ")"));
			}
		}
	}

	public ResultList<Bairro> getBairros(Long cepLocalidadeId) {
		Localidade localidade = getDAO().get(cepLocalidadeId);

		List<Bairro> entities = localidade.getBairros();

		ResultList<Bairro> result = new ResultList<>();

		result.setResult(entities);
		result.setLimit(entities.size());

		return result;
	}

	public ResultList<Logradouro> getLogradouros(Long cepLocalidadeId) {
		Localidade localidade = getDAO().get(cepLocalidadeId);

		List<Logradouro> entities = localidade.getLogradouros();

		ResultList<Logradouro> result = new ResultList<>();

		result.setResult(entities);
		result.setLimit(entities.size());

		return result;
	}

	public ResultList<Cep> getCeps(Long cepLocalidadeId) {
		Localidade localidade = getDAO().get(cepLocalidadeId);

		List<Cep> entities = localidade.getCeps();

		ResultList<Cep> result = new ResultList<>();

		result.setResult(entities);
		result.setLimit(entities.size());

		return result;
	}
}