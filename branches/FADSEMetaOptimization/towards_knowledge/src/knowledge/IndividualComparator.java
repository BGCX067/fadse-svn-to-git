/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package knowledge;

import java.util.Comparator;
import knowledge.fuzzyrepresentation.Individual;

/**
 *
 * @author jahrralf
 */
class IndividualComparator implements Comparator<Individual> {
    private int o0_index;
    private int o1_index;
   
    public IndividualComparator(int o0_index, int o1_index) {
        this.o0_index = o0_index;
        this.o1_index = o1_index;
    }

    public int compare(Individual o1, Individual o2) {
        if (o1.objectives[o0_index] != o2.objectives[o0_index]) {
            return Double.compare(o1.objectives[o0_index], o2.objectives[o0_index]);
        } else {
            return Double.compare(o1.objectives[o1_index], o2.objectives[o1_index]);
        }
    }
    
}
