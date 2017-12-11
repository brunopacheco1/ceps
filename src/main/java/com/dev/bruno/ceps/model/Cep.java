package com.dev.bruno.ceps.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "CEP")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Cep extends AbstractModel {

	private static final long serialVersionUID = -5660205458426323459L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "COD_CEP")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "COD_CEP_LOCALIDADE")
	private Localidade localidade;

	@ManyToOne
	@JoinColumn(name = "COD_CEP_BAIRRO")
	private Bairro bairro;

	@ManyToOne
	@JoinColumn(name = "COD_CEP_LOGRADOURO")
	private Logradouro logradouro;

	@Column(name = "DSC_CEP", nullable = false)
	@NotNull
	@Pattern(regexp = "\\d{8}")
	private String numeroCep;

	@Column(name = "TIP_CEP", nullable = false)
	@NotNull
	@Enumerated(EnumType.STRING)
	private TipoCepEnum tipoCep;

	@Column(name = "DSC_CAIXA_POSTAL")
	private String caixaPostal;

	@Column(name = "DSC_NOME_ESPECIAL")
	private String nomeEspecial;

	public Localidade getLocalidade() {
		return localidade;
	}

	public Long getLocalidadeId() {
		return localidade != null ? localidade.getId() : null;
	}

	public void setLocalidade(Localidade localidade) {
		this.localidade = localidade;
	}

	public Bairro getBairro() {
		return bairro;
	}

	public Long getBairroId() {
		return bairro != null ? bairro.getId() : null;
	}

	public void setBairro(Bairro bairro) {
		this.bairro = bairro;
	}

	public Logradouro getLogradouro() {
		return logradouro;
	}

	public Long getLogradouroId() {
		return logradouro != null ? logradouro.getId() : null;
	}

	public void setLogradouro(Logradouro logradouro) {
		this.logradouro = logradouro;
	}

	public String getNumeroCep() {
		return numeroCep;
	}

	public void setNumeroCep(String numeroCep) {
		this.numeroCep = numeroCep;
	}

	public TipoCepEnum getTipoCep() {
		return tipoCep;
	}

	public void setTipoCep(TipoCepEnum tipoCep) {
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