/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jmetal.metaheuristics.metaoptimizations.metrics;

import extended.qualityIndicator.CoverageOfTwoSets;
import jmetal.base.SolutionSet;

/**
 *
 * @author Cristian
 */
public class CoverageMetric extends CoverageOfTwoSets implements Metric {

    @Override
    public double compute(SolutionSet... solutionSets) {
        return computeCoverage(solutionSets[0], solutionSets[1]);
    }
    
}
