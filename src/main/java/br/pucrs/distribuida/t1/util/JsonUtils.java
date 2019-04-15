package br.pucrs.distribuida.t1.util;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class JsonUtils {
	
	private static final Gson GSON = new Gson();
	
	public static String toJson(Object object) {
		return GSON.toJson(object);
	}
	
	public static <T> T fromJson(String json, Class<T> classOfT) throws JsonSyntaxException {
		return GSON.fromJson(json, classOfT);
	}
	
}
