/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package extended.problems.simulators.network.client;

import environment.document.InputDocument;
import extended.problems.SimulatorWrapper;
import extended.problems.simulators.network.Message;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ini4j.Wini;
import persistence.ConnectionPool;

/**
 *
 * @author Horia Calborean
 */
public class IndividualReceiver implements Runnable {

    ServerSocket serverSocket;
    private Thread simulationRunner;
    private ClientSimulatorRunner clientSimulatorRunner;
    public long connectionWaitStartTime;
    public boolean simulating = false;
    int startPort = 4445;
    int currentPort;

    public IndividualReceiver() throws IOException {
        String currentdir = System.getProperty("user.dir");
        File dir = new File(currentdir);
        Wini ini = new Wini(new File(dir + System.getProperty("file.separator") + "configs" + System.getProperty("file.separator") + "fadseConfig.ini"));
        startPort = ini.get("Client", "listenPort", int.class);
        init(startPort);
    }

    public IndividualReceiver(int port) {
        init(port);
    }

    private void init(int port) {
        boolean isPortAvailable = true;
        do {
            try {
                Logger.getLogger(IndividualReceiver.class.getName()).log(Level.INFO, "Trying on port "+port);
                serverSocket = new ServerSocket(port);
                isPortAvailable = true;
            } catch (IOException ex) {
                Logger.getLogger(IndividualReceiver.class.getName()).log(Level.SEVERE, "Port already assigned "+port);
                port++;
                isPortAvailable = false;
            }
        } while (!isPortAvailable);
        Logger.getLogger(IndividualReceiver.class.getName()).log(Level.INFO, "Current IndividualReceiver port is "+port);
        currentPort = port;
    }

    public int GetCurrentPort() {
        return currentPort;
    }

    public void run() {
        //throw new UnsupportedOperationException("Not supported yet.");
        ObjectInputStream dis = null;
        ObjectOutputStream dos = null;
        Socket socket = null;
        Message m = null;
        SimulatorWrapper sim = null;
        int receivedIndividuals = 1;
        boolean simulationStart = false;
        while (true) {
            try {
                simulationStart = false;
                connectionWaitStartTime = System.currentTimeMillis();
                socket = serverSocket.accept();
                System.out.println("IndividualReceiver: Received ind - " + (receivedIndividuals++) + "");
//                System.out.println("IndividualReceiver: Received a message");
                dos = new ObjectOutputStream(socket.getOutputStream());
                dos.flush();
                dis = new ObjectInputStream(socket.getInputStream());

                // Logger.getLogger(IndividualReceiver.class.getName()).log(Level.INFO,"I'll try to read a Message...");
                m = (Message) dis.readObject();
                // Logger.getLogger(IndividualReceiver.class.getName()).log(Level.INFO,"Reading message worked.");

                m.setServerIP(socket.getInetAddress());
//                System.out.println("IndividualReceiver: Looking for simulator - " + m.getSimulatorName());
                //set the real name of the simualtor on this clent (in the environment will be written ServerSimualtor otherwise)
                m.getIndividual().getEnvironment().getInputDocument().setSimulatorName(m.getSimulatorName());
                //make unique names
                InputDocument inputDocument = m.getIndividual().getEnvironment().getInputDocument();
                for (String key : inputDocument.getSimulatorParameters().keySet()) {
                    String p = inputDocument.getSimulatorParameters().get(key);
                    inputDocument.getSimulatorParameters().put(key, p.replace("#", System.currentTimeMillis() + "_" + m.getMessageId()));
                }
                //end
                sim = SimulatorFactory.getSimulator(m.getSimulatorName(), m.getIndividual().getEnvironment());
                //send ack only if I have the requested type of simulator, else return err simulator not found
                if (sim == null) {
                    // Logger.getLogger(IndividualReceiver.class.getName()).log(Level.INFO,"TYPE_ERR_SIMULATOR_NOT_INSTALLED...");

                    m.setType(Message.TYPE_ERR_SIMULATOR_NOT_INSTALLED);
                    System.out.println("IndividualReceiver: Simulator NOT found");
                } else if (m.getType() == Message.TYPE_CLOSE_SIMULATION_REQUEST) {
                    // Logger.getLogger(IndividualReceiver.class.getName()).log(Level.INFO,"TYPE_CLOSE_SIMULATION_REQUEST...");

                    sim.closeSimulation(m.getIndividual());
                    m.setType(Message.TYPE_ACK);
                    // Logger.getLogger(IndividualReceiver.class.getName()).log(Level.INFO,"I'll try to send a response message.");
                    dos.writeObject(m);
                    dos.flush();
                    // Logger.getLogger(IndividualReceiver.class.getName()).log(Level.INFO,"I've send a response.");
                } else {
                    // Logger.getLogger(IndividualReceiver.class.getName()).log(Level.INFO,"ELSE...");

                    ConnectionPool.setInputDocument(m.getIndividual().getEnvironment().getInputDocument());
                    m.setType(Message.TYPE_ACK);

                    // Logger.getLogger(IndividualReceiver.class.getName()).log(Level.INFO,"I'll try to send a response message.");
                    dos.writeObject(m);
                    dos.flush();
                    // Logger.getLogger(IndividualReceiver.class.getName()).log(Level.INFO,"I've send a response.");
                    simulationStart = true;
                }
            } catch (IOException ex) {
                Logger.getLogger(IndividualReceiver.class.getName()).log(Level.SEVERE, "IOException", ex);
                simulationStart = false;
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(IndividualReceiver.class.getName()).log(Level.SEVERE, "ClassNotFoundException", ex);
                simulationStart = false;
            } finally {
                closeAllConnections(dis, dos, socket);
            }
            if (simulationStart) {
                Logger.getLogger(IndividualReceiver.class.getName()).log(Level.INFO, "Now I can start the simulation...");
                startSimulation(m, sim);
                Logger.getLogger(IndividualReceiver.class.getName()).log(Level.INFO, "I've finished the simulation (?)");
            }
        }
    }

    private void startSimulation(Message m, SimulatorWrapper sim) {
        //TODO start a thread then (be carefull not to start more simulations than this coputer is capable of) send Individual to simulator
        //For the time beeing make sure that only one thread is started
//        if(simulationRunner!=null && simulationRunner.isAlive()){
//            try {
//                Logger.getLogger(IndividualReceiver.class.getName()).log(Level.WARNING,"IndividualReceiver: the simulation is still running... joining");
//                simulationRunner.join();
//                connectionWaitStartTime = System.currentTimeMillis();
//            } catch (InterruptedException ex) {
//                Logger.getLogger(IndividualReceiver.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        System.out.println("Starting now the ClientSimulationRunner...");
        simulating = true;
        clientSimulatorRunner = new ClientSimulatorRunner(m.getIndividual(), sim, m);
        clientSimulatorRunner.run();
        simulating = false;
//        System.out.println("ClientSimulationRunner has finished its work.");
//        simulationRunner = new Thread(clientSimulatorRunner);
//        simulationRunner.setDaemon(true);
//        simulationRunner.start();
    }

    private void closeAllConnections(ObjectInputStream dis, ObjectOutputStream dos, Socket socket) {
        try {
            dos.close();
        } catch (IOException ex) {
            Logger.getLogger(IndividualReceiver.class.getName()).log(Level.SEVERE, "DOS could not be closed" + ex.getMessage(), ex);
        }
        try {
            dis.close();
        } catch (IOException ex) {
            Logger.getLogger(IndividualReceiver.class.getName()).log(Level.SEVERE, "DIS could not be closed" + ex.getMessage(), ex);
        }
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(IndividualReceiver.class.getName()).log(Level.SEVERE, "Socket could not be closed" + ex.getMessage(), ex);
        }
    }
}
