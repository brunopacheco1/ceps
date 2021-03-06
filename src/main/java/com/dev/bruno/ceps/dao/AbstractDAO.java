package com.dev.bruno.ceps.dao;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import com.dev.bruno.ceps.exceptions.EntityNotFoundException;
import com.dev.bruno.ceps.exceptions.InvalidValueException;
import com.dev.bruno.ceps.exceptions.MandatoryFieldsException;
import com.dev.bruno.ceps.model.AbstractModel;

public abstract class AbstractDAO<MODEL1 extends AbstractModel> {

	private static final String ENTIDADE_NAO_ENCONTRADA = "Entidade não encontrada";

	@Inject
	protected EntityManager manager;

	protected Class<MODEL1> type;

	protected Set<String> orderOptions;

	protected Set<String> queryOptions;

	protected Set<String> dirOptions;

	@SuppressWarnings("unchecked")
	@PostConstruct
	private void init() {
		Type t = getClass().getGenericSuperclass();
		ParameterizedType pt = (ParameterizedType) t;
		type = (Class<MODEL1>) pt.getActualTypeArguments()[0];

		List<Field> fields = new ArrayList<>();

		fields.addAll(Arrays.asList(type.getDeclaredFields()));

		orderOptions = new HashSet<>();
		queryOptions = new HashSet<>();

		for (Field field : fields) {
			orderOptions.add(field.getName());

			if (field.getType().equals(String.class)) {
				queryOptions.add(field.getName());
			}
		}

		dirOptions = new HashSet<>();
		dirOptions.add("asc");
		dirOptions.add("desc");
	}

	public MODEL1 get(Long id) {
		try {
			return manager.find(type, id);
		} catch (NoResultException e) {
			throw new EntityNotFoundException(ENTIDADE_NAO_ENCONTRADA);
		}
	}

	public void remove(Long id) {
		MODEL1 entity = get(id);

		manager.remove(entity);
	}

	public void add(MODEL1 entity) {
		if (entity == null) {
			throw new EntityNotFoundException(ENTIDADE_NAO_ENCONTRADA);
		}

		manager.persist(entity);
	}

	public void update(MODEL1 entity) {
		if (entity == null) {
			throw new EntityNotFoundException(ENTIDADE_NAO_ENCONTRADA);
		}

		manager.merge(entity);
	}

	public List<MODEL1> list(String queryStr, Integer start, Integer limit, String order, String dir) {
		if (start == null || limit == null || order == null || dir == null) {
			throw new MandatoryFieldsException("start, limit, order e dir são obrigatórios");
		}

		if (!orderOptions.contains(order)) {
			String msg = String.format("Possíveis valores para order[%s]", String.join(", ", orderOptions));

			throw new InvalidValueException(msg);
		}

		if (!dirOptions.contains(dir)) {
			String msq = String.format("Possíveis valores para dir[%s]", String.join(", ", dirOptions));

			throw new InvalidValueException(msq);
		}

		StringBuilder hql = new StringBuilder("select e from ");

		hql.append(type.getSimpleName());

		hql.append(" e where 1=1");

		if (queryStr != null && !queryStr.isEmpty()) {
			hql.append(" and (");

			boolean first = true;

			for (String queryOption : queryOptions) {
				if (!first) {
					hql.append(" or ");
				}

				hql.append("upper(e.").append(queryOption).append(") like upper(:").append(queryOption).append(")");

				first = false;
			}

			hql.append(")");
		}

		hql.append(" order by e.").append(order).append(" ").append(dir);

		TypedQuery<MODEL1> query = manager.createQuery(hql.toString(), type);

		if (queryStr != null && !queryStr.isEmpty()) {
			for (String queryOption : queryOptions) {
				query.setParameter(queryOption, "%" + queryStr + "%");
			}
		}

		return query.setFirstResult(start).setMaxResults(limit).getResultList();
	}

	public Boolean exists(Long id) {
		if (id == null) {
			throw new MandatoryFieldsException("id é obrigatório");
		}

		StringBuilder hql = new StringBuilder();

		hql.append("select count(e) from ");

		hql.append(type.getSimpleName());

		hql.append(" e where e.id = :id");

		TypedQuery<Long> query = manager.createQuery(hql.toString(), Long.class);

		query.setParameter("id", id);

		Long result = query.getSingleResult();

		return result > 0;
	}

	public Class<MODEL1> getType() {
		return type;
	}
}