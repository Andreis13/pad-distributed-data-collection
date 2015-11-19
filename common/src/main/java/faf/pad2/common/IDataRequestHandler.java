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
public interface IDataRequestHandler {
    public void handle(LocalDataRequest r);
    public void handle(NeighbourDataRequest r);
    public void handle(RecursiveDataRequest r);
}
