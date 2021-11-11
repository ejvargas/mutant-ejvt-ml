package co.com.ejvt.ml.mutant;

public class Mutant {

	String CaracteresValidos = "ACGT";
	int TamanhoReferencia = Integer.MIN_VALUE;
	String[] Array = null;
	
	
	public Mutant(String[] array) {
		//TAMANHO_REFERENCIA = Integer.parseInt(System.getenv().get("TAMANHO_MATRIZ"));
		TamanhoReferencia = array.length;
		CaracteresValidos = "ACGT"; //System.getenv().get("CARACTERES_VALIDOS")
		Array = array;
	}

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String[] dna = { "GTGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG" };
		
		boolean resultado = (new Mutant(dna)).isMutant();
		
		// Guarda el resultado en BD
		guardarEnBD();
	}

	
	
	
	


	public boolean isMutant() {
		if (!isValid())
			return false;
		
		
		this.recorrerHorizontal();
		
		return false;
	}

	private boolean isValid() {
		
		// 1. Se valida que el numero de filas del Array, sea el mismo de las 
		//		columnas (definida por el tamaño de la primera fila)
		if (Array.length!=TamanhoReferencia) {
			System.out.print("Error Validación 1"); 
			return false;
		}
		
		
		for (String fila : Array) {
			
			// 2. Se valida que cada fila tenga el mismo tamaño de la de referencia
			if (fila.trim().length()!=TamanhoReferencia){
				System.out.print("Error Validación 2"); 
				return false;
			}
			
			// 3. Se valida que cada fila cumpla con contener solamente los caracteres A, C, G, T
			if (!fila.matches("^[ACGT]+$")){
				System.out.print("Error Validación 3"); 
				return false;
			}
		}
		
		return true;
	}

	private void recorrerHorizontal() {
		for (int i = 0; i < TamanhoReferencia; i++) {
			for (int j = 0; j < TamanhoReferencia; j++) {
				System.out.print("[" + i + "][" + j + "] ");
			}
		}
	}
	
	private void recorrerVertical() {
		
	}
	
	private void recorrerOblicuaPositiva() {
		
	}

	private void recorrerOblicuaNegativa() {
		
	}
	
	private static void guardarEnBD() {
		// TODO Auto-generated method stub
		
	}
}
