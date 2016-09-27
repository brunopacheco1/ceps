package com.dev.bruno.ceps.dto;


public class CepLogradouroDTO extends AbstractDTO {
	
	private static final long serialVersionUID = 4542587527145303945L;

	private Long id;
	
	private Long codCepLocalidade;
	
	private Long codCepBairro;
	
	private Long codCepTipoLogradouro;
	
	private String nome;
	
	private String nomeNormalizado;
	
	private String complemento;

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

	public Long getCodCepTipoLogradouro() {
		return codCepTipoLogradouro;
	}

	public void setCodCepTipoLogradouro(Long codCepTipoLogradouro) {
		this.codCepTipoLogradouro = codCepTipoLogradouro;
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
}