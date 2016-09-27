package com.dev.bruno.ceps.service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.interceptor.Interceptors;

import org.apache.commons.beanutils.PropertyUtils;

import com.dev.bruno.ceps.dao.AbstractDAO;
import com.dev.bruno.ceps.dto.AbstractDTO;
import com.dev.bruno.ceps.dto.ResultDTO;
import com.dev.bruno.ceps.exception.EntityNotFoundException;
import com.dev.bruno.ceps.interceptor.RequestTimeInterceptor;
import com.dev.bruno.ceps.model.AbstractModel;

@Interceptors(RequestTimeInterceptor.class)
public abstract class AbstractService<ENTITY extends AbstractModel, DTO extends AbstractDTO> {

	protected Logger logger = Logger.getLogger(this.getClass().getName());
	
	protected Class<ENTITY> entityType;
	
	protected Class<DTO> dtoType;
	
	@SuppressWarnings("unchecked")
	@PostConstruct
	private void init() {
		Type t = getClass().getGenericSuperclass();
        ParameterizedType pt = (ParameterizedType) t;
        entityType = (Class<ENTITY>) pt.getActualTypeArguments()[0];
        dtoType = (Class<DTO>) pt.getActualTypeArguments()[1];
	}
	
	protected abstract AbstractDAO<ENTITY> getDAO();
	
	public ResultDTO<DTO> list(String queryStr, Integer start, Integer limit, String order, String dir) throws Exception {
		if(start == null) {
			start = 0;
		}
		
		if(limit == null) {
			limit = 100;
		}
		
		if(order == null) {
			order = "id";
		}
		
		if(dir == null) {
			dir = "asc";
		}
		
		List<DTO> dtos = new ArrayList<>();
		
		for(ENTITY entity : getDAO().list(queryStr, start, limit, order, dir)) {
			dtos.add(entityToDTO(entity));
		}
		
		ResultDTO<DTO> result = new ResultDTO<>();
		
		result.setResultSize((long) dtos.size());
		result.setTotalSize(count(queryStr));
		result.setResult(dtos);
		result.setDir(dir);
		result.setLimit(limit);
		result.setOrder(order);
		result.setStart(start);
		
		return result;
	}
	
	public ResultDTO<DTO> list() throws Exception {
		List<DTO> dtos = new ArrayList<>();
		
		for(ENTITY entity : getDAO().list()) {
			dtos.add(entityToDTO(entity));
		}
		
		ResultDTO<DTO> result = new ResultDTO<>();
		
		result.setResultSize((long) dtos.size());
		result.setTotalSize((long) dtos.size());
		result.setResult(dtos);
		result.setLimit(dtos.size());
		result.setOrder("id");
		result.setStart(0);
		
		return result;
	}
	
	public DTO get(Long id) throws Exception {
		return entityToDTO(getDAO().get(id));
	}
	
	public DTO add(DTO dto) throws Exception {
		validate(null, dto);
		
		ENTITY entity = dtoToEntity(null, null, dto);
		
		getDAO().add(entity);
		
		return entityToDTO(entity);
	}
	
	public DTO update(Long id, DTO dto) throws Exception {
		validate(id, dto);
		
		ENTITY entity = getDAO().get(id);
				
		entity = dtoToEntity(id, entity, dto);
		
		getDAO().update(entity);
		
		return entityToDTO(entity);
	}
	
	public void remove(Long id) throws Exception {
		ENTITY entity = getDAO().get(id);
		getDAO().remove(entity);
	}
	
	public Long count(String queryStr) {
		return getDAO().count(queryStr);
	}
	
	public void validate(Long id, DTO dto) throws Exception {
		if(id != null && !getDAO().exists(id)) {
			throw new EntityNotFoundException(entityType.getSimpleName() + "[" + id + "] não encontrado.");
		}
		
		dto.setId(id);
		
		customValidation(dto);
	}
	
	public abstract void customValidation(DTO dto) throws Exception;
	
	protected DTO entityToDTO(ENTITY entity) throws Exception {
		DTO dto = dtoType.newInstance();
		
		if(entity == null) {
			return dto;
		}
		
		PropertyUtils.copyProperties(dto, entity);
		
		return dto;
	}
	
	protected ENTITY dtoToEntity(Long id, ENTITY entity, DTO dto) throws Exception {
		if(entity == null) {
			entity = entityType.newInstance();
		}
		
		if(dto == null) {
			return entity;
		}
		
		PropertyUtils.copyProperties(entity, dto);
		
		entity.setId(id);
		
		return entity;
	}
}