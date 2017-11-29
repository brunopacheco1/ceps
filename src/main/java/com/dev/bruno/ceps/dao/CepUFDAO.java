package com.dev.bruno.ceps.dao;

import javax.ejb.Stateless;

import com.dev.bruno.ceps.model.CepUF;

@Stateless
public class CepUFDAO extends AbstractDAO<CepUF> {

	public CepUF buscarPorUF(String uf) {
		return manager.createQuery("select uf from CepUF uf where uf.uf = :uf", CepUF.class).setParameter("uf", uf)
				.getSingleResult();
	}
}