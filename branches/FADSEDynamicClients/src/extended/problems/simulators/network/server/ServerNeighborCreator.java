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
public class ServerNeighborCreator implements Runnable {

    private static ServerNeighborCreator instance;
    private ServerSocket serversServerSocket;
    private LinkedList<Socket> socketList;
    private LinkedList<ObjectOutputStream> socketOutputStreamList;

    private ServerNeighborCreator() throws IOException {
        String currentdir = System.getProperty("user.dir");
        File dir = new File(currentdir);
        Wini ini = new Wini(new File(dir + System.getProperty("file.separator") + "configs" + System.getProperty("file.separator") + "fadseConfig.ini"));
        int port = ini.get("ClientsAdministrator", "serverPort", int.class);
        serversServerSocket = new ServerSocket(port);
        socketList = new LinkedList<Socket>();
        socketOutputStreamList = new LinkedList<ObjectOutputStream>();
        Logger.getLogger(ResultsReceiver.class.getName()).log(Level.CONFIG, "listening on port - " + port);
    }

    public static ServerNeighborCreator GetInstance() throws IOException {
        if (instance == null) {
            instance = new ServerNeighborCreator();
        }
        return instance;
    }

    public LinkedList<Socket> GetServersSocketList() {
        return socketList;
    }

    public LinkedList<ObjectOutputStream> GetServersOutputStreamList() {
        return socketOutputStreamList;
    }

    public void run() {
        Logger.getLogger(ResultsReceiver.class.getName()).log(Level.INFO, "thread started");
        System.out.println();
        ObjectOutputStream out;
        ObjectInputStream in;
        while (true) {
            try {
                final Socket socket = serversServerSocket.accept();
                socketList.add(socket);
                out = new ObjectOutputStream(socket.getOutputStream());
                socketOutputStreamList.add(out);
                out.flush();
                in = new ObjectInputStream(socket.getInputStream());
                out.writeObject(NeighborhoodSingletonWrapper.getInstance().getNeighbors());
                out.flush();
                Logger.getLogger(ServerNeighborCreator.class.getName()).log(Level.INFO, "Connected:" + socket.getInetAddress() + " port: " + socket.getLocalPort());
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(ServerNeighborCreator.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ServerNeighborCreator.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
//                try {
//                    socket.close();
//                } catch (IOException ex) {
//                    Logger.getLogger(ResultsReceiver.class.getName()).log(Level.SEVERE, null, ex);
//                }
            }
        }
    }
}
