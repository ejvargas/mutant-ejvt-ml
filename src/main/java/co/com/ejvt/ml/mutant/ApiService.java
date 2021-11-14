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

	@ResponseBody
	@PostMapping(value = "/mutant", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> mutant(@RequestBody String bodyJson) {
		try {
			return isMutant(bodyJson);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getBodyError(e.getMessage()));
		}
	}

	@ResponseBody
	@GetMapping(value = "/stats", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> statistics() {
		return ResponseEntity.status(HttpStatus.OK).body(getStatistics2());
	}
	
	@ResponseBody
	@GetMapping(value = "/random", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> random(@RequestParam(required = true, name = "tamanho", defaultValue = "4") int tamanho) {
		return ResponseEntity.status(HttpStatus.OK).body((new VTRandomMatrix()).getRandomMatrixInJson(tamanho));
	}

	@Autowired
	public ResponseEntity isMutant(String bodyRequest) throws Exception {
		if (bodyRequest == null || bodyRequest.trim().isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{ error : \"El cuerpo (body) de la petición no puede estar vacía.\"}");
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
								true, ma.secHorizontal, ma.secVertical, ma.secOblicuasPositivas,
								ma.secOblicuasNegativas));
						
					} else {
						return ResponseEntity.status(HttpStatus.FORBIDDEN).body(String.format(
								"{ \"isMutant\" = %s,  \"horizontales\" = %s, \"verticales\" = %s, \"oblicuas_positivas\" = %s, \"oblicuas_negativas\" = %s }",
								false, ma.secHorizontal, ma.secVertical, ma.secOblicuasPositivas,
								ma.secOblicuasNegativas));
					}
					
				} catch (Exception e) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{ error : \"Error en el análisis de la cadena de ADN.\"}");
				}
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{ error : \"El cuerpo (body) de la petición no permite obtenención una estructura JSON válida.\"}");
			}
		}
	}

	public String getStatistics1() {
		BDAccess consultasBD = new BDAccess();
		int sumaHumans = consultasBD.getSumHumans();
		int sumaMutants = consultasBD.getSumMutants();
		float ratio = (float) sumaMutants / (float) sumaHumans;
		return String.format(Locale.US, "{\"count_mutant_dna\":%d, \"count_human_dna\":%d, \"ratio\":%.2f}",
				sumaMutants, sumaHumans, ratio);
	}

	public String getStatistics2() {
		BDAccess consultasBD = new BDAccess();
		int[] statisticsJoint = consultasBD.getStatistics();
		float ratio = statisticsJoint[0]!=0 ?  (float) statisticsJoint[1] / (float) statisticsJoint[0] : 1;
		return String.format(Locale.US, "{\"count_mutant_dna\":%d, \"count_human_dna\":%d, \"ratio\":%.2f}",
				statisticsJoint[1], statisticsJoint[0], ratio);
	}

	@Autowired
	public String getBodyError(String error) {
		return String.format("{ error: \"%s\"}", error);
	}

	@Bean
	public String getAs() {
		return "{ Success }";
	}

}
