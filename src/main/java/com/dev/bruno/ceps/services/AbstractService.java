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

public abstract class AbstractService<MODEL1 extends AbstractModel> {

	@Inject
	protected Validator validator;

	@SuppressWarnings("unchecked")
	protected Class<MODEL1> getEntityType() {
		Type t = getClass().getGenericSuperclass();
		ParameterizedType pt = (ParameterizedType) t;
		return (Class<MODEL1>) pt.getActualTypeArguments()[0];
	}

	protected abstract AbstractDAO<MODEL1> getDAO();

	public ResultList<MODEL1> list(String queryStr, Integer start, Integer limit, String order, String dir) {
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

		List<MODEL1> entities = getDAO().list(queryStr, start, limit, order, dir);

		ResultList<MODEL1> result = new ResultList<>();

		result.setResult(entities);
		result.setDir(dir);
		result.setLimit(limit);
		result.setOrder(order);
		result.setStart(start);

		return result;
	}

	public MODEL1 get(Long id) {
		return getDAO().get(id);
	}

	public MODEL1 add(MODEL1 entity) {
		validateAndBuild(null, entity);

		getDAO().add(entity);

		return entity;
	}

	protected abstract void build(MODEL1 entity);

	public MODEL1 update(Long id, MODEL1 entity) {
		validateAndBuild(id, entity);

		getDAO().update(entity);

		return entity;
	}

	public void remove(Long id) {
		getDAO().remove(id);
	}

	public void validateAndBuild(Long id, MODEL1 model) {
		if (model == null) {
			throw new MandatoryFieldsException(getEntityType().getSimpleName() + " nao encontrada na requisicao.");
		}

		if (id != null && !getDAO().exists(id)) {
			throw new EntityNotFoundException(getEntityType().getSimpleName() + "[" + id + "] não encontrado.");
		}

		build(model);

		Set<ConstraintViolation<MODEL1>> violations = validator.validate(model);

		if (!violations.isEmpty()) {
			List<String> msgs = new ArrayList<>();

			for (ConstraintViolation<MODEL1> violation : violations) {
				msgs.add(String.format("%s field %s", violation.getPropertyPath(), violation.getMessage()));
			}

			throw new ConstraintViolationException(String.join(", ", msgs));
		}

		model.setId(id);
	}
}