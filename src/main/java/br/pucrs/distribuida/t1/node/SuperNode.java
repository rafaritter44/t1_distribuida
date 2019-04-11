package br.pucrs.distribuida.t1.node;

import java.util.List;
import java.util.stream.Collectors;

import br.pucrs.distribuida.t1.resource.Resource;

public class SuperNode {
	
	private String multicastIp;
	private List<Node> nodes;

	public SuperNode(String multicastIp, List<Node> nodes) {
		this.multicastIp = multicastIp;
		this.nodes = nodes;
	}
	
	public List<Resource> find(String fileName) {
		return nodes.stream()
				.map(node -> node.contains(fileName))
				.flatMap(List::stream)
				.collect(Collectors.toList());
	}

	@Override
	public String toString() {
		return "SuperNode{" +
				"multicastIp='" + multicastIp + '\'' +
				", nodes=" + nodes +
				'}';
	}
}
