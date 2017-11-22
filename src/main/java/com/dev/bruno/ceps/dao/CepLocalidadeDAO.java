package com.dev.bruno.ceps.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import com.dev.bruno.ceps.model.CepLocalidade;

@Stateless
public class CepLocalidadeDAO extends AbstractDAO<CepLocalidade> {
	
	@SuppressWarnings("unchecked")
	public List<CepLocalidade> buscarLocalidadesNaoValidadas() {
		return manager.createNativeQuery("select distinct l.* from cep_localidade l where l.cod_cep_localidade in (4306,10801,351,377,468,270,728,1012,4839,5346,5431,5503,5917,6006,9055,7040,8064,8780,6816,1450,1538,1555,1799,1916,1946,9450,10782,10581,2850,2912,3033,3385,3529,1387,1724,1736,3386,17,4367,5290,8375,2966) order by l.cod_cep_localidade asc", CepLocalidade.class).getResultList();
	}
	
	public List<Long> buscarLocalidadeSemCepUnico(String uf) {
		return manager.createQuery("select distinct l.id from CepLocalidade l where l.cepUF.uf = :uf and l.id in (select c.cepLocalidade.id from Cep c where c.tipoCep = 'UNI')", Long.class).setParameter("uf", uf).setMaxResults(1).getResultList();
	}
	
	public CepLocalidade buscarLocalidadePorNomeUF(String uf, String nomeNormalizado) {
		return manager.createQuery("select distinct l from CepLocalidade l where l.cepUF.uf = :uf and l.nomeNormalizado = :nomeNormalizado", CepLocalidade.class).setParameter("uf", uf).setParameter("nomeNormalizado", nomeNormalizado).getSingleResult();
	}

	public Boolean existsByNomeDistrito(String uf, String nome, String distrito) {
		String hql = "select count(l) from CepLocalidade l where l.cepUF.uf = :uf and l.nome = :nome";
		
		if(distrito != null) {
			hql += " and l.distrito = :distrito";
		} else {
			hql += " and l.distrito is null";
		}
		
		TypedQuery<Long> query = manager.createQuery(hql, Long.class).setParameter("nome", nome).setParameter("uf", uf);
		
		if(distrito != null) {
			query.setParameter("distrito", distrito);
		}
		
		Long result = query.getSingleResult();
		
		return result > 0;
	}

	public List<CepLocalidade> listarLocalidadesSemFaixaCep(Integer limit) {
		return manager.createQuery("select l from CepLocalidade l where l.distrito is null and l.faixaCEP is null order by l.nomeNormalizado", CepLocalidade.class).setMaxResults(limit).getResultList();
	}
	
	public List<CepLocalidade> listarLocalidadesPorUF(String uf) {
		return manager.createQuery("select l from CepLocalidade l where l.cepUF.uf = :uf order by l.nomeNormalizado", CepLocalidade.class).setParameter("uf", uf).getResultList();
	}
}