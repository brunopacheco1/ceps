package com.dev.bruno.ceps.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import com.dev.bruno.ceps.model.Bairro;
import com.dev.bruno.ceps.model.Localidade;
import com.dev.bruno.ceps.model.Logradouro;

@Stateless
public class LogradouroDAO extends AbstractDAO<Logradouro> {

	public Boolean existByLocalidadeBairroEnderecoComplemento(Localidade cepLocalidade, Bairro cepBairro,
			String nome, String complemento) {
		String hql = "select count(l) from CepLogradouro l where l.cepLocalidade.id = :cepLocalidade and l.nome = :nome";

		if (cepBairro != null) {
			hql += " and l.cepBairro.id = :cepBairro";
		} else {
			hql += " and l.cepBairro is null";
		}

		if (complemento != null) {
			hql += " and l.complemento = :complemento";
		} else {
			hql += " and l.complemento is null";
		}

		TypedQuery<Long> query = manager.createQuery(hql, Long.class)
				.setParameter("cepLocalidade", cepLocalidade.getId()).setParameter("nome", nome);

		if (cepBairro != null) {
			query.setParameter("cepBairro", cepBairro.getId());
		}

		if (complemento != null) {
			query.setParameter("complemento", complemento);
		}

		Long result = query.getSingleResult();

		return result > 0;
	}

	public Logradouro buscarByLocalidadeBairroEnderecoComplemento(Localidade cepLocalidade, Bairro cepBairro,
			String nome, String complemento) {
		String hql = "select l from CepLogradouro l where l.cepLocalidade.id = :cepLocalidade and l.nome = :nome";

		if (cepBairro != null) {
			hql += " and l.cepBairro.id = :cepBairro";
		} else {
			hql += " and l.cepBairro is null";
		}

		if (complemento != null) {
			hql += " and l.complemento = :complemento";
		} else {
			hql += " and l.complemento is null";
		}

		TypedQuery<Logradouro> query = manager.createQuery(hql, Logradouro.class)
				.setParameter("cepLocalidade", cepLocalidade.getId()).setParameter("nome", nome);

		if (cepBairro != null) {
			query.setParameter("cepBairro", cepBairro.getId());
		}

		if (complemento != null) {
			query.setParameter("complemento", complemento);
		}

		return query.getSingleResult();
	}

	public List<Logradouro> listarLogradourosSemTipo() {
		String hql = "select l from CepLogradouro l where l.cepTipoLogradouro is null";

		return manager.createQuery(hql, Logradouro.class).setMaxResults(1000).getResultList();
	}
}