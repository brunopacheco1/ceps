package com.dev.bruno.ceps.service;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.dev.bruno.ceps.dao.AbstractDAO;
import com.dev.bruno.ceps.dao.CepBairroDAO;
import com.dev.bruno.ceps.dao.CepLocalidadeDAO;
import com.dev.bruno.ceps.dao.CepLogradouroDAO;
import com.dev.bruno.ceps.dao.CepTipoLogradouroDAO;
import com.dev.bruno.ceps.dto.CepLogradouroDTO;
import com.dev.bruno.ceps.model.CepLogradouro;

@Stateless
public class CepLogradouroService extends AbstractService<CepLogradouro, CepLogradouroDTO> {

	@Inject
	private CepLocalidadeDAO cepLocalidadeDAO;
	
	@Inject
	private CepBairroDAO cepBairroDAO;
	
	@Inject
	private CepLogradouroDAO cepLogradouroDAO;
	
	@Inject
	private CepTipoLogradouroDAO cepTipoLogradouroDAO;
	
	@Override
	protected AbstractDAO<CepLogradouro> getDAO() {
		return cepLogradouroDAO;
	}

	@Override
	public void customValidation(CepLogradouroDTO dto) throws Exception {
		//INCLUIR VALIDACOES
	}

	@Override
	protected CepLogradouroDTO entityToDTO(CepLogradouro entity) throws Exception {
		CepLogradouroDTO dto = super.entityToDTO(entity);
		
		if(entity.getCepBairro() != null) {
			dto.setCodCepBairro(entity.getCepBairro().getId());
		}
		
		if(entity.getCepLocalidade() != null) {
			dto.setCodCepLocalidade(entity.getCepLocalidade().getId());
		}
		
		if(entity.getCepTipoLogradouro() != null) {
			dto.setCodCepTipoLogradouro(entity.getCepTipoLogradouro().getId());
		}
		
		return dto;
	}

	@Override
	protected CepLogradouro dtoToEntity(Long id, CepLogradouro entity, CepLogradouroDTO dto) throws Exception {
		CepLogradouro cepLogradouro = super.dtoToEntity(id, entity, dto);
		
		if(dto.getCodCepBairro() != null) {
			cepLogradouro.setCepBairro(cepBairroDAO.get(dto.getCodCepBairro()));
		}
		
		if(dto.getCodCepLocalidade() != null) {
			cepLogradouro.setCepLocalidade(cepLocalidadeDAO.get(dto.getCodCepLocalidade()));
		}
		
		if(dto.getCodCepTipoLogradouro() != null) {
			cepLogradouro.setCepTipoLogradouro(cepTipoLogradouroDAO.get(dto.getCodCepTipoLogradouro()));
		}
		
		return cepLogradouro;
	}
}