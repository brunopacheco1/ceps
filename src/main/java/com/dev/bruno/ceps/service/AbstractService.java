package com.dev.bruno.ceps.service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import com.dev.bruno.ceps.dao.AbstractDAO;
import com.dev.bruno.ceps.exception.EntityNotFoundException;
import com.dev.bruno.ceps.model.AbstractModel;
import com.dev.bruno.ceps.model.ResultList;

public abstract class AbstractService<ENTITY extends AbstractModel> {

	@Inject
	protected Logger logger;

	@Inject
	protected Validator validator;

	protected Class<ENTITY> entityType;

	@SuppressWarnings("unchecked")
	@PostConstruct
	private void init() {
		Type t = getClass().getGenericSuperclass();
		ParameterizedType pt = (ParameterizedType) t;
		entityType = (Class<ENTITY>) pt.getActualTypeArguments()[0];
	}

	protected abstract AbstractDAO<ENTITY> getDAO();

	public ResultList<ENTITY> list(String queryStr, Integer start, Integer limit, String order, String dir)
			throws Exception {
		if (start == null) {
			start = 0;
		}

		if (limit == null) {
			limit = 100;
		}

		if (order == null) {
			order = "id";
		}

		if (dir == null) {
			dir = "asc";
		}

		List<ENTITY> entities = getDAO().list(queryStr, start, limit, order, dir);

		ResultList<ENTITY> result = new ResultList<>();

		result.setResultSize((long) entities.size());
		result.setTotalSize(count(queryStr));
		result.setResult(entities);
		result.setDir(dir);
		result.setLimit(limit);
		result.setOrder(order);
		result.setStart(start);

		return result;
	}

	public ResultList<ENTITY> list() throws Exception {
		List<ENTITY> entities = getDAO().list();

		ResultList<ENTITY> result = new ResultList<>();

		result.setResultSize((long) entities.size());
		result.setTotalSize((long) entities.size());
		result.setResult(entities);
		result.setLimit(entities.size());
		result.setOrder("id");
		result.setStart(0);

		return result;
	}

	public ENTITY get(Long id) throws Exception {
		return getDAO().get(id);
	}

	public ENTITY add(ENTITY entity) throws Exception {
		validate(null, entity);

		getDAO().add(entity);

		return entity;
	}

	public ENTITY update(Long id, ENTITY entity) throws Exception {
		validate(id, entity);

		getDAO().update(entity);

		return entity;
	}

	public void remove(Long id) throws Exception {
		ENTITY entity = getDAO().get(id);
		getDAO().remove(entity);
	}

	public Long count(String queryStr) {
		return getDAO().count(queryStr);
	}

	public void validate(Long id, ENTITY entity) throws Exception {
		if (id != null && !getDAO().exists(id)) {
			throw new EntityNotFoundException(entityType.getSimpleName() + "[" + id + "] não encontrado.");
		}

		Set<ConstraintViolation<ENTITY>> violations = validator.validate(entity);

		if (!violations.isEmpty()) {
			throw new ConstraintViolationException(violations);
		}

		entity.setId(id);
	}
}