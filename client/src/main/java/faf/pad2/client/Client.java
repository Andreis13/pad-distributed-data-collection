/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package faf.pad2.client;

import faf.pad2.common.Employee;
import faf.pad2.common.NeighbourDataRequest;
import faf.pad2.common.NeighbourXmlDataRequest;
import faf.pad2.common.LocalJsonDataRequest;
import faf.pad2.common.NodeInfo;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.xml.sax.SAXException;

/**
 *
 * @author andrew
 */
public class Client {
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException, JAXBException, SAXException {

        DiscoveryService ds = new DiscoveryService(3000);
        
        Collection<NodeInfo> nodeInfos = ds.discover(InetAddress.getByName("230.0.0.1"), 4000, 3000);
        
        System.out.println("Node Infos:");
        nodeInfos.forEach(nodeInfo -> System.out.println(nodeInfo));
        
        NodeInfo ni = selectMaven(nodeInfos).get();
        
        System.out.print("\nGetting data from Maven: ");
        System.out.print(ni.hostAddress);
        System.out.print(" ");
        System.out.println(ni.dataPort);

        Socket s = new Socket();
        s.connect(new InetSocketAddress(ni.hostAddress, ni.dataPort));

        ObjectOutput oout = new ObjectOutputStream(s.getOutputStream());
        oout.writeObject(new NeighbourXmlDataRequest());
        
        InputStream in = s.getInputStream();
        
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = sf.newSchema(new File("schema1.xsd"));
        Validator validator = schema.newValidator();
        validator.validate(new StreamSource(in));
        
//        byte[] buffer = new byte[1024];
//        int count;
//        while ((count = in.read(buffer)) != -1) {
//            System.out.write(buffer, 0, count);
//        }
        
//        Collection<Employee> coll = new ArrayList<Employee>();
//        JAXBContext jaxbContext = JAXBContext.newInstance(coll.getClass());
//        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
//        coll = (Collection<Employee>) jaxbUnmarshaller.unmarshal(s.getInputStream());
//                
//        double totalAvg = coll.stream().collect(Collectors.averagingDouble((e) -> e.salary));
//        
//        Map<String, List<Employee>> groups = coll.stream()
//                .filter((e) -> e.salary > totalAvg)
//                .sorted((e1, e2) -> e1.lastName.compareTo(e2.lastName))
//                .collect(Collectors.groupingBy(Employee::getDepartment));
//        
//        groups.forEach((department, list) -> {
//            System.out.print("--> ");
//            System.out.println(department);
//            list.forEach(element -> System.out.println(element));
//        });
    }
    
    public static Optional<NodeInfo> selectMaven(Collection<NodeInfo> nodes) {
        double avgDataItems = nodes.stream().collect(Collectors.averagingInt((ni) -> ni.dataItemsCount));
        return nodes.stream().max((n1, n2) -> {
            return (int)Math.round((n1.neighboursCount * avgDataItems + n1.dataItemsCount) - (n2.neighboursCount * avgDataItems + n2.dataItemsCount));
        });
    }
}
