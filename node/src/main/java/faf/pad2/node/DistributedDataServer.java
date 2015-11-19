/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package faf.pad2.node;

import faf.pad2.common.IDataRequest;
import faf.pad2.common.IDataRequestHandler;
import faf.pad2.common.LocalDataRequest;
import faf.pad2.common.NeighbourDataRequest;
import faf.pad2.common.NodeInfo;
import faf.pad2.common.RecursiveDataRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author andrew
 */
public class DistributedDataServer extends Thread {
    
    public DistributedDataServer(NodeInfo thisNodeInfo, Collection<NodeInfo> neighbours, Collection<? extends Serializable> dataItems) throws IOException {
        this.serverSocket = new ServerSocket(thisNodeInfo.dataPort);
        this.neighbourNodeInfos = new LinkedBlockingQueue<>(neighbours);
        this.dataItems = new LinkedBlockingQueue<>(dataItems);
        this.taskExecutor = Executors.newCachedThreadPool();
    }

    @Override
    public void run() {
        System.out.println("Started DataServer.");
        System.out.println("Serving data:");
        System.out.println(dataItems);
        while (true) {
            try {
                Socket client = serverSocket.accept();
                taskExecutor.submit(() -> handleConnection(client));
            } catch (IOException ex) {
                Logger.getLogger(DistributedDataServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    private void handleConnection(Socket client) {
        
        try {
            InputStream clientIn = client.getInputStream();
            ObjectInput oin = new ObjectInputStream(clientIn);
            
            IDataRequest dr = (IDataRequest) oin.readObject();
            System.out.print("Received request: ");
            System.out.println(dr);
            
            dr.accept(new DataDispatcher(client));
            
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(DistributedDataServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private Collection getNeighbourData() {
        return (Collection) neighbourNodeInfos.stream().map((NodeInfo ni) -> {
            return taskExecutor.submit(() -> {
                Socket s = new Socket();
                s.connect(new InetSocketAddress(ni.hostAddress, ni.dataPort));

                ObjectOutput oout = new ObjectOutputStream(s.getOutputStream());
                oout.writeObject(new LocalDataRequest());

                ObjectInput oin = new ObjectInputStream(s.getInputStream());
                return (Collection) oin.readObject();
            });
        }).map((f) -> {
            try {
                return f.get().stream();
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(DistributedDataServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Stream.empty();
        }).reduce(dataItems.stream(), (a, b) -> Stream.concat(a, b)).collect(Collectors.toList());
    }
    
    public class DataDispatcher implements IDataRequestHandler {
    
        public DataDispatcher(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void handle(LocalDataRequest r) {
            System.out.println("Handle local request");
            System.out.println("Local data:");
            System.out.println(dataItems);
            try {
                sendCollection(dataItems, clientSocket.getOutputStream());
            } catch (IOException ex) {
                Logger.getLogger(DistributedDataServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void handle(NeighbourDataRequest r) {
            System.out.println("Handle neighbour request");
            try {
                sendCollection(getNeighbourData(), clientSocket.getOutputStream());
            } catch (IOException ex) {
                Logger.getLogger(DistributedDataServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void handle(RecursiveDataRequest r) {

        }
        
        private void sendCollection(Collection coll, OutputStream out) throws IOException {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutput oout = new ObjectOutputStream(byteOut);

            oout.writeObject(coll);
            InputStream in = new ByteArrayInputStream(byteOut.toByteArray());
                    
            byte[] buffer = new byte[1024];
            while (-1 != in.read(buffer)) {
                out.write(buffer);
            }
        }
        
        private final Socket clientSocket;
    }

    
    private final BlockingQueue<? extends Serializable> dataItems;
    private final BlockingQueue<NodeInfo> neighbourNodeInfos;
    private final ServerSocket serverSocket;
    private final ExecutorService taskExecutor;

}
