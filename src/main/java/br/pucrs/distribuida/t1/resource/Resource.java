package br.pucrs.distribuida.t1.resource;

public class Resource {
	
	private String hash;
	private String fileName;
	private String ip;
	
	public boolean contains(String name) {
		return fileName.contains(name);
	}
	
}
