package com.dev.bruno.ceps.service;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.dev.bruno.ceps.dao.AbstractDAO;
import com.dev.bruno.ceps.dao.CepUFDAO;
import com.dev.bruno.ceps.dto.CepUFDTO;
import com.dev.bruno.ceps.model.CepUF;

@Stateless
public class CepUFService extends AbstractService<CepUF, CepUFDTO> {

	@Inject
	private CepUFDAO cepUFDAO;
	
	@Override
	protected AbstractDAO<CepUF> getDAO() {
		return cepUFDAO;
	}

	@Override
	public void customValidation(CepUFDTO dto) throws Exception {
		//INCLUIR VALIDACOES
	}

	@Override
	protected CepUFDTO entityToDTO(CepUF entity) throws Exception {
		CepUFDTO dto = super.entityToDTO(entity);
		
		return dto;
	}

	@Override
	protected CepUF dtoToEntity(Long id, CepUF entity, CepUFDTO dto) throws Exception {
		CepUF cepUF = super.dtoToEntity(id, entity, dto);
		
		return cepUF;
	}
}