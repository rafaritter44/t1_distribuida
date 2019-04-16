package br.pucrs.distribuida.t1.cli;

import java.util.Scanner;

import br.pucrs.distribuida.t1.node.Node;

public class NodeCLI {
	
	private static final String RESOURCE = "r";
	private static final String FILE = "f";
	private static final String ADD = "a";
	
	private Node node;
	private Scanner keyboard;
	
	public NodeCLI(Node node) {
		this.node = node;
		keyboard = new Scanner(System.in);
	}
	
	public void run() {
		System.out.println("===== WELCOME TO NODE COMMAND-LINE INTERFACE =====");
		while (true) {
			showOptions();
			selectOption();
		}
	}
	
	private void showOptions() {
		System.out.println(String.format("(%s) Request resource to Super Node", RESOURCE));
		System.out.println(String.format("(%s) Request file to other node", FILE));
		System.out.println(String.format("(%s) Add resource", ADD));
	}
	
	private void selectOption() {
		String option = keyboard.nextLine();
		switch(option) {
		case RESOURCE: requestResource(); break;
		case FILE: requestFile(); break;
		case ADD: addResource(); break;
		default: System.out.println(String.format("Invalid option: %s", option));
		}
	}
	
	private void requestResource() {
		System.out.println("Please, enter the file name:");
		String fileName = keyboard.nextLine();
		node.requestResource(fileName);
	}
	
	private void requestFile() {
		System.out.println("Please, enter the resource owner IP:");
		String ip = keyboard.nextLine();
		System.out.println("Please, enter the resource owner port:");
		int port;
		try {
			port = Integer.parseInt(keyboard.nextLine());
		} catch(NumberFormatException e) {
			System.out.println(e.getMessage());
			return;
		}
		System.out.println("Please, enter the resource hash:");
		String hash = keyboard.nextLine();
		node.requestFileFromNode(ip, port, hash);
	}
	
	private void addResource() {
		System.out.println("Please, enter the file name:");
		String fileName = keyboard.nextLine();
		node.addResource(fileName);
	}
	
}
