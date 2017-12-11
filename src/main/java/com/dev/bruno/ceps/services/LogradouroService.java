package com.dev.bruno.ceps.services;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Validator;

import com.dev.bruno.ceps.dao.AbstractDAO;
import com.dev.bruno.ceps.dao.BairroDAO;
import com.dev.bruno.ceps.dao.LocalidadeDAO;
import com.dev.bruno.ceps.dao.LogradouroDAO;
import com.dev.bruno.ceps.dao.TipoLogradouroDAO;
import com.dev.bruno.ceps.model.Cep;
import com.dev.bruno.ceps.model.Bairro;
import com.dev.bruno.ceps.model.Localidade;
import com.dev.bruno.ceps.model.Logradouro;
import com.dev.bruno.ceps.model.TipoLogradouro;
import com.dev.bruno.ceps.responses.ResultList;
import com.dev.bruno.ceps.utils.StringUtils;

@Stateless
public class LogradouroService extends AbstractService<Logradouro> {

	@Inject
	private LocalidadeDAO cepLocalidadeDAO;

	@Inject
	private BairroDAO cepBairroDAO;

	@Inject
	private TipoLogradouroDAO cepTipoLogradouroDAO;

	@Inject
	private LogradouroDAO cepLogradouroDAO;

	@Override
	protected AbstractDAO<Logradouro> getDAO() {
		return cepLogradouroDAO;
	}

	public LogradouroService() {
	}

	public LogradouroService(LocalidadeDAO cepLocalidadeDAO, BairroDAO cepBairroDAO,
			TipoLogradouroDAO cepTipoLogradouroDAO, LogradouroDAO cepLogradouroDAO, Validator validator) {
		this.cepLocalidadeDAO = cepLocalidadeDAO;
		this.cepBairroDAO = cepBairroDAO;
		this.cepTipoLogradouroDAO = cepTipoLogradouroDAO;
		this.cepLogradouroDAO = cepLogradouroDAO;
		this.validator = validator;
	}

	@Override
	protected void build(Logradouro entity) {
		Bairro bairro = null;

		TipoLogradouro tipoLogradouro = null;

		Long cepLocalidadeId = entity.getCepLocalidadeId();

		Long cepBairroId = entity.getCepBairroId();

		Long cepTipoLogradouroId = entity.getCepTipoLogradouroId();

		Localidade localidade = cepLocalidadeDAO.get(cepLocalidadeId);

		if (cepBairroId != null) {
			bairro = cepBairroDAO.get(cepBairroId);
		}

		if (cepTipoLogradouroId != null) {
			tipoLogradouro = cepTipoLogradouroDAO.get(cepTipoLogradouroId);
		}

		entity.setCepLocalidade(localidade);

		entity.setCepBairro(bairro);

		entity.setCepTipoLogradouro(tipoLogradouro);

		String nome = entity.getNome();

		if (nome != null) {
			entity.setNomeNormalizado(StringUtils.normalizarNome(nome));
		}
	}

	public ResultList<Cep> getCeps(Long cepLogradouroId) {
		Logradouro logradouro = getDAO().get(cepLogradouroId);

		List<Cep> entities = logradouro.getCeps();

		ResultList<Cep> result = new ResultList<>();

		result.setResult(entities);
		result.setLimit(entities.size());

		return result;
	}
}