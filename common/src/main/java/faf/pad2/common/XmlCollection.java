/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package faf.pad2.common;

import java.util.ArrayList;
import java.util.Collection;
import javax.xml.bind.annotation.*;

/**
 *
 * @author andrew
 */
@XmlRootElement(name="collection")
public class XmlCollection<T> {
    public XmlCollection() {
        collection = new ArrayList<T>();
    }
    public XmlCollection(Collection<T> col) {
        collection = col;
    }
 
    @XmlAnyElement(lax=true)
    public Collection<T> getItems() {
        return collection;
    }
    
    private Collection<T> collection;
}
