package br.pucrs.distribuida.t1.node;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import br.pucrs.distribuida.t1.resource.Resource;
import br.pucrs.distribuida.t1.resource.ResourceManager;
import br.pucrs.distribuida.t1.util.JsonUtils;
import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import reactor.core.publisher.Mono;

public class Node extends AbstractRSocket {
	
	public static final long ALIVE_NOTIFICATION_TIME = 5L;
	
	private static final long NOTIFY_SUPER_NODE_IMMEDIATELY = 0L;
	
	private String ip;
	private int port;
	private String superNodeIp;
	private int superNodePort;
	private List<Resource> resources;
	private Instant lastNotification;

	public Node(String ip, int port, String superNodeIp, int superNodePort) {
		this.ip = ip;
		this.port = port;
		this.superNodeIp = superNodeIp;
		this.superNodePort = superNodePort;
		this.resources = Collections.synchronizedList(new ArrayList<>());
		this.lastNotification = Instant.now();
	}
	
	public void run() {
		notifySuperNodePeriodically();
		RSocketFactory.receive()
				.acceptor((setupPayload, reactiveSocket) -> Mono.just(this))
				.transport(TcpServerTransport.create(this.ip, this.port))
				.start()
				.subscribe();
	}
	
	public void addResource(String fileName) {
		Resource resource = ResourceManager.get().create(this.ip, this.port, fileName);
		addResource(resource);
		RSocketFactory.connect()
				.transport(TcpClientTransport.create(superNodeIp, superNodePort))
				.start()
				.block()
				.requestResponse(DefaultPayload.create(JsonUtils.toJson(resource), this.ip + ":" + this.port))
				.map(Payload::getDataUtf8)
				.subscribe(System.out::println);
	}
	
	@Override
	public Mono<Void> fireAndForget(Payload payload) {
		System.out.println(payload.getDataUtf8());
		return Mono.empty();
	}
	
	public void requestResource(String fileName) {
		RSocketFactory.connect()
				.transport(TcpClientTransport.create(superNodeIp, superNodePort))
				.start()
				.block()
				.requestStream(DefaultPayload.create(fileName, this.ip + ":" + this.port))
				.map(Payload::getDataUtf8)
				.subscribe(System.out::println);
	}
	
	public void requestFileFromNode(String ip, int port, String hash) {
		RSocketFactory.connect()
				.transport(TcpClientTransport.create(ip, port))
				.start()
				.block()
				.requestResponse(DefaultPayload.create(hash))
				.map(Payload::getDataUtf8)
				.subscribe(System.out::println);
	}
	
	@Override
	public Mono<Payload> requestResponse(Payload payload) {
		String hash = payload.getDataUtf8();
		return getResource(hash)
				.map(Resource::getFileName)
				.map(fileName -> {
					try {
						return ResourceManager.get().readContent(fileName);
					} catch (IOException e) {
						e.printStackTrace();
						return null;
					}
				})
				.map(DefaultPayload::create)
				.map(Mono::just)
				.orElseGet(() -> Mono.just(DefaultPayload.create("Not found!")));
	}
	
	private Optional<Resource> getResource(String hash) {
		return resources.stream()
				.filter(resource -> resource.getHash().equals(hash))
				.findAny();
	}

	public List<Resource> contains(String fileName) {
		return resources.stream()
				.filter(resource -> resource.contains(fileName))
				.collect(Collectors.toList());
	}
	
	public boolean isAlive() {
		return Duration.between(lastNotification, Instant.now()).getSeconds() < ALIVE_NOTIFICATION_TIME;
	}
	
	private void notifySuperNodePeriodically() {
		Executors.newSingleThreadScheduledExecutor()
				.scheduleWithFixedDelay(
						this::tryToNotifySuperNode,
						NOTIFY_SUPER_NODE_IMMEDIATELY,
						ALIVE_NOTIFICATION_TIME - 1L,
						TimeUnit.SECONDS);
	}
	
	private void tryToNotifySuperNode() {
		try {
			notifySuperNode();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void notifySuperNode() throws IOException {
		DatagramSocket datagramSocket = new DatagramSocket();
		InetAddress superNodeAddress = InetAddress.getByName(superNodeIp);
		byte[] buffer = "ping".getBytes();
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, superNodeAddress, superNodePort);
		datagramSocket.send(packet);
		datagramSocket.close();
	}
	
	public void notified() {
		lastNotification = Instant.now();
	}
	
	public void addResource(Resource resource) {
		resources.add(resource);
	}
	
	public String getIp() {
		return this.ip;
	}
	
	public int getPort() {
		return this.port;
	}
	
}
