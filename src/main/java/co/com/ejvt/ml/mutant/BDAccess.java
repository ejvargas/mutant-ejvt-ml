package co.com.ejvt.ml.mutant;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class BDAccess {

	static Logger logger = LoggerFactory.getLogger(BDAccess.class);

	@Value("${spring.datasource.url}")
	private String jdbcUrl;// = "jdbc:postgresql://localhost:5432/mutant?user=postgres&password=pro";

	@Value("${sql.query.upsert}")
	private String sqlUpsert = "INSERT INTO stats (adnkey, adnsequence, ishuman, ismutant, conteo) VALUES('%s','%s', %s, %s, 1) ON CONFLICT ON CONSTRAINT stats_un_adnkey DO UPDATE SET conteo = stats.conteo + 1;";

	private String sqlSumHumans = "select SUM(conteo) as \"SUMA\" from STATS where ishuman";
	private String sqlSumMutants = "select SUM(conteo) as \"SUMA\" from STATS where ismutant;";
	private String sqlSumJoint = "select (select SUM(conteo) from STATS where ishuman) as \"Humans\", (select SUM(conteo) from STATS where ismutant) as \"Mutants\";";

	@Autowired
	private DataSource dataSource;

	public static void main(String[] args) {
		int[] dasda = (new BDAccess()).getStatistics();
		logger.info("HUM: "+dasda[0]);
		logger.info("MUT: "+dasda[1]);
	}

	@Bean
	public boolean guardarAnalisisADN(String adn, boolean isMutant) {
		String hashedADN;

		try {
			Connection connection = (new DataSourceConfiguration()).customDataSource().getConnection();
			Statement stmt = connection.createStatement();
			hashedADN = (new Utilidades()).stringInSHA(adn);

			stmt.execute(String.format(sqlUpsert, hashedADN, adn, !isMutant, isMutant, 1));

			stmt.close();
			connection.close();
			connection=null;
			return true;
		} catch (NoSuchAlgorithmException e) {
			logger.error(String.format("Error calculando Hash: %s", e.getMessage()));
		} catch (Exception e) {
			logger.error(String.format("Error actualizando la BD: %s", e.getMessage()));
		}finally {}

		return false;
	}

	@Bean
	public int[] getStatistics() {
		int[] statistics = new int[2];
		try {
			Connection connection = (new DataSourceConfiguration()).customDataSource().getConnection();
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(sqlSumJoint);

			while (rs.next()) {
				statistics[0] = rs.getInt(1);
				statistics[1] = rs.getInt(2);
			}
			stmt.close();
			connection.close();
			connection=null;
		} catch (Exception e) {
			logger.error(String.format("Error actualizando la BD: %s", e.getMessage()));
		}

		return statistics;
	}
	
	@Bean
	public int getSumHumans() {
		int suma = 0;
		try {
			Connection connection = (new DataSourceConfiguration()).customDataSource().getConnection();
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(sqlSumHumans);

			while (rs.next()) {
				suma = rs.getInt(1);
			}
			stmt.close();
			connection.close();
			connection=null;
		} catch (Exception e) {
			logger.error(String.format("Error actualizando la BD: %s", e.getMessage()));
		}

		return suma;
	}

	@Bean
	public int getSumMutants() {
		int suma = 0;
		try {
			Connection connection = (new DataSourceConfiguration()).customDataSource().getConnection();
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(sqlSumMutants);

			while (rs.next()) {
				suma = rs.getInt(1);
			}
			stmt.close();
			connection.close();
			connection=null;
		} catch (Exception e) {
			logger.error(String.format("Error actualizando la BD: %s", e.getMessage()));
		}

		return suma;
	}

//	public DataSource dataSource() throws SQLException {
//		if (jdbcUrl == null || jdbcUrl.isEmpty()) {
//			return new HikariDataSource();
//		} else {
//			HikariConfig config = new HikariConfig();
//			config.setJdbcUrl(jdbcUrl);
//			config.setMaxLifetime(1000);
//			config.setMinimumIdle(2);
//			return new HikariDataSource(config);
//		}
//	}

}
