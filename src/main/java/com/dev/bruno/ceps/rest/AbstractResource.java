package com.dev.bruno.ceps.rest;

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

import com.dev.bruno.ceps.model.AbstractModel;
import com.dev.bruno.ceps.responses.GenericResponse;
import com.dev.bruno.ceps.responses.ResultList;
import com.dev.bruno.ceps.service.AbstractService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Produces(MediaType.APPLICATION_JSON)
public abstract class AbstractResource<ENTITY extends AbstractModel> {

	protected abstract AbstractService<ENTITY> getService();

	@GET
	@Operation(description = "Listar Entidades")
	@SecurityRequirement(name = "api_key")
	public ResultList<ENTITY> list(@QueryParam("query") String queryStr, @QueryParam("start") Integer start,
			@QueryParam("limit") Integer limit, @QueryParam("order") String order, @QueryParam("dir") String dir)
			throws Exception {
		return getService().list(queryStr, start, limit, order, dir);
	}

	@GET
	@Path("/{id:\\d+}")
	@Operation(description = "Buscar Entidade Por ID")
	@SecurityRequirement(name = "api_key")
	public ENTITY get(@PathParam("id") Long id) throws Exception {
		return getService().get(id);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(description = "Adicionar Entidade")
	@SecurityRequirement(name = "api_key")
	public GenericResponse add(ENTITY dto) throws Exception {
		getService().add(dto);

		return new GenericResponse(true);
	}

	@PUT
	@Path("/{id:\\d+}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(description = "Atualizar Entidade")
	@SecurityRequirement(name = "api_key")
	public GenericResponse update(@PathParam("id") Long id, ENTITY dto) throws Exception {
		getService().update(id, dto);

		return new GenericResponse(true);
	}

	@DELETE
	@Path("/{id:\\d+}")
	@Operation(description = "Remover Entidade")
	@SecurityRequirement(name = "api_key")
	public GenericResponse remove(@PathParam("id") Long id) throws Exception {
		getService().remove(id);

		return new GenericResponse(true);
	}
}