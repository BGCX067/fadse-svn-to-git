/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package extended.problems.simulators;
import extended.problems.simulators.msim3.MsimOutputParser;
import extended.problems.simulators.msim3.MsimRunner;
import environment.Environment;

/**
 *
 * @author Andrei
 */
public class MsimSimulator extends SimulatorBase {

    /**
     * class constructor
     * @param inputDocument
     */
    public MsimSimulator(Environment environment) throws ClassNotFoundException{
        super(environment);
        // TODO: Where should this constant be kept?
        this.simulatorOutputFile = environment.getInputDocument().getSimulatorParameter("simulator_output_file");
        this.simulatorOutputParser = new MsimOutputParser(this);
        this.simulatorRunner = new MsimRunner(this);
    }

   
  
}
