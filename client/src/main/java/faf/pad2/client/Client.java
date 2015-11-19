/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package faf.pad2.client;

import faf.pad2.common.Employee;
import faf.pad2.common.NeighbourDataRequest;
import faf.pad2.common.NodeInfo;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author andrew
 */
public class Client {
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {

        DiscoveryService ds = new DiscoveryService(3000);
        
        Collection<NodeInfo> nodeInfos = ds.discover(InetAddress.getByName("230.0.0.1"), 4000, 3000);
        
        NodeInfo ni = selectMaven(nodeInfos).get();
        
        System.out.print("Getting data from: ");
        System.out.print(ni.hostAddress);
        System.out.print(" ");
        System.out.println(ni.dataPort);

        Socket s = new Socket();
        s.connect(new InetSocketAddress(ni.hostAddress, ni.dataPort));

        ObjectOutput oout = new ObjectOutputStream(s.getOutputStream());
        oout.writeObject(new NeighbourDataRequest());
        
        ObjectInput oin = new ObjectInputStream(s.getInputStream());

        Collection<Employee> coll = (Collection<Employee>) oin.readObject();
        double totalAvg = coll.stream().collect(Collectors.averagingDouble((e) -> e.salary));
        
        System.out.println(coll.stream()
                .filter((e) -> e.salary > totalAvg)
                .sorted((e1, e2) -> e1.lastName.compareTo(e2.lastName))
                .collect(Collectors.groupingBy(Employee::getDepartment)));
        
    }
    
    public static Optional<NodeInfo> selectMaven(Collection<NodeInfo> nodes) {
        double avgDataItems = nodes.stream().collect(Collectors.averagingInt((ni) -> ni.dataItemsCount));
        return nodes.stream().max((n1, n2) -> {
            return (int)Math.round((n1.neighboursCount * avgDataItems + n1.dataItemsCount) - (n2.neighboursCount * avgDataItems + n2.dataItemsCount));
        });
    }
}
