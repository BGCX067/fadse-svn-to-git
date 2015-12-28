/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package knowledge.fuzzyrepresentation;


/** This class describes an intervall of the following form: a < X <= b */
public class LltInterval implements Comparable<LltInterval>{

    public static int MIN = Integer.MIN_VALUE;
    public static int MAX = Integer.MAX_VALUE;
    public int a;
    public int b;
    public int min = 0;
    public int max = 4711;
    public FuzzyParameter parameter;

    public LltInterval(int min, int max, int a, int b, FuzzyParameter parameter) {
        this.min = min;
        this.max = max;
        this.a = a;
        this.b = b;
        this.parameter = parameter;
    }

    public String toString() {
        String result = "Interval ";
        result += "]";

        if (a == MIN) {
            result += "MIN{" + min + "}";
        } else {
            result += a;
        }

        result += "; ";

        if (b == MAX) {
            result += "MAX{" + max + "}";
        } else {
            result += b;
        }

        result += "]";

        return result;
    }

    public String toFuzzyClass() {
        String result = "    TERM " + getClassName() + " := ";

        if (a == MIN) {
            result += "(" + min + ",1) ";
        } else {
            result += "(" + a + ",0) ";
            result += "(" + (a + 1) + ",1) ";
        }

        if (b == MAX) {
            result += "(" + max + ",1) ";
        } else {
            result += "(" + b + ",1) ";
            result += "(" + (b + 1) + ",0) ";
        }

        result += ";";

        return result;
    }

    public String getClassName() {
        String a_name = (a == MIN) ? "min" : ("" + a);
        String b_name = (b == MAX) ? "max" : ("" + b);
        // String result = "region_" + min + "-" + a_name + "-" + b_name + "-" + max;
        String result = "I" + a_name + "TO" + b_name + "of" + min + "TO" + max;
        result = result.replace("-", "o");
        result = result.replace("_", "o");
        return result;
    }

    public int compareTo(LltInterval o) {
        return Double.compare(this.hashCode(), o.hashCode());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + this.a;
        hash = 13 * hash + this.b;
        hash = 13 * hash + this.min;
        hash = 13 * hash + this.max;
        hash = 13 * hash + (this.parameter != null ? this.parameter.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object o) {
        return this.hashCode() == o.hashCode();
    }
}