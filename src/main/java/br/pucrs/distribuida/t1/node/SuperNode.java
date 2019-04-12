package br.pucrs.distribuida.t1.node;

import java.util.List;
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
	
	public void removeDeadNodes() {
		nodes.removeIf(node -> !node.isAlive());
	}

	@Override
	public String toString() {
		return ToString.from(this);
	}
}
