package defaultPackage;

import extended.problems.simulators.network.client.IndividualReceiver;
import extended.problems.simulators.network.server.DynamicNeighborhood;
import extended.problems.simulators.network.server.NeighborhoodSingletonFactory;
import extended.problems.simulators.network.server.NeighborhoodSingletonWrapper;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.ini4j.Wini;

/*
 * This file is part of the FADSE tool.
 * 
 *   Authors: Horia Andrei Calborean {horia.calborean at ulbsibiu.ro}, Andrei Zorila
 *   Copyright (c) 2009-2010
 *   All rights reserved.
 * 
 *   Redistribution and use in source and binary forms, with or without modification,
 *   are permitted provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 * 
 *   * Redistributions in binary form must reproduce the above copyright notice,
 *      this list of conditions and the following disclaimer in the documentation
 *      and/or other materials provided with the distribution.
 * 
 *   The names of its contributors NOT may be used to endorse or promote products
 *   derived from this software without specific prior written permission.
 * 
 *   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *   AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 *   THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *   PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 *   CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *   EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *   PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 *   OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 *   WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *   ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 *   OF THE POSSIBILITY OF SUCH DAMAGE.

 */
/**
 *
 * @author Horia Calborean
 */
public class BootClient {

    public static void main(String[] args) {
        try {
            IndividualReceiver indReceiver;
            //args[0] = client always; args[1] can be port number
            if (args.length < 2) {
                indReceiver = new IndividualReceiver();
            } else {
                indReceiver = new IndividualReceiver(Integer.parseInt(args[1]));
            }

            String currentdir = System.getProperty("user.dir");
            File dir = new File(currentdir);
            String neighborConfig = dir + System.getProperty("file.separator") + "configs" + System.getProperty("file.separator") + "neighborConfig.xml";

            NeighborhoodSingletonFactory.InitializeNeighborhood(dir, neighborConfig);
            if (NeighborhoodSingletonWrapper.getInstance() instanceof DynamicNeighborhood) {
                while (!ConnectToServer(indReceiver.GetCurrentPort())) {
                    Logger.getLogger(BootClient.class.getName()).log(Level.SEVERE, "No server to connect to available! Retrying in 1 min!");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(BootClient.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            Thread t = new Thread(indReceiver);
            t.setDaemon(true);
            t.start();
            System.out.println("ClientStarted...");

            while (true) {
                try {
                    Thread.sleep(3000);//5 minutes
                } catch (InterruptedException ex) {
                    Logger.getLogger(BootClient.class.getName()).log(Level.SEVERE, null, ex);
                }
                //Watchdog
                if (!indReceiver.simulating) {
                    long elapsedTimeSinceNoMessage = System.currentTimeMillis() - indReceiver.connectionWaitStartTime;                 
                    Wini ini = new Wini(new File(dir + System.getProperty("file.separator") + "configs" + System.getProperty("file.separator") + "fadseConfig.ini"));
                    int time = ini.get("Watchdog", "time", int.class);
                    if (elapsedTimeSinceNoMessage > (60000 * time)) {//1minute*param
                        t.interrupt();
                        Logger.getLogger(BootClient.class.getName()).log(Level.SEVERE, "Watchdog had to stop this client and restart it");
                        System.exit(1);
                    }
                }
            }
//        } catch (InterruptedException ex) {
//            Logger.getLogger(BootClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(BootClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BootClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static boolean ConnectToServer(int currentPort) throws IOException {
        try {
            Socket requestSocket;
            ObjectOutputStream out;
            ObjectInputStream in;
            String message;

            String currentdir = System.getProperty("user.dir");
            File dir = new File(currentdir);
            Wini ini = new Wini(new File(dir + System.getProperty("file.separator") + "configs" + System.getProperty("file.separator") + "fadseConfig.ini"));
            String ip = ini.get("ClientsAdministrator", "ip");
            int clientPort = ini.get("ClientsAdministrator", "clientPort", int.class);

            requestSocket = new Socket(ip, clientPort);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.writeInt(currentPort);
            out.flush();
            out.close();
            System.out.println("Connected to " + ip + " in port " + clientPort);
            //2. get Input and Output streams
//            out = new ObjectOutputStream(requestSocket.getOutputStream());
//            out.flush();
//            in = new ObjectInputStream(requestSocket.getInputStream());
            return true;
        } catch (ConnectException ex) {
            return false;
        }

    }
}
