/**
 * CrossoverFactory.java
 *
 * @author Juanjo Durillo
 * @version 1.0
 */

package jmetal.base.operator.crossover;

import extended.base.operator.crossover.RelationalSinglePointCrossover;
import java.util.Properties;

import jmetal.base.operator.crossover.Crossover;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.PropUtils;

/**
 * Class implementing a crossover factory.
 */
public class CrossoverFactory {
    
  /**
   * Gets a crossover operator through its name.
   * @param name Name of the operator
   * @return The operator
   */
  public static Crossover getCrossoverOperator(String name) throws JMException {
    if (name.equalsIgnoreCase("SBXCrossover"))
      return new SBXCrossover();
    else if (name.equalsIgnoreCase("SinglePointCrossover"))
        return new SinglePointCrossover();
    else if (name.equalsIgnoreCase("PMXCrossover"))
      return new PMXCrossover();
    else if (name.equalsIgnoreCase("TwoPointsCrossover"))
      return new TwoPointsCrossover();
    else if (name.equalsIgnoreCase("HUXCrossover"))
      return new HUXCrossover();
    else if (name.equalsIgnoreCase("DifferentialEvolutionCrossover"))
      return new DifferentialEvolutionCrossover();
    else if (name.equalsIgnoreCase("RelationalSinglePointCrossover"))
      return new RelationalSinglePointCrossover();
    else {
      Configuration.logger_.severe("CrossoverFactory.getCrossoverOperator. " +
          "Operator '" + name + "' not found ");
      throw new JMException("Exception in " + name + ".getCrossoverOperator()") ;
    } // else        
  } // getCrossoverOperator


  /**
   * Gets a crossover operator through its name.
   * @param name Name of the operator
   * @return The operator
   */
  public static Crossover getCrossoverOperator(String name, Properties properties) throws JMException {
    if (name.equalsIgnoreCase("SBXCrossover"))
      return new SBXCrossover(PropUtils.getPropertiesWithPrefix(properties, name+"."));
    else if (name.equalsIgnoreCase("SinglePointCrossover"))
        return new SinglePointCrossover(PropUtils.getPropertiesWithPrefix(properties, name+"."));
    else if (name.equalsIgnoreCase("PMXCrossover"))
      return new PMXCrossover(PropUtils.getPropertiesWithPrefix(properties, name+"."));
    else if (name.equalsIgnoreCase("TwoPointsCrossover"))
      return new TwoPointsCrossover(PropUtils.getPropertiesWithPrefix(properties, name+"."));
    else if (name.equalsIgnoreCase("HUXCrossover"))
      return new HUXCrossover(PropUtils.getPropertiesWithPrefix(properties, name+"."));
    else if (name.equalsIgnoreCase("DifferentialEvolutionCrossover"))
      return new DifferentialEvolutionCrossover(PropUtils.getPropertiesWithPrefix(properties, name+"."));
    else if (name.equalsIgnoreCase("RelationalSinglePointCrossover"))
      return new RelationalSinglePointCrossover(PropUtils.getPropertiesWithPrefix(properties, name+"."));

    else {
      Configuration.logger_.severe("CrossoverFactory.getCrossoverOperator. " +
          "Operator '" + name + "' not found ");
      throw new JMException("Exception in " + name + ".getCrossoverOperator()") ;
    } // else
  } // getCrossoverOperator

} // CrossoverFactory
