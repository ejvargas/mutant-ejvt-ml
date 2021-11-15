package co.com.ejvt.ml.mutant;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class Utilidades {

	Logger logger = LoggerFactory.getLogger(Utilidades.class);

	/*
	 * Método utilitario con el objetivo de extraer el array de strings de la
	 * estructura JSON
	 */
	@Bean
	public String[] getJsonArray(String bodyRest) {
		String[] arrayADN = {};
		try {

			JsonObject json = new Gson().fromJson(bodyRest, JsonObject.class);
			JsonArray dnaItem = json.get("dna").getAsJsonArray();

			Iterator<JsonElement> itExtract = dnaItem.iterator();
			List<String> cads = new ArrayList<>();

			while (itExtract.hasNext()) {
				cads.add(itExtract.next().getAsJsonPrimitive().toString().replace("\"", ""));
			}

			arrayADN = cads.stream().toArray(String[]::new);

		} catch (JsonSyntaxException jse) {
			logger.warn(String.format("El cuerpo del mensaje no tiene una estructura válida: %s", jse.getMessage()));
		}

		return arrayADN;
	}

	/*
	 * Método utilitario con el objetivo de calcular el Hash de una cadena en SHA256
	 */
	@Bean
	public String stringInSHA(String cadena) {
		return Hashing.sha256().hashString(cadena, StandardCharsets.UTF_8).toString();
	}

}
