package com.dev.bruno.ceps.dao;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import com.dev.bruno.ceps.model.Bairro;
import com.dev.bruno.ceps.model.Localidade;
import com.dev.bruno.ceps.model.Logradouro;

@Stateless
public class LogradouroDAO extends AbstractDAO<Logradouro> {

	public Boolean existeLogradouro(Localidade localidade, Bairro bairro, String nome, String complemento) {
		TypedQuery<Long> query = buildQuery("select count(l) ", localidade, bairro, nome, complemento, Long.class);

		Long result = query.getSingleResult();

		return result > 0;
	}

	public Logradouro buscarLogradouro(Localidade localidade, Bairro bairro, String nome, String complemento) {
		TypedQuery<Logradouro> query = buildQuery("select l ", localidade, bairro, nome, complemento, Logradouro.class);

		return query.getSingleResult();
	}

	private <T> TypedQuery<T> buildQuery(String select, Localidade localidade, Bairro bairro, String nome,
			String complemento, Class<T> clazz) {
		StringBuilder hql = new StringBuilder();

		hql.append(select);

		hql.append("from Logradouro l where l.localidade = :localidade and l.nome = :nome");

		if (bairro != null) {
			hql.append(" and l.bairro = :bairro");
		} else {
			hql.append(" and l.bairro is null");
		}

		if (complemento != null) {
			hql.append(" and l.complemento = :complemento");
		} else {
			hql.append(" and l.complemento is null");
		}

		TypedQuery<T> query = manager.createQuery(hql.toString(), clazz);

		query.setParameter("localidade", localidade);
		query.setParameter("nome", nome);

		if (bairro != null) {
			query.setParameter("bairro", bairro);
		}

		if (complemento != null) {
			query.setParameter("complemento", complemento);
		}

		return query;
	}
}