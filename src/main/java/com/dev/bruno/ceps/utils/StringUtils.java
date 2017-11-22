package com.dev.bruno.ceps.utils;

public class StringUtils {

	public static String normalizarNome(String text) {
		if (text == null) {
			return null;
		}

		return java.text.Normalizer.normalize(text.toUpperCase().trim(), java.text.Normalizer.Form.NFD)
				.replaceAll("[^\\p{ASCII}]", "");
	}
}