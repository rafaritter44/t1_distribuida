package br.pucrs.distribuida.t1.resource;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ResourceManager {
	
	private static ResourceManager instance;
	
	private MessageDigest md5;
	
	private ResourceManager() {
		try {
			this.md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public static ResourceManager get() {
		if (instance == null) {
			instance = new ResourceManager();
		}
		return instance;
	}
	
	public Resource create(String ip, String fileName) {
		return new Resource(ip, fileName, md5(fileName));
	}
	
	private String md5(String fileName) {
		String hash = getHash(fileName);
		while (hash.length() < 32) {
			hash = "0" + hash;
		}
		return hash;
	}
	
	private synchronized String getHash(String fileName) {
		try {
			return new BigInteger(1, md5.digest(readContent(fileName))).toString(16);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			md5.reset();
		}
	}
	
	public byte[] readContent(String fileName) throws IOException {
		return Files.readAllBytes(Paths.get(fileName));
	}
	
}
