package br.pucrs.distribuida.t1.node;

import java.util.List;

public class SuperNode {
	
	private String multicastIp;
	private List<Node> nodes;

	public SuperNode(String multicastIp, List<Node> nodes) {
		this.multicastIp = multicastIp;
		this.nodes = nodes;
	}

	@Override
	public String toString() {
		return "SuperNode{" +
				"multicastIp='" + multicastIp + '\'' +
				", nodes=" + nodes +
				'}';
	}
}
