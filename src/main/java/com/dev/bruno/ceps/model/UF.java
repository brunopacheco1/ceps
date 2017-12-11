package com.dev.bruno.ceps.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "CEP_UF")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class UF extends AbstractModel {

	private static final long serialVersionUID = 6001113379795063656L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "COD_CEP_UF")
	private Long id;

	@Column(name = "DSC_UF", nullable = false)
	@NotNull
	private String nome;

	@Column(name = "FAIXA_CEP")
	private String faixaCEP;

	@OneToMany(mappedBy = "uf")
	@XmlTransient
	private List<Localidade> localidades = new ArrayList<>();

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

	public String getFaixaCEP() {
		return faixaCEP;
	}

	public void setFaixaCEP(String faixaCEP) {
		this.faixaCEP = faixaCEP;
	}

	public List<Localidade> getLocalidades() {
		return localidades;
	}

	public void setLocalidades(List<Localidade> localidades) {
		this.localidades = localidades;
	}
}