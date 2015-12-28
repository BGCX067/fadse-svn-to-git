/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package extended.problems.simulators.network.server;

import extended.problems.simulators.network.server.status.SimulationStatus;
import extended.problems.simulators.network.Message;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.ini4j.Wini;

/**
 *
 * @author Radu Chis
 */
public class ClientListReceiver implements Runnable {

    public List<Message> results;
    private static ClientListReceiver instance;
    private SimulationStatus simulationStatus;
    private int serverPort;
    private String serverIp;

    public ClientListReceiver() throws IOException {
        simulationStatus = SimulationStatus.getInstance();
        results = Collections.synchronizedList(new LinkedList<Message>());
        String currentdir = System.getProperty("user.dir");
        File dir = new File(currentdir);
        Wini ini = new Wini(new File(dir + System.getProperty("file.separator") + "configs" + System.getProperty("file.separator") + "fadseConfig.ini"));
        serverPort = ini.get("ClientsAdministrator", "serverPort", int.class);
        serverIp = ini.get("ClientsAdministrator", "ip");  
        
        Logger.getLogger(ClientListReceiver.class.getName()).log(Level.INFO, "Connected to "+serverIp+":"+serverPort);
    } 

    public void run() {
        Logger.getLogger(ClientListReceiver.class.getName()).log(Level.INFO, "thread started");
        System.out.println();
        try {
            final Socket socket = new Socket(serverIp, serverPort);
            //when this has been reached a client has responded. Create a thread to communicate with the client
//                Logger.getLogger(ResultsReceiver.class.getName()).log(Level.INFO, "A client is sending the response");
            Thread t = new Thread(new Runnable() {

                public void run() {
                    ObjectOutputStream out = null;
                    ObjectInputStream in = null;
                    try {
                        out = new ObjectOutputStream(socket.getOutputStream());
                        out.flush();
                        in = new ObjectInputStream(socket.getInputStream());

                        while (true) {
                            Object obj = in.readObject();
                            LinkedList<Neighbor> neighborList = (LinkedList<Neighbor>) obj;
                            NeighborhoodSingletonWrapper.getInstance().setNeighbors(neighborList);
                            Logger.getLogger(ClientListReceiver.class.getName()).log(Level.INFO, "Added another list of "+neighborList.size()+" neighbors");
                        }

                    } catch (ParserConfigurationException ex) {
                        Logger.getLogger(ClientListReceiver.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(ClientListReceiver.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(ClientListReceiver.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        try {
                            in.close();

                        } catch (IOException ex) {
                            Logger.getLogger(ClientListReceiver.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        try {
                            out.close();
                        } catch (IOException ex) {
                            Logger.getLogger(ClientListReceiver.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        try {
                            socket.close();
                        } catch (IOException ex) {
                            Logger.getLogger(ClientListReceiver.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            });
            t.start();
        } catch (IOException ex) {
            Logger.getLogger(ClientListReceiver.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
//                try {
//                    socket.close();
//                } catch (IOException ex) {
//                    Logger.getLogger(ResultsReceiver.class.getName()).log(Level.SEVERE, null, ex);
//                }
        }
    }

    public List<Message> getResults() {
        return results;
    }

    public void clearResults() {
        results.clear();
    }
}
