package co.com.ejvt.ml.mutant;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class ApiServiceTest {

	   String bodyJSon4isMutantOKMutant = "{\"dna\":[\"ATGCGA\",\"CAGTGC\",\"TTATGT\",\"AGAAGG\",\"CCCCTA\",\"TCACTG\"]}";
		String bodyJSon4isMutantOKHuman = "{\"dna\":[\"ATGCTA\",\"CAGTGC\",\"TTATGT\",\"AGAAGG\",\"ACCCTA\",\"TCACTG\"]}";
		String bodyJSon4isMutantErrorMalformado = "{\"dna\" [\"ATGCTA\",\"CAGTGC\",\"TTATGT\",\"AGAAGG\",\"ACCCTA\",\"TCACTG\"]}";

		@Test
		void testIsMutantOKMutant() {
			ApiService apiServiceTester = new ApiService();
			assertEquals(ResponseEntity.status(HttpStatus.OK).body(
					"{ \"isMutant\" = true,  \"horizontales\" = 1, \"verticales\" = 1, \"oblicuas_positivas\" = 0, \"oblicuas_negativas\" = 1 }"),
					apiServiceTester.isMutant(bodyJSon4isMutantOKMutant));
		}

		@Test
		void testIsMutantOKHuman() {
			ApiService apiServiceTester = new ApiService();
			assertEquals(ResponseEntity.status(HttpStatus.FORBIDDEN).body(
					"{ \"isMutant\" = false,  \"horizontales\" = 0, \"verticales\" = 0, \"oblicuas_positivas\" = 0, \"oblicuas_negativas\" = 1 }"),
					apiServiceTester.isMutant(bodyJSon4isMutantOKHuman));
		}

		@Test
		void testIsMutantErrorVacio() {
			ApiService apiServiceTester = new ApiService();
			assertEquals(
					ResponseEntity.status(HttpStatus.FORBIDDEN)
							.body("{ error : \"El cuerpo (body) de la petición no puede estar vacía.\"}").getBody(),
					apiServiceTester.isMutant(" ").getBody());
		}

		@Test
		void testIsMutantErrorMalformado() {
			ApiService apiServiceTester = new ApiService();
			assertEquals(ResponseEntity.status(HttpStatus.FORBIDDEN).body(
					"{ error : \"El cuerpo (body) de la petición no permite obtenención una estructura JSON válida.\"}").getBody(),
					apiServiceTester.isMutant(bodyJSon4isMutantErrorMalformado).getBody());
		}
		
		@Test
		void testIsMutantErorGeneral() {
			ApiService apiServiceTester = new ApiService();
			assertTrue(apiServiceTester.mutant("").getStatusCode().is4xxClientError());
		}

		@Test
		void testStatistics() {
			ApiService apiServiceTester = new ApiService();
			assertTrue(apiServiceTester.statistics().getStatusCode().is2xxSuccessful());
		}
		
		@Test
		void testRandom() {
			ApiService apiServiceTester = new ApiService();
			assertTrue(apiServiceTester.random(5).getStatusCode().is2xxSuccessful());
		}
		
		@Test
		void testGetBodyError() {
			ApiService apiServiceTester = new ApiService();
			assertEquals("{ error: \"Error de Test\"}", apiServiceTester.getBodyError("Error de Test"));
		}
		
		@Test
		void testGetAs() {
			ApiService apiServiceTester = new ApiService();
			assertEquals("{ Success }", apiServiceTester.getAs());
		}
		
		@Test
		void testGetStatistics1() {
			ApiService apiServiceTester = new ApiService();
			assertTrue(apiServiceTester.getStatistics1().contains("count_mutant_dna"));
		}
		
		String bodyJSon4isMutantOK = "{\"dna\":[\"ATGCGA\",\"CAGTGC\",\"TTATGT\",\"AGAAGG\",\"CCCCTA\",\"TCACTG\"]}";
		String bodyJSon4isMutantError = "{\"dna\" [\"ATGCGA\",\"CAGTGC\",\"TTATGT\",\"AGAAGG\",\"CCCCTA\",\"TCACTG\"]}";
		
		@Test
		void testGetJsonArray() {
			Utilidades utilidadesTester = new Utilidades();
			assertArrayEquals(utilidadesTester.getJsonArray(bodyJSon4isMutantOK),
					new String[] { "ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG" });
		}

		@Test
		void testGetJsonArrayException() {
			Utilidades utilidadesTester = new Utilidades();
			String[] arrayADN = {};
			assertArrayEquals(utilidadesTester.getJsonArray(bodyJSon4isMutantError), arrayADN);
		}

		@Test
		void testStringInSHA() {
			Utilidades utilidadesTester = new Utilidades();
			assertEquals(utilidadesTester
					.stringInSHA("[ \"GAGCGA\", \"TCCAAT\", \"TATACT\", \"ATAGGC\", \"CATTAC\", \"AGAAGC\" ]"), "cb94c68e28af1bef14dfd82c2b32028661d2e9b99885e86ed545a4f2c86ec9fb");
		}
		
		
		
		
		@Test
		void testBDAccessMain() {
			BDAccess.main(new String[] {});
		}
		
		
		String[] stringArrayOK = { "ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG" };
		@Test
		void testMutantAnalyzer() {
			MutantAnalyzer tester = new MutantAnalyzer(stringArrayOK);
			assertTrue(tester.isMutant());
		}
		
}
