/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package extended.problems.simulators;
import environment.Environment;
import extended.problems.simulators.tem.TemOutputParser;
import extended.problems.simulators.tem.TemRunner;

/**
 *
 * @author Rolf
 */
public class TemSimulator extends SimulatorBase {

    /**
     * class constructor
     * @param inputDocument
     */
    public TemSimulator(Environment environment) throws ClassNotFoundException{
        super(environment);
        this.simulatorOutputFile = environment.getInputDocument().getSimulatorParameter("simulator_output_file");
        this.simulatorOutputParser = new TemOutputParser(this);
        this.simulatorRunner = new TemRunner(this);
    }
}
