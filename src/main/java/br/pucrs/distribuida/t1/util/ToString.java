package br.pucrs.distribuida.t1.util;

import com.google.gson.Gson;

public class ToString {
	
	private static final Gson GSON = new Gson();
	
	public static String from(Object object) {
		return GSON.toJson(object);
	}
	
}
