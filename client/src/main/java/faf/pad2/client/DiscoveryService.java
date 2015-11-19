/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package faf.pad2.client;

import faf.pad2.common.NodeInfo;
import faf.pad2.common.NodeInfoRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author andrew
 */
public class DiscoveryService {
    private Object Lists;

    public DiscoveryService(int listenPort) throws UnknownHostException {
        this.listenPort = listenPort;
        this.nodesQueue = new LinkedBlockingQueue<>();
    }
    
    public Collection<NodeInfo> discover(InetAddress multicastGroup, int multicastPort, final int timeout) throws InterruptedException, IOException {

        Thread listener = new Thread() {
            @Override
            public void run() {
                try {
                    listen(timeout);
                } catch (SocketException ex) {
                    Logger.getLogger(DiscoveryService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        
        listener.start();
        poll(multicastGroup, multicastPort);
        wait(timeout);
        listener.interrupt();

        return nodesQueue;
    }
    
    private void poll(InetAddress multicastGroup, int multicastPort) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutput oout = new ObjectOutputStream(byteStream);
        
        DatagramSocket socket = new DatagramSocket();
        
        NodeInfoRequest nir = new NodeInfoRequest();
        nir.port = listenPort;

        oout.writeObject(nir);
        
        socket.send(new DatagramPacket(
                byteStream.toByteArray(),
                byteStream.size(),
                multicastGroup,
                multicastPort
        ));

        socket.close();
    }
    
    private void wait(int timeout) throws InterruptedException {
        Thread.sleep(timeout);
        // while(nodesQueue.isEmpty()) { }
    }
    
    private void listen(int timeout) throws SocketException {
        DatagramSocket socket = new DatagramSocket(listenPort);
        socket.setSoTimeout(timeout);
        
        while (!Thread.interrupted()) {
            try {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                
                ByteArrayInputStream byteArray = new ByteArrayInputStream(buffer);
                ObjectInput oin = new ObjectInputStream(byteArray);

                NodeInfo nodeInfo = (NodeInfo) oin.readObject();
                nodeInfo.hostAddress = packet.getAddress();

                nodesQueue.put(nodeInfo);
            } catch (SocketTimeoutException ex) {
                System.out.println("Socket Timeout");
            } catch (InterruptedException | IOException | ClassNotFoundException ex) {
                Logger.getLogger(DiscoveryService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private final int listenPort;
    private final LinkedBlockingQueue<NodeInfo> nodesQueue;
}
