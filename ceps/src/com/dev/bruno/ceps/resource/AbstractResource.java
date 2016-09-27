package com.dev.bruno.ceps.resource;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.dev.bruno.ceps.dto.AbstractDTO;
import com.dev.bruno.ceps.dto.ResultDTO;
import com.dev.bruno.ceps.model.AbstractModel;
import com.dev.bruno.ceps.response.GenericResponse;
import com.dev.bruno.ceps.service.AbstractService;

@Produces(MediaType.APPLICATION_JSON)
public abstract class AbstractResource<ENTITY extends AbstractModel, DTO extends AbstractDTO> {

	protected Logger logger = Logger.getLogger(this.getClass().getName());
	
	protected abstract AbstractService<ENTITY, DTO> getService();
	
	@GET
	public ResultDTO<DTO> list(@QueryParam("query") String queryStr, @QueryParam("start") Integer start, @QueryParam("limit") Integer limit, @QueryParam("order") String order, @QueryParam("dir") String dir) throws Exception {
		return getService().list(queryStr, start, limit, order, dir);
	}
	
	@GET
	@Path("/{id}") 
	public DTO get(@PathParam("id") Long id) throws Exception {
		return getService().get(id);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public GenericResponse add(DTO dto) throws Exception {
		getService().add(dto);
		
		return new GenericResponse(true);
	}
	
	@PUT
	@Path("/{id}") 
	@Consumes(MediaType.APPLICATION_JSON)
	public GenericResponse update(@PathParam("id") Long id, DTO dto) throws Exception {
		getService().update(id, dto);
		
		return new GenericResponse(true);
	}
	
	@DELETE
	@Path("/{id}")
	public GenericResponse remove(@PathParam("id") Long id) throws Exception {
		getService().remove(id);
		
		return new GenericResponse(true);
	}
	
	public Long count(String queryStr) {
		return getService().count(queryStr);
	}
}