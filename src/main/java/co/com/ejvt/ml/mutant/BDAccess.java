package co.com.ejvt.ml.mutant;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

public class BDAccess {

	static Logger logger = LoggerFactory.getLogger(BDAccess.class);

	private String sqlUpsert = "INSERT INTO stats (adnkey, adnsequence, ishuman, ismutant, conteo) VALUES('%s','%s', %s, %s, 1) ON CONFLICT ON CONSTRAINT stats_un_adnkey DO UPDATE SET conteo = stats.conteo + 1;";
	private String sqlSumHumans = "select SUM(conteo) as \"SUMA\" from STATS where ishuman";
	private String sqlSumMutants = "select SUM(conteo) as \"SUMA\" from STATS where ismutant;";
	private String sqlSumJoint = "select (select coalesce(sum(conteo), 0)  from STATS where ishuman) as \"Humans\", (select coalesce(sum(conteo), 0)  from STATS where ismutant) as \"Mutants\";";

	/*
	 * Creacion/Actualización (UPSERT)de Análisis hechos guardarAnalisisADN. Cada
	 * que se ejecuta un análisis, se ejecuta un UPSERT en la tabla, basado en el
	 * hash generado para una cadena única. Si la secuencia de ADN no se ha
	 * registrado, se inserta toda la información y el campo conteo se establece en
	 * 1. Si ya se encuentra registrada en la tabla (comparacion con el cambio
	 * adnkey), se actualiza el campo conteo.
	 */
	@Bean
	public boolean guardarAnalisisADN(String adn, boolean isMutant) {
		String hashedADN;
		try (Connection connection = VTDataSource.getConnection()) {
			try (Statement stmt = connection.createStatement()) {
				hashedADN = (new Utilidades()).stringInSHA(adn);

				stmt.execute(String.format(sqlUpsert, hashedADN, adn, !isMutant, isMutant, 1));
				return true;
			} catch (Exception e) {
				logger.error(String.format("Error guardando el Análisis de ADN la BD: %s", e.getMessage()));
			}
		} catch (Exception e) {
			logger.error(String.format("Error obteniendo la conexión para guardando el Análisis de ADN la BD: %s",
					e.getMessage()));
		}

		return false;
	}

	/*
	 * Consulta a BD para obtener la cantidad de analisis que se han hecho con
	 * resultados humanos, mutantes y su proporción.
	 */
	@Bean
	public int[] getStatistics() {
		int[] statistics = new int[2];

		try (Connection connection = VTDataSource.getConnection()) {
			try (Statement stmt = connection.createStatement()) {
				ResultSet rs = stmt.executeQuery(sqlSumJoint);
				while (rs.next()) {
					statistics[0] = rs.getInt(1);
					statistics[1] = rs.getInt(2);
				}
			} catch (Exception e) {
				logger.error(String.format("Error obteniendo las estadísticas de la BD: %s", e.getMessage()));
			}
		} catch (Exception e) {
			logger.error(
					String.format("Error obteniendo la conexión para las estadísticas de la BD: %s", e.getMessage()));
		}

		return statistics;
	}

	/*
	 * Consulta a BD para obtener la cantidad de analisis que se han hecho con
	 * resultados humanos.
	 */
	@Bean
	public int getSumHumans() {
		int suma = 0;
		try (Connection connection = VTDataSource.getConnection()) {
			try (Statement stmt = connection.createStatement()) {
				ResultSet rs = stmt.executeQuery(sqlSumHumans);

				while (rs.next()) {
					suma = rs.getInt(1);
				}
			} catch (Exception e) {
				logger.error(String.format("Error obteniendo la suma de humanos de la BD: %s", e.getMessage()));
			}
		} catch (Exception e) {
			logger.error(
					String.format("Error obteniendo la conexión para la suma de humanos de la BD: %s", e.getMessage()));
		}

		return suma;
	}

	/* Consulta a BD para obtener la cantidad de analisis que se han hecho con
	 * resultados mutantes.*/
	@Bean
	public int getSumMutants() {
		int suma = 0;
		try (Connection connection = VTDataSource.getConnection()) {
			try (Statement stmt = connection.createStatement()) {
				ResultSet rs = stmt.executeQuery(sqlSumMutants);

				while (rs.next()) {
					suma = rs.getInt(1);
				}
			} catch (Exception e) {
				logger.error(String.format("Error obteniendo la suma de humanos de la BD: %s", e.getMessage()));
			}
		} catch (Exception e) {
			logger.error(String.format("Error obteniendo la conexión para la suma de mutantes de la BD: %s",
					e.getMessage()));
		}

		return suma;
	}

}
