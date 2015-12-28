/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package knowledge.fuzzyrepresentation;

import java.util.HashSet;

public class FuzzyParameter {

    private int min, max;
    private boolean exponential;
    public HashSet<LltInterval> classes = new HashSet<LltInterval>();
    private String name;

    public boolean getExponential() {
        return exponential;
    }

    public FuzzyParameter(String name, int min, int max, boolean exponential) {
        if(exponential && min != LltInterval.MIN && min != LltInterval.MAX) min = (int)Math.pow(2, min);
        if(exponential && max != LltInterval.MIN && max != LltInterval.MAX) max = (int)Math.pow(2, max);
        this.name = name;
        this.min = min;
        this.max = max;
        this.exponential = exponential;
    }

    public String getName() {
        return name;
    }

    /* public void addLltInterval(int a, int b) {
        this.classes.add(new LltInterval(min, max, a, b, this));
    } */

    public LltInterval getLltInterval(int a, int b) {
        if(exponential && a != LltInterval.MIN && a != LltInterval.MAX) a = (int)Math.pow(2, a);
        if(exponential && b != LltInterval.MIN && b != LltInterval.MAX) b = (int)Math.pow(2, b);
        LltInterval interval = new LltInterval(min, max, a, b, this);
        classes.add(interval);
        return interval;
    }

    public String getFuzzify() {
        String result = "";
        result += "// Fuzzify input variable '" + this.name + "'" + "\n";
        result += "FUZZIFY " + this.name + "\n";
        for (LltInterval item : this.classes) {
            result += item.toFuzzyClass() + " // " + item.toString() + "\n";
        }
        result += "END_FUZZIFY";
        return result;
    }

    public String getDefuzzify() {
        String result = "";
        result += "// Defuzzify output variable '" + this.name + "'" + "\n";
        result += "DEFUZZIFY out" + this.name + "\n";
        for (LltInterval item : this.classes) {
            result += item.toFuzzyClass() + " // " + item.toString() + "\n";
        }
        result += "    METHOD : COG;" + "\n";
        result += "    DEFAULT := -1;" + "\n";
        result += "END_DEFUZZIFY";
        return result;
    }

    public LltInterval[] getUncoveredLltIntervals() {
        LltInterval[] result = { new LltInterval(min, max, 0,0, this) };

        boolean[] checklist = new boolean[max - min + 1];
        for(int i = min; i <= max; i++) checklist[i - min] = false;

        for(LltInterval interval : classes) {
            for(int i = Math.max(min, interval.a); i <= Math.min(max, interval.b); i++)
                checklist[i - min] = true;
        }

        System.out.println("// Uncovered for " + this.getName() + ": Offset " + min + " >> " + toString(checklist, min));

        return result;
    }


    /**
     * Print an array of booleans to standard output.
     */
    public static String toString(boolean[] a, int offset) {
        String result = "{ ";

        int N = a.length;
        for (int i = 0; i < N; i++) {
            if(a[i]) result += "[" + ( i+offset) + "]1 ";
            if(!a[i])     result += "[" + ( i+offset) + "]0 ";
        }
        result += "}";

        return result;
    }
}