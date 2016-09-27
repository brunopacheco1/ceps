package com.dev.bruno.ceps.service;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.dev.bruno.ceps.dao.AbstractDAO;
import com.dev.bruno.ceps.dao.CepBairroDAO;
import com.dev.bruno.ceps.dao.CepDAO;
import com.dev.bruno.ceps.dao.CepLocalidadeDAO;
import com.dev.bruno.ceps.dao.CepTipoLogradouroDAO;
import com.dev.bruno.ceps.dto.CepTipoLogradouroDTO;
import com.dev.bruno.ceps.model.CepTipoLogradouro;

@Stateless
public class CepTipoLogradouroService extends AbstractService<CepTipoLogradouro, CepTipoLogradouroDTO> {

	@EJB
	private CepDAO contactDAO;
	
	@EJB
	private CepLocalidadeDAO cepLocalidadeDAO;
	
	@EJB
	private CepBairroDAO cepBairroDAO;
	
	@EJB
	private CepTipoLogradouroDAO cepTipoLogradouroDAO;
	
	@Override
	protected AbstractDAO<CepTipoLogradouro> getDAO() {
		return cepTipoLogradouroDAO;
	}

	@Override
	public void customValidation(CepTipoLogradouroDTO dto) throws Exception {
		//INCLUIR VALIDACOES
	}

	@Override
	protected CepTipoLogradouroDTO entityToDTO(CepTipoLogradouro entity) throws Exception {
		CepTipoLogradouroDTO dto = super.entityToDTO(entity);
		
		return dto;
	}

	@Override
	protected CepTipoLogradouro dtoToEntity(Long id, CepTipoLogradouro entity, CepTipoLogradouroDTO dto) throws Exception {
		CepTipoLogradouro cepTipoLogradouro = super.dtoToEntity(id, entity, dto);
		
		return cepTipoLogradouro;
	}
}