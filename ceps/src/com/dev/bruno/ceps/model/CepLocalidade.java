package com.dev.bruno.ceps.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="CEP_LOCALIDADE")
public class CepLocalidade extends AbstractModel {

	private static final long serialVersionUID = 6380647707611003240L;

	@Id
	@GeneratedValue
	@Column(name="COD_CEP_LOCALIDADE", nullable=false)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "COD_CEP_UF")
	private CepUF cepUF;
	
	@Column(name="DSC_NOME", nullable=false)
	private String nome;
	
	@Column(name="DSC_DISTRITO")
	private String distrito;
	
	@Column(name="DSC_NOME_NORMALIZADO", nullable=false)
	private String nomeNormalizado;
	
	@Column(name="FAIXA_CEP")
	private String faixaCEP;
	
	@OneToMany(mappedBy="cepLocalidade")
	private List<CepBairro> bairros = new ArrayList<>();
	
	@OneToMany(mappedBy="cepLocalidade")
	private List<CepLogradouro> logradouros = new ArrayList<>();
	
	@OneToMany(mappedBy="cepLocalidade")
	private List<Cep> ceps = new ArrayList<>();
	
//	@Column(name="DAT_ULTIMO_PROCESSAMENTO")
//	private Date ultimoProcessamento;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CepUF getCepUF() {
		return cepUF;
	}

	public void setCepUF(CepUF cepUF) {
		this.cepUF = cepUF;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDistrito() {
		return distrito;
	}

	public void setDistrito(String distrito) {
		this.distrito = distrito;
	}

	public String getNomeNormalizado() {
		return nomeNormalizado;
	}

	public void setNomeNormalizado(String nomeNormalizado) {
		this.nomeNormalizado = nomeNormalizado;
	}

	public String getFaixaCEP() {
		return faixaCEP;
	}

	public void setFaixaCEP(String faixaCEP) {
		this.faixaCEP = faixaCEP;
	}

	public List<CepBairro> getBairros() {
		return bairros;
	}

	public void setBairros(List<CepBairro> bairros) {
		this.bairros = bairros;
	}

	public List<CepLogradouro> getLogradouros() {
		return logradouros;
	}

	public void setLogradouros(List<CepLogradouro> logradouros) {
		this.logradouros = logradouros;
	}

	public List<Cep> getCeps() {
		return ceps;
	}

	public void setCeps(List<Cep> ceps) {
		this.ceps = ceps;
	}

//	public Date getUltimoProcessamento() {
//		return ultimoProcessamento;
//	}
//
//	public void setUltimoProcessamento(Date ultimoProcessamento) {
//		this.ultimoProcessamento = ultimoProcessamento;
//	}
}