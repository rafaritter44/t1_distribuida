package br.pucrs.distribuida.t1.node;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import br.pucrs.distribuida.t1.resource.Resource;
import br.pucrs.distribuida.t1.util.ToString;
import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import reactor.core.publisher.Mono;

public class SuperNode extends AbstractRSocket {
	
	private String ip;
	private int port;
	private String multicastIp;
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
	public Mono<Payload> requestResponse(Payload payload) {
		return Mono.fromCallable(payload::getDataUtf8)
				.map(this::find)
				.map(ToString::from)
				.map(DefaultPayload::create);
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
		return ToString.from(this);
	}

}
