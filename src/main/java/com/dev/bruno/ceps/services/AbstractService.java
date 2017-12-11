package com.dev.bruno.ceps.services;

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

public abstract class AbstractService<MODEL> {

	@Inject
	protected Validator validator;

	@SuppressWarnings("unchecked")
	protected Class<MODEL> getEntityType() {
		Type t = getClass().getGenericSuperclass();
		ParameterizedType pt = (ParameterizedType) t;
		return (Class<MODEL>) pt.getActualTypeArguments()[0];
	}

	protected abstract AbstractDAO<MODEL> getDAO();

	public ResultList<MODEL> list(String queryStr, Integer start, Integer limit, String order, String dir) {
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

		List<MODEL> entities = getDAO().list(queryStr, start, limit, order, dir);

		ResultList<MODEL> result = new ResultList<>();

		result.setResult(entities);
		result.setDir(dir);
		result.setLimit(limit);
		result.setOrder(order);
		result.setStart(start);

		return result;
	}

	public ResultList<MODEL> list() {
		List<MODEL> entities = getDAO().list();

		ResultList<MODEL> result = new ResultList<>();

		result.setResult(entities);
		result.setLimit(entities.size());

		return result;
	}

	public MODEL get(Long id) {
		return getDAO().get(id);
	}

	public MODEL add(MODEL entity) {
		validateAndBuild(null, entity);

		getDAO().add(entity);

		return entity;
	}

	protected abstract void build(MODEL entity);

	public MODEL update(Long id, MODEL entity) {
		validateAndBuild(id, entity);

		getDAO().update(entity);

		return entity;
	}

	public void remove(Long id) {
		MODEL entity = getDAO().get(id);
		getDAO().remove(entity);
	}

	public void validateAndBuild(Long id, MODEL model) {
		if (model == null || !(model instanceof AbstractModel)) {
			throw new MandatoryFieldsException(getEntityType().getSimpleName() + " nao encontrada na requisicao.");
		}

		if (id != null && !getDAO().exists(id)) {
			throw new EntityNotFoundException(getEntityType().getSimpleName() + "[" + id + "] não encontrado.");
		}

		build(model);

		Set<ConstraintViolation<MODEL>> violations = validator.validate(model);

		if (!violations.isEmpty()) {
			List<String> msgs = new ArrayList<>();

			for (ConstraintViolation<MODEL> violation : violations) {
				msgs.add(String.format("%s field %s", violation.getPropertyPath(), violation.getMessage()));
			}

			ConstraintViolationException exception = new ConstraintViolationException(String.join(", ", msgs));

			throw exception;
		}

		AbstractModel abstractModel = (AbstractModel) model;

		abstractModel.setId(id);
	}
}