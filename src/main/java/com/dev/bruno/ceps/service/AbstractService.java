package com.dev.bruno.ceps.service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import com.dev.bruno.ceps.dao.AbstractDAO;
import com.dev.bruno.ceps.exceptions.ConstraintViolationException;
import com.dev.bruno.ceps.exceptions.EntityNotFoundException;
import com.dev.bruno.ceps.exceptions.MandatoryFieldsException;
import com.dev.bruno.ceps.model.AbstractModel;
import com.dev.bruno.ceps.responses.ResultList;

public abstract class AbstractService<ENTITY extends AbstractModel> {

	@Inject
	protected Validator validator;

	@SuppressWarnings("unchecked")
	protected Class<ENTITY> getEntityType() {
		Type t = getClass().getGenericSuperclass();
		ParameterizedType pt = (ParameterizedType) t;
		return (Class<ENTITY>) pt.getActualTypeArguments()[0];
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

		result.setResult(entities);
		result.setLimit(entities.size());

		return result;
	}

	public ENTITY get(Long id) throws Exception {
		return getDAO().get(id);
	}

	public ENTITY add(ENTITY entity) {
		validate(null, entity);

		getDAO().add(entity);

		return entity;
	}

	protected abstract void build(ENTITY entity);

	public ENTITY update(Long id, ENTITY entity) {
		validate(id, entity);

		getDAO().update(entity);

		return entity;
	}

	public void remove(Long id) {
		ENTITY entity = getDAO().get(id);
		getDAO().remove(entity);
	}

	public void validate(Long id, ENTITY entity) {
		if (entity == null) {
			throw new MandatoryFieldsException(getEntityType().getSimpleName() + " nao encontrada na requisicao.");
		}

		if (id != null && !getDAO().exists(id)) {
			throw new EntityNotFoundException(getEntityType().getSimpleName() + "[" + id + "] não encontrado.");
		}

		build(entity);

		Set<ConstraintViolation<ENTITY>> violations = validator.validate(entity);

		if (!violations.isEmpty()) {
			List<String> msgs = new ArrayList<>();

			for (ConstraintViolation<ENTITY> violation : violations) {
				msgs.add(String.format("%s field %s", violation.getPropertyPath(), violation.getMessage()));
			}

			ConstraintViolationException exception = new ConstraintViolationException(String.join(", ", msgs));

			throw exception;
		}

		entity.setId(id);
	}
}