package com.dev.bruno.ceps.model;

import java.time.LocalDateTime;
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
@Table(name = "CEP_BAIRRO")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Bairro extends AbstractModel {

	private static final long serialVersionUID = 6821851936948464935L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "COD_CEP_BAIRRO")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "COD_CEP_LOCALIDADE", nullable = false)
	@NotNull
	private Localidade localidade;

	@Column(name = "DSC_NOME", nullable = false)
	@NotNull
	private String nome;

	@Column(name = "DSC_NOME_NORMALIZADO", nullable = false)
	@NotNull
	private String nomeNormalizado;

	@OneToMany(mappedBy = "bairro")
	@XmlTransient
	private List<Logradouro> logradouros = new ArrayList<>();

	@OneToMany(mappedBy = "bairro")
	@XmlTransient
	private List<Cep> ceps = new ArrayList<>();

	@Column(name = "DAT_ULTIMO_PROCESSAMENTO")
	@XmlTransient
	private LocalDateTime dataUltimoProcessamento;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Localidade getLocalidade() {
		return localidade;
	}

	public Long getLocalidadeId() {
		return localidade != null ? localidade.getId() : null;
	}

	public void setLocalidade(Localidade cepLocalidade) {
		this.localidade = cepLocalidade;
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

	public List<Logradouro> getLogradouros() {
		return logradouros;
	}

	public void setLogradouros(List<Logradouro> logradouros) {
		this.logradouros = logradouros;
	}

	public List<Cep> getCeps() {
		return ceps;
	}

	public void setCeps(List<Cep> ceps) {
		this.ceps = ceps;
	}

	public LocalDateTime getDataUltimoProcessamento() {
		return dataUltimoProcessamento;
	}

	public void setDataUltimoProcessamento(LocalDateTime dataUltimoProcessamento) {
		this.dataUltimoProcessamento = dataUltimoProcessamento;
	}
}