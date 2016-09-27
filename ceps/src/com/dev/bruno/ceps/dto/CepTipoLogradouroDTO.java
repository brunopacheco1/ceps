package com.dev.bruno.ceps.dto;


public class CepTipoLogradouroDTO extends AbstractDTO {
	
	private static final long serialVersionUID = 4542587527145303945L;

	private Long id;

	private String nome;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
}