package br.pucrs.distribuida.t1.node;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import br.pucrs.distribuida.t1.resource.Resource;
import br.pucrs.distribuida.t1.util.JsonUtils;
import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class SuperNode extends AbstractRSocket {
	
	private static final int IP = 0;
	private static final int PORT = 1;
	private static final int FILE_NAME = 0;
	private static final int IP_AND_PORT = 1;
	
	private String ip;
	private int port;
	private String multicastIp;
	private int multicastPort;
	private List<Node> nodes;
	private MulticastSocket multicastSocket;
	private Disposable unicastServer;

	public SuperNode(String ip, int port, String multicastIp, List<Node> nodes) {
		this.ip = ip;
		this.port = port;
		this.multicastIp = multicastIp;
		this.nodes = nodes;
	}
	
	public void run() throws IOException {
		startServer();
		removeDeadNodesPeriodically();
	}
	
	private void startServer() throws IOException {
		startUnicastServer();
		startMulticastServer();
	}
	
	private void startUnicastServer() {
		RSocketFactory.receive()
				.acceptor((setupPayload, reactiveSocket) -> Mono.just(this))
				.transport(TcpServerTransport.create(ip, port))
				.start()
				.subscribe();
	}
	
	private void startMulticastServer() throws IOException {
		@SuppressWarnings("resource")
		MulticastSocket multicastSocket = new MulticastSocket(multicastPort);
		InetAddress group = InetAddress.getByName(multicastIp);
		multicastSocket.joinGroup(group);
		while (true) {
			try {
				handleOtherSuperNodesRequests();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void handleOtherSuperNodesRequests() throws IOException {
		DatagramPacket packet = receiveFromSuperNode();
		String[] received = new String(packet.getData(), 0, packet.getLength()).split(";");
		String fileName = received[FILE_NAME];
		String nodeIpAndPort = received[IP_AND_PORT];
		String superNodeIp = packet.getAddress().getHostAddress();
		int superNodePort = packet.getPort();
		sendResourcesToSuperNode(fileName, nodeIpAndPort, superNodeIp, superNodePort);
	}
	
	private void sendResourcesToSuperNode(String fileName, String nodeIpAndPort, String superNodeIp, int superNodePort) {
		RSocketFactory.connect()
				.transport(TcpClientTransport.create(superNodeIp, superNodePort))
				.start()
				.flatMap(rsocket -> rsocket.fireAndForget(DefaultPayload.create(
						JsonUtils.toJson(find(fileName)), nodeIpAndPort)))
				.subscribe();
	}
	
	private DatagramPacket receiveFromSuperNode() throws IOException {
		byte[] buffer = new byte[256];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		multicastSocket.receive(packet);
		return packet;
	}
	
	@Override
	public Mono<Void> fireAndForget(Payload payload) {
		String[] ipAndPort = payload.getMetadataUtf8().split(":");
		return RSocketFactory.connect()
				.transport(TcpClientTransport.create(ipAndPort[IP], Integer.parseInt(ipAndPort[PORT])))
				.start()
				.flatMap(rsocket -> rsocket.fireAndForget(payload));
	}
	
	@Override
	public Flux<Payload> requestStream(Payload payload) {
		String fileName = payload.getDataUtf8();
		String nodeIpAndPort = payload.getMetadataUtf8();
		return Mono.just(fileName)
				.flatMapIterable(this::find)
				.map(JsonUtils::toJson)
				.map(DefaultPayload::create)
				.doOnComplete(() -> {
					try {
						requestFromOtherSuperNodes(fileName, nodeIpAndPort);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
	}
	
	private void requestFromOtherSuperNodes(String fileName, String nodeIpAndPort) throws IOException {
		DatagramSocket datagramSocket = new DatagramSocket();
		InetAddress group = InetAddress.getByName(multicastIp);
		byte[] buffer = (fileName + ";" + nodeIpAndPort).getBytes();
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, multicastPort);
		datagramSocket.send(packet);
		datagramSocket.close();
	}
	
	private List<Resource> find(String fileName) {
		return nodes.stream()
				.map(node -> node.contains(fileName))
				.flatMap(List::stream)
				.collect(Collectors.toList());
	}
	
	private void removeDeadNodesPeriodically() {
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

	public void close() {
		multicastSocket.close();
		unicastServer.dispose();
	}
}
