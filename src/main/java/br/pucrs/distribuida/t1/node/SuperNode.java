package br.pucrs.distribuida.t1.node;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.gson.reflect.TypeToken;

import br.pucrs.distribuida.t1.resource.Resource;
import br.pucrs.distribuida.t1.util.JsonUtils;
import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class SuperNode extends AbstractRSocket {
	
	private static final Type RESOURCE_LIST = new TypeToken<List<Resource>>() {}.getType();
	private static final long REQUEST_TIMEOUT = 3L;
	
	private String ip;
	private int port;
	private String multicastIp;
	private int multicastPort;
	private List<Node> nodes;

	public SuperNode(String ip, int port, String multicastIp, List<Node> nodes) {
		this.ip = ip;
		this.port = port;
		this.multicastIp = multicastIp;
		this.nodes = nodes;
	}
	
	public void run() {
		initServer();
		removeDeadNodesPeriodically();
	}
	
	private void initServer() {
		RSocketFactory.receive()
				.acceptor((setupPayload, reactiveSocket) -> Mono.just(this))
				.transport(TcpServerTransport.create(ip, port))
				.start()
				.subscribe();
	}
	
	@Override
	public Flux<Payload> requestStream(Payload payload) {
		return Mono.fromCallable(payload::getDataUtf8)
				.map(this::find)
				.flux()
				.flatMap(Flux::fromIterable)
				.concatWith(ifNodeRequestFromOtherSuperNodes(payload))
				.map(JsonUtils::toJson)
				.map(DefaultPayload::create);
	}
	
	private Flux<Resource> ifNodeRequestFromOtherSuperNodes(Payload payload) {
		if (isNode(payload.getMetadataUtf8())) {
			try {
				return requestFromOtherSuperNodes(payload.getDataUtf8());
			} catch (IOException e) {
				e.printStackTrace();
				return Flux.empty();
			}
		}
		else {
			return Flux.empty();
		}
	}
	
	private boolean isNode(String ip) {
		return !(ip.equals(multicastIp));
	}
	
	private Flux<Resource> requestFromOtherSuperNodes(String fileName) throws IOException {
		MulticastSocket multicastSocket = new MulticastSocket(multicastPort);
		InetAddress group = InetAddress.getByName(multicastIp);
		multicastSocket.joinGroup(group);
		return Mono.fromCallable(() -> requestResources(fileName, multicastSocket))
				.repeat()
				.take(Duration.ofSeconds(REQUEST_TIMEOUT))
				.flatMap(Flux::fromIterable);
	}
	
	private List<Resource> requestResources(String fileName, MulticastSocket multicastSocket) throws IOException {
		byte[] buffer = new byte[256];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		multicastSocket.receive(packet);
		String json = new String(packet.getData(), 0, packet.getLength());
		List<Resource> resources = JsonUtils.fromJson(json, RESOURCE_LIST);
		return resources;
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

	@Override
	public String toString() {
		return JsonUtils.toJson(this);
	}

}
