package co.com.ejvt.ml.mutant;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

public class VTRandomMatrix {

	static Logger logger = LoggerFactory.getLogger(VTRandomMatrix.class);

	/* Método desde el cual se obtiene una Matriz NxN de acuerdo al parámetro */
	@Bean
	public String getRandomMatrixInJson(int tamanho) {
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i <= tamanho; i++) {
			sb.append(String.format("\"%s\", ", getRandomSequence(tamanho)));
		}
		String cortado = sb.substring(0, sb.lastIndexOf(","));

		return String.format("{ \"dna\" : [ %s ] }", cortado);
	}

	/*
	 * Método desde el cual se obtiene una Secuencia de NxN caracteres, de acuerdo
	 * al parámetro
	 */
	private String getRandomSequence(int tamanho) {
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i <= tamanho; i++) {
			sb.append(getRandomLetter());
		}
		return sb.toString();
	}
	
	/* Método desde el cual se obtiene de forma aleatoria, una letra de las que se encuentran en el dominio*/
	private String getRandomLetter() {
		String[] dominio = { "A", "C", "G", "T" };
		int index = 0;
		Random random;
		try {
			random = SecureRandom.getInstanceStrong();
			index = random.nextInt(dominio.length);
		} catch (NoSuchAlgorithmException e) {
			logger.warn(String.format("Error en la generación de Random: %s", e.getMessage()));
		}

		return dominio[index];
	}

}