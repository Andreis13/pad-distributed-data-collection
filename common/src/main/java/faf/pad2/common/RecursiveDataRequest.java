/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package faf.pad2.common;

/**
 *
 * @author andrew
 */
public class RecursiveDataRequest implements IDataRequest {

    @Override
    public void accept(IDataRequestHandler handler) {
        handler.handle(this);
    }
    
}
