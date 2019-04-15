package br.pucrs.distribuida.t1.p2p;

import br.pucrs.distribuida.t1.node.Node;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class P2PManager {

    public void register(Node node, String superNodeIP, int port) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        InetAddress address = InetAddress.getByName(superNodeIP);

        byte buf[] = { 12, 13 };
        byte buf1[] = new byte[2];
        DatagramPacket dp = new DatagramPacket(buf, 2, address, port);
        DatagramPacket dptorec = new DatagramPacket(buf1, 2);

        // connect() method
        socket.connect(address, port);

        // isBound() method
        System.out.println("IsBound : " + socket.isBound());

        // isConnected() method
        System.out.println("isConnected : " + socket.isConnected());

        // getInetAddress() method
        System.out.println("InetAddress : " + socket.getInetAddress());

        // getPort() method
        System.out.println("Port : " + socket.getPort());

        // getRemoteSocketAddress() method
        System.out.println("Remote socket address : " +
                socket.getRemoteSocketAddress());

        // getLocalSocketAddress() method
        System.out.println("Local socket address : " +
                socket.getLocalSocketAddress());

        // send() method
        socket.send(dp);
        System.out.println("...packet sent successfully....");

        // receive() method
        socket.receive(dptorec);
        System.out.println("Received packet data : " +
                Arrays.toString(dptorec.getData()));

        // getLocalPort() method
        System.out.println("Local Port : " + socket.getLocalPort());

        // getLocalAddress() method
        System.out.println("Local Address : " + socket.getLocalAddress());

        // setSOTimeout() method
        socket.setSoTimeout(50);

        // getSOTimeout() method
        System.out.println("SO Timeout : " + socket.getSoTimeout());
    }

}
