/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package co.com.ejvt.ml.mutant;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;


//@RestController
//@SpringBootApplication
public class Main {

  @Value("${spring.datasource.url}")
  private String dbUrl;

  @Autowired
  private DataSource dataSource;

  public static void main(String[] args) throws Exception {
    //SpringApplication.run(Main.class, args);
  }

//  @RequestMapping("/")
//  String index() {
//    return "";
//  }
//
//  @ApiResponses(value = {
//    @ApiResponse(code = 200, message = "Ok"),
//    @ApiResponse(code = 403, message = "Forbidden")})
//  @ResponseBody
//  @RequestMapping(value="/mutant", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
//  public ResponseEntity<?> mutant(@RequestBody String body) {
//	  if (isMutant(body))
//	  	return ResponseEntity.status(HttpStatus.OK).body(null);
//	  else
//		  return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
//  }
//  
//  @ApiResponses(value = {
//    @ApiResponse(code = 200, message = "Ok")})
//  @ResponseBody
//  @RequestMapping(value="/stats", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
//  public ResponseEntity<String> statistics() {
//  	return ResponseEntity.status(HttpStatus.OK).body(getStatistics());
//  }
//
//  @Bean
//  public boolean isMutant(String dna) {
//   
//      return true;
//  }
//  
//  @Bean
//  public String getStatistics() {
//   
//      return "{ Success }";
//  }

  @RequestMapping("/db")
  String db(Map<String, Object> model) {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)");
      stmt.executeUpdate("INSERT INTO ticks VALUES (now())");
      ResultSet rs = stmt.executeQuery("SELECT tick FROM ticks");

      ArrayList<String> output = new ArrayList<String>();
      while (rs.next()) {
        output.add("Read from DB: " + rs.getTimestamp("tick"));
      }

      model.put("records", output);
      return "db";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @Bean
  public DataSource dataSource() throws SQLException {
    if (dbUrl == null || dbUrl.isEmpty()) {
      return new HikariDataSource();
    } else {
      HikariConfig config = new HikariConfig();
      config.setJdbcUrl(dbUrl);
      return new HikariDataSource(config);
    }
  }
  
}
