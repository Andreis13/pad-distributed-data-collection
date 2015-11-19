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
public class NodeInfoRequest implements Serializable {
    
    public int port;
    public InetAddress hostAddress;

}
