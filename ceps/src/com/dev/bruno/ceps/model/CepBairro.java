package com.dev.bruno.ceps.model;

import java.util.ArrayList;
import java.util.Date;
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
@Table(name="CEP_BAIRRO")
public class CepBairro extends AbstractModel {

	private static final long serialVersionUID = 6821851936948464935L;

	@Id
	@GeneratedValue
	@Column(name="COD_CEP_BAIRRO", nullable=false)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "COD_CEP_LOCALIDADE", nullable=false)
	private CepLocalidade cepLocalidade;
	
	@Column(name="DSC_NOME", nullable=false)
	private String nome;
	
	@Column(name="DSC_NOME_NORMALIZADO", nullable=false)
	private String nomeNormalizado;
	
	@OneToMany(mappedBy="cepBairro")
	private List<CepLogradouro> logradouros = new ArrayList<>();
	
	@OneToMany(mappedBy="cepBairro")
	private List<Cep> ceps = new ArrayList<>();
	
	@Column(name="DAT_ULTIMO_PROCESSAMENTO")
	private Date ultimoProcessamento;

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

	public Date getUltimoProcessamento() {
		return ultimoProcessamento;
	}

	public void setUltimoProcessamento(Date ultimoProcessamento) {
		this.ultimoProcessamento = ultimoProcessamento;
	}
}