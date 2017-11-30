package com.dev.bruno.ceps.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.dev.bruno.ceps.dao.AbstractDAO;
import com.dev.bruno.ceps.dao.CepBairroDAO;
import com.dev.bruno.ceps.dao.CepLocalidadeDAO;
import com.dev.bruno.ceps.dao.CepLogradouroDAO;
import com.dev.bruno.ceps.dao.CepTipoLogradouroDAO;
import com.dev.bruno.ceps.model.Cep;
import com.dev.bruno.ceps.model.CepBairro;
import com.dev.bruno.ceps.model.CepLocalidade;
import com.dev.bruno.ceps.model.CepLogradouro;
import com.dev.bruno.ceps.model.CepTipoLogradouro;
import com.dev.bruno.ceps.responses.ResultList;
import com.dev.bruno.ceps.utils.StringUtils;

@Stateless
public class CepLogradouroService extends AbstractService<CepLogradouro> {

	@Inject
	private CepLocalidadeDAO cepLocalidadeDAO;

	@Inject
	private CepBairroDAO cepBairroDAO;

	@Inject
	private CepTipoLogradouroDAO cepTipoLogradouroDAO;

	@Inject
	private CepLogradouroDAO cepLogradouroDAO;

	@Override
	protected AbstractDAO<CepLogradouro> getDAO() {
		return cepLogradouroDAO;
	}

	@Override
	protected void build(CepLogradouro entity) throws Exception {
		CepBairro bairro = null;

		CepTipoLogradouro tipoLogradouro = null;

		Long cepLocalidadeId = entity.getCepLocalidadeId();

		Long cepBairroId = entity.getCepBairroId();

		Long cepTipoLogradouroId = entity.getCepTipoLogradouroId();

		CepLocalidade localidade = cepLocalidadeDAO.get(cepLocalidadeId);

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

	public ResultList<Cep> getCeps(Long cepLogradouroId) throws Exception {
		CepLogradouro logradouro = getDAO().get(cepLogradouroId);

		List<Cep> entities = logradouro.getCeps();

		ResultList<Cep> result = new ResultList<>();

		result.setResult(entities);
		result.setLimit(entities.size());

		return result;
	}
}