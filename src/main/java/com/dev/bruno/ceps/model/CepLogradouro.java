package com.dev.bruno.ceps.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "CEP_LOGRADOURO")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CepLogradouro extends AbstractModel {

	private static final long serialVersionUID = 4542587527145303945L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "COD_CEP_LOGRADOURO")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "COD_CEP_LOCALIDADE", nullable = false)
	@NotNull
	private CepLocalidade cepLocalidade;

	@ManyToOne
	@JoinColumn(name = "COD_CEP_BAIRRO")
	private CepBairro cepBairro;

	@ManyToOne
	@JoinColumn(name = "COD_CEP_TIPO_LOGRADOURO")
	private CepTipoLogradouro cepTipoLogradouro;

	@Column(name = "DSC_NOME", nullable = false)
	@NotNull
	private String nome;

	@Column(name = "DSC_NOME_NORMALIZADO", nullable = false)
	@NotNull
	private String nomeNormalizado;

	@Column(name = "DSC_COMPLEMENTO")
	private String complemento;

	@OneToMany(mappedBy = "cepLogradouro")
	@XmlTransient
	private List<Cep> ceps = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CepLocalidade getCepLocalidade() {
		return cepLocalidade;
	}

	public Long getCepLocalidadeId() {
		return cepLocalidade != null ? cepLocalidade.getId() : null;
	}

	public void setCepLocalidade(CepLocalidade cepLocalidade) {
		this.cepLocalidade = cepLocalidade;
	}

	public CepBairro getCepBairro() {
		return cepBairro;
	}

	public Long getCepBairroId() {
		return cepBairro != null ? cepBairro.getId() : null;
	}

	public void setCepBairro(CepBairro cepBairro) {
		this.cepBairro = cepBairro;
	}

	public CepTipoLogradouro getCepTipoLogradouro() {
		return cepTipoLogradouro;
	}

	public Long getCepTipoLogradouroId() {
		return cepTipoLogradouro != null ? cepTipoLogradouro.getId() : null;
	}

	public void setCepTipoLogradouro(CepTipoLogradouro cepTipoLogradouro) {
		this.cepTipoLogradouro = cepTipoLogradouro;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNomeNormalizado() {
		return nomeNormalizado;
	}

	public void setNomeNormalizado(String nomeNormalizado) {
		this.nomeNormalizado = nomeNormalizado;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public List<Cep> getCeps() {
		return ceps;
	}

	public void setCeps(List<Cep> ceps) {
		this.ceps = ceps;
	}
}