package ro.ulbsibiu.fadse.extended.problems.simulators.fems;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.csv.CSVPrinter;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.train.strategy.end.SimpleEarlyStoppingStrategy;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.lma.LevenbergMarquardtTraining;
import org.encog.persist.EncogDirectoryPersistence;

public class FemsTimeSeriesSimulator {
	public static final String RESULTS_FILENAME = "results.csv";
	public static final double TRAINING_RATIO = 0.7;

	public Map<String, Double> performSimulation(FemsSimulatorParameters simulatorParameters,
			FemsIndividualParameters individualParameters, TreeMap<Long, HashMap<String, Double>> fieldsPerTimestamp) {
		// Create training & validation set
		System.out.println("  Create training & validation set");
		Long firstTimestamp = fieldsPerTimestamp.firstKey();
		Long lastTimestamp = fieldsPerTimestamp.lastKey();
		Long splitTimestamp = (long) ((lastTimestamp - firstTimestamp) * TRAINING_RATIO + firstTimestamp);
		TreeMap<Long, HashMap<String, Double>> trainingMap = new TreeMap<Long, HashMap<String, Double>>(
				fieldsPerTimestamp.headMap(splitTimestamp));
		MLDataSet mlTrainingSet = getMLDataSet(trainingMap, individualParameters.getField(),
				simulatorParameters.getLeadWindowSize(), individualParameters.getLagWindowSize());
		TreeMap<Long, HashMap<String, Double>> validationMap = new TreeMap<Long, HashMap<String, Double>>(
				fieldsPerTimestamp.tailMap(splitTimestamp));
		MLDataSet mlValidationSet = getMLDataSet(validationMap, individualParameters.getField(),
				simulatorParameters.getLeadWindowSize(), individualParameters.getLagWindowSize());

		// Create network
		System.out.println("  Create network");
		BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(null, true, individualParameters.getLagWindowSize()));
		// Sigmoid: range 0..1
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, individualParameters.getNoOfHiddenNeurons()));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), false, simulatorParameters.getLeadWindowSize()));
		network.getStructure().finalizeStructure();
		network.reset(1001);

		// Write iteration to CSV file
		FileWriter iterationFileWriter = null;
		CSVPrinter iterationFilePrinter = null;
		try {
			File iterationCsvfile = new File(simulatorParameters.getOutputPath(), String.format("%s_H%d_LAG%d.csv", individualParameters.getField(), 
					individualParameters.getNoOfHiddenNeurons(), individualParameters.getLagWindowSize()));
			iterationFileWriter = new FileWriter(iterationCsvfile, false);
			iterationFilePrinter = new CSVPrinter(iterationFileWriter, FemsConstants.CSV_FORMAT);
			iterationFilePrinter.print("Iteration");
			iterationFilePrinter.print("TrainingError");
			iterationFilePrinter.print("ValidationError");
			//TODO: measure time per iteration and write to csv
			iterationFilePrinter.println();
			
			// Train network
			System.out.println("  Train network");
			final LevenbergMarquardtTraining train = new LevenbergMarquardtTraining(network, mlTrainingSet);
			SimpleEarlyStoppingStrategy earlyStop = new SimpleEarlyStoppingStrategy(mlValidationSet);
			train.addStrategy(earlyStop);
			while (!train.isTrainingDone()) {
				train.iteration(1);
				String iteration = Integer.toString(train.getIteration());
				String trainingError = String.format("%.5f", earlyStop.getTrainingError());
				String validationError = String.format("%.5f", earlyStop.getValidationError());
				System.out.println(String.format("    Iteration: %s, Training Error: %s, Validation Error: %s",
						iteration, trainingError, validationError));
				iterationFilePrinter.print(iteration);
				iterationFilePrinter.print(trainingError);
				iterationFilePrinter.print(validationError);
				//TODO: Fix validationError
				iterationFilePrinter.println();
			}
			train.finishTraining();
		
		// Clear iteration CSV file
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				iterationFilePrinter.flush();
				iterationFilePrinter.close();
				iterationFileWriter.close();
			} catch (IOException e) { }
		}

		// Prepare results
		double trainingError = network.calculateError(mlTrainingSet);
		double validationError = network.calculateError(mlValidationSet);
		TreeMap<String, Double> returnMap = new TreeMap<String, Double>();
		returnMap.put("TrainingError", trainingError);
		returnMap.put("ValidationError", validationError);

		// Serialize network object for later use and write results csv file
		System.out.println("  Serialize network object");
		serializeMethod(simulatorParameters, individualParameters, network);
		try {
			writeResultToCsv(simulatorParameters, individualParameters, returnMap);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return returnMap;
	}

	private void serializeMethod(FemsSimulatorParameters simulatorParameters,
			FemsIndividualParameters individualParameters, MLRegression method) {
		String filename = String.format("%s_H%d_LAG%d.method", individualParameters.getField(), 
				individualParameters.getNoOfHiddenNeurons(), individualParameters.getLagWindowSize());
		EncogDirectoryPersistence.saveObject(new File(simulatorParameters.getOutputPath(), filename), method);
	}

	private void writeResultToCsv(FemsSimulatorParameters simulatorParameters,
			FemsIndividualParameters individualParameters, TreeMap<String, Double> resultMap) throws IOException {
		File csvfile = new File(simulatorParameters.getOutputPath(), RESULTS_FILENAME);
		boolean appendToFile = false;
		if (csvfile.exists()) {
			appendToFile = true;
		}
		FileWriter csvFileWriter = null;
		CSVPrinter csvFilePrinter = null;
		try {
			csvFileWriter = new FileWriter(csvfile, appendToFile);
			csvFilePrinter = new CSVPrinter(csvFileWriter, FemsConstants.CSV_FORMAT);
			if (!appendToFile) {
				for (String header : FemsSimulatorParameters.getCsvHeaders()) {
					csvFilePrinter.print(header);
				}
				for (String header : FemsIndividualParameters.getCsvHeaders()) {
					csvFilePrinter.print(header);
				}
				for(String header : resultMap.keySet()) {
					csvFilePrinter.print(header);
				}
				csvFilePrinter.println();
			}
			for(String value : simulatorParameters.getCsvLine()) {
				csvFilePrinter.print(value);
			}
			for(String value : individualParameters.getCsvLine()) {
				csvFilePrinter.print(value);
			}
			for (Double value : resultMap.values()) {
				csvFilePrinter.print(String.format("%.8f", value));
			}
			csvFilePrinter.println();
		} finally {
			csvFilePrinter.flush();
			csvFilePrinter.close();
			csvFileWriter.close();
		}
	}

	private MLDataSet getMLDataSet(TreeMap<Long, HashMap<String, Double>> fieldsPerTimestamp, String field,
			int leadWindowSize, int lagWindowSize) {
		MLDataSet mlDataSet = new BasicMLDataSet();

		for (Map.Entry<Long, HashMap<String, Double>> fieldPerTimestamp : fieldsPerTimestamp.entrySet()) {
			// timestamp of first ideal value
			Long timestamp = fieldPerTimestamp.getKey();
			boolean windowComplete = true;

			// Set ideal data
			MLData mlIdealData = new BasicMLData(leadWindowSize);
			{
				Map.Entry<Long, HashMap<String, Double>> previousHigherEntry = fieldPerTimestamp;
				for (int i = 0; i < leadWindowSize; i++) {
					// add the next leadWindowSize-number of values
					if (previousHigherEntry == null) {
						windowComplete = false;
						break;
					}
					mlIdealData.setData(i, previousHigherEntry.getValue().get(field));
					previousHigherEntry = fieldsPerTimestamp.higherEntry(previousHigherEntry.getKey());
				}
			}

			// Set input data
			MLData mlInputData = new BasicMLData(lagWindowSize);
			{
				Map.Entry<Long, HashMap<String, Double>> previousLowerEntry = fieldsPerTimestamp.lowerEntry(timestamp);
				for (int i = lagWindowSize - 1; i > -1; i--) {
					// add the strictly previous lagWindowSize-number of values
					if (previousLowerEntry == null) {
						windowComplete = false;
						break;
					}
					double value = previousLowerEntry.getValue().get(field);
					mlInputData.setData(i, value);
					previousLowerEntry = fieldsPerTimestamp.lowerEntry(previousLowerEntry.getKey());
				}
			}

			// Set data pair
			if (windowComplete) {
				BasicMLDataPair mlDataPair = new BasicMLDataPair(mlInputData, mlIdealData);
				mlDataSet.add(mlDataPair);
			}
		}
		return mlDataSet;
	}
}
