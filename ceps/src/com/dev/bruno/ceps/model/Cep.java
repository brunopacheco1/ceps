package com.dev.bruno.ceps.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="CEP")
public class Cep extends AbstractModel {

	private static final long serialVersionUID = -5660205458426323459L;

	@Id
	@GeneratedValue
	@Column(name="COD_CEP", nullable=false)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "COD_CEP_LOCALIDADE")
	private CepLocalidade cepLocalidade;
	
	@ManyToOne
	@JoinColumn(name = "COD_CEP_BAIRRO")
	private CepBairro cepBairro;
	
	@ManyToOne
	@JoinColumn(name = "COD_CEP_LOGRADOURO")
	private CepLogradouro cepLogradouro;
	
	@Column(name="DSC_CEP", nullable=false)
	private String cep;
	
	@Column(name="TIP_CEP", nullable=false)
	private String tipoCep;
	
	@Column(name="DSC_CAIXA_POSTAL")
	private String caixaPostal;
	
	@Column(name="DSC_NOME_ESPECIAL")
	private String nomeEspecial;

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

	public CepLogradouro getCepLogradouro() {
		return cepLogradouro;
	}

	public void setCepLogradouro(CepLogradouro cepLogradouro) {
		this.cepLogradouro = cepLogradouro;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getTipoCep() {
		return tipoCep;
	}

	public void setTipoCep(String tipoCep) {
		this.tipoCep = tipoCep;
	}

	public String getCaixaPostal() {
		return caixaPostal;
	}

	public void setCaixaPostal(String caixaPostal) {
		this.caixaPostal = caixaPostal;
	}

	public String getNomeEspecial() {
		return nomeEspecial;
	}

	public void setNomeEspecial(String nomeEspecial) {
		this.nomeEspecial = nomeEspecial;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}