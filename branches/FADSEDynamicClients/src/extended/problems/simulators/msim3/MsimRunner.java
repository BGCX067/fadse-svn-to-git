/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package extended.problems.simulators.msim3;
import extended.problems.simulators.msim3.MsimConstants;
import extended.problems.simulators.CompositeParameter;
import extended.problems.simulators.SimulatorRunner;
import extended.problems.simulators.SimulatorBase;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Andrei
 */
public class MsimRunner extends SimulatorRunner {
	protected LinkedHashMap<String, String> receivedParameters;
	
//	private CompositeParameter CacheDL1, CacheDL2, CacheIL1, CacheIL2, CacheDL3, CacheIL3;
        private CompositeParameter CacheDL1, CacheDL2, CacheIL1;
	private CompositeParameter ITLB, DTLB, BPred_2lev, BPred_BTB, CPred_2lev, CPred_BTB;
	private CompositeParameter MemConfig;
	
    public MsimRunner(SimulatorBase simulator){
        super(simulator);
        this.receivedParameters = this.simpleParameters; 
    }
    
    // @TODO Read the source code of MSIM 3 to make sure these are the real defaults
    public void prepareCompositeParameters() {
    	CacheDL1 = MsimConstants.getCacheDL1(); 
    	CacheDL2 = MsimConstants.getCacheDL2();
    	CacheIL1 = MsimConstants.getCacheIL1();  
//    	CacheIL2 = MsimConstants.getCacheIL2();
//    	CacheDL3 = MsimConstants.getCacheDL3();
//    	CacheIL3 = MsimConstants.getCacheIL3();
    	
    	ITLB = MsimConstants.getITLB();
    	DTLB = MsimConstants.getDTLB();
    	BPred_2lev = MsimConstants.getBPred_2lev();
    	BPred_BTB = MsimConstants.getBPred_BTB();
    	CPred_2lev = MsimConstants.getCPred_2lev();
    	CPred_BTB = MsimConstants.getBPred_BTB();
    	
    	MemConfig = MsimConstants.getMemConfig_Chunk();
    }

    /**
     * creates the simpleParameter list and adds the fixed msim parameters
     * needed for proper execution
     *
     */
    @Override
    public void prepareParameters(){
        super.prepareParameters();
        this.addSimpleParameter("power_print_stats", "true");
        prepareCompositeParameters();
    }
    
    private void appendSimpleParams(StringBuilder paramstring) {
    	// The M-SIM 3 connector supports a number of simple (non-composite) parameters
    	Map<String, String> simpleParams = MsimConstants.getSimpleParameters();
    	
    	// See which simple parameters have been received, by looking for every supported simple parameter
    	// in receivedParameters
    	for (Map.Entry<String, String> param:  simpleParams.entrySet()) {
    		// The parameter's name
    		String pkey = param.getKey();
    		
    		// The parameter's command-line option
    		String pvalue = param.getValue();
    		
    		// Has the parameter been received?
            if (this.receivedParameters.containsKey(pkey)) {
                paramstring.append(" " + pvalue);
                // Have we also received a value for the parameter? 
                if (!this.receivedParameters.get(pkey).isEmpty())
                    paramstring.append(" " + this.receivedParameters.get(pkey));
            }
    	}
    }
    
    @Override
    protected String[] getCommandLine() {
    	StringBuilder Output = new StringBuilder();
    	
    	// start with appending the executable
        Output.append(this.simulator.getInputDocument().getSimulatorParameter("simulator_executable"));
        
        Output.append(" -redir:sim " + this.simulator.getInputDocument().getSimulatorParameter("simulator_output_file"));
        
        // append simple and composite parameters
        appendSimpleParams(Output);
        
        CacheDL1.assembleParameterValues(receivedParameters, Output);
    	CacheDL2.assembleParameterValues(receivedParameters, Output);
    	CacheIL1.assembleParameterValues(receivedParameters, Output);  
//    	CacheIL2.assembleParameterValues(receivedParameters, Output);
//    	CacheDL3.assembleParameterValues(receivedParameters, Output);
//    	CacheIL3.assembleParameterValues(receivedParameters, Output);
    	
    	ITLB.assembleParameterValues(receivedParameters, Output);
    	DTLB.assembleParameterValues(receivedParameters, Output);
    	BPred_2lev.assembleParameterValues(receivedParameters, Output);
    	BPred_BTB.assembleParameterValues(receivedParameters, Output);
    	CPred_2lev.assembleParameterValues(receivedParameters, Output);
    	CPred_BTB.assembleParameterValues(receivedParameters, Output);
    	
    	MemConfig.assembleParameterValues(receivedParameters, Output);
        
        // append benchmark
        Output.append(" " + individual.getBenchmark());
        
        return Output.toString().split(" ");
    }
}
