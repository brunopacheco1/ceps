package com.dev.bruno.ceps.dao;

import java.time.LocalDateTime;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import com.dev.bruno.ceps.model.CepBairro;
import com.dev.bruno.ceps.model.CepLocalidade;

@Stateless
public class CepBairroDAO extends AbstractDAO<CepBairro> {

	public Boolean existsByNomeLocalidade(CepLocalidade cepLocalidade, String nome) {
		Long result = manager
				.createQuery(
						"select count(b) from CepBairro b where b.cepLocalidade = :cepLocalidade and b.nome = :nome",
						Long.class)
				.setParameter("cepLocalidade", cepLocalidade).setParameter("nome", nome).getSingleResult();

		return result > 0;
	}

	public CepBairro buscarByNomeLocalidade(CepLocalidade localidade, String bairro) {
		return manager
				.createQuery("select b from CepBairro b where b.cepLocalidade = :cepLocalidade and b.nome = :nome",
						CepBairro.class)
				.setParameter("cepLocalidade", localidade).setParameter("nome", bairro).getSingleResult();
	}

	public List<CepBairro> listarBairrosNaoProcessados(Integer limit) {
		LocalDateTime date = LocalDateTime.now().minusDays(7L);

		TypedQuery<CepBairro> query = manager.createQuery(
				"select b from CepBairro b where b.ultimoProcessamento is null or b.ultimoProcessamento < :date order by b.ultimoProcessamento",
				CepBairro.class);

		query.setParameter("date", date);

		return query.setMaxResults(limit).getResultList();
	}
}