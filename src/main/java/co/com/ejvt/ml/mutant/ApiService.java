package co.com.ejvt.ml.mutant;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@SpringBootApplication
public class ApiService {

//	@Value("${spring.datasource.url}")
//	private String dbUrl;
//
//	@Autowired
//	private DataSource dataSource;
	
	public static void main(String[] args) {
		SpringApplication.run(ApiService.class, args);
		
		
	}

	@RequestMapping("/")
	String index() {
		return "";
	}

	@ResponseBody
	@RequestMapping(value = "/mutant", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<?> mutant(@RequestBody String bodyJson) {
		//System.out.print("PARXC: "+dbUrl);
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
	
	
	
//	@RequestMapping("/db")
//	  String db(Map<String, Object> model) {
//		System.out.print("PARXC2: "+dbUrl);
//	    try (Connection connection = dataSource.getConnection()) {
//	      Statement stmt = connection.createStatement();
//	      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)");
//	      stmt.executeUpdate("INSERT INTO ticks VALUES (now())");
//	      ResultSet rs = stmt.executeQuery("SELECT tick FROM ticks");
//
//	      ArrayList<String> output = new ArrayList<String>();
//	      while (rs.next()) {
//	        output.add("Read from DB: " + rs.getTimestamp("tick"));
//	      }
//
//	      model.put("records", output);
//	      return "db";
//	    } catch (Exception e) {
//	      model.put("message", e.getMessage());
//	      return "error";
//	    }
//	  }
//
//	  @Bean
//	  public DataSource dataSource() throws SQLException {
//		  System.out.print("PARXC3: "+dbUrl);
//	    if (dbUrl == null || dbUrl.isEmpty()) {
//	      return new HikariDataSource();
//	    } else {
//	      HikariConfig config = new HikariConfig();
//	      config.setJdbcUrl(dbUrl);
//	      return new HikariDataSource(config);
//	    }
//	  }

}
