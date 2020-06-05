package net.euler.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {

	public static String toString(Object obj) {
		try {
			ObjectMapper json = new ObjectMapper();
			return json.writeValueAsString(obj);
		} catch (JsonProcessingException ex) {
			return ex.getMessage();
		}
	}
}
