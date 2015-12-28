/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package extended.problems.simulators;
import extended.problems.simulators.msim3.Msim3OutputParser;
import extended.problems.simulators.msim3.Msim3Runner;
import environment.Environment;

/**
 *
 * @author Andrei
 */
public class Msim3Simulator extends SimulatorBase {
	private String ParserOutFile;
	private String ParserErrFile;
	
    /**
     * class constructor
     * @param inputDocument
     */
    public Msim3Simulator(Environment environment) throws ClassNotFoundException{
        super(environment);
        // TODO: Where should this constant be kept?
        this.simulatorOutputFile = environment.getInputDocument().getSimulatorParameter("simulator_output_file");
        this.ParserOutFile = environment.getInputDocument().getSimulatorParameter("parser_out_file");
        this.ParserErrFile = environment.getInputDocument().getSimulatorParameter("parser_err_file");
        this.simulatorOutputParser = new Msim3OutputParser(this);
        this.simulatorRunner = new Msim3Runner(this);
    }
    
    public String getParserOutFile() {
    	return ParserOutFile;
    }
    
    public String getParserErrFile() {
    	return ParserErrFile;
    }
}
