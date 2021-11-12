package co.com.ejvt.ml.mutant;

import org.springframework.beans.factory.annotation.Autowired;

public class Mutant {

	String CaracteresValidos;
	int TamanhoReferencia = Integer.MIN_VALUE;
	int SecuenciaDeIguales = Integer.MIN_VALUE;
	int NumeroDeSecuenciasParaSerMutante = Integer.MIN_VALUE;
	String[] Array = null;

	@Autowired
	public Mutant(String[] array) {
		SecuenciaDeIguales = System.getenv().get("SECUENCIA_DE_IGUALES") == null ? 4
				: Integer.parseInt(System.getenv().get("SECUENCIA_DE_IGUALES"));
		NumeroDeSecuenciasParaSerMutante = System.getenv().get("SECUENCIA_PARA_SER_MUTANTE") == null ? 2
				: Integer.parseInt(System.getenv().get("SECUENCIA_PARA_SER_MUTANTE"));
		CaracteresValidos = System.getenv().get("CARACTERES_VALIDOS") == null ? "ACGT"
				: System.getenv().get("CARACTERES_VALIDOS");

		paramValidations(array);
		Array = array;
	}

	@Autowired
	private void paramValidations(String[] array) {
		//imprimirArray(array);
		
		// 1. Validación básica del array
		if (array == null || array.length == 0) {
			throw new IllegalArgumentException("El array no puede ser nulo o estar vacío.");
		}

		TamanhoReferencia = array.length;

		for (String fila : array) {

			// 3. Se valida que cada fila tenga el mismo tamaño de la de referencia
			if (fila.trim().length() != TamanhoReferencia) {
				throw new IllegalArgumentException(
						String.format("La matriz no es de %dx%d. Error en la fila '%s'", TamanhoReferencia, TamanhoReferencia, fila));
			}

			// 4. Se valida que cada fila cumpla con contener solamente los caracteres A, C,
			// G, T, a traves de una expresión regular
			String regex = "^[" + CaracteresValidos + "]+$";
			if (!fila.matches(regex)) {
				throw new IllegalArgumentException(String.format("La fila '%s' contiene información no válida.", fila));
			}
		}
	}

	public static void main(String[] args) {

		// ORIGINAL: {"ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"};
		// OB_POSITIVA: {"ATGCGA","CACTGC","TCATGT","CGAAGG","CCCCTA","TCACTG"};

		String[] dna = { "ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG" };
		(new Mutant(dna)).isMutant();

	}
	
	@Autowired
	public boolean isMutant() {
		if (TamanhoReferencia < SecuenciaDeIguales)
			return false;

		int secHorizontal = recorrerHorizontal();
		int secVertical = recorrerVertical();
		int secOblicuasPositivas = recorrerOblicuasPositivas();
		int secOblicuasNegativas = recorrerOblicuasNegativas();

		mostrarResumen(secHorizontal, secVertical, secOblicuasPositivas, secOblicuasNegativas);

		return (secHorizontal + secVertical + secOblicuasPositivas
				+ secOblicuasNegativas >= NumeroDeSecuenciasParaSerMutante);
	}

	@Autowired
	private int recorrerHorizontal() {
		int contadorSecuencias = 0;
		int contadorReps = 0;

		for (int i = 0; i < TamanhoReferencia; i++) {
			String anterior = " ";
			contadorReps = 0;
			for (int j = 0; j < TamanhoReferencia; j++) {
				String actual = Array[i].substring(j, j + 1);

				if (actual.equals(anterior)) {
					contadorReps++;
					if (contadorReps == (SecuenciaDeIguales - 1)) {
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

		for (int i = 0; i < TamanhoReferencia; i++) {
			String anterior = " ";
			contadorReps = 0;
			for (int j = 0; j < TamanhoReferencia; j++) {
				String actual = Array[j].substring(i, i + 1);

				if (actual.equals(anterior)) {
					contadorReps++;
					if (contadorReps == (SecuenciaDeIguales - 1)) {
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

		for (int k = 1; k <= TamanhoReferencia; k++) {
			String anterior = " ";
			contadorReps = 0;
			for (int i = 0; i <= k; i++) {
				for (int j = 0; j <= k; j++) {
					if (j + i == k - 1) {
						String actual = Array[i].substring(j, j + 1);

						// System.out.print("[" + i + "][" + j + "]");
						// System.out.print(" " + anterior + "_" + actual);

						if (actual.equals(anterior)) {
							contadorReps++;
							if (contadorReps == (SecuenciaDeIguales - 1)) {
								contadorSecuencias++;
								contadorReps = 0;
							}
						} else {
							contadorReps = 0;
						}

						// System.out.println(" " + contadorReps + "_" + contadorSecuencias);
						anterior = actual;
					}
				}
			}
		}
		for (int k = TamanhoReferencia + 1; k <= (TamanhoReferencia * 2) - 1; k++) {
			String anterior = " ";
			contadorReps = 0;
			for (int i = 1; i <= TamanhoReferencia - 1; i++) {
				for (int j = TamanhoReferencia - 1; j >= 0; j--) {
					if (j + i == k - 1) {
						String actual = Array[i].substring(j, j + 1);

						// System.out.print("[" + i + "][" + j + "]");
						// System.out.print(" " + anterior + "_" + actual);

						if (actual.equals(anterior)) {
							contadorReps++;
							if (contadorReps == (SecuenciaDeIguales - 1)) {
								contadorSecuencias++;
								contadorReps = 0;
							}
						} else {
							contadorReps = 0;
						}

						// System.out.println(" " + contadorReps + "_" + contadorSecuencias);
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

		for (int k = TamanhoReferencia - 1; k >= 0; k--) {
			String anterior = " ";
			contadorReps = 0;
			for (int i = 0; i <= TamanhoReferencia - 1; i++) {
				for (int j = TamanhoReferencia - 1; j >= 0; j--) {
					if (j - i == k) {
						String actual = Array[i].substring(j, j + 1);

						// System.out.print("[" + i + "][" + j + "]");
						// System.out.print(" " + anterior + "_" + actual);

						if (actual.equals(anterior)) {
							contadorReps++;
							if (contadorReps == (SecuenciaDeIguales - 1)) {
								contadorSecuencias++;
								contadorReps = 0;
							}
						} else {
							contadorReps = 0;
						}

						// System.out.println(" " + contadorReps + "_" + contadorSecuencias);
						anterior = actual;
					}
				}
			}
		}
		for (int k = 1; k <= TamanhoReferencia - 1; k++) {
			String anterior = " ";
			contadorReps = 0;
			for (int i = 1; i <= TamanhoReferencia - 1; i++) {
				for (int j = TamanhoReferencia - 2; j >= 0; j--) {
					if (i - j == k) {
						String actual = Array[i].substring(j, j + 1);

						// System.out.print("[" + i + "][" + j + "]");
						// System.out.print(" " + anterior + "_" + actual);

						if (actual.equals(anterior)) {
							contadorReps++;
							if (contadorReps == (SecuenciaDeIguales - 1)) {
								contadorSecuencias++;
								contadorReps = 0;
							}
						} else {
							contadorReps = 0;
						}

						// System.out.println(" " + contadorReps + "_" + contadorSecuencias);
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
		int secuenciasEncontradas = secHorizontal + secVertical +secOblicuasPositivas + secOblicuasNegativas;

		StringBuilder sbf = new StringBuilder();
		for (String fila : Array) {
			sbf.append("     "+fila+"\r\n");
		}
		System.out.print("Resumen de secuencias continuas para la muestra de ADN: \r\n");
		System.out.print(sbf);
		System.out.print(
				String.format("Total [%s]: Horizontales [%s], Verticales [%s], Oblicuas Positivas [%s], Oblicuas Negativas [%s]",
						secuenciasEncontradas, secHorizontal, secVertical, secOblicuasPositivas, secOblicuasNegativas));
	}
	
	@Autowired
	private void imprimirArray(String[] array) {
		for (String fila : array) {
			System.out.println(fila);
		}
	}
	
}
