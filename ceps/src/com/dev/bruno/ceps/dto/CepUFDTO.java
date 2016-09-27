package com.dev.bruno.ceps.dto;


public class CepUFDTO extends AbstractDTO {

	private static final long serialVersionUID = 6001113379795063656L;

	private Long id;
	
	private String uf;
	
	private String faixaCEP;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	public String getFaixaCEP() {
		return faixaCEP;
	}

	public void setFaixaCEP(String faixaCEP) {
		this.faixaCEP = faixaCEP;
	}
}