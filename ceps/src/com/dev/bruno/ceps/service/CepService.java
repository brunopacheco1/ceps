package com.dev.bruno.ceps.service;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.dev.bruno.ceps.dao.AbstractDAO;
import com.dev.bruno.ceps.dao.CepBairroDAO;
import com.dev.bruno.ceps.dao.CepDAO;
import com.dev.bruno.ceps.dao.CepLocalidadeDAO;
import com.dev.bruno.ceps.dao.CepLogradouroDAO;
import com.dev.bruno.ceps.dto.CepDTO;
import com.dev.bruno.ceps.model.Cep;

@Stateless
public class CepService extends AbstractService<Cep, CepDTO> {

	@EJB
	private CepDAO cepDAO;
	
	@EJB
	private CepLocalidadeDAO cepLocalidadeDAO;
	
	@EJB
	private CepBairroDAO cepBairroDAO;
	
	@EJB
	private CepLogradouroDAO cepLogradouroDAO;
	
	@Override
	protected AbstractDAO<Cep> getDAO() {
		return cepDAO;
	}

	@Override
	public void customValidation(CepDTO dto) throws Exception {
		//INCLUIR VALIDACOES
	}

	@Override
	protected CepDTO entityToDTO(Cep entity) throws Exception {
		CepDTO dto = super.entityToDTO(entity);
		
		if(entity.getCepBairro() != null) {
			dto.setCodCepBairro(entity.getCepBairro().getId());
		}
		
		if(entity.getCepLocalidade() != null) {
			dto.setCodCepLocalidade(entity.getCepLocalidade().getId());
		}
		
		if(entity.getCepLogradouro() != null) {
			dto.setCodCepLogradouro(entity.getCepLogradouro().getId());
		}
		
		return dto;
	}

	@Override
	protected Cep dtoToEntity(Long id, Cep entity, CepDTO dto) throws Exception {
		Cep cep = super.dtoToEntity(id, entity, dto);
		
		if(dto.getCodCepBairro() != null) {
			cep.setCepBairro(cepBairroDAO.get(dto.getCodCepBairro()));
		}
		
		if(dto.getCodCepLocalidade() != null) {
			cep.setCepLocalidade(cepLocalidadeDAO.get(dto.getCodCepLocalidade()));
		}
		
		if(dto.getCodCepLogradouro() != null) {
			cep.setCepLogradouro(cepLogradouroDAO.get(dto.getCodCepLogradouro()));
		}
		
		return cep;
	}
}