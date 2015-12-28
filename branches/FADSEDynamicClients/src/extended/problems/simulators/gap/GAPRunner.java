package extended.problems.simulators.gap;

import extended.problems.simulators.SimulatorRunner;
import extended.problems.simulators.SimulatorBase;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
// import java.security.AccessController;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import shared.FileCopy;
import stepstepgui.batchhelpers.GaptimizeRunner;
import stepstepgui.benchmarks.Benchmark;
import stepstepgui.benchmarks.BenchmarkRepository;
import stepstepgui.datastructures.YamlParameters;
// import sun.security.action.GetPropertyAction;

/**
 * GAPRunner extends SimulatorRunner to properly create the command line string
 * for simulations with the GAP. Well, it also does other stuff like running
 * GAPtimize...
 * @author Andrei, Ralf
 */
public class GAPRunner extends SimulatorRunner {

    private File benchmarkDirectory;
    private static final long TIME_BETWEEN_PROCESS_CHECKS = 10000;
    private static final long TIMEOUT_TO_KILL_SIMULATION = 30000;

    /** Constructor */
    public GAPRunner(SimulatorBase simulator) {
        super(simulator);

        // Configure benchmark repository
        BenchmarkRepository.setFilenames(
                simulator.getInputDocument().getSimulatorParameter("benchmark_yaml_file"),
                null,
                simulator.getInputDocument().getSimulatorParameter("benchmark_repository_path"));

        // Configure the GAPtimize-runner
        GaptimizeRunner.setExecutable(
                simulator.getInputDocument().getSimulatorParameter("gaptimize_executable_file"));

        // Add a shutdown hook to kill GAP simulator if necessary
        Runtime.getRuntime().addShutdownHook(
                new Thread(
                new Runnable() {

                    public void run() {
                        System.out.println("## Shutdown hook is running for the GAP simulator...");
                        if (p != null) {
                            System.out.println("There is a process running.");
                            try {
                                p.destroy();
                                System.out.println("...and it should have been killed.");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        System.out.println("## The shutdown-thread terminated.");
                    }
                }));
    }

    /**
     * This method creates the simpleParameter list.
     * To have well-defined parameteres also for unused parameteres we set default values.
     */
    @Override
    public void prepareParameters() {
        this.addSimpleParameter("//lb", "//lb");

        this.addSimpleParameter(GAPConstants.DO_LOOP_ACCELERATION, "1");
        this.addSimpleParameter(GAPConstants.DO_BRANCH_PREDICTION, "1");
        this.addSimpleParameter(GAPConstants.DO_PEX, "0");
        this.addSimpleParameter(GAPConstants.DO_FUNCTION_INLINING, "0");
        this.addSimpleParameter(GAPConstants.DO_STATIC_SPECULATION, "0");

        super.prepareParameters();
    }
    private static int simulator_id = 0;

    private String getMySimulator() throws IOException {
        if (simulator_id == 0) {
            simulator_id = (int) ((Math.random() * 900) + 100);
        }

        String sim = this.simulator.getInputDocument().getSimulatorParameter("simulator_executable");
        String my_sim = sim.replace(".exe", simulator_id + ".exe");

        FileCopy.copy(new File(sim), new File(my_sim));

        return my_sim;
    }

    /**
     * @todo this must be corrected: not all parameteres are necessary for the
     * command line! find the right values and use only these.
     * @return
     */
    @Override
    protected String[] getCommandLine() {
        // The parameters as linked list of string
        LinkedList<String> sbParamList = new LinkedList<String>();

        try {
            // Add executable of simulator
            sbParamList.add(getMySimulator());
        } catch (IOException ex) {
            Logger.getLogger(GAPRunner.class.getName()).log(Level.SEVERE, null, ex);
            sbParamList.add(
                    this.simulator.getInputDocument().getSimulatorParameter("simulator_executable"));
        }

        // Output directory to build
        String outputFileString = "";

        // search for benchmark
        if (this.simpleParameters.containsKey(GAPConstants.P_TARGET_DIRECTORY)) {
            sbParamList.add(this.simpleParameters.get(GAPConstants.P_TARGET_DIRECTORY));
            outputFileString += this.simpleParameters.get(GAPConstants.P_TARGET_DIRECTORY);
        } else {
            /// AAAAH forget it.
            System.out.println("Invalid configuration.");
        }

        // set the output file path from the parameters
        outputFileString += File.separator + "results";

        String switches = "/";

        // For all parameter choose either default or specified value
        for (String parameter_key : GAPConstants.commandLineParameters) {
            String parameter_value = "";

            // Set Parameter to either specified value or default value
            if (this.simpleParameters.containsKey(parameter_key)) {
                // There is a value available.
                parameter_value = this.simpleParameters.get(parameter_key);
            } else if (parameter_key.equals(GAPConstants.N_COLUMNS)) {
                parameter_value = this.simpleParameters.get(GAPConstants.N_LINES);
            } else {
                // Use default value.
                parameter_value = GAPConstants.getDefaultParameterMap().get(parameter_key);
            }

            System.out.println("Checking " + parameter_key + " with value " + parameter_value);

            if (parameter_key.equals(GAPConstants.DO_LOOP_ACCELERATION)) {
                if (parameter_value.equals("1")) {
                    switches += "l";
                    outputFileString += "_loop";
                }
            } else if (parameter_key.equals(GAPConstants.DO_PEX)) {
                if (parameter_value.equals("1")) {
                    switches += "p";
                    outputFileString += "_pred";
                }
            } else if (parameter_key.equals(GAPConstants.DO_BRANCH_PREDICTION)) {
                if (parameter_value.equals("1")) {
                    switches += "b";
                    outputFileString += "_bpred";
                }
            } else {
                sbParamList.add(parameter_value);
                // see if string is numeric and add to output directory
                if (parameter_value.matches("[0-9]+")) {
                    outputFileString += "_" + parameter_value;
                }
            }
        }

        outputFileString = outputFileString.replace("_bpred_pred", "_pred_bpred");

        if (switches.length() > 1) {
            sbParamList.add(switches);
        }

        this.simulator.setSimulatorOutputFile(outputFileString);
        String[] result = new String[sbParamList.size()];
        sbParamList.toArray(result);

        System.out.println("We found as command line: " + sbParamList);
        System.out.println("We found as result directory: " + outputFileString);

        return result;
    }

    /** Execute :) */
    @Override
    public void run() {
        // Target Directory for Benchmark
        this.benchmarkDirectory = null;

        // Check if Benchmark exists (can be found in yaml file which has been set in gapsimin)
        String basename = individual.getBenchmark();
        Benchmark bench = BenchmarkRepository.getDump(basename);
        if (bench == null) {
            System.out.println("ERROR: Benchmark " + basename + " was not found!");
            return;
        }

        // Create target directory
        benchmarkDirectory = new File(
                simulator.getInputDocument().getSimulatorParameter("benchmark_target_directory"));
        benchmarkDirectory = new File(
                benchmarkDirectory.getAbsolutePath() + "\\gap_dump_" + System.currentTimeMillis() + "_" + basename);

        benchmarkDirectory.mkdirs();

        GAPDirectoryDustman.register(benchmarkDirectory);

        System.out.println("- Target directory: " + benchmarkDirectory);

        // Let's start here a large try-catch-statement. 
        // Cleanup must be done in the finally-section.
        try {
            // Create copy of benchmark

            // Initialize parameters
            YamlParameters para = new YamlParameters(
                    bench.getDumpdir(),
                    bench.getStreamfile_compressed(),
                    benchmarkDirectory.getAbsolutePath());

            // Extract parameteres for GAPtimize

            // a) inlining
            para.put(
                    YamlParameters.DO_FUNCTION_INLINING,
                    (simpleParameters.get(GAPConstants.DO_FUNCTION_INLINING)).equals("1"));

            if (simpleParameters.get(GAPConstants.DO_FUNCTION_INLINING).equals("1")) {
                para.put(
                        YamlParameters.FINLINE_KPI_INSNS_PER_CALLER,
                        Integer.parseInt(simpleParameters.get(GAPConstants.FINLINE_KPI_INSNS_PER_CALLER)));
                para.put(
                        YamlParameters.FINLINE_LENGTH_OF_FUNCTION,
                        Integer.parseInt(simpleParameters.get(GAPConstants.FINLINE_LENGTH_OF_FUNCTION)));
                para.put(
                        YamlParameters.FINLINE_MAX_CALLER_COUNT,
                        Integer.parseInt(simpleParameters.get(GAPConstants.FINLINE_MAX_CALLER_COUNT)));
                para.put(
                        YamlParameters.FINLINE_WEIGHT_OF_CALLER,
                        Integer.parseInt(simpleParameters.get(GAPConstants.FINLINE_WEIGHT_OF_CALLER)));
            }

            // b) static speculation
            para.put(
                    YamlParameters.DO_STATIC_SPECULATION,
                    simpleParameters.get(GAPConstants.DO_STATIC_SPECULATION).equals("1"));

            if (simpleParameters.get(GAPConstants.DO_STATIC_SPECULATION).equals("1")) {
                para.put(
                        YamlParameters.SSPEC_MIN_NODE_WEIGHT,
                        Integer.parseInt(simpleParameters.get(GAPConstants.SSPEC_MIN_NODE_WEIGHT)));
                para.put(
                        YamlParameters.SSPEC_MIN_PROBABILITY_PERCENT,
                        Integer.parseInt(simpleParameters.get(GAPConstants.SSPEC_MIN_PROBABILITY_PERCENT)));
                para.put(
                        YamlParameters.SSPEC_HOT_FUNCTION_RATIO_PERCENT,
                        Integer.parseInt(simpleParameters.get(GAPConstants.SSPEC_HOT_FUNCTION_RATIO_PERCENT)));
                para.put(
                        YamlParameters.SSPEC_MAX_NUMBER_OF_INSNS_TO_SHIFT,
                        Integer.parseInt(simpleParameters.get(GAPConstants.SSPEC_MAX_NUMBER_OF_INSNS_TO_SHIFT)));
                para.put(
                        YamlParameters.SSPEC_MAX_NUMBER_OF_BLOCKS_TO_SHIFT,
                        Integer.parseInt(simpleParameters.get(GAPConstants.SSPEC_MAX_NUMBER_OF_BLOCKS_TO_SHIFT)));
                para.put(
                        YamlParameters.SSPEC_MAX_ALLOWED_ADDITIONAL_HEIGHT,
                        Integer.parseInt(simpleParameters.get(GAPConstants.SSPEC_MAX_ALLOWED_ADDITIONAL_HEIGHT)));
            }

            // c) predicated execution
            para.put(
                    YamlParameters.DO_PEX,
                    (simpleParameters.get(GAPConstants.DO_PEX)).equals("1"));

            if (simpleParameters.get(GAPConstants.DO_PEX).equals("1")) {
                para.put(
                        YamlParameters.PEX_MAX_TOTAL_DYNAMIC_LENGTH,
                        Integer.parseInt(simpleParameters.get(GAPConstants.PEX_MAX_TOTAL_DYNAMIC_LENGTH)));
                para.put(
                        YamlParameters.PEX_MAX_TOTAL_DYNAMIC_LENGTH_IN_LOOPS,
                        Integer.parseInt(simpleParameters.get(GAPConstants.PEX_MAX_TOTAL_DYNAMIC_LENGTH_IN_LOOPS)));

                if (simpleParameters.get(GAPConstants.PEX_DECIDEDNESS_MIN) != null) {
                    para.put(
                            YamlParameters.PEX_DECIDEDNESS_MIN,
                            Integer.parseInt(simpleParameters.get(GAPConstants.PEX_DECIDEDNESS_MIN)));
                }
                if (simpleParameters.get(GAPConstants.PEX_DECIDEDNESS_MAX) != null) {
                    para.put(
                            YamlParameters.PEX_DECIDEDNESS_MAX,
                            Integer.parseInt(simpleParameters.get(GAPConstants.PEX_DECIDEDNESS_MAX)));
                }
                if (simpleParameters.get(GAPConstants.PEX_RELEVANCE_MIN) != null) {
                    para.put(
                            YamlParameters.PEX_RELEVANCE_MIN,
                            Integer.parseInt(simpleParameters.get(GAPConstants.PEX_RELEVANCE_MIN)));
                }
                if (simpleParameters.get(GAPConstants.PEX_RELEVANCE_MAX) != null) {
                    para.put(
                            YamlParameters.PEX_RELEVANCE_MAX,
                            Integer.parseInt(simpleParameters.get(GAPConstants.PEX_RELEVANCE_MAX)));
                }
            }

            // Initialize timer for optimization time
            long optimization_time = 0;

            // Check if parameteres are set in a manner to modify the benchmark and, eigher way, create copy of benchmark
            System.out.println("Parameters so far: " + para.toLongString());
            if (para.isModifyingBenchmark()) {
                // Run GAPtimize
                System.out.println("Let's run GAPtimize...");
                long start_of_gaptimize = System.currentTimeMillis();

                GaptimizeRunner.runGaptimize(para, true);

                long end_of_gaptimize = System.currentTimeMillis();
                optimization_time = end_of_gaptimize - start_of_gaptimize;

                System.out.println("Running GAPtimize took " + (optimization_time / 1000) + " s");
                System.out.println("Benchmark will execute " + bench.getExecuted_instructions_ref() + " instructions");
            } else {
                // Copy benchmarks
                System.out.println("GAPtimize not needed, let's copy the benchmark... (Para decision)");
                bench.copyBenchmark(benchmarkDirectory);
            }

            // Write optimization time to result file
            try {
                String filename = para.get(YamlParameters.PATH_TARGET_DIRECTORY) + "//optimization_time.txt";
                File optimization_time_file = new File(filename);
                BufferedWriter bw = new BufferedWriter(new FileWriter(optimization_time_file));
                bw.append(optimization_time + "");
                bw.flush();
                bw.close();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }

            // Create copy of benchmark's input
            bench.copyInput(benchmarkDirectory);

            // Update Target directory
            this.simpleParameters.put(GAPConstants.P_TARGET_DIRECTORY, benchmarkDirectory.getAbsolutePath());

            String executeCommand = "";
            for (String s : this.getCommandLine()) {
                executeCommand += " " + s;
            }

            System.out.println(
                    "- Starting simulator: ["
                    + simulator.getInputDocument().getSimulatorName()
                    + "] with the following command: \n" + executeCommand);

            // Init progression check
            initProgressionCheck();

            // Generate batch file for manual re-execution
            generateBatchForGap(benchmarkDirectory.getAbsolutePath());

            // Execute simulator, set working directory to benchmark directory
            p = Runtime.getRuntime().exec(this.getCommandLine(), null, benchmarkDirectory);

            // Wait for the simulation to end
            boolean is_terminated = false;
            boolean is_progressing = true;
            boolean is_out_of_bound = false;
            String reason = "";
            boolean do_terminate = false;

            // Execute the benchmark
            do {
                try {
                    System.out.println(
                            "Simulation: Let's wait for " + TIME_BETWEEN_PROCESS_CHECKS + " ms...");
                    synchronized (p) {
                        p.wait(TIME_BETWEEN_PROCESS_CHECKS);
                    }
                } catch (Exception iex) {
                    System.out.println("Trouble while waiting: " + iex.getMessage());
                }

                // Check if we want to stop waiting for the simulation...
                is_terminated = isExecutionTerminated(p);
                is_progressing = isExecutionProgressing();
                is_out_of_bound = isExecutionOutOfBounds(
                        bench.getExecuted_instructions_ref(), 0.25);

                if (is_terminated) {
                    reason = "The process has terminated.";
                    do_terminate = true;
                } else if (!is_progressing) {
                    reason = "There is no progress in the simulation detectable.";
                    do_terminate = true;
                } else if (is_out_of_bound) {
                    reason = "The simulation has hit its predefined bounds.";
                    do_terminate = true;
                } else {
                    do_terminate = false;
                }
            } while (!do_terminate);
            System.out.println("The Simulation has been terminated because: " + reason);

            // Check correctness of generated data
            HashMap<String, String> files = new HashMap<String, String>();
            System.out.println("Interesting filenames: ");
            System.out.println("REF  " + bench.getOutput_files_reference());
            System.out.println("CALC " + bench.getOutput_files_generated());

            for (int i = 0; i < bench.getOutput_files_reference().size(); i++) {
                String ref = bench.getOutput_files_reference().get(i);
                String gen = bench.getOutput_files_generated().get(i);
                if (gen.contains("RESULT")) {
                    gen = gen.replace("RESULT", this.simulator.getSimulatorOutputFile());
                }
                System.out.println("files to compare: " + ref + " and " + gen);
                files.put(ref, gen);
            }

            addOptimizationTimeToResultFile(optimization_time);

            if (GAPResultChecker.compareResults(files)) {
                System.out.println("Correct!");
            } else {
                System.out.println("Not correct!");
                this.getIndividual().markAsInfeasibleAndSetBadValuesForObjectives("Output is not correct");
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.getIndividual().markAsInfeasibleAndSetBadValuesForObjectives(
                    "Exception while running, " + e.getMessage());
        } finally {
            System.out.println("Now let's terminate the process if it is still existing.");
            // Kill process if necessary (it is still there)
            if (p != null && !isExecutionTerminated(p)) {
                System.out.println("There is a process, it is not terminated - kill it.");
                try {
                    p.destroy();
                    System.out.println("  Mission accomplished.");
                } catch (Exception e) {
                    System.out.println("  Exception during destroying process: " + e.getMessage());
                }
            }

            // Delete Benchmark directory if wanted... todo.

        }

        p = null;
        System.out.println("- Simulation completed (with or without errors)");
    }
    long last_successful_progression_check = System.currentTimeMillis();
    int last_progression_check_executed_instructions = -1;

    private void initProgressionCheck() {
        last_successful_progression_check = System.currentTimeMillis();
        last_progression_check_executed_instructions = -1;
    }

    /** Checks if there is still some progress in the simulation, Not yet implemented. */
    private boolean isExecutionProgressing() {
        int executed_instructions = this.getExecutedInstructions();
        if (executed_instructions > last_progression_check_executed_instructions) {
            last_progression_check_executed_instructions = executed_instructions;
            last_successful_progression_check = System.currentTimeMillis();
            return true;
        } else if (System.currentTimeMillis() - last_successful_progression_check < TIMEOUT_TO_KILL_SIMULATION) {
            // It has a chance for some sec...
            return true;
        } else {
            return false;
        }
    }

    /** Checks if the simulation is still running */
    private boolean isExecutionTerminated(Process p) {
        if (p == null) {
            System.out.println("Process is null - terminate!");
            return true;
        }

        // If running => we will get an exception!
        try {
            int exit_value = p.exitValue();
            System.out.println("Exit value is: " + exit_value);
            return true;
        } catch (IllegalThreadStateException ex) {
            // ex.printStackTrace();
            return false;
        }
    }

    private int getExecutedInstructions() {
        int executed_instructions = -1;

        // Get last line of ipcDump.txt
        String last_line = null;
        String resultDirName = this.simulator.getSimulatorOutputFile();
        File ipcDump = new File(resultDirName + "\\" + "IpcDump.txt");
        // System.out.println("File to monitor: " + ipcDump);

        try {
            // Fetch last line
            last_line = getLastLineFromFile(ipcDump);

            // Check last line if it is OK
            if (last_line != null && last_line.length() > 0) {
                // Check the last line of the file
                Scanner sc = new Scanner(last_line);
                if (sc.hasNextInt()) {
                    executed_instructions = sc.nextInt();
                }
                sc.close();
            }
        } catch (IOException ex) {
            System.out.println("Exception while checking ipcDump.txt: " + ex.getMessage());
        }

        return executed_instructions;
    }

    /** Checks if the simulation has executed more than N percent instructinons than the ref simulation */
    private boolean isExecutionOutOfBounds(int ref_instructions, double threshold) {
        // Return true if no ref-value is given
        if (ref_instructions <= 0) {
            return false;
        }

        // Else: Check it

        double allowed_instructions = ref_instructions * (1 + threshold);
        double executed_instructions = this.getExecutedInstructions();
        double percentage = (double) executed_instructions / (double) allowed_instructions;

        if (executed_instructions <= allowed_instructions) {
            // in case of an error, executed_instructions is smaller than 0.
            System.out.println("OK " + executed_instructions + " <= " + allowed_instructions + " (" + percentage + ")");
            return false;
        } else {
            System.out.println("ERROR " + executed_instructions + " > " + allowed_instructions + " (" + percentage + ")");
            return true;
        }
    }

    /** Returns the last line of a file as String */
    private String getLastLineFromFile(File file) throws IOException {
        IOException ioex = null;

        String last_line = null, prev_line = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));

            prev_line = null;
            while ((last_line = br.readLine()) != null) {
                prev_line = last_line;
            }
            last_line = prev_line;
        } catch (IOException ex) {
            ioex = ex;
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (Exception e) {
                // who cares.
            }
        }

        if (ioex != null) {
            throw ioex;
        }

        System.out.println("Last line of " + file + " is: " + last_line);
        return last_line;
    }

    private void addOptimizationTimeToResultFile(long optimization_time) {
        // TODO: regroup results
        File file = new File(this.simulator.getSimulatorOutputFile());

        // Check file it is dir or not...
        if (file.isDirectory()) {
            // Somebody specified a directory... browse through it.
            for (File item : file.listFiles()) {
                if (item.getName().endsWith("results.txt")) {
                    file = item;
                    break;
                }
            }
        }
        System.out.println("Found result file: " + file);

        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file, true));
            String line = GAPOutputParser.OBJECTIVE_OPTIMIZATION_TIME + " : " + optimization_time;
            bw.append("\r\n" + line + "\r\n");

        } catch (IOException ex) {
            Logger.getLogger(GAPRunner.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (Exception e) {
                // Nothing to do
            }
        }

        // throw new UnsupportedOperationException("Not yet implemented");
    }

    private void generateBatchForGap(String absolutePath) {
        File file = new File(absolutePath + "\\run.bat");

        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file, true));

            String executeCommand = "";
            for (String s : this.getCommandLine()) {
                executeCommand += " " + s;
            }
            bw.append("REM " + individual + "\r\n");
            bw.append(executeCommand + "\r\n");
            bw.append("\r\n");

        } catch (IOException ex) {
            Logger.getLogger(GAPRunner.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (Exception e) {
                // Nothing to do
            }
        }
    }
}
