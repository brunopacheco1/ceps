package com.dev.bruno.ceps.dao;

import java.time.LocalDateTime;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import com.dev.bruno.ceps.model.Bairro;
import com.dev.bruno.ceps.model.Localidade;

@Stateless
public class BairroDAO extends AbstractDAO<Bairro> {

	public Boolean existsByNomeLocalidade(Localidade cepLocalidade, String nome) {
		Long result = manager
				.createQuery(
						"select count(b) from CepBairro b where b.cepLocalidade = :cepLocalidade and b.nome = :nome",
						Long.class)
				.setParameter("cepLocalidade", cepLocalidade).setParameter("nome", nome).getSingleResult();

		return result > 0;
	}

	public Bairro buscarByNomeLocalidade(Localidade localidade, String bairro) {
		return manager
				.createQuery("select b from CepBairro b where b.cepLocalidade = :cepLocalidade and b.nome = :nome",
						Bairro.class)
				.setParameter("cepLocalidade", localidade).setParameter("nome", bairro).getSingleResult();
	}

	public List<Bairro> listarBairrosNaoProcessados(Integer limit) {
		LocalDateTime date = LocalDateTime.now().minusDays(7L);

		TypedQuery<Bairro> query = manager.createQuery(
				"select b from CepBairro b where b.ultimoProcessamento is null or b.ultimoProcessamento < :date order by b.ultimoProcessamento",
				Bairro.class);

		query.setParameter("date", date);

		return query.setMaxResults(limit).getResultList();
	}
}