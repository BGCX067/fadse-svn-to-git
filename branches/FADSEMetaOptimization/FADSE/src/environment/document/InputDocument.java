/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package environment.document;

import environment.Objective;
import environment.parameters.Parameter;
import environment.rule.Rule;
import extended.base.relation.RelationTree;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Horia
 */
public class InputDocument implements Serializable {
    
    public static class InputMetaOptimizedAlgorithm implements Serializable {
        private String name;
        private String configPath;
        private Double initialPercentage;
        private Double minimumPercentage;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getConfigPath() {
            return configPath;
        }

        public void setConfigPath(String configPath) {
            this.configPath = configPath;
        }

        public Double getInitialPercentage() {
            return initialPercentage;
        }

        public void setInitialPercentage(Double initialPercentage) {
            this.initialPercentage = initialPercentage;
        }

        public Double getMinimumPercentage() {
            return minimumPercentage;
        }

        public void setMinimumPercentage(Double minimumPercentage) {
            this.minimumPercentage = minimumPercentage;
        }
    }
    
    public static class InputMetric implements Serializable {
        private String name;
        private Double weight;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Double getWeight() {
            return weight;
        }

        public void setWeight(Double weight) {
            this.weight = weight;
        }
    }

    private Parameter[] parameters;
    private Parameter[] virtualParameters;
    private Map<String, Objective> objectives;
    private List<Rule> rules;
    private List<Rule> relations;
    private String simulatorName;
    private String simulatorType;
    private String metaheuristicName;
    private String metaheuristicConfigPath;
    private String metaheuristicType;
    private List<InputMetaOptimizedAlgorithm> metaOptimizedAlgorithms;
    private List<InputMetric> metrics;
    private String databaseIp;
    private String databasePort;
    private String databaseName;
    private String databaseUser;
    private String databasePassword;
    private Map<String, String> simulatorParameters;
    private LinkedList<String> benchmarks;
    private RelationTree relationTree1;
    private RelationTree relationTree2;
    private String outputPath;

    public InputDocument() {
        this.simulatorParameters = new HashMap<String, String>();
    }

    public Map<String, String> getSimulatorParameters() {
        return simulatorParameters;
    }

    public void setSimulatorParameters(Map<String, String> simulatorParameters) {
        this.simulatorParameters = simulatorParameters;
    }

    public void addSimulatorParameter(String name, String value) {
        simulatorParameters.put(name, value);
    }

    public String getSimulatorParameter(String name) {
        return simulatorParameters.get(name);
    }

    public Parameter[] getParameters() {
        return parameters;
    }

    public void setParameters(Parameter[] parameters) {
        this.parameters = parameters;
    }

    public Map<String, Objective> getObjectives() {
        return objectives;
    }

    public void setObjectives(Map<String, Objective> objectives) {
        this.objectives = objectives;
    }

    public void setRules(List<Rule> rulesList) {
        rules = rulesList;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public String getSimulatorType() {
        return simulatorType;
    }

    public void setSimulatorType(String simulatorType) {
        this.simulatorType = simulatorType;
    }

    public String getSimulatorName() {
        return simulatorName;
    }

    public void setSimulatorName(String simulatorName) {
        this.simulatorName = simulatorName;
    }

    public String getMetaheuristicName() {
        return metaheuristicName;
    }

    public void setMetaheuristicName(String metaheuristicName) {
        this.metaheuristicName = metaheuristicName;
    }

    public String getMetaheuristicConfigPath() {
        return metaheuristicConfigPath;
    }

    public void setMetaheuristicConfigPath(String metaheuristicConfigPath) {
        this.metaheuristicConfigPath = metaheuristicConfigPath;
    }

    public String getDatabaseIp() {
        return databaseIp;
    }

    public void setDatabaseIp(String databaseIp) {
        this.databaseIp = databaseIp;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDatabasePassword() {
        return databasePassword;
    }

    public void setDatabasePassword(String databasePassword) {
        this.databasePassword = databasePassword;
    }

    public String getDatabasePort() {
        return databasePort;
    }

    public void setDatabasePort(String databasePort) {
        this.databasePort = databasePort;
    }

    public String getDatabaseUser() {
        return databaseUser;
    }

    public void setDatabaseUser(String databaseUser) {
        this.databaseUser = databaseUser;
    }

    public LinkedList<String> getBenchmarks() {
        return benchmarks;
    }

    public void setBenchmarks(LinkedList<String> benchmarks) {
        this.benchmarks = benchmarks;
    }

    public List<Rule> getRelations() {
        return relations;
    }

    public void setRelations(List<Rule> relations) {
        this.relations = relations;
    }

    public RelationTree getRelationTree1() {
        return relationTree1;
    }

    public void setRelationTree1(RelationTree relationTree1) {
        this.relationTree1 = relationTree1;
    }

    public RelationTree getRelationTree2() {
        return relationTree2;
    }

    public void setRelationTree2(RelationTree relationTree2) {
        this.relationTree2 = relationTree2;
    }

    public Parameter[] getVirtualParameters() {
        return virtualParameters;
    }

    public void setVirtualParameters(Parameter[] virtualParameters) {
        this.virtualParameters = virtualParameters;
    }
    
    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public String getMetaheuristicType() {
        return metaheuristicType;
    }

    public void setMetaheuristicType(String metaheuristicType) {
        this.metaheuristicType = metaheuristicType;
    }

    public List<InputMetaOptimizedAlgorithm> getMetaOptimizedAlgorithms() {
        return metaOptimizedAlgorithms;
    }

    public void setMetaOptimizedAlgorithms(List<InputMetaOptimizedAlgorithm> metaOptimizedAlgorithms) {
        this.metaOptimizedAlgorithms = metaOptimizedAlgorithms;
    }

    public List<InputMetric> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<InputMetric> metrics) {
        this.metrics = metrics;
    }
    
}
