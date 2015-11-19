/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package faf.pad2.common;

import java.io.Serializable;

/**
 *
 * @author andrew
 */
public interface IDataRequest extends Serializable {
    public void accept(IDataRequestHandler handler);
}
