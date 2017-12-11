package com.dev.bruno.ceps.dao;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import com.dev.bruno.ceps.model.UF;
import com.dev.bruno.ceps.model.UFEnum;

@Stateless
public class UFDAO extends AbstractDAO<UF> {

	public UF buscarPorUF(UFEnum uf) {
		String hql = "select u from UF u where uf.nome = :uf";

		TypedQuery<UF> query = manager.createQuery(hql, UF.class);
		
		query.setParameter("uf", uf.name());

		return query.getSingleResult();
	}
}