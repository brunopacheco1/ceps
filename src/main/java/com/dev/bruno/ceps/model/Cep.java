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
	private Localidade cepLocalidade;

	@ManyToOne
	@JoinColumn(name = "COD_CEP_BAIRRO")
	private Bairro cepBairro;

	@ManyToOne
	@JoinColumn(name = "COD_CEP_LOGRADOURO")
	private Logradouro cepLogradouro;

	@Column(name = "DSC_CEP", nullable = false)
	@NotNull
	@Pattern(regexp = "\\d{5}-\\d{3}")
	private String numeroCep;

	@Column(name = "TIP_CEP", nullable = false)
	@NotNull
	@Enumerated(EnumType.STRING)
	private TipoCepEnum tipoCep;

	@Column(name = "DSC_CAIXA_POSTAL")
	private String caixaPostal;

	@Column(name = "DSC_NOME_ESPECIAL")
	private String nomeEspecial;

	public Localidade getCepLocalidade() {
		return cepLocalidade;
	}

	public Long getCepLocalidadeId() {
		return cepLocalidade != null ? cepLocalidade.getId() : null;
	}

	public void setCepLocalidade(Localidade cepLocalidade) {
		this.cepLocalidade = cepLocalidade;
	}

	public Bairro getCepBairro() {
		return cepBairro;
	}

	public Long getCepBairroId() {
		return cepBairro != null ? cepBairro.getId() : null;
	}

	public void setCepBairro(Bairro cepBairro) {
		this.cepBairro = cepBairro;
	}

	public Logradouro getCepLogradouro() {
		return cepLogradouro;
	}

	public Long getCepLogradouroId() {
		return cepLogradouro != null ? cepLogradouro.getId() : null;
	}

	public void setCepLogradouro(Logradouro cepLogradouro) {
		this.cepLogradouro = cepLogradouro;
	}

	public String getNumeroCodigo() {
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