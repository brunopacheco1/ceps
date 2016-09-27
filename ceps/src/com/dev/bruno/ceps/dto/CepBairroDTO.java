package com.dev.bruno.ceps.dto;

import java.util.Date;


public class CepBairroDTO extends AbstractDTO {

	private static final long serialVersionUID = 6821851936948464935L;

	private Long id;
	
	private Long codCepLocalidade;
	
	private String nome;
	
	private String nomeNormalizado;
	
	private Date ultimoProcessamento;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Long getCodCepLocalidade() {
		return codCepLocalidade;
	}

	public void setCodCepLocalidade(Long codCepLocalidade) {
		this.codCepLocalidade = codCepLocalidade;
	}

	public Date getUltimoProcessamento() {
		return ultimoProcessamento;
	}

	public void setUltimoProcessamento(Date ultimoProcessamento) {
		this.ultimoProcessamento = ultimoProcessamento;
	}
}