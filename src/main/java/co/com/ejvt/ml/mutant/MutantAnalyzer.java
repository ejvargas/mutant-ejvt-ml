package co.com.ejvt.ml.mutant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class MutantAnalyzer {

	Logger logger = LoggerFactory.getLogger(MutantAnalyzer.class);
	org.slf4j.Marker marker;
	public int secHorizontal;
	public int secVertical;
	public int secOblicuasPositivas;
	public int secOblicuasNegativas;
	String caracteresValidos;
	int tamanhoReferencia;
	int secuenciaDeIguales;
	int numeroDeSecuenciasParaSerMutante;
	String[] arrayWorking = null;

	@Autowired
	public MutantAnalyzer(String[] array) {
		secuenciaDeIguales = System.getenv().get("SECUENCIA_DE_IGUALES") == null ? 4
				: Integer.parseInt(System.getenv().get("SECUENCIA_DE_IGUALES"));
		numeroDeSecuenciasParaSerMutante = System.getenv().get("SECUENCIA_PARA_SER_MUTANTE") == null ? 2
				: Integer.parseInt(System.getenv().get("SECUENCIA_PARA_SER_MUTANTE"));
		caracteresValidos = System.getenv().get("CARACTERES_VALIDOS") == null ? "ACGT"
				: System.getenv().get("CARACTERES_VALIDOS");

		paramValidations(array);
		arrayWorking = array;
	}

	@Autowired
	private void paramValidations(String[] array) {

		// 1. Validación básica del array
		if (array == null || array.length == 0) {
			throw new IllegalArgumentException("El array no puede ser nulo o estar vacío.");
		}

		tamanhoReferencia = array.length;

		for (String fila : array) {

			// 3. Se valida que cada fila tenga el mismo tamaño de la de referencia
			if (fila.trim().length() != tamanhoReferencia) {
				throw new IllegalArgumentException(String.format("La matriz no es de %dx%d. Error en la fila '%s'",
						tamanhoReferencia, tamanhoReferencia, fila));
			}

			// 4. Se valida que cada fila cumpla con contener solamente los caracteres A, C,
			// G, T, a traves de una expresión regular
			String regex = "^[" + caracteresValidos + "]+$";
			if (!fila.matches(regex)) {
				throw new IllegalArgumentException(String.format("La fila '%s' contiene información no válida.", fila));
			}
		}
	}

	public static void main(String[] args) {

		// ORIGINAL: {"ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"};
		// OB_POSITIVA: {"ATGCGA","CACTGC","TCATGT","CGAAGG","CCCCTA","TCACTG"};

		String[] dna = { "ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG" };
		(new MutantAnalyzer(dna)).isMutant();

	}

	@Autowired
	public boolean isMutant() {
		if (tamanhoReferencia < secuenciaDeIguales)
			return false;

		secHorizontal = recorrerHorizontal();
		secVertical = recorrerVertical();
		secOblicuasPositivas = recorrerOblicuasPositivas();
		secOblicuasNegativas = recorrerOblicuasNegativas();

		mostrarResumen(secHorizontal, secVertical, secOblicuasPositivas, secOblicuasNegativas);

		return (secHorizontal + secVertical + secOblicuasPositivas
				+ secOblicuasNegativas >= numeroDeSecuenciasParaSerMutante);
	}

	@Autowired
	private int recorrerHorizontal() {
		int contadorSecuencias = 0;
		int contadorReps = 0;

		for (int i = 0; i < tamanhoReferencia; i++) {
			String anterior = " ";
			contadorReps = 0;
			for (int j = 0; j < tamanhoReferencia; j++) {
				String actual = arrayWorking[i].substring(j, j + 1);

				if (actual.equals(anterior)) {
					contadorReps++;
					if (contadorReps == (secuenciaDeIguales - 1)) {
						contadorSecuencias++;
						contadorReps = 0;
					}
				} else {
					contadorReps = 0;
				}

				anterior = actual;
			}
		}

		return contadorSecuencias;
	}

	@Autowired
	private int recorrerVertical() {
		int contadorSecuencias = 0;
		int contadorReps = 0;

		for (int i = 0; i < tamanhoReferencia; i++) {
			String anterior = " ";
			contadorReps = 0;
			for (int j = 0; j < tamanhoReferencia; j++) {
				String actual = arrayWorking[j].substring(i, i + 1);

				if (actual.equals(anterior)) {
					contadorReps++;
					if (contadorReps == (secuenciaDeIguales - 1)) {
						contadorSecuencias++;
						contadorReps = 0;
					}
				} else {
					contadorReps = 0;
				}

				anterior = actual;
			}
		}
		return contadorSecuencias;

	}

	@Autowired
	private int recorrerOblicuasPositivas() {
		int contadorSecuencias = 0;
		int contadorReps = 0;

		for (int k = 1; k <= tamanhoReferencia; k++) {
			String anterior = " ";
			contadorReps = 0;
			for (int i = 0; i <= k; i++) {
				for (int j = 0; j <= k; j++) {
					if (j + i == k - 1) {
						String actual = arrayWorking[i].substring(j, j + 1);

						if (actual.equals(anterior)) {
							contadorReps++;
							if (contadorReps == (secuenciaDeIguales - 1)) {
								contadorSecuencias++;
								contadorReps = 0;
							}
						} else {
							contadorReps = 0;
						}

						anterior = actual;
					}
				}
			}
		}
		for (int k = tamanhoReferencia + 1; k <= (tamanhoReferencia * 2) - 1; k++) {
			String anterior = " ";
			contadorReps = 0;
			for (int i = 1; i <= tamanhoReferencia - 1; i++) {
				for (int j = tamanhoReferencia - 1; j >= 0; j--) {
					if (j + i == k - 1) {
						String actual = arrayWorking[i].substring(j, j + 1);

						if (actual.equals(anterior)) {
							contadorReps++;
							if (contadorReps == (secuenciaDeIguales - 1)) {
								contadorSecuencias++;
								contadorReps = 0;
							}
						} else {
							contadorReps = 0;
						}

						anterior = actual;
					}
				}
			}
		}

		return contadorSecuencias;

	}

	@Autowired
	private int recorrerOblicuasNegativas() {
		int contadorSecuencias = 0;
		int contadorReps = 0;

		for (int k = tamanhoReferencia - 1; k >= 0; k--) {
			String anterior = " ";
			contadorReps = 0;
			for (int i = 0; i <= tamanhoReferencia - 1; i++) {
				for (int j = tamanhoReferencia - 1; j >= 0; j--) {
					if (j - i == k) {
						String actual = arrayWorking[i].substring(j, j + 1);

						if (actual.equals(anterior)) {
							contadorReps++;
							if (contadorReps == (secuenciaDeIguales - 1)) {
								contadorSecuencias++;
								contadorReps = 0;
							}
						} else {
							contadorReps = 0;
						}

						anterior = actual;
					}
				}
			}
		}
		for (int k = 1; k <= tamanhoReferencia - 1; k++) {
			String anterior = " ";
			contadorReps = 0;
			for (int i = 1; i <= tamanhoReferencia - 1; i++) {
				for (int j = tamanhoReferencia - 2; j >= 0; j--) {
					if (i - j == k) {
						String actual = arrayWorking[i].substring(j, j + 1);

						if (actual.equals(anterior)) {
							contadorReps++;
							if (contadorReps == (secuenciaDeIguales - 1)) {
								contadorSecuencias++;
								contadorReps = 0;
							}
						} else {
							contadorReps = 0;
						}

						anterior = actual;
					}
				}
			}
		}

		return contadorSecuencias;
	}

	@Autowired
	private void mostrarResumen(int secHorizontal, int secVertical, int secOblicuasPositivas,
			int secOblicuasNegativas) {
		int secuenciasEncontradas = secHorizontal + secVertical + secOblicuasPositivas + secOblicuasNegativas;

		StringBuilder sbf = new StringBuilder();
		sbf.append("\r\nResumen de secuencias continuas para la muestra de ADN:");
		for (String fila : arrayWorking) {
			sbf.append("\r\n     " + fila + "");
		}
		sbf.append(String.format(
				"\r\nTotal [%s]: Horizontales [%s], Verticales [%s], Oblicuas Positivas [%s], Oblicuas Negativas [%s]",
				secuenciasEncontradas, secHorizontal, secVertical, secOblicuasPositivas, secOblicuasNegativas));

		logger.info(marker, sbf.toString());


	}

}
