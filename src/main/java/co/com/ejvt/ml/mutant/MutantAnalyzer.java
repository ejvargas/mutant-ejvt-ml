package co.com.ejvt.ml.mutant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class MutantAnalyzer {

	Logger logger = LoggerFactory.getLogger(MutantAnalyzer.class);
	org.slf4j.Marker marker;

	public int getCantidadSecuenciasHorizontal() {
		return cantidadSecuenciasHorizontal;
	}

	public int getCantidadSecuenciasVertical() {
		return cantidadSecuenciasVertical;
	}

	public int getCantidadSecuenciasOblicuasPositivas() {
		return cantidadSecuenciasOblicuasPositivas;
	}

	public int getCantidadSecuenciasOblicuasNegativas() {
		return cantidadSecuenciasOblicuasNegativas;
	}

	private int cantidadSecuenciasHorizontal;
	private int cantidadSecuenciasVertical;
	private int cantidadSecuenciasOblicuasPositivas;
	private int cantidadSecuenciasOblicuasNegativas;

	int tamanhoReferencia;
	int secuenciaDeIguales = System.getenv().get("SECUENCIA_DE_IGUALES") == null ? 4
			: Integer.parseInt(System.getenv().get("SECUENCIA_DE_IGUALES"));
	int numeroDeSecuenciasParaSerMutante = System.getenv().get("SECUENCIA_PARA_SER_MUTANTE") == null ? 2
			: Integer.parseInt(System.getenv().get("SECUENCIA_PARA_SER_MUTANTE"));
	String caracteresValidos = System.getenv().get("CARACTERES_VALIDOS") == null ? "ACGT"
			: System.getenv().get("CARACTERES_VALIDOS");

	String[] arrayWorking = null;

	public static void main(String[] args) {
		String[] dna = { "CTGCGA", "CAGTAC", "TTGAGT", "AGAGGG", "CCTCGA", "TCACTG" };
		(new MutantAnalyzer(dna)).isMutant();
	}

	@Autowired
	public MutantAnalyzer(String[] array) {
		paramValidations(array);
		arrayWorking = array;
	}

	/*
	 * 
	 * Antes de hacer los recorridos se valida que la matriz esté correctamente
	 * formada:
	 * 
	 * 1. No esté vacía o nula 2. Filas (# de cadenas dentro del array) y columnas
	 * (# de caracteres dentro de cada cadena dentro del array) sean la misma
	 * cantidad. 3. Se toma como referencia el tamaño de la primera cadena del
	 * array. Si es de 4 caracteres, se asume que la matriz es de 4x4.
	 * 
	 */
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

	/*
	 * Metodo para lo que hace es recibir el parámetro (Array) o tomarlo del
	 * constructor de la clase (para el caso en que sea invocado desde el API); hace
	 * cada uno de los recorridos, suma lo que devolvió cada uno de esos recorridos
	 * y basado en el número mínimo de secuencias para ser mutante (2) en la
	 * varibale (numeroDeSecuenciasParaSerMutante) responde falso o verdadero. Por
	 * último imprime por consola la matriz y el resumen de lo que encontró en cada
	 * recorrido.
	 */
	@Autowired
	public boolean isMutant() {
		if (tamanhoReferencia < secuenciaDeIguales)
			return false;

		cantidadSecuenciasHorizontal = recorrerHorizontal();
		cantidadSecuenciasVertical = recorrerVertical();
		cantidadSecuenciasOblicuasPositivas = recorrerOblicuasPositivas();
		cantidadSecuenciasOblicuasNegativas = recorrerOblicuasNegativas();

		mostrarResumen(cantidadSecuenciasHorizontal, cantidadSecuenciasVertical, cantidadSecuenciasOblicuasPositivas,
				cantidadSecuenciasOblicuasNegativas);

		return (cantidadSecuenciasHorizontal + cantidadSecuenciasVertical + cantidadSecuenciasOblicuasPositivas
				+ cantidadSecuenciasOblicuasNegativas >= numeroDeSecuenciasParaSerMutante);
	}

	@Autowired
	public boolean isMutant(String[] array) {
		this.paramValidations(array);
		return this.isMutant();
	}

	/*
	 * Esta funcion tiene como objetivo recorrer el array parámetro de forma
	 * HORIZONTAL, toma la cadena de la posicion fila y la subcadena de la posicion
	 * columna y en su recorrido va guardando su anterior posición; cuando encuentra
	 * 4 de ellas (secuenciaDeIguales), la acumula como una secuencia encontrada
	 * (contadorSecuencia). Cuando termina el recorrido, devuelve el conteo de
	 * secuencias encontradas
	 */
	@Autowired
	private int recorrerHorizontal() {
		int contadorSecuenciasH = 0;
		int contadorRepsH = 0;

		for (int filaH = 0; filaH < tamanhoReferencia; filaH++) {
			String anteriorH = " ";
			contadorRepsH = 0;
			for (int columnaH = 0; columnaH < tamanhoReferencia; columnaH++) {
				String actualH = arrayWorking[filaH].substring(columnaH, columnaH + 1);

				if (actualH.equals(anteriorH)) {
					contadorRepsH++;
					if (contadorRepsH == (secuenciaDeIguales - 1)) {
						contadorSecuenciasH++;
						contadorRepsH = 0;
					}
				} else {
					contadorRepsH = 0;
				}

				anteriorH = actualH;
			}
		}

		return contadorSecuenciasH;
	}

	/*
	 * Esta funcion tiene como objetivo recorrer el array parámetro de forma
	 * VERTICAL, toma la cadena de la posicion fila y la subcadena de la posicion
	 * columna y en su recorrido va guardando su anterior posición; cuando encuentra
	 * 4 de ellas (secuenciaDeIguales), la acumula como una secuencia encontrada
	 * (contadorSecuencia). Cuando termina el recorrido, devuelve el conteo de
	 * secuencias encontradas
	 */
	@Autowired
	private int recorrerVertical() {
		int contadorSecuenciasV = 0;
		int contadorRepsV = 0;

		for (int filaV = 0; filaV < tamanhoReferencia; filaV++) {
			String anteriorV = " ";
			contadorRepsV = 0;
			for (int columnaV = 0; columnaV < tamanhoReferencia; columnaV++) {
				String actualV = arrayWorking[columnaV].substring(filaV, filaV + 1);

				if (actualV.equals(anteriorV)) {
					contadorRepsV++;
					if (contadorRepsV == (secuenciaDeIguales - 1)) {
						contadorSecuenciasV++;
						contadorRepsV = 0;
					}
				} else {
					contadorRepsV = 0;
				}

				anteriorV = actualV;
			}
		}
		return contadorSecuenciasV;

	}

	/*
	 * Esta funcion tiene como objetivo recorrer el array parámetro de forma OBLICUA
	 * PARA PENDIENTES POSITIVAS, toma la cadena de la posicion fila y la subcadena
	 * de la posicion columna y en su recorrido va guardando su anterior posición;
	 * cuando encuentra 4 de ellas (secuenciaDeIguales), la acumula como una
	 * secuencia encontrada (contadorSecuencia). Cuando termina el recorrido,
	 * devuelve el conteo de secuencias encontradas
	 */
	@Autowired
	private int recorrerOblicuasPositivas() {
		int contadorSecuenciasOP = 0;
		int contadorRepsOP = 0;

		for (int pendienteOPDiagonalNorte = 1; pendienteOPDiagonalNorte <= tamanhoReferencia; pendienteOPDiagonalNorte++) {
			String anteriorOPDN = " ";
			contadorRepsOP = 0;
			for (int filaOPDN = 0; filaOPDN <= pendienteOPDiagonalNorte; filaOPDN++) {
				for (int columnaOPDN = 0; columnaOPDN <= pendienteOPDiagonalNorte; columnaOPDN++) {
					if (columnaOPDN + filaOPDN == pendienteOPDiagonalNorte - 1) {
						String actualOPDN = arrayWorking[filaOPDN].substring(columnaOPDN, columnaOPDN + 1);

						if (actualOPDN.equals(anteriorOPDN)) {
							contadorRepsOP++;
							if (contadorRepsOP == (secuenciaDeIguales - 1)) {
								contadorSecuenciasOP++;
								contadorRepsOP = 0;
							}
						} else {
							contadorRepsOP = 0;
						}

						anteriorOPDN = actualOPDN;
					}
				}
			}
		}
		for (int pendienteOPDS = tamanhoReferencia + 1; pendienteOPDS <= (tamanhoReferencia * 2) - 1; pendienteOPDS++) {
			String anteriorOPDS = " ";
			contadorRepsOP = 0;
			for (int filaOPDS = 1; filaOPDS <= tamanhoReferencia - 1; filaOPDS++) {
				for (int columnaOPDS = tamanhoReferencia - 1; columnaOPDS >= 0; columnaOPDS--) {
					if (columnaOPDS + filaOPDS == pendienteOPDS - 1) {
						String actualOPDS = arrayWorking[filaOPDS].substring(columnaOPDS, columnaOPDS + 1);

						if (actualOPDS.equals(anteriorOPDS)) {
							contadorRepsOP++;
							if (contadorRepsOP == (secuenciaDeIguales - 1)) {
								contadorSecuenciasOP++;
								contadorRepsOP = 0;
							}
						} else {
							contadorRepsOP = 0;
						}

						anteriorOPDS = actualOPDS;
					}
				}
			}
		}

		return contadorSecuenciasOP;

	}

	/*
	 * Esta funcion tiene como objetivo recorrer el array parámetro de forma OBLICUA
	 * PARA PENDIENTES NEGATIVAS, toma la cadena de la posicion fila y la subcadena
	 * de la posicion columna y en su recorrido va guardando su anterior posición;
	 * cuando encuentra 4 de ellas (secuenciaDeIguales), la acumula como una
	 * secuencia encontrada (contadorSecuencia). Cuando termina el recorrido,
	 * devuelve el conteo de secuencias encontradas
	 */
	@Autowired
	private int recorrerOblicuasNegativas() {
		int contadorSecuenciasON = 0;
		int contadorRepsON = 0;

		for (int pendienteON = tamanhoReferencia - 1; pendienteON >= 0; pendienteON--) {
			String anteriorON = " ";
			contadorRepsON = 0;
			for (int filaON = 0; filaON <= tamanhoReferencia - 1; filaON++) {
				for (int columnaON = tamanhoReferencia - 1; columnaON >= 0; columnaON--) {
					if (columnaON - filaON == pendienteON) {
						String actualON = arrayWorking[filaON].substring(columnaON, columnaON + 1);

						if (actualON.equals(anteriorON)) {
							contadorRepsON++;
							if (contadorRepsON == (secuenciaDeIguales - 1)) {
								contadorSecuenciasON++;
								contadorRepsON = 0;
							}
						} else {
							contadorRepsON = 0;
						}

						anteriorON = actualON;
					}
				}
			}
		}
		for (int pendienteON = 1; pendienteON <= tamanhoReferencia - 1; pendienteON++) {
			String anteriorON = " ";
			contadorRepsON = 0;
			for (int filaON = 1; filaON <= tamanhoReferencia - 1; filaON++) {
				for (int columnaON = tamanhoReferencia - 2; columnaON >= 0; columnaON--) {
					if (filaON - columnaON == pendienteON) {
						String actualON = arrayWorking[filaON].substring(columnaON, columnaON + 1);

						if (actualON.equals(anteriorON)) {
							contadorRepsON++;
							if (contadorRepsON == (secuenciaDeIguales - 1)) {
								contadorSecuenciasON++;
								contadorRepsON = 0;
							}
						} else {
							contadorRepsON = 0;
						}

						anteriorON = actualON;
					}
				}
			}
		}

		return contadorSecuenciasON;
	}

	/* Este método imprime el resumen de una ejecución de análisis de ADN mutante*/
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
				"%sTotal [%s]: Horizontales [%s], Verticales [%s], Oblicuas Positivas [%s], Oblicuas Negativas [%s]",
				"\r\n", secuenciasEncontradas, secHorizontal, secVertical, secOblicuasPositivas, secOblicuasNegativas));

		if (!sbf.toString().isEmpty()) {
			String resumen = String.format("Resumen: %s", sbf.toString());
			logger.info(resumen);
		}

	}

}
