/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmetal.metaheuristics.metaoptimizations;

import extended.problems.simulators.ServerSimulator;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jmetal.base.Problem;
import jmetal.base.Solution;
import jmetal.base.SolutionSet;
import jmetal.util.JMException;
import jmetal.util.Ranking;

/**
 *
 * @author Cristian
 */
public class IsmailAlgorithm extends BaseMetaOptimizationAlgorithm {
 
    public IsmailAlgorithm(Problem problem) {
        super(problem);        
    }

    @Override
    public SolutionSet execute() throws JMException, ClassNotFoundException {      
        
        readInputParameters();
        
        SolutionSet masterPopulation = readOrCreateInitialSolutionSet();
             
        if (problem_ instanceof ServerSimulator) {
            ((ServerSimulator) problem_).join();
            ((ServerSimulator) problem_).dumpCurrentPopulation(masterPopulation);
        }

        while (evaluations < maxEvaluations) {
            List<SolutionSet> offspringSets = new ArrayList<>();
            updatePopulationSizes();
            for (int i = 0; i < moas.size(); ++i) {
                SolutionSet offsprings = moas.get(i).generateOffsprings(masterPopulation,
                        populationSize);
                offspringSets.add(selectRandom(masterPopulation.union(offsprings), currentPopulationSizes[i]));
            }

            masterPopulation.clear();
            for (SolutionSet ss : offspringSets) {
                for (int i = 0; i < ss.size(); ++i) {
                    masterPopulation.add(ss.get(i));
                }
            }

            for (int i = 0; i < masterPopulation.size(); ++i) {
                Solution sol = masterPopulation.get(i);
                problem_.evaluate(sol);
                problem_.evaluateConstraints(sol);
                evaluations++;
            }                        
            
            if (problem_ instanceof ServerSimulator) {
                ((ServerSimulator) problem_).join();
                ((ServerSimulator) problem_).dumpCurrentPopulation(masterPopulation);
            }

            for (int i = 0; i < moas.size(); i++) {
                ((ServerSimulator) problem_).dumpCurrentPopulation("off" + moas.get(i).getName() + System.currentTimeMillis(), offspringSets.get(i));
            }
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Evaluations until now " + evaluations);
            updatePercentages(offspringSets);
        }

        Ranking ranking = new Ranking(masterPopulation);
        return ranking.getSubfront(0);
    }

    private SolutionSet selectRandom(SolutionSet offsprings, int count) {
        SolutionSet selected = new SolutionSet(count);
        for (; count > 0; count--) {
            int i = rand.nextInt(offsprings.size());
            selected.add(offsprings.get(i));
            offsprings.remove(i);
        }
        return selected;
    }  
}
