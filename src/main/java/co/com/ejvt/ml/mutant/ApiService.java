package co.com.ejvt.ml.mutant;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@SpringBootApplication
public class ApiService {

	Logger logger = LoggerFactory.getLogger(ApiService.class);

	public static void main(String[] args) {
		SpringApplication.run(ApiService.class, args);
	}

	@GetMapping(path = "/", produces = { MediaType.APPLICATION_JSON_VALUE })
	public String index() {
		return "";
	}

	/*
	 * Interfaz REST para extender la funcionalidad de la clase MutantAnalyzer.java.
	 * El parámetro es una estructura JSON con una pareja "adn" y como valor, el
	 * array que se le va a pasar a MutantAnalyzer. La respuesta es HTTP 200-OK en
	 * caso de ser mutante y 403-Forbidden en caso de no serlo. Además devuelve en
	 * el cuerpo de la respuesta REST, una estructura JSON con el siguiente formato
	 */
	@ResponseBody
	@PostMapping(value = "/mutant", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> mutant(@RequestBody String bodyJson) {
		try {
			return isMutant(bodyJson);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getBodyError(e.getMessage()));
		}
	}

	/*
	 * Interfaz REST para obtener la cantidad de
	 * analisis que se han hecho con resultados humanos, mutantes y su proporción.
	 * La respuesta es HTTP 200-OK con la siguiente estructura
	 */
	@ResponseBody
	@GetMapping(value = "/stats", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> statistics() {
		return ResponseEntity.status(HttpStatus.OK).body(getStatistics2());
	}

	/*
	 * Interfaz REST que genera una estructura JSON con secuencias aleatorias de
	 * ADN, para que puedan ser utilizadas en el recurso Mutant. El valor por
	 * defecto es 4, pero si se establece el parámetro "tamanho", se devolverá una
	 * matriz de ese tamanho
	 */
	@ResponseBody
	@GetMapping(value = "/random", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> random(
			@RequestParam(required = true, name = "tamanho", defaultValue = "4") int tamanho) {
		return ResponseEntity.status(HttpStatus.OK).body((new VTRandomMatrix()).getRandomMatrixInJson(tamanho));
	}

	/*
	 * Valida el JSON de entrada, ejecuta el analizador de cadenas ADN Mutantes,
	 * persite el resultado y retorna un mensaje JSON para cada respuesta
	 */
	@Autowired
	public ResponseEntity<String> isMutant(String bodyRequest) {
		if (bodyRequest == null || bodyRequest.trim().isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("{ error : \"El cuerpo (body) de la petición no puede estar vacía.\"}");
		} else {
			String[] arrayCorrectamenteFormado = (new Utilidades()).getJsonArray(bodyRequest);

			if (arrayCorrectamenteFormado != null && arrayCorrectamenteFormado.length > 0) {
				String arrayForBD = String.format("[ \"%s\" ]", String.join("\", \"", arrayCorrectamenteFormado));
				try {
					MutantAnalyzer ma = new MutantAnalyzer(arrayCorrectamenteFormado);
					boolean resultadoMa = ma.isMutant();
					(new BDAccess()).guardarAnalisisADN(arrayForBD, resultadoMa);

					if (resultadoMa) {
						return ResponseEntity.status(HttpStatus.OK).body(String.format(
								"{ \"isMutant\" = %s,  \"horizontales\" = %s, \"verticales\" = %s, \"oblicuas_positivas\" = %s, \"oblicuas_negativas\" = %s }",
								true, ma.getCantidadSecuenciasHorizontal(), ma.getCantidadSecuenciasVertical(),
								ma.getCantidadSecuenciasOblicuasPositivas(),
								ma.getCantidadSecuenciasOblicuasNegativas()));

					} else {
						return ResponseEntity.status(HttpStatus.FORBIDDEN).body(String.format(
								"{ \"isMutant\" = %s,  \"horizontales\" = %s, \"verticales\" = %s, \"oblicuas_positivas\" = %s, \"oblicuas_negativas\" = %s }",
								false, ma.getCantidadSecuenciasHorizontal(), ma.getCantidadSecuenciasVertical(),
								ma.getCantidadSecuenciasOblicuasPositivas(),
								ma.getCantidadSecuenciasOblicuasNegativas()));
					}

				} catch (Exception e) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body("{ error : \"Error en el análisis de la cadena de ADN.\"}");
				}
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
						"{ error : \"El cuerpo (body) de la petición no permite obtenención una estructura JSON válida.\"}");
			}
		}
	}

	/*
	 * Ejecuta la consulta de estadisticas de la BD y retorna un mensaje JSON para
	 * cada respuesta
	 */
	@Autowired
	public String getStatistics1() {
		BDAccess consultasBD = new BDAccess();
		int sumaHumans = consultasBD.getSumHumans();
		int sumaMutants = consultasBD.getSumMutants();
		float ratio = (float) sumaMutants / (float) sumaHumans;
		return String.format(Locale.US, "{\"count_mutant_dna\":%d, \"count_human_dna\":%d, \"ratio\":%.2f}",
				sumaMutants, sumaHumans, ratio);
	}

	/* Ejecuta la consulta de estadisticas de la BD y retorna un mensaje JSON para
	 * cada respuesta
	 */
	@Autowired
	public String getStatistics2() {
		BDAccess consultasBD = new BDAccess();
		int[] statisticsJoint = consultasBD.getStatistics();
		float ratio = statisticsJoint[0] != 0 ? (float) statisticsJoint[1] / (float) statisticsJoint[0] : 1;
		return String.format(Locale.US, "{\"count_mutant_dna\":%d, \"count_human_dna\":%d, \"ratio\":%.2f}",
				statisticsJoint[1], statisticsJoint[0], ratio);
	}

	/* Retorna un mensaje JSON en caso de error */
	@Autowired
	public String getBodyError(String error) {
		return String.format("{ error: \"%s\"}", error);
	}

	@Bean
	public String getAs() {
		return "{ Success }";
	}

}
