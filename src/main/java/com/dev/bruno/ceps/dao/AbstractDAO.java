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
	}

	public MODEL1 get(Long id) {
		if (!exists(id)) {
			throw new EntityNotFoundException(ENTIDADE_NAO_ENCONTRADA);
		}

		return manager.find(type, id);
	}

	public void remove(MODEL1 entity) {
		if (entity == null) {
			throw new EntityNotFoundException(ENTIDADE_NAO_ENCONTRADA);
		}

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

		if (!orderOptions.contains(order) || !dirOptions().contains(dir)) {
			throw new InvalidValueException(String.format("Possíveis valores para order[%s] e dir[%s]",
					String.join(", ", orderOptions), String.join(", ", dirOptions())));
		}

		StringBuilder hql = new StringBuilder("select e from " + type.getSimpleName() + " e where 1=1");

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

		hql.append(" order by e." + order + " " + dir);

		TypedQuery<MODEL1> query = manager.createQuery(hql.toString(), type);

		if (queryStr != null && !queryStr.isEmpty()) {
			for (String queryOption : queryOptions) {
				query.setParameter(queryOption, "%" + queryStr + "%");
			}
		}

		return query.setFirstResult(start).setMaxResults(limit).getResultList();
	}

	public List<MODEL1> list() {
		return manager.createQuery("select e from " + type.getSimpleName() + " e order by e.id", type).getResultList();
	}

	public Boolean exists(Long id) {
		if (id == null) {
			throw new MandatoryFieldsException("id é obrigatório");
		}

		Long result = manager
				.createQuery("select count(e) from " + type.getSimpleName() + " e where e.id = :id", Long.class)
				.setParameter("id", id).getSingleResult();

		return result > 0;
	}

	public Set<String> dirOptions() {
		Set<String> dirOptions = new HashSet<>();

		dirOptions.add("asc");
		dirOptions.add("desc");

		return dirOptions;
	}

	public Class<MODEL1> getType() {
		return type;
	}
}