/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package faf.pad2.node;

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
import java.net.MulticastSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author andrew
 */
public class DiscoveryService extends Thread {
    public DiscoveryService(InetAddress multicastGroup, int listenPort, NodeInfo nodeInfo) throws IOException {
        socket = new MulticastSocket(listenPort);
        this.nodeInfo = nodeInfo;
        socket.joinGroup(multicastGroup);
    }
    
    @Override
    public void run() {
        try {
            while (true) {
                processRequest(waitForRequest());
            }            
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(DiscoveryService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private NodeInfoRequest waitForRequest() throws IOException, ClassNotFoundException {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);

        ByteArrayInputStream byteArray = new ByteArrayInputStream(buffer);
        ObjectInput oin = new ObjectInputStream(byteArray);

        NodeInfoRequest nodeInfoRequest = (NodeInfoRequest) oin.readObject();
        nodeInfoRequest.hostAddress = packet.getAddress();
        
        return nodeInfoRequest;
    }
    
    private void processRequest(NodeInfoRequest request) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutput oout = new ObjectOutputStream(byteStream);
        
        oout.writeObject(nodeInfo);
        
        DatagramSocket ds = new DatagramSocket();
        ds.send(new DatagramPacket(
                byteStream.toByteArray(),
                byteStream.size(),
                request.hostAddress,
                request.port
        ));
    }
    
    private final MulticastSocket socket;
    private final NodeInfo nodeInfo;
}
