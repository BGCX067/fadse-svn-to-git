package defaultPackage;

import environment.Environment;
import environment.parameters.CheckpointFileParameter;
import extended.problems.simulators.network.server.ClientListReceiver;
import extended.problems.simulators.network.server.Neighbor;
import extended.problems.simulators.network.server.NeighborCreator;
import extended.problems.simulators.network.server.DynamicNeighborhood;
import extended.problems.simulators.network.server.NeighborhoodSingletonFactory;
import extended.problems.simulators.network.server.NeighborhoodSingletonWrapper;
import extended.problems.simulators.network.server.NeighborhoodType;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import jmetal.util.JMException;
import org.ini4j.Wini;
import tools.monitor.SwingMonitor;

/*
 * 
 *
 * This file is part of the FADSE tool.
 *
 *  Authors: Horia Andrei Calborean {horia.calborean at ulbsibiu.ro}, Andrei Zorila
 *  Copyright (c) 2009-2010
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification,
 *  are permitted provided that the following conditions are met:
 *
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *
 *   * Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *  The names of its contributors NOT may be used to endorse or promote products
 *  derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 *  THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *  PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 *  OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 *  WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 *  OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 */
/**
 *
 * @author Horia Calborean <horia.calborean at ulbsibiu.ro>
 */
public class Boot {

    public static void main(String[] args) {
        System.out.println("#########################################");
        System.out.println("# FADSE              client server or xml");
        System.out.println("#########################################");

//        System.setErr(new PrintStream(new LoggingOutputStream(Category.getRoot())));

        ExitInputLister.addExitListener();

        if (args.length > 0 && args[0].equals("client")) {
            BootClient.main(args);
        } else if (args.length > 0 && args[0].equals("monitor")) {
            SwingMonitor.main(args);
        } else {
            String currentdir = System.getProperty("user.dir");
            File dir = new File(currentdir);

            // String xmlFileName = "gapsimin_ralf_uau.xml";
            String xmlFileName = "falsesimin.xml";
            if (args.length > 0) {
                xmlFileName = args[0];
            }
            String checkpointFile = "";
            String secondFile = "";

//            String fuzzyConfigFile = "gap.fcl";
            String fuzzyConfigFile = null;
            String environmentConfigFile = dir + System.getProperty("file.separator") + "configs" + System.getProperty("file.separator") + xmlFileName;
            String neighborConfig = dir + System.getProperty("file.separator") + "configs" + System.getProperty("file.separator") + "neighborConfig.xml";
            for (int i = 1; i < args.length; i++) {
                if (args[i].endsWith(".xml")) {
                    neighborConfig = args[i];
                } else if (args[i].endsWith(".csv")) {
                    checkpointFile = args[i];
                } else if (args[i].endsWith(".spd")) { //for SMPSO speed checkpointFile
                    secondFile = args[i];
                } else if (args[i].endsWith(".fcl")) {
                    fuzzyConfigFile = args[i];
                }
            }



            Environment env = new Environment(environmentConfigFile);
            CheckpointFileParameter checkpointFileParameter = new CheckpointFileParameter(checkpointFile, secondFile);
            env.setCheckpointFileParameter(checkpointFileParameter);
            env.setFuzzyInputFile(fuzzyConfigFile);
            //TODO
            env.setNeighborsConfigFile(neighborConfig);
            //END TODO


            try {                        

                NeighborhoodSingletonFactory.InitializeNeighborhood(dir, neighborConfig);
                if (NeighborhoodSingletonWrapper.getInstance() instanceof DynamicNeighborhood) {

                    ClientListReceiver clientsReceiver = new ClientListReceiver();
                    Thread t = new Thread(clientsReceiver);
                    t.start();

                    while (NeighborhoodSingletonWrapper.getInstance().getNeighbors().size() == 0) {
                        Logger.getLogger(Boot.class.getName()).log(Level.WARNING, "No clients yet! Sleeping for 1 minute before trying again!");
                        Thread.sleep(6000); //sleep 1 min
                    }
                }

                AlgorithmRunner algRunner = new AlgorithmRunner();
                algRunner.run(env);

                 System.out.print("Server says finished!");
                 
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(Boot.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JMException ex) {
                Logger.getLogger(Boot.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                Logger.getLogger(Boot.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Boot.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(Boot.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(Boot.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Boot.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(Boot.class.getName()).log(Level.SEVERE, null, ex);
            }          
        }
    } 
  
}
