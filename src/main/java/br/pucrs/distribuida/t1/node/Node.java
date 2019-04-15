package br.pucrs.distribuida.t1.node;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.gson.reflect.TypeToken;

import br.pucrs.distribuida.t1.resource.Resource;
import br.pucrs.distribuida.t1.resource.ResourceManager;
import br.pucrs.distribuida.t1.util.JsonUtils;
import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import reactor.core.publisher.Mono;

public class Node extends AbstractRSocket {
	
	public static final long ALIVE_NOTIFICATION_TIME = 5;
	
	private static final Type RESOURCE_LIST = new TypeToken<List<Resource>>() {}.getType();
	
	private String ip;
	private int port;
	private String superNodeIp;
	private int superNodePort;
	private List<Resource> resources;
	private Instant lastNotification;

	public Node(String ip, int port, String superNodeIp, int superNodePort,
			List<Resource> resources, Instant lastNotification) {
		this.ip = ip;
		this.port = port;
		this.superNodeIp = superNodeIp;
		this.superNodePort = superNodePort;
		this.resources = resources;
		this.lastNotification = lastNotification;
	}
	
	public void run() {
		RSocketFactory.receive()
				.acceptor((setupPayload, reactiveSocket) -> Mono.just(this))
				.transport(TcpServerTransport.create(ip, port))
				.start()
				.subscribe();
	}
	
	@Override
	public Mono<Void> fireAndForget(Payload payload) {
		System.out.println(payload.getDataUtf8());
		return Mono.empty();
	}
	
	@Override
	public Mono<Payload> requestResponse(Payload payload) {
		String hash = payload.getDataUtf8();
		return get(hash).map(Resource::getFileName)
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
	
	private Optional<Resource> get(String hash) {
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

	@Override
	public String toString() {
		return JsonUtils.toJson(this);
	}
}
