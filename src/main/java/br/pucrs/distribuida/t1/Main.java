package br.pucrs.distribuida.t1;

import br.pucrs.distribuida.t1.node.Node;
import br.pucrs.distribuida.t1.node.SuperNode;

public class Main {
	
	public static void main(String[] args) {

		if(args[0].matches("supernode")) {
			SuperNode superNode = new SuperNode(null, null);
			System.out.println(" I'm SUPER NODO = \n" + superNode.toString());
		}
		if(args[0].equalsIgnoreCase("node")) {
			Node node = new Node(null, null);
			System.out.println(" I'm a NODE \n" + node.toString());
		}

	}

}
