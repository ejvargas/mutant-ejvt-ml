package co.com.ejvt.ml.mutant;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

//@Configuration
public class VTDataSource {

	static Logger logger = LoggerFactory.getLogger(VTDataSource.class);

	private static String jdbcUrl = System.getenv().get("JDBC_DATABASE_URL") == null
			? "jdbc:postgresql://localhost:5432/mutant?user=postgres&password=pro"
			: System.getenv().get("JDBC_DATABASE_URL");

	private static HikariConfig config = new HikariConfig();
	private static HikariDataSource ds;

	static {
		config.setJdbcUrl(jdbcUrl);
		config.setMaxLifetime(1000);
		config.setMinimumIdle(5);
		ds = new HikariDataSource(config);
	}

	private VTDataSource() {
	}

	@Bean(name = "getConnection")
	// @ConfigurationProperties("spring.datasource")
	public static Connection getConnection() throws SQLException {
		logger.info(String.format("Using DS-Hikari-PoolName: %s", ds.getPoolName()));
		return ds.getConnection();
	}

}
