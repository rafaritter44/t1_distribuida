package br.pucrs.distribuida.t1.resource;

import br.pucrs.distribuida.t1.util.ToString;

public class Resource {
	
	private String ip;
	private String fileName;
	private String hash;
	
	public Resource(String ip, String fileName, String hash) {
		this.ip = ip;
		this.fileName = fileName;
		this.hash = hash;
	}
	
	public boolean contains(String name) {
		return fileName.contains(name);
	}
	
	@Override
	public String toString() {
		return ToString.from(this);
	}
	
}
