/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package knowledge;

import au.com.bytecode.opencsv.CSVReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import knowledge.fuzzyrepresentation.Individual;

/**
 *
 * @author Ralf
 */
public class Analyzer {

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /*
         * BufferedWriter bw = null;
         *
         * try { AnalyzerGui ag = new AnalyzerGui();
         *
         * File[] my_files = { new File("X:\\TEX Dissertation -
         * Ausarbeitung\\resources_ralf\\dse\\hw\\rohdaten\\hw_data.csv"), /*
         * new
         * File("C:\\Users\\jahrralf\\Desktop\\optimal_parameters_for_single_benchmarks\\12x12x1\\dijkstra.csv"),
         *
         * for (File my_file : my_files) { File[] input_files = {my_file};
         *
         * int[] objectives = {0,1};
         *
         * runAllTasks(ag, input_files, .27, objectives); File ffile = new
         * File(my_file.getAbsolutePath() + "_" + 33); bw = new
         * BufferedWriter(new FileWriter(ffile));
         * bw.append(ag.wekainput.getText()); bw.close(); } } catch (Exception
         * ex) { Logger.getLogger(Analyzer.class.getName()).log(Level.SEVERE,
         * null, ex); } finally { try { bw.close(); } catch (IOException ex) {
         * Logger.getLogger(Analyzer.class.getName()).log(Level.SEVERE, null,
         * ex); } }
         */

        String[] filenames = {
            /* "C:\\Users\\jahrralf\\Desktop\\ingle_benchmarks\\results1344246536779\\analysis\\data.csv",
             "C:\\Users\\jahrralf\\Desktop\\ingle_benchmarks\\results1344255283181\\analysis\\data.csv",
             "C:\\Users\\jahrralf\\Desktop\\ingle_benchmarks\\results1344263529868\\analysis\\data.csv",
             "C:\\Users\\jahrralf\\Desktop\\ingle_benchmarks\\results1344287692786\\analysis\\data.csv",
             "C:\\Users\\jahrralf\\Desktop\\ingle_benchmarks\\results1344298857691\\analysis\\data.csv",
             "C:\\Users\\jahrralf\\Desktop\\ingle_benchmarks\\results1344319710810\\analysis\\data.csv",
             "C:\\Users\\jahrralf\\Desktop\\ingle_benchmarks\\results1344338096682\\analysis\\data.csv",
             "C:\\Users\\jahrralf\\Desktop\\ingle_benchmarks\\results1344346784601\\analysis\\data.csv",
             "C:\\Users\\jahrralf\\Desktop\\ingle_benchmarks\\results1344355644307\\analysis\\data.csv",
             "C:\\Users\\jahrralf\\Desktop\\ingle_benchmarks\\results1344366437356\\analysis\\data.csv",
             "C:\\Users\\jahrralf\\Desktop\\ingle_benchmarks\\results1344376181140\\analysis\\data.csv",
             "C:\\Users\\jahrralf\\Desktop\\ingle_benchmarks\\results1344407796689\\analysis\\data.csv",
             "C:\\Users\\jahrralf\\Desktop\\ingle_benchmarks\\results1344433993352\\analysis\\data.csv",
             "C:\\Users\\jahrralf\\Desktop\\ingle_benchmarks\\results1344443220225\\analysis\\data.csv", */
            "C:\\Users\\jahrralf\\Desktop\\ingle_benchmarks\\results1344453112117\\analysis\\data.csv"
        };

        for (String input_name : filenames) {
            File input = new File(input_name);
            AnalyzerGui ag = new AnalyzerGui();

            File[] input_files = {new File(input_name)};

            Individual.setObjectiveCount(2);
            Individual.setHasBenchmark(true);

            int[][] objective_sets = {{1, 2}, {2, 3}}; // energy, filesize, cycles, ctime

            for (int[] objectives : objective_sets) {
                BufferedWriter bw = null;
                try {
                    Analyzer.runAllTasks(ag, input_files, .33, objectives);
                    String output_name = input.getAbsolutePath().replace(".csv", "") + "_" + 33 + ".csv";
                    File output = new File(output_name);
                    bw = new BufferedWriter(new FileWriter(output));
                    bw.append(ag.classification.getText());
                    bw.close();
                    output_name = input.getAbsolutePath().replace(".csv", "") + "_pareto_" + objectives[0] + "-" + objectives[1] + ".csv";
                    output = new File(output_name);
                    bw = new BufferedWriter(new FileWriter(output));
                    bw.append(ag.pareto_csv.getText());
                    bw.close();
                    System.out.println("Data was written to " + output.getAbsolutePath());
                } catch (Exception ex) {
                    Logger.getLogger(Analyzer.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        bw.close();
                    } catch (IOException ex) {
                        Logger.getLogger(Analyzer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    /**
     * Reads individuals from a CSV file ignoring the first line
     *
     * @param file
     * @param individuals
     * @throws Exception
     */
    private static void readIndividualsFromFile(File file, ArrayList<Individual> individuals) throws Exception {
        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(file));
            String[] nextLine;
            nextLine = reader.readNext();

            Individual.setHeaders(nextLine);

            while ((nextLine = reader.readNext()) != null) {
                String[] line = new String[nextLine.length];
                boolean infeasible = false;

                for (int j = 0; j < nextLine.length; j++) {
                    if (nextLine[j].contains("7976931348623157")) {
                        infeasible = true;
                    }
                    line[j] = nextLine[j];
                }

                Individual i = new Individual(line);

                if (!infeasible) {
                    System.out.println("FEASIBLE " + i);
                    individuals.add(i);
                } else {
                    System.out.println("INFEASIBLE " + i);
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * Calculated the pareto front minimizing two objectives
     *
     * First sort the individuals by one of the objectives, then iterate all
     * individuals ignoring duplicates calculating pareto and the other
     * individuals, then set the input individuals to the set of individuals
     * wihtout duplicates
     *
     * @param data Input individuals
     * @param objective_index Array with the objectives to handle
     * @param pareto Set of pareto optimal individuals
     * @param others Non-optimal individuals
     * @throws Exception thrown if != two objectives are specified
     */
    public static void calculatePareto(ArrayList<Individual> data, int[] objective_index, ArrayList<Individual> pareto, ArrayList<Individual> others) throws Exception {
        if (objective_index.length != 2) {
            throw new Exception("More than or less than two obejctives are not implemented yet.");
        }

        Collections.sort(data, new IndividualComparator(objective_index[0], objective_index[1]));

        ArrayList<Individual> all = new ArrayList<Individual>();

        double known_min = Double.MAX_VALUE;
        for (Individual item : data) {
            if (item.objectives[objective_index[1]] < known_min) {
                // Pareto
                known_min = item.objectives[objective_index[1]];
                pareto.add(item);
            } else {
                // Non-Pareto
                System.out.println("       " + item);
                if (!pareto.contains(item) && !others.contains(item)) {
                    others.add(item);
                }
            }
            // All non-duplicates
            if (!all.contains(item)) {
                all.add(item);
            }
        }

        data = all;
    }

    public static void runAllTasks(AnalyzerGui ag, File[] input_files, double perfect_ratio, int[] focus_objectives) throws Exception {

        // IGNORE THIS: For validation ob classifcation with fuzzy rules
        boolean running_gap = false; // (input_file_index == 0);

        ArrayList<Individual> individuals = new ArrayList<Individual>();



        System.out.println("##################");
        System.out.println("Read individuals from files:");

        for (File file : input_files) {
            readIndividualsFromFile(file, individuals);
        }



        System.out.println("##################");
        System.out.println("Calculate Pareto-Front:");

        StringBuffer sb = new StringBuffer();

        ArrayList<Individual> pareto = new ArrayList<Individual>();
        ArrayList<Individual> others = new ArrayList<Individual>();

        calculatePareto(individuals, focus_objectives, pareto, others);

        for (Individual item : pareto) {
            sb.append("PARETO " + item + "\n");
        }
        sb.append("\n");

        sb.append("Total individuals:  " + individuals.size() + "\n");
        sb.append("Pareto individuals: " + pareto.size() + "\n");
        sb.append("Other individuals: " + others.size() + "\n");
        sb.append("\n");


        System.out.println("##################");
        System.out.println("Calculate spread for all objectives:");

        double[] objective_min = new double[Individual.getObjectiveCount()];
        double[] objective_max = new double[Individual.getObjectiveCount()];
        double[] objective_delta = new double[Individual.getObjectiveCount()];

        for (int i = 0; i < Individual.getObjectiveCount(); i++) {
            objective_min[i] = Double.MAX_VALUE;
            objective_max[i] = Double.MIN_VALUE;
        }

        for (Individual item : individuals) {
            for (int i = 0; i < item.objectives.length; i++) {
                objective_min[i] = Math.min(objective_min[i], item.objectives[i]);
                objective_max[i] = Math.max(objective_max[i], item.objectives[i]);
            }
        }

        for (int i = 0; i < objective_min.length; i++) {
            objective_delta[i] = objective_max[i] - objective_min[i];
            if (objective_delta[i] == 0) {
                objective_delta[i] = 0.000000001;
            }
            sb.append("objective_delta_" + i + " = " + objective_delta[i] + " (min_0 = " + objective_min[i] + ", max_0 = " + objective_max[i] + ")" + "\n");
        }
        sb.append("\n");



        System.out.println("##################");
        System.out.println("Calculate spread for all parameters:");

        double[] parameter_min = new double[Individual.getParameterCount()];
        double[] parameter_max = new double[Individual.getParameterCount()];
        double[] parameter_delta = new double[Individual.getParameterCount()];

        for (int i = 0; i < Individual.getParameterCount(); i++) {
            parameter_min[i] = Double.MAX_VALUE;
            parameter_max[i] = Double.MIN_VALUE;
        }

        for (Individual item : pareto) {
            for (int i = 0; i < Individual.getParameterCount(); i++) {
                parameter_min[i] = Math.min(parameter_min[i], item.parameters[i]);
                parameter_max[i] = Math.max(parameter_max[i], item.parameters[i]);
            }
        }

        for (int i = 0; i < Individual.getParameterCount(); i++) {
            parameter_delta[i] = parameter_max[i] - parameter_min[i];
            sb.append("parameter_delta_" + i + " = " + parameter_delta[i] + " (min_0 = " + parameter_min[i] + ", max_0 = " + parameter_max[i] + ")" + "\n");
        }
        sb.append("\n");

        ag.individuals.setText(sb.toString());
        sb = new StringBuffer();



        System.out.println("##################");
        System.out.println("FORMAT the Pareto Front as CSV");

        // Header
        for (int i = 0; i < Individual.getParameterCount(); i++) {
            sb.append(Individual.parameter_names[i] + ",");
        }
        for (int i = 0; i < Individual.getObjectiveCount(); i++) {
            sb.append(Individual.objective_names[i] + ",");
        }

        /* sb.append("distance,fuzzyclass");
         if (running_gap) {
         sb.append(",guessed_class");
         } */
        sb.append("\n");

        // Data
        for (Individual item : pareto) {
            for (int i = 0; i < item.parameters.length; i++) {
                sb.append(item.parameters[i] + ",");
            }
            for (int i = 0; i < item.objectives.length; i++) {
                sb.append(item.objectives[i]);
                if (i < item.objectives.length - 1) {
                    sb.append(",");
                } else {
                    sb.append("\n");
                }
            }
        }

        ag.pareto_csv.setText(sb.toString());
        sb = new StringBuffer();

        ag.setVisible(true);

        /* if (1 + 1 == 2) {
         return;
         } */



        System.out.println("##################");
        System.out.println("Calculate distance between all individuals to the closest pareto object :");
        System.out.println("This is not as important as you might thing, it is for info only.");
        for (Individual item : individuals) { // was: others
            Individual best = null;
            double best_distance = Double.MAX_VALUE;

            // Iterate pareto optimal individuals, remember the closest one
            for (Individual i : pareto) {
                if (item.objectiveDistanceTo(i, objective_delta) < best_distance) {
                    best_distance = item.objectiveDistanceTo(i, objective_delta);
                    best = i;
                }
            }

            if (best != null) {
                System.out.println("Best true Pareto point - objective-distance - parameter-distance;" + item.objectiveDistanceTo(best, objective_delta) + ";" + item.parameterDistanceTo(best, parameter_delta));
            }
        }

        System.out.println("##################");
        System.out.println("Now trying to close wholes in the pareto approximation with linear interpolation:");
        System.out.println("Here the pareto array gets 'watered' with imaginary points.");

        for (int i = 0; i < 50; i++) {

            PriorityQueue<Individual> pareto_pq = new PriorityQueue<Individual>();
            for (Individual item : pareto) {
                pareto_pq.add(item);
            }

            Individual start = pareto_pq.poll();
            while (!pareto_pq.isEmpty()) {
                if (start.objectiveDistanceTo(pareto_pq.peek(), objective_delta) > 0.005) {
                    sb.append("PARETO   " + start + "\n");
                    sb.append("NEXT     " + pareto_pq.peek() + "\n");
                    sb.append("DISTANCE " + start.objectiveDistanceTo(pareto_pq.peek(), objective_delta) + "\n");

                    double o0_avg = (start.objectives[0] + pareto_pq.peek().objectives[0]) / Individual.getObjectiveCount();
                    double o1_avg = (start.objectives[1] + pareto_pq.peek().objectives[1]) / Individual.getObjectiveCount();

                    // Individual fake = new Individual(o0_avg, o1_avg);

                    Individual fake = new Individual();
                    // fake.setObjectiveCount(2);
                    fake.objectives[0] = o0_avg;
                    fake.objectives[1] = o1_avg;
                    fake.fake = true;

                    pareto_pq.add(fake);
                    pareto.add(fake);

                    sb.append("FAKE     " + fake + "\n");
                }

                start = pareto_pq.poll();
            }
        }
        sb.append("\n");

        sb.append("#################\n");
        sb.append("New Pareto front with interpolation:\n");
        for (Individual item : pareto) {
            sb.append("PARETO " + item + "\n");
        }

        System.out.println(sb);

        ag.pareto.setText(sb.toString());
        sb = new StringBuffer();

        System.out.println("##################");

        System.out.print("Now setting for all individuals the min distance to an object of the pareto front...");
        sb.append("Distance to pareto in object space,Distance to pareto in parameter space\n");
        for (Individual item : individuals) {
            Individual best = null;
            item.closest_pareto_distance = Double.MAX_VALUE;

            for (Individual i : pareto) {
                if (item.objectiveDistanceTo(i, objective_delta) < item.closest_pareto_distance) {
                    item.closest_pareto_distance = item.objectiveDistanceTo(i, objective_delta);
                    best = i;
                }
            }

            if (best != null && !best.fake) {
                sb.append("" + item.objectiveDistanceTo(best, objective_delta) + "," + item.parameterDistanceTo(best, parameter_delta) + "\n");
            } else if (best != null) {
                sb.append("" + item.objectiveDistanceTo(best, objective_delta) + "," + "\n");
            }
        }
        System.out.println("done");

        ag.distances.setText(sb.toString());
        sb = new StringBuffer();

        System.out.println("ALL INDIVIDUALS AND DISTANCE TO CLOSEST APPROXIMATED PARETO POINT");

        /*
         * for (int i = 0; i < Individual.PARAMETERS; i++) {
         * System.out.print("para_" + i + ";"); }
         * System.out.println("distance");
         *
         * for (Individual item : all) { for (int i = 0; i < item.para.length;
         * i++) { System.out.print(item.para[i] + ";"); }
         * System.out.println(item.closest_pareto_distance); }
         */

        System.out.println("##################");
        System.out.println("Now calculate the threshold for the " + perfect_ratio + " best individuals:");
        double threshold = Double.MAX_VALUE;
        PriorityQueue<Double> distance_pq = new PriorityQueue<Double>();
        for (Individual item : individuals) {
            if (item.closest_pareto_distance < 100) {
                distance_pq.add(item.closest_pareto_distance);
            }
        }
        int distance_pq_size = distance_pq.size();
        for (int i = 0; i < ((double) distance_pq_size * perfect_ratio); i++) {
            distance_pq.poll();
        }
        threshold = distance_pq.peek();
        System.out.println("Threshold: " + threshold);


        System.out.println("##################");
        System.out.println("NOW THE FINAL EVALUATION");

        for (int i = 0; i < Individual.getParameterCount(); i++) {
            sb.append("para_" + i + ",");
        }
        sb.append("objective_0,objective_1,distance,fuzzyclass");
        if (running_gap) {
            sb.append(",guessed_class");
        }
        sb.append("\n");

        for (Individual item : individuals) {
            double best_distance = item.closest_pareto_distance;
            if (best_distance < 10) {
                for (int i = 0; i < item.parameters.length; i++) {
                    sb.append(item.parameters[i] + ",");
                }

                sb.append(item.objectives[0] + ",");
                sb.append(item.objectives[1] + ",");

                sb.append(item.closest_pareto_distance + ",");

                if (best_distance <= threshold) { // 670 of 2900
                    sb.append("PERFECT");
                } else {
                    sb.append("OK");
                }

                if (running_gap) {
                    sb.append(";" + item.getMyClass());
                }

                sb.append("\n");
            }
        }

        ag.classification.setText(sb.toString());
        sb = new StringBuffer();

        System.out.println("##################");
        System.out.println("NOW THE FORMATTING FOR WEKA");

        for (int i = 0; i < Individual.getParameterCount(); i++) {
            sb.append("para_" + i + ",");
        }
        sb.append("fuzzyclass\n");

        for (Individual item : individuals) {
            double best_distance = item.closest_pareto_distance;
            if (best_distance < 10) {
                for (int i = 0; i < item.parameters.length; i++) {
                    sb.append(item.parameters[i] + ",");
                }

                if (best_distance <= threshold) { // 670 of 2900
                    sb.append("PERFECT");
                } else /*
                 * if (best_distance < 2)
                 */ {
                    sb.append("OK");
                }

                sb.append("\n");
            }
        }

        ag.wekainput.setText(sb.toString());
        sb = new StringBuffer();

        /*
         * for (Individual item : pareto) { double best_distance =
         * item.closest_pareto_distance; // if (best_distance < 2) { for (int i
         * = 0; i < item.para.length; i++) { System.out.print(item.para[i] +
         * ";"); }
         *
         * System.out.print("PARETO");
         *
         * if(item.fake) System.out.print("_interpolated");
         *
         * if(!item.fake) System.out.print(";" + item.getMyClass()); else
         * System.out.print(";FAKE");
         *
         * System.out.print(";" + item.obje[0]); System.out.println(";" +
         * item.obje[1]); // } }
         */

        ag.setVisible(true);
    }
}
