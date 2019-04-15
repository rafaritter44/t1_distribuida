package br.pucrs.distribuida.t1;

import br.pucrs.distribuida.t1.node.Node;
import br.pucrs.distribuida.t1.node.SuperNode;

public class Main {
	
	public static void main(String[] args) {

		if (args.length != 1) {
			System.out.println("Usage: java Main <node type>");
			System.out.println(" 'node type' :");
			System.out.println(" 'supernode' ");
			System.out.println(" 'node' ");
			System.exit(1);
		}

		if(args[0].equalsIgnoreCase("supernode")) {
			SuperNode superNode = new SuperNode(null, 0, null, null);
			System.out.println(" I'm SUPER NODO = \n" + superNode.toString());
		}
		else if(args[0].equalsIgnoreCase("node")) {
			Node node = new Node(null, 0, null, 0);
			System.out.println(" I'm a NODE \n" + node.toString());
		}
		else {
			System.out.println(" Command not found");
			System.out.println(" Use 'supernode' or 'node' ");
		}

	}

}
