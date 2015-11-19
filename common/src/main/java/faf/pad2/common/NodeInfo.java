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
    
    public InetAddress hostAddress;
    public int dataPort;
    public int neighboursCount;
    public int dataItemsCount;
}
