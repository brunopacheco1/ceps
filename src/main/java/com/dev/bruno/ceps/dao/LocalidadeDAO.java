package com.dev.bruno.ceps.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import com.dev.bruno.ceps.model.Localidade;
import com.dev.bruno.ceps.model.UFEnum;

@Stateless
public class LocalidadeDAO extends AbstractDAO<Localidade> {

	public Boolean existeLocalidade(UFEnum uf, String nome, String distrito) {
		StringBuilder hql = new StringBuilder();

		hql.append("select count(l) from Localidade l where l.uf.uf = :uf and l.nome = :nome");

		if (distrito != null) {
			hql.append(" and l.distrito = :distrito");
		} else {
			hql.append(" and l.distrito is null");
		}

		TypedQuery<Long> query = manager.createQuery(hql.toString(), Long.class);

		query.setParameter("nome", nome);

		query.setParameter("uf", uf.name());

		if (distrito != null) {
			query.setParameter("distrito", distrito);
		}

		Long result = query.getSingleResult();

		return result > 0;
	}

	public List<Localidade> listarLocalidadesSemFaixaCep(Integer limit) {
		String hql = "select l from Localidade l where l.distrito is null and l.faixaCEP is null order by l.nomeNormalizado";

		return manager.createQuery(hql, Localidade.class).setMaxResults(limit).getResultList();
	}

	public List<Long> listarLocalidadesIdsPorUF(UFEnum uf) {
		String hql = "select l.id from Localidade l where l.uf.nome = :uf order by l.nomeNormalizado";

		return manager.createQuery(hql, Long.class).setParameter("uf", uf.name()).getResultList();
	}
}