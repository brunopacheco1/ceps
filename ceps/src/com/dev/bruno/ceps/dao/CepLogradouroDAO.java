package com.dev.bruno.ceps.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import com.dev.bruno.ceps.model.CepBairro;
import com.dev.bruno.ceps.model.CepLocalidade;
import com.dev.bruno.ceps.model.CepLogradouro;

@Stateless
public class CepLogradouroDAO extends AbstractDAO<CepLogradouro> {

	public Boolean existByLocalidadeBairroEnderecoComplemento(CepLocalidade cepLocalidade, CepBairro cepBairro, String nome, String complemento) {
		String hql = "select count(l) from CepLogradouro l where l.cepLocalidade.id = :cepLocalidade and l.nome = :nome";
		
		if(cepBairro != null) {
			hql += " and l.cepBairro.id = :cepBairro";
		} else {
			hql += " and l.cepBairro is null";
		}
		
		if(complemento != null) {
			hql += " and l.complemento = :complemento";
		} else {
			hql += " and l.complemento is null";
		}
		
		TypedQuery<Long> query = manager.createQuery(hql, Long.class)
				.setParameter("cepLocalidade", cepLocalidade.getId())
				.setParameter("nome", nome);
		
		if(cepBairro != null) {
			query.setParameter("cepBairro", cepBairro.getId());
		}
		
		if(complemento != null) {
			query.setParameter("complemento", complemento);
		}
		
		Long result = query.getSingleResult();
		
		return result > 0;
	}
	
	public CepLogradouro buscarByLocalidadeBairroEnderecoComplemento(CepLocalidade cepLocalidade, CepBairro cepBairro, String nome, String complemento) {
		String hql = "select l from CepLogradouro l where l.cepLocalidade.id = :cepLocalidade and l.nome = :nome";
		
		if(cepBairro != null) {
			hql += " and l.cepBairro.id = :cepBairro";
		} else {
			hql += " and l.cepBairro is null";
		}
		
		if(complemento != null) {
			hql += " and l.complemento = :complemento";
		} else {
			hql += " and l.complemento is null";
		}
		
		TypedQuery<CepLogradouro> query = manager.createQuery(hql, CepLogradouro.class)
				.setParameter("cepLocalidade", cepLocalidade.getId())
				.setParameter("nome", nome);
		
		if(cepBairro != null) {
			query.setParameter("cepBairro", cepBairro.getId());
		}
		
		if(complemento != null) {
			query.setParameter("complemento", complemento);
		}
		
		return query.getSingleResult();
	}
	
	public List<CepLogradouro> listarLogradourosSemTipo() {
		String hql = "select l from CepLogradouro l where l.cepTipoLogradouro is null";
		
		return manager.createQuery(hql, CepLogradouro.class).setMaxResults(1000).getResultList();
	}
}