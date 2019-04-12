package br.pucrs.distribuida.t1.node;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import br.pucrs.distribuida.t1.resource.Resource;
import br.pucrs.distribuida.t1.util.ToString;

public class SuperNode {
	
	private String ip;
	private String multicastIp;
	private List<Node> nodes;

	public SuperNode(String ip, String multicastIp, List<Node> nodes) {
		this.ip = ip;
		this.multicastIp = multicastIp;
		this.nodes = nodes;
	}
	
	public List<Resource> find(String fileName) {
		return nodes.stream()
				.map(node -> node.contains(fileName))
				.flatMap(List::stream)
				.collect(Collectors.toList());
	}
	
	public void removeDeadNodesPeriodically() {
		Executors.newSingleThreadScheduledExecutor()
				.scheduleWithFixedDelay(
						this::removeDeadNodes,
						Node.ALIVE_NOTIFICATION_TIME,
						Node.ALIVE_NOTIFICATION_TIME,
						TimeUnit.SECONDS);
	}
	
	private synchronized void removeDeadNodes() {
		nodes.removeIf(node -> !node.isAlive());
	}

	@Override
	public String toString() {
		return ToString.from(this);
	}

}
