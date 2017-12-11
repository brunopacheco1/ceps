package com.dev.bruno.ceps.dao;

import javax.ejb.Stateless;

import com.dev.bruno.ceps.model.UF;

@Stateless
public class UFDAO extends AbstractDAO<UF> {

	public UF buscarPorUF(String uf) {
		return manager.createQuery("select uf from CepUF uf where uf.uf = :uf", UF.class).setParameter("uf", uf)
				.getSingleResult();
	}
}