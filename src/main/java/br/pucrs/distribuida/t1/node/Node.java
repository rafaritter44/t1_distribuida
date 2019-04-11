package br.pucrs.distribuida.t1.node;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import br.pucrs.distribuida.t1.resource.Resource;

public class Node {
	
	private static final long ALIVE_NOTIFICATION_TIME = 5;
	
	private List<Resource> resources;
	private Instant lastNotification;

	public Node(List<Resource> resources, Instant lastNotification) {
		this.resources = resources;
		this.lastNotification = lastNotification;
	}

	public List<Resource> contains(String fileName) {
		return resources.stream()
				.filter(resource -> resource.contains(fileName))
				.collect(Collectors.toList());
	}
	
	public boolean isAlive() {
		return Duration.between(lastNotification, Instant.now()).getSeconds() < ALIVE_NOTIFICATION_TIME;
	}

	@Override
	public String toString() {
		return "Node{" +
				"resources=" + resources +
				", lastNotification=" + lastNotification +
				'}';
	}
}
