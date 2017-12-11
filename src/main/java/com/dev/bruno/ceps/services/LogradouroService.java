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
import com.dev.bruno.ceps.model.Bairro;
import com.dev.bruno.ceps.model.Cep;
import com.dev.bruno.ceps.model.Localidade;
import com.dev.bruno.ceps.model.Logradouro;
import com.dev.bruno.ceps.model.TipoLogradouro;
import com.dev.bruno.ceps.responses.ResultList;
import com.dev.bruno.ceps.utils.StringUtils;

@Stateless
public class LogradouroService extends AbstractService<Logradouro> {

	@Inject
	private LocalidadeDAO localidadeDAO;

	@Inject
	private BairroDAO bairroDAO;

	@Inject
	private TipoLogradouroDAO tipoLogradouroDAO;

	@Inject
	private LogradouroDAO logradouroDAO;

	@Override
	protected AbstractDAO<Logradouro> getDAO() {
		return logradouroDAO;
	}

	public LogradouroService() {
	}

	public LogradouroService(LocalidadeDAO localidadeDAO, BairroDAO bairroDAO, TipoLogradouroDAO tipoLogradouroDAO,
			LogradouroDAO logradouroDAO, Validator validator) {
		this.localidadeDAO = localidadeDAO;
		this.bairroDAO = bairroDAO;
		this.tipoLogradouroDAO = tipoLogradouroDAO;
		this.logradouroDAO = logradouroDAO;
		this.validator = validator;
	}

	@Override
	protected void build(Logradouro logradouro) {
		Bairro bairro = null;

		TipoLogradouro tipoLogradouro = null;

		Long localidadeId = logradouro.getLocalidadeId();

		Long bairroId = logradouro.getBairroId();

		Long tipoLogradouroId = logradouro.getTipoLogradouroId();

		Localidade localidade = localidadeDAO.get(localidadeId);

		if (bairroId != null) {
			bairro = bairroDAO.get(bairroId);
		}

		if (tipoLogradouroId != null) {
			tipoLogradouro = tipoLogradouroDAO.get(tipoLogradouroId);
		}

		logradouro.setLocalidade(localidade);

		logradouro.setBairro(bairro);

		logradouro.setTipoLogradouro(tipoLogradouro);

		String nome = logradouro.getNome();

		if (nome != null) {
			logradouro.setNomeNormalizado(StringUtils.normalizarNome(nome));
		}
	}

	public ResultList<Cep> getCeps(Long logradouroId) {
		Logradouro logradouro = getDAO().get(logradouroId);

		List<Cep> ceps = logradouro.getCeps();

		ResultList<Cep> result = new ResultList<>();

		result.setResult(ceps);
		result.setLimit(ceps.size());

		return result;
	}
}