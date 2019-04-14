package br.pucrs.distribuida.t1.util;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class JsonUtils {
	
	private static final Gson GSON = new Gson();
	
	public static String toJson(Object object) {
		return GSON.toJson(object);
	}
	
	public static <T> T fromJson(String json, Type type) throws JsonSyntaxException {
		return GSON.fromJson(json, type);
	}
	
}
