package co.com.ejvt.ml.mutant;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class Utilidades {
	
	Logger logger = LoggerFactory.getLogger(Utilidades.class);

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

	@Bean
	public String stringInSHA(String cadena) throws NoSuchAlgorithmException {
		return Hashing.sha256().hashString(cadena, StandardCharsets.UTF_8).toString();
	}

}
