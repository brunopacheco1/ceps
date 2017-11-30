package com.dev.bruno.ceps.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.dev.bruno.ceps.dao.AbstractDAO;
import com.dev.bruno.ceps.dao.CepLocalidadeDAO;
import com.dev.bruno.ceps.dao.CepUFDAO;
import com.dev.bruno.ceps.model.Cep;
import com.dev.bruno.ceps.model.CepBairro;
import com.dev.bruno.ceps.model.CepLocalidade;
import com.dev.bruno.ceps.model.CepLogradouro;
import com.dev.bruno.ceps.model.CepUF;
import com.dev.bruno.ceps.responses.ResultList;
import com.dev.bruno.ceps.utils.StringUtils;

@Stateless
public class CepLocalidadeService extends AbstractService<CepLocalidade> {

	@Inject
	private CepLocalidadeDAO cepLocalidadeDAO;

	@Inject
	private CepUFDAO cepUFDAO;

	@Override
	protected AbstractDAO<CepLocalidade> getDAO() {
		return cepLocalidadeDAO;
	}

	@Override
	protected void build(CepLocalidade entity) throws Exception {
		Long ufId = entity.getCepUFId();

		CepUF uf = cepUFDAO.get(ufId);

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

	public ResultList<CepBairro> getBairros(Long cepLocalidadeId) throws Exception {
		CepLocalidade localidade = getDAO().get(cepLocalidadeId);

		List<CepBairro> entities = localidade.getBairros();

		ResultList<CepBairro> result = new ResultList<>();

		result.setResult(entities);
		result.setLimit(entities.size());

		return result;
	}

	public ResultList<CepLogradouro> getLogradouros(Long cepLocalidadeId) throws Exception {
		CepLocalidade localidade = getDAO().get(cepLocalidadeId);

		List<CepLogradouro> entities = localidade.getLogradouros();

		ResultList<CepLogradouro> result = new ResultList<>();

		result.setResult(entities);
		result.setLimit(entities.size());

		return result;
	}

	public ResultList<Cep> getCeps(Long cepLocalidadeId) throws Exception {
		CepLocalidade localidade = getDAO().get(cepLocalidadeId);

		List<Cep> entities = localidade.getCeps();

		ResultList<Cep> result = new ResultList<>();

		result.setResult(entities);
		result.setLimit(entities.size());

		return result;
	}
}