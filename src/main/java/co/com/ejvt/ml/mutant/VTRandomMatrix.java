package co.com.ejvt.ml.mutant;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class VTRandomMatrix {

	static Logger logger = LoggerFactory.getLogger(VTRandomMatrix.class);
	
	public static void main(String[] args) {
		logger.info(String.format("Random: %s", (new VTRandomMatrix()).getRandomMatrixInJson(6)));
	}
	
	
	public String getRandomMatrixInJson(int tamanho) {
		StringBuilder sb = new StringBuilder();
		for (int i=1; i<=tamanho;i++) {
			sb.append(String.format("\"%s\", ", getRandomSequence(tamanho)));
		}
		String cortado = sb.substring(0, sb.lastIndexOf(","));
		
		return String.format("{ \"dna\" : [ %s ] }", cortado);
	} 
	
	private String getRandomSequence(int tamanho) {
		StringBuilder sb = new StringBuilder();
		for (int i=1; i<=tamanho;i++) {
			sb.append(getRandomLetter());
		}
		return sb.toString();
	} 
	
	private String getRandomLetter() {
		String[] dominio = {"A", "C", "G", "T"};
		Random random = new Random();
		int index = random.nextInt(dominio.length);
		
		return dominio[index];
	} 

}