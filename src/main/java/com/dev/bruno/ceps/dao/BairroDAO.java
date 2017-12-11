package com.dev.bruno.ceps.dao;

import java.time.LocalDateTime;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import com.dev.bruno.ceps.model.Bairro;
import com.dev.bruno.ceps.model.Localidade;

@Stateless
public class BairroDAO extends AbstractDAO<Bairro> {

	public Boolean existeBairro(Localidade localidade, String nome) {
		TypedQuery<Long> query = buildQuery("select count(b) ", localidade, nome, Long.class);

		Long result = query.getSingleResult();

		return result > 0;
	}

	public Bairro buscarBairro(Localidade localidade, String nome) {
		TypedQuery<Bairro> query = buildQuery("select b ", localidade, nome, Bairro.class);

		return query.getSingleResult();
	}

	private <T> TypedQuery<T> buildQuery(String select, Localidade localidade, String nome, Class<T> clazz) {
		StringBuilder hql = new StringBuilder(select);

		hql.append("from Bairro b where b.localidade = :localidade and b.nome = :nome");

		TypedQuery<T> query = manager.createQuery(hql.toString(), clazz);

		query.setParameter("localidade", localidade);
		
		query.setParameter("nome", nome);

		return query;
	}

	public List<Long> listarBairrosNaoProcessados(Integer limit) {
		String hql = "select b.id from Bairro b where b.dataUltimoProcessamento is null or b.dataUltimoProcessamento < :date order by b.dataUltimoProcessamento";
		
		LocalDateTime date = LocalDateTime.now().minusDays(7L);

		TypedQuery<Long> query = manager.createQuery(hql, Long.class);

		query.setParameter("date", date);

		return query.setMaxResults(limit).getResultList();
	}
}