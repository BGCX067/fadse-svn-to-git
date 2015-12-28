/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package extended.problems.simulators;
import extended.problems.simulators.Multi2Sim.Multi2SimOutputParser;
import extended.problems.simulators.Multi2Sim.Multi2SimRunner;
import environment.Environment;

/**
 *
 * @author Andrei
 */
public class Multi2SimSimulator extends SimulatorBase {
    
    /**
     * class constructor
     * @param inputDocument 
     */
    public Multi2SimSimulator(Environment environment) throws ClassNotFoundException{
        super(environment);
        this.simulatorOutputFile = environment.getInputDocument().getSimulatorParameter("simulator_output_file");
        this.simulatorOutputParser = new Multi2SimOutputParser(this);
        this.simulatorRunner = new Multi2SimRunner(this);
    }



}
