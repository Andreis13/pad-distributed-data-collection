/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package faf.pad2.common;

import java.io.Serializable;
import java.net.InetAddress;

/**
 *
 * @author andrew
 */
public class NodeInfo implements Serializable {
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(hostAddress);
        s.append(":");
        s.append(dataPort);
        s.append(" | Neighbours: ");
        s.append(neighboursCount);
        s.append(" | Items: ");
        s.append(dataItemsCount);
        return s.toString();
    }
    
    public InetAddress hostAddress;
    public int dataPort;
    public int neighboursCount;
    public int dataItemsCount;
}
