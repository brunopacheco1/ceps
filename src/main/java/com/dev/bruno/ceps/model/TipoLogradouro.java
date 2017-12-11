package com.dev.bruno.ceps.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "CEP_TIPO_LOGRADOURO")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TipoLogradouro extends AbstractModel {

	private static final long serialVersionUID = 4542587527145303945L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "COD_CEP_TIPO_LOGRADOURO")
	private Long id;

	@Column(name = "DSC_NOME", nullable = false)
	@NotNull
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