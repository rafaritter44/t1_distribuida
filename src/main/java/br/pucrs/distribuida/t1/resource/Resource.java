package br.pucrs.distribuida.t1.resource;

import br.pucrs.distribuida.t1.util.JsonUtils;

public class Resource {
	
	private String ip;
	private int port;
	private String fileName;
	private String hash;
	
	public Resource(String ip, int port, String fileName, String hash) {
		this.ip = ip;
		this.port = port;
		this.fileName = fileName;
		this.hash = hash;
	}
	
	public boolean contains(String name) {
		return fileName.contains(name);
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public String getHash() {
		return hash;
	}
	
	@Override
	public String toString() {
		return JsonUtils.toJson(this);
	}
	
}
