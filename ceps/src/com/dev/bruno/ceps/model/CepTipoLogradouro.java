package com.dev.bruno.ceps.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="CEP_TIPO_LOGRADOURO")
public class CepTipoLogradouro extends AbstractModel {
	
	private static final long serialVersionUID = 4542587527145303945L;

	@Id
	@GeneratedValue
	@Column(name="COD_CEP_TIPO_LOGRADOURO", nullable=false)
	private Long id;

	@Column(name="DSC_NOME", nullable=false)
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