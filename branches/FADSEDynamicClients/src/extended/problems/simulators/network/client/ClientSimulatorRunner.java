/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package extended.problems.simulators.network.client;

import environment.Individual;
import extended.problems.SimulatorWrapper;
import extended.problems.simulators.network.Message;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Horia Calborean
 */
public class ClientSimulatorRunner implements Runnable {

    private Individual individual;
    private SimulatorWrapper simualtor;
    private Message m;

    public ClientSimulatorRunner(Individual individual, SimulatorWrapper simualtor, Message m) {
//        System.out.println("Client Simulator Runner configured");
        this.individual = individual;
        this.simualtor = simualtor;
        this.m = m;
    }

    public void run() {
        try {
//            System.out.println("Client Simulator Runner started ...");
            //simualtor.evaluate(individual.getSolution());
            simualtor.performSimulation(individual);
            //retrieve the results of the simulation
//            System.out.println("ClientSimulatorRunner: Simulation ended. Prepare to send back the results");
            ResultsSender resSender = new ResultsSender();
            resSender.send(individual, m);
        } catch (IOException ex) {
            Logger.getLogger(ClientSimulatorRunner.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
//            System.out.println("Client Simulator Runner finished.");
        }
    }

    public Individual getIndividual() {
        return individual;
    }
}
