package com.dev.bruno.ceps.model;

public enum TipoCepEnum {

	UNI, LOG, PRO, CPC, GRU, UOP;

	public static TipoCepEnum getTipoPorCEP(String numeroCep) {
		if (numeroCep == null || !numeroCep.matches("\\d{8}")) {
			return null;
		}

		Long sufixo = Long.parseLong(numeroCep.substring(5));

		if (sufixo == 0) {
			return TipoCepEnum.UNI;
		}

		if (sufixo <= 899) {
			return TipoCepEnum.LOG;
		}

		if (sufixo <= 959) {
			return TipoCepEnum.GRU;
		}

		if (sufixo <= 969) {
			return TipoCepEnum.PRO;
		}

		if (sufixo <= 989 || sufixo == 999) {
			return TipoCepEnum.UOP;
		}

		if (sufixo <= 998) {
			return TipoCepEnum.CPC;
		}

		return null;
	}
}