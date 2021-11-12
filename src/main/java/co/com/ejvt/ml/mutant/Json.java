package co.com.ejvt.ml.mutant;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.context.annotation.Bean;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class Json {
	
	@Bean
	public String[] getJsonArray(String bodyRest) {
		String[] arrayADN = {};
		try {

			JsonObject json = new Gson().fromJson(bodyRest, JsonObject.class);
			JsonArray dnaItem = json.get("dna").getAsJsonArray();

			Iterator<JsonElement> itExtract = dnaItem.iterator();
			List<String> cads = new ArrayList<String>();

			while (itExtract.hasNext()) {
				cads.add(itExtract.next().getAsJsonPrimitive().toString().replace("\"", ""));
			}

			arrayADN = cads.stream().toArray(String[]::new);

		} catch (JsonSyntaxException jse) {
			System.out.println("El cuerpo del mensaje no tiene una estructura válida: " + jse.getMessage());
		}

		return arrayADN;
	}

}
