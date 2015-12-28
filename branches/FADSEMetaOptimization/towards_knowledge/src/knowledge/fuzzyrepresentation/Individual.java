/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package knowledge.fuzzyrepresentation;

import java.util.Arrays;
import knowledge.*;

// TODO: Comparison for more than two Objectives

public class Individual implements Comparable<Individual> {

    private static int objective_count = 2;
    private static int parameter_count = 0;
    private static boolean has_benchmark = true;

    /**
     * @return the objective_count
     */
    public static int getObjectiveCount() {
        return objective_count;
    }

    /**
     * @param aObjective_count the objective_count to set
     */
    public static void setObjectiveCount(int aObjective_count) {
        objective_count = aObjective_count;
    }

    /**
     * @return the parameter_count
     */
    public static int getParameterCount() {
        return parameter_count;
    }

    /**
     * @param aParameter_count the parameter_count to set
     */
    public static void setParameterCount(int aParameter_count) {
        parameter_count = aParameter_count;
    }

    /**
     * @return the has_benchmark
     */
    public static boolean getHasBenchmark() {
        return has_benchmark;
    }

    /**
     * @param aHas_benchmark the has_benchmark to set
     */
    public static void setHasBenchmark(boolean aHas_benchmark) {
        has_benchmark = aHas_benchmark;
    }

    public static void setHeaders(String[] line) {
        int with_benchmark = (has_benchmark)?1:0;
        if(parameter_count == 0) parameter_count = line.length - objective_count - with_benchmark;
        
        parameter_names = new String[parameter_count];
        objective_names = new String[objective_count];
        
        for(int i = 0; i < parameter_count; i++) 
            parameter_names[i] = line[i];

        for(int i = 0; i < objective_count; i++) 
            objective_names[i] = line[parameter_count + i + with_benchmark];
    }
    
    
    public double[] parameters;
    public double[] objectives;
    public static String[] parameter_names;
    public static String[] objective_names;
    public String benchmark = ""; 
    public boolean fake = false;
    public double closest_pareto_distance = Double.MAX_VALUE;
    
    

    /** Create new instance based on other instance, copy all values */
    @Override
    public Individual clone() {
        Individual item = new Individual();
        
        item.parameters = new double[this.parameters.length];
        System.arraycopy(this.parameters, 0, item.parameters, 0, item.parameters.length);
        
        item.objectives = new double[this.objectives.length];
        System.arraycopy(this.objectives, 0, item.objectives, 0, item.objectives.length);
        
        item.fake = this.fake;
        item.closest_pareto_distance = this.closest_pareto_distance;
        item.benchmark = this.benchmark;
                return item;
    }
    
    

    /** Initialize new individual */
    public Individual(String[] line) throws Exception {
        int with_benchmark = (has_benchmark)?1:0;
        if(parameter_count == 0) parameter_count = line.length - objective_count - with_benchmark;
        
        this.parameters = new double[parameter_count];
        this.objectives = new double[objective_count];

        if(line.length != objective_count + parameter_count + with_benchmark) {
            System.out.println("this line does not fit... " + line.length + " instead of " + (objective_count + parameter_count + with_benchmark));
            throw new Exception("this line does not fit... " + line.length + " instead of " + (getObjectiveCount() + getParameterCount() + with_benchmark));
        }

        for (int i = 0; i < parameter_count; i++) {
            parameters[i] = Double.parseDouble(line[i]);
        }
        
        for (int i = 0; i < objective_count; i++) {
            objectives[i] = Double.parseDouble(line[i + parameter_count + with_benchmark]);
        }
    }
    
    public Individual() {
        this.parameters = new double[parameter_count];
        this.objectives = new double[objective_count];
    }

    /*
     * @deprecated
     */
    public Individual(double o0, double o1) {
        objectives = new double[objective_count];
        objectives[0] = o0;
        objectives[1] = o1;
        parameters = new double[0];
        fake = true;
    }

    public String toString() {
        String result = "Individual ";

        result += "{";
        for (int i = 0; i < parameters.length; i++) {
            result += " " + parameters[i] + " ";
        }
        result += "}";

        result += " => ";

        result += "{";
        for (int i = 0; i < objectives.length; i++) {
            result += " " + objectives[i] + " ";
        }
        result += "}";

        return result;
    }

    public int compareTo(Individual o) {
        /* if(objective_count == 2) {
            return new IndividualComparator(0, 1).
        } */
        
        if (this.objectives[0] != o.objectives[0]) {
            return Double.compare(this.objectives[0], o.objectives[0]);
        } else {
            return Double.compare(this.objectives[1], o.objectives[1]);
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + Arrays.hashCode(this.parameters);
        hash = 73 * hash + Arrays.hashCode(this.objectives);
        hash = 73 * hash + this.benchmark.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        return (o.hashCode() == this.hashCode());
    }

    public double objectiveDistanceTo(Individual i, double delta[]) {
        if (i == null) {
            System.out.println("TEST");
        }
        return distance(this.objectives, i.objectives, delta);
    }

    public double parameterDistanceTo(Individual i, double delta[]) {
        return distance(this.parameters, i.parameters, delta);
    }

    // Euklidischer Abstand zwischen zwei Vektoren
    protected static double distance(double[] v1, double[] v2, double[] delta) {
        double dist = 0;
        // double [] dists = new double[v1.length];
        for (int i = 0; i < v1.length; i++) {
            double diff = (v1[i] - v2[i]) / delta[i];
            dist += diff * diff;
            // dists[i] = diff;
        }
        double result = Math.sqrt(dist);
        return result;
    }

    public String getMyClass() {
        if (this.parameters[4] > 7 && this.parameters[4] <= 10) {
            if (this.parameters[2] <= 3) {
                if (this.parameters[1] <= 8) {
                    if (this.parameters[5] <= 1 && this.parameters[5] > 0) {
                        return "PERFECT"; //  (70.0/30.0) [4]
                    }
                }
            }
        }
        if (this.parameters[2] > 3) {
            if (this.parameters[1] <= 20) {
                if (this.parameters[4] <= 8 && this.parameters[4] > 7) {
                    if (this.parameters[5] <= 4 && this.parameters[5] > 1) {
                        return "PERFECT"; //  (81.0/15.0) [3]
                    }
                }
            }
        }
        if (this.parameters[5] <= 4) {
            if (this.parameters[2] > 3) {
                if (this.parameters[1] <= 20) {
                    if (this.parameters[4] > 8) {
                        return "PERFECT"; //  (652.0/150.0) [1]
                    }
                }
            }
        }
        if (this.parameters[4] > 11) {
            if (this.parameters[5] <= 2) {
                if (this.parameters[2] <= 5 && this.parameters[2] > 3) {
                    if (this.parameters[1] > 11 && this.parameters[1] <= 18) {
                        return "PERFECT"; // (34.0/9.0) [5]
                    }
                }
            }
        }
        if (this.parameters[1] <= 20) {
            if (this.parameters[4] > 11) {
                if (this.parameters[2] > 5) {
                    if (this.parameters[5] <= 1) {
                        return "PERFECT"; // (153.0/41.0) [2]
                    }
                }
            }
        }
        return "OK";
    }
}