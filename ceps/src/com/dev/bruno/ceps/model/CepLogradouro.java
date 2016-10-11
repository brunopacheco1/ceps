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

@Entity
@Table(name="CEP_LOGRADOURO")
public class CepLogradouro extends AbstractModel {
	
	private static final long serialVersionUID = 4542587527145303945L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="COD_CEP_LOGRADOURO", nullable=false)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "COD_CEP_LOCALIDADE", nullable=false)
	private CepLocalidade cepLocalidade;
	
	@ManyToOne
	@JoinColumn(name = "COD_CEP_BAIRRO")
	private CepBairro cepBairro;
	
	@ManyToOne
	@JoinColumn(name = "COD_CEP_TIPO_LOGRADOURO")
	private CepTipoLogradouro cepTipoLogradouro;
	
	@Column(name="DSC_NOME", nullable=false)
	private String nome;
	
	@Column(name="DSC_NOME_NORMALIZADO", nullable=false)
	private String nomeNormalizado;
	
	@Column(name="DSC_COMPLEMENTO")
	private String complemento;

	@OneToMany(mappedBy="cepLogradouro")
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

	public void setCepLocalidade(CepLocalidade cepLocalidade) {
		this.cepLocalidade = cepLocalidade;
	}

	public CepBairro getCepBairro() {
		return cepBairro;
	}

	public void setCepBairro(CepBairro cepBairro) {
		this.cepBairro = cepBairro;
	}

	public CepTipoLogradouro getCepTipoLogradouro() {
		return cepTipoLogradouro;
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