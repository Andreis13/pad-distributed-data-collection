/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package faf.pad2.node;

import faf.pad2.common.Employee;
import faf.pad2.common.NodeInfo;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 *
 * @author andrew
 */
public class Node {
    public static void main(String[] args) throws IOException {
        
        FileInputStream configin = new FileInputStream(args[0]);
        JSONObject configJson = new JSONObject(new JSONTokener(configin));
        
        JSONArray neighboursJson = configJson.getJSONArray("neighbours");
        ArrayList<NodeInfo> neighbours = new ArrayList<>();
        
        for (int i=0; i<neighboursJson.length(); i++) {
            NodeInfo ni = new NodeInfo();
            JSONObject neighbour = neighboursJson.getJSONObject(i);
            ni.hostAddress = InetAddress.getByName(neighbour.getString("host"));
            ni.dataPort = neighbour.getInt("port");
            neighbours.add(ni);
        }
        
        List<Employee> dataItems = loadData(args[1]);
        
        NodeInfo thisNodeInfo = new NodeInfo();
        
        thisNodeInfo.hostAddress = InetAddress.getByName(configJson.getString("host"));
        thisNodeInfo.dataPort = configJson.getInt("port");
        thisNodeInfo.neighboursCount = neighbours.size();
        thisNodeInfo.dataItemsCount = dataItems.size();
        

        DistributedDataServer<Employee> server = new DistributedDataServer<Employee>(
                thisNodeInfo, neighbours, dataItems, Employee.class
        );
        server.start();
        
        DiscoveryService ds = new DiscoveryService(InetAddress.getByName("230.0.0.1"), 4000, thisNodeInfo);
        ds.start();
        
    }

    public static ArrayList<Employee> loadData(String dataPath) throws FileNotFoundException {
        FileInputStream dataIn = new FileInputStream(dataPath);
        JSONArray array = new JSONArray(new JSONTokener(dataIn));
        
        ArrayList<Employee> employees = new ArrayList<>();
        
        for (int i=0; i<array.length(); i++) {
            employees.add(new Employee(array.getJSONObject(i)));
        }
        
        return employees;
    }
}
