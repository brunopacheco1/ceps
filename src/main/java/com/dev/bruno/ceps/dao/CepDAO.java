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

	@SuppressWarnings("unchecked")
	public List<Cep> buscarCepsNaoValidados(Long codCepLocalidade) {
		StringBuilder sql = new StringBuilder(
				"select * from cep where dsc_cep in (select * from (select dsc_cep from cep where dsc_caixa_postal is null minus select case when LENGTH(TO_CHAR(nro_cep)) < 8 then '0' || TO_CHAR(nro_cep) else TO_CHAR(nro_cep) end NRO_CEPLOCALIDADE from (select nro_ceplogradouro as nro_cep from volta_tab.logradouro_cep union select nro_ceplocalidade as nro_cep from volta_tab.localidade_cep union select nro_cepespecial as nro_cep from volta_tab.especial_cep))) and tip_cep not in ('UNI', 'CPC', 'PRO')");

		if (codCepLocalidade != null) {
			sql.append(" and cod_cep_localidade = ").append(codCepLocalidade);
		}

		return manager.createNativeQuery(sql.toString(), Cep.class).setMaxResults(1000).getResultList();
	}

	public Boolean existsByCEP(String cep) {
		Long result = manager.createQuery("select count(c) from Cep c where c.cep = :cep", Long.class)
				.setParameter("cep", cep).getSingleResult();

		return result > 0;
	}

	public Cep buscarByCEP(String cep) {
		return manager.createQuery("select c from Cep c where c.cep = :cep", Cep.class).setParameter("cep", cep)
				.getSingleResult();
	}

	public List<Cep> list(TipoCepEnum tipoCep, Integer start, Integer limit, String order, String dir) {
		if (tipoCep == null || start == null || limit == null || order == null || dir == null) {
			throw new MandatoryFieldsException("tipo, start, limit, order e dir são obrigatórios");
		}

		if (!orderOptions.contains(order) || !dirOptions().contains(dir)) {
			throw new InvalidValueException(String.format("Possíveis valores para order[%s] e dir[%s]",
					String.join(", ", orderOptions), String.join(", ", dirOptions())));
		}

		StringBuilder hql = new StringBuilder("select c from Cep c where 1=1");

		hql.append(" and c.tipoCep = :tipoCep order by c." + order + " " + dir);

		TypedQuery<Cep> query = manager.createQuery(hql.toString(), Cep.class);

		query.setParameter("tipoCep", tipoCep);

		return query.setFirstResult(start).setMaxResults(limit).getResultList();
	}
}