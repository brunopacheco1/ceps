package com.dev.bruno.ceps.dao;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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

public abstract class AbstractDAO<ENTITY extends AbstractModel> {

	private static final String ENTIDADE_NAO_ENCONTRADA = "Entidade não encontrada"; 
	
	@Inject
	protected EntityManager manager;

	protected Class<ENTITY> type;

	protected Set<String> orderOptions;

	protected Set<String> queryOptions;

	@SuppressWarnings("unchecked")
	@PostConstruct
	private void init() {
		Type t = getClass().getGenericSuperclass();
		ParameterizedType pt = (ParameterizedType) t;
		type = (Class<ENTITY>) pt.getActualTypeArguments()[0];

		List<Field> fields = new ArrayList<Field>();

		fields.addAll(Arrays.asList(type.getDeclaredFields()));

		orderOptions = new HashSet<>();
		queryOptions = new HashSet<>();

		for (Field field : fields) {
			if (field.getType().equals(Long.class) || field.getType().equals(Long.class)
					|| field.getType().equals(Date.class)) {
				orderOptions.add(field.getName());
			} else if (field.getType().equals(String.class)) {
				orderOptions.add(field.getName());
				queryOptions.add(field.getName());
			}
		}
	}

	public ENTITY get(Long id) {
		if (!exists(id)) {
			throw new EntityNotFoundException(ENTIDADE_NAO_ENCONTRADA);
		}

		ENTITY result = manager.find(type, id);

		return result;
	}

	public void remove(ENTITY entity) {
		if (entity == null) {
			throw new EntityNotFoundException(ENTIDADE_NAO_ENCONTRADA);
		}

		manager.remove(entity);
	}

	public void add(ENTITY entity) {
		if (entity == null) {
			throw new EntityNotFoundException(ENTIDADE_NAO_ENCONTRADA);
		}

		manager.persist(entity);
	}

	public void update(ENTITY entity) {
		if (entity == null) {
			throw new EntityNotFoundException(ENTIDADE_NAO_ENCONTRADA);
		}

		manager.merge(entity);
	}

	public List<ENTITY> list(String queryStr, Integer start, Integer limit, String order, String dir) {
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

		TypedQuery<ENTITY> query = manager.createQuery(hql.toString(), type);

		if (queryStr != null && !queryStr.isEmpty()) {
			for (String queryOption : queryOptions) {
				query.setParameter(queryOption, "%" + queryStr + "%");
			}
		}

		return query.setFirstResult(start).setMaxResults(limit).getResultList();
	}

	public List<ENTITY> list() {
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

	public Class<ENTITY> getType() {
		return type;
	}
}