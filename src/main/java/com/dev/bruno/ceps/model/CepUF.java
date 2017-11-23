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
public class CepUF extends AbstractModel {

	private static final long serialVersionUID = 6001113379795063656L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "COD_CEP_UF")
	private Long id;

	@Column(name = "DSC_UF", nullable = false)
	@NotNull
	private String uf;

	@Column(name = "FAIXA_CEP")
	private String faixaCEP;

	@OneToMany(mappedBy = "cepUF")
	@XmlTransient
	private List<CepLocalidade> localidades = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	public String getFaixaCEP() {
		return faixaCEP;
	}

	public void setFaixaCEP(String faixaCEP) {
		this.faixaCEP = faixaCEP;
	}

	public List<CepLocalidade> getLocalidades() {
		return localidades;
	}

	public void setLocalidades(List<CepLocalidade> localidades) {
		this.localidades = localidades;
	}
}