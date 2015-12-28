/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package extended.problems.simulators;
import environment.Environment;
import extended.problems.simulators.unimap.UniMapOutputParser;
import extended.problems.simulators.unimap.UniMapRunner;

/**
 *
 * @author Andrei
 */
public class UniMapSimulator extends SimulatorBase {

    /**
     * class constructor
     * @param inputDocument
     */
    public UniMapSimulator(Environment environment) throws ClassNotFoundException{
        super(environment);
        this.simulatorOutputFile = environment.getInputDocument().getSimulatorParameter("simulator_output_file");
        this.simulatorOutputParser = new UniMapOutputParser(this);
        this.simulatorRunner = new UniMapRunner(this);
    }
}
