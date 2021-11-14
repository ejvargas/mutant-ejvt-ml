package co.com.ejvt.ml.mutant;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfiguration {

	//private String jdbcUrl = "jdbc:postgresql://localhost:5432/mutant?user=postgres&password=pro";
	private String jdbcUrl = System.getenv().get("JDBC_DATABASE_URL") == null ? "jdbc:postgresql://localhost:5432/mutant?user=postgres&password=pro"
			: "jdbc:"+System.getenv().get("JDBC_DATABASE_URL");
	
    @Bean(name="customDataSource")
    @ConfigurationProperties("spring.datasource")
    public DataSource customDataSource() {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(jdbcUrl);
		config.setMaxLifetime(10000);
		config.setMinimumIdle(2);
		return new HikariDataSource(config);

    	
    	//      DataSourceBuilder ds = new DataSource();
//;  
//    	return DataSourceBuilder.create().build();
    }
}
