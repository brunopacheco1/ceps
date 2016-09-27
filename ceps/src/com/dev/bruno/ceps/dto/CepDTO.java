package com.dev.bruno.ceps.dto;


public class CepDTO extends AbstractDTO {

	private static final long serialVersionUID = 6681761225052853713L;

	private Long id;
	
	private Long codCepLocalidade;
	
	private Long codCepBairro;
	
	private Long codCepLogradouro;
	
	private String cep;
	
	private String tipoCep;
	
	private String caixaPostal;
	
	private String nomeEspecial;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCodCepLocalidade() {
		return codCepLocalidade;
	}

	public void setCodCepLocalidade(Long codCepLocalidade) {
		this.codCepLocalidade = codCepLocalidade;
	}

	public Long getCodCepBairro() {
		return codCepBairro;
	}

	public void setCodCepBairro(Long codCepBairro) {
		this.codCepBairro = codCepBairro;
	}

	public Long getCodCepLogradouro() {
		return codCepLogradouro;
	}

	public void setCodCepLogradouro(Long codCepLogradouro) {
		this.codCepLogradouro = codCepLogradouro;
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
}