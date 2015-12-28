/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package extended.problems.simulators.network.server;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import extended.problems.simulators.network.server.status.SimulationStatus;
import extended.problems.simulators.network.Message;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.derby.impl.store.raw.log.ReadOnly;
import org.ini4j.Wini;

/**
 *
 * @author Radu Chis
 */
public class NeighborCreator implements Runnable {

    private ServerSocket clientsServerSocket;
    private LinkedList<ObjectOutputStream> serversSocketOutputStreamList;
    private LinkedList<Socket> clientConnectedSocketList;
    private int standardClientPort;
    private String neighborConfigFilePath;

    public NeighborCreator() throws IOException {
        String currentdir = System.getProperty("user.dir");
        File dir = new File(currentdir);
        String path = dir + System.getProperty("file.separator") + "configs" + System.getProperty("file.separator");
        neighborConfigFilePath = path+"dynamicNeighborConfig.xml";
        Wini ini = new Wini(new File(path + "fadseConfig.ini"));
        int clientsPort = ini.get("ClientsAdministrator", "clientPort", int.class);
        clientsServerSocket = new ServerSocket(clientsPort);
        serversSocketOutputStreamList = ServerNeighborCreator.GetInstance().GetServersOutputStreamList();
        clientConnectedSocketList = new LinkedList<Socket>();
        standardClientPort = ini.get("Client", "listenPort", int.class);
        Logger.getLogger(ResultsReceiver.class.getName()).log(Level.CONFIG, "listening on port - "+clientsPort);
    }

    public void run() {
        Logger.getLogger(ResultsReceiver.class.getName()).log(Level.INFO, "thread started");
        System.out.println();
        while (true) {
            try {
                final Socket socket = clientsServerSocket.accept();
                clientConnectedSocketList.add(socket);

                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                int port = in.readInt();

                //String ip = socket.getInetAddress().getHostAddress();
                Neighbor nb = new Neighbor();
                nb.setIp(socket.getInetAddress());
                nb.setNumberOfOcupiedSlots(0);
                nb.setNumberOfSlots(1);
                nb.setPort(port);
                try {
                    NeighborhoodSingletonWrapper.getInstance().getNeighbors().add(nb);

                    RemoveClosedClients();

                    for (ObjectOutputStream outStreams : serversSocketOutputStreamList) {                                               
                        outStreams.writeObject(new LinkedList<Neighbor>(NeighborhoodSingletonWrapper.getInstance().getNeighbors()));
                        outStreams.flush();
                    }

                    NeighborCreator.SaveNeighborsToFile(neighborConfigFilePath, NeighborhoodSingletonWrapper.getInstance().getNeighbors());

                    Logger.getLogger(NeighborCreator.class.getName()).log(Level.INFO, "Neighborhood size is: " + NeighborhoodSingletonWrapper.getInstance().getSize());
                    Logger.getLogger(NeighborCreator.class.getName()).log(Level.INFO, "Just added: " +nb.getIp()+" : "+nb.getPort());
                } catch (ParserConfigurationException ex) {
                    Logger.getLogger(NeighborCreator.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (IOException ex) {
                Logger.getLogger(NeighborCreator.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
//                try {
//                    socket.close();
//                } catch (IOException ex) {
//                    Logger.getLogger(ResultsReceiver.class.getName()).log(Level.SEVERE, null, ex);
//                }
            }
        }
    }

    private void RemoveClosedClients() {        
            for (Socket sock : clientConnectedSocketList) {
                if(sock.isClosed()){
                    //RemoveClientAndNotifyServers(sock);
                }
            }      
    }

    public static boolean SaveNeighborsToFile(String filename, List<Neighbor> neighbors){
        PrintWriter out = null;
        boolean isOk = true;
        try {            
            String ENCODING = "UTF-8";
            out = new PrintWriter(new FileOutputStream(filename));
            out.println("<?xml version=\"1.0\" encoding=\"" + ENCODING + "\"?>");            
            out.println("<neighbors>");
            for (int i = 0; i < neighbors.size(); i++) {
                out.println("<neighbor ip=\"" + neighbors.get(i).getIp().getHostAddress() +
                        "\" listenPort=\"" + neighbors.get(i).getPort() +
                        "\" availableSlots=\"" + neighbors.get(i).getNumberOfSlots() +"\" "+ "/>");
            }
            out.println("</neighbors>");
            return isOk;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(NeighborCreator.class.getName()).log(Level.SEVERE, null, ex);
            isOk  = false;
        } catch(Exception ex){
           Logger.getLogger(NeighborCreator.class.getName()).log(Level.SEVERE, null, ex);
            isOk  = false;  
        }
        finally {
            out.close();
        }

        return isOk;
    }
}
