package co.com.ejvt.ml.mutant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


@RestController
@SpringBootApplication
public class ApiService {

	public static void main(String[] args) {
		SpringApplication.run(ApiService.class, args);
	}

	@RequestMapping("/")
	String index() {
		return "";
	}

	@ApiResponses(value = { @ApiResponse(code = 200, message = "Ok"), @ApiResponse(code = 403, message = "Forbidden") })
	@ResponseBody
	@RequestMapping(value = "/mutant", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> mutant(@RequestBody String bodyJson) {

		if (bodyJson == null || bodyJson.isEmpty())
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		
		try {
			if (isMutant(bodyJson)) {
				return ResponseEntity.status(HttpStatus.OK).body(null);
			}else {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getBodyError(e.getMessage()));
		}
	}

	@ApiResponses(value = { @ApiResponse(code = 200, message = "Ok") })
	@ResponseBody
	@RequestMapping(value = "/stats", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> statistics() {
		return ResponseEntity.status(HttpStatus.OK).body(getStatistics());
	}

	@Autowired
	public boolean isMutant(String bodyJson) {
		String[] array = (new Json()).getJsonArray(bodyJson);
		if (array==null || array.length==0) 
			return false;
		else
			return (new Mutant(array)).isMutant();
	}

	public String getStatistics() {
		return "{ Success }";
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
