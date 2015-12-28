/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package extended.problems.simulators.Multi2Sim;
import extended.problems.simulators.msim3.MsimConstants;
import extended.problems.simulators.SimulatorRunner;
import extended.problems.simulators.SimulatorBase;
import java.util.LinkedHashMap;
import environment.parameters.Parameter;
import java.io.File;
import java.util.LinkedList;
import java.util.Map;
/**
 *
 * @author Andrei
 */ 
public class Multi2SimRunner extends SimulatorRunner {
    public Multi2SimRunner(SimulatorBase simulator){
        super(simulator);
    }

    /**
     * creates the simpleParameter list and adds the fixed multi2sim parameters
     * needed for proper execution
     *
     */
    @Override 
    public void prepareParameters(){
        super.prepareParameters();

        this.addSimpleParameter("report:pipeline",
                this.simulator.getInputDocument().getSimulatorParameter("simulator_path") + Multi2SimConstants.OF_PIPELINE);
        this.addSimpleParameter("report:cache",
                this.simulator.getInputDocument().getSimulatorParameter("simulator_path") + Multi2SimConstants.OF_CACHE);
    }

    @Override
    //./m2s -report:pipeline outp2.txt -report:cache outc2.txt ~/multi2sim-2.3.2/minibench/test-sort.i386
    protected String[] getCommandLine(){
        LinkedList<String> sbParamList = new LinkedList<String>();
        //sbParamList.add();
        sbParamList.add(this.simulator.getInputDocument().getSimulatorParameter("simulator_executable"));
        String outputFileString = "";

        // Search the basic params and add the existing ones
        Map<String, String> basicParams = Multi2SimConstants.getSimpleParameters();
        for (Map.Entry<String, String> param:  basicParams.entrySet()){
            String p = "";
            if (this.simpleParameters.containsKey(param.getKey())){
                p = "-" + param.getKey();
                System.out.println(p);
                if (this.simpleParameters.get(param.getKey())!=null && !this.simpleParameters.get(param.getKey()).isEmpty()){
                    p += " " + (this.simpleParameters.get(param.getKey()));
                }
            }
            if (p!="")
                sbParamList.add(p);
        }

        // search for benchmark
        if (this.simpleParameters.containsKey(MsimConstants.P_BENCHMARK)){
            sbParamList.add(this.simpleParameters.get(MsimConstants.P_BENCHMARK));
            outputFileString += this.simpleParameters.get(MsimConstants.P_BENCHMARK);
        }

        String[] result = new String[sbParamList.size()];
        sbParamList.toArray(result);
        return result;
    }


}
