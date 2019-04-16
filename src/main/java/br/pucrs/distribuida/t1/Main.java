package br.pucrs.distribuida.t1;

import java.io.IOException;

import br.pucrs.distribuida.t1.cli.NodeCLI;
import br.pucrs.distribuida.t1.node.Node;
import br.pucrs.distribuida.t1.node.SuperNode;

public class Main {
	
	public static void main(String[] args) throws IOException {
		if (args.length != 5) {
			System.out.println("Usage: java Main <node type> <ip> <port> <ip> <port>");
			System.out.println(" 'node type' :");
			System.out.println(" 'supernode' ");
			System.out.println(" 'node' ");
			System.exit(1);
		}
		String type = args[0];
		switch(type) {
		case "node":
			String superNodeIp = args[1];
			int superNodePort = Integer.parseInt(args[2]);
			String multicastIp = args[3];
			int multicastPort = Integer.parseInt(args[4]);
			SuperNode superNode = new SuperNode(superNodeIp, superNodePort, multicastIp, multicastPort);
			superNode.run();
			break;
		case "supernode":
			String nodeIp = args[1];
			int nodePort = Integer.parseInt(args[2]);
			superNodeIp = args[3];
			superNodePort = Integer.parseInt(args[4]);
			Node node = new Node(nodeIp, nodePort, superNodeIp, superNodePort);
			node.run();
			NodeCLI cli = new NodeCLI(node);
			cli.run();
			break;
		default:
			System.out.println(" Command not found");
			System.out.println(" Use 'supernode' or 'node' ");
		}
	}

}
