package com.dev.bruno.ceps.dto;



public class CepLocalidadeDTO extends AbstractDTO {

	private static final long serialVersionUID = 6380647707611003240L;

	private Long id;
	
	private Long codCepUF;
	
	private String nome;
	
	private String distrito;
	
	private String nomeNormalizado;
	
	private String faixaCEP;
	
//	private Date ultimoProcessamento;
	
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

	public Long getCodCepUF() {
		return codCepUF;
	}

	public void setCodCepUF(Long codCepUF) {
		this.codCepUF = codCepUF;
	}

//	public Date getUltimoProcessamento() {
//		return ultimoProcessamento;
//	}
//
//	public void setUltimoProcessamento(Date ultimoProcessamento) {
//		this.ultimoProcessamento = ultimoProcessamento;
//	}
}