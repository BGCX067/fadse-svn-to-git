/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package extended.problems.simulators;
import extended.problems.simulators.*;
import extended.problems.simulators.gap.GAPOutputParser;
import extended.problems.simulators.gap.GAPRunner;
import environment.Environment;

/**
 *
 * @author Andrei
 */
public class GAPSimulator extends SimulatorBase {

    /**
     * class constructor
     * @param inputDocument
     */
    public GAPSimulator(Environment environment) throws ClassNotFoundException{
        super(environment);
        this.simulatorOutputFile = environment.getInputDocument().getSimulatorParameter("simulator_output_file");
        this.simulatorOutputParser = new GAPOutputParser(this);
        this.simulatorRunner = new GAPRunner(this);
    }
}
