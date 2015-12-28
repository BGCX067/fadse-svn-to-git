/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package extended.problems.simulators;
import environment.Environment;
import extended.problems.simulators.m5.M5OutputParser;
import extended.problems.simulators.m5.M5Runner;

/**
 *
 * @author Andrei
 */
public class M5Simulator extends SimulatorBase {

    /**
     * class constructor
     * @param inputDocument
     */
    public M5Simulator(Environment environment) throws ClassNotFoundException{
        super(environment);
        // TODO: Where should this constant be kept?
        this.simulatorOutputFile = environment.getInputDocument().getSimulatorParameter("simulator_output_file");
        this.simulatorOutputParser = new M5OutputParser(this);
        this.simulatorRunner = new M5Runner(this);
    }
}
