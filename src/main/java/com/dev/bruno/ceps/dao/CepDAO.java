package com.dev.bruno.ceps.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import com.dev.bruno.ceps.exceptions.InvalidValueException;
import com.dev.bruno.ceps.exceptions.MandatoryFieldsException;
import com.dev.bruno.ceps.model.Cep;
import com.dev.bruno.ceps.model.TipoCepEnum;

@Stateless
public class CepDAO extends AbstractDAO<Cep> {

	public Boolean existeCep(String numeroCep) {
		String hql = "select count(c) from Cep c where c.numeroCep = :numeroCep";

		TypedQuery<Long> query = manager.createQuery(hql, Long.class);

		query.setParameter("numeroCep", numeroCep);

		Long result = query.getSingleResult();

		return result > 0;
	}

	public List<Cep> listarPorTipo(TipoCepEnum tipoCep, Integer start, Integer limit, String order, String dir) {
		if (tipoCep == null || start == null || limit == null || order == null || dir == null) {
			throw new MandatoryFieldsException("tipo, start, limit, order e dir são obrigatórios");
		}

		if (!orderOptions.contains(order)) {
			String msg = String.format("Possíveis valores para order[%s]", String.join(", ", orderOptions));

			throw new InvalidValueException(msg);
		}

		if (!dirOptions.contains(dir)) {
			String msq = String.format("Possíveis valores para dir[%s]", String.join(", ", dirOptions));

			throw new InvalidValueException(msq);
		}

		StringBuilder hql = new StringBuilder("select c from Cep c where ");

		hql.append("c.tipoCep = :tipoCep order by c.").append(order).append(" ").append(dir);

		TypedQuery<Cep> query = manager.createQuery(hql.toString(), Cep.class);

		query.setParameter("tipoCep", tipoCep);

		return query.setFirstResult(start).setMaxResults(limit).getResultList();
	}
}