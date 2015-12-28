package ro.ulbsibiu.fadse.extended.problems.simulators.fems;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import ro.ulbsibiu.fadse.environment.Individual;
import ro.ulbsibiu.fadse.environment.parameters.Parameter;

public class FemsIndividualParameters {
	public static FemsIndividualParameters factory(Individual individual) throws Exception {
		// Parse benchmark
		String field = individual.getBenchmark();

		// Parse parameters
		Integer noOfHiddenNeurons = null;
		Integer lagWindowSize = null;
		for (Parameter parameter : individual.getParameters()) {
			String name = parameter.getName();
			if (name.equals("NoOfHiddenNeurons")) {
				noOfHiddenNeurons = (Integer) parameter.getValue();
			} else if (name.equals("LagWindowSize")) {
				lagWindowSize = (Integer) parameter.getValue();
			}
		}
		if (noOfHiddenNeurons == null) {
			throw new Exception("Missing Parameter: NoOfHiddenNeurons");
		} else if (lagWindowSize == null) {
			throw new Exception("Missing Parameter: LagWindowSize");
		}
		return new FemsIndividualParameters(field, noOfHiddenNeurons, lagWindowSize);
	}

	final private String field;
	final private int lagWindowSize;
	final private int noOfHiddenNeurons;

	public FemsIndividualParameters(String field, int noOfHiddenNeurons, int lagWindowSize) {
		this.field = field;
		this.noOfHiddenNeurons = noOfHiddenNeurons;
		this.lagWindowSize = lagWindowSize;
	}

	@Override
	public String toString() {
		return "FemsIndividualParameters [field=" + field + ", lagWindowSize=" + lagWindowSize + ", noOfHiddenNeurons="
				+ noOfHiddenNeurons + "]";
	}

	public static List<String> getCsvHeaders() {
		return new LinkedList<String>(Arrays.asList("Field", "NoOfHiddenNeurons", "LagWindowSize"));
	}

	public List<String> getCsvLine() {
		return new LinkedList<String>(Arrays.asList(field, 
				Integer.toString(noOfHiddenNeurons), Integer.toString(lagWindowSize)));
	}

	public String getField() {
		return field;
	}

	public int getLagWindowSize() {
		return lagWindowSize;
	}

	public int getNoOfHiddenNeurons() {
		return noOfHiddenNeurons;
	}
}
