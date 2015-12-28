package ro.ulbsibiu.fadse.extended.problems.simulators.fems;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import ro.ulbsibiu.fadse.environment.document.InputDocument;

public class FemsSimulatorParameters {
	private final File outputPath;
	private final String device;
	private final Integer leadWindowSize; 
	
	public static FemsSimulatorParameters factory(InputDocument inputDocument) throws Exception {
		String device = inputDocument.getSimulatorParameter("Device");
		Integer leadWindowSize = Integer.parseInt(inputDocument.getSimulatorParameter("LeadWindowSize")); 
		
		// Set output path
		File outputPath = Paths.get(inputDocument.getOutputPath()).toFile();
		if(!outputPath.exists()) {
			throw new Exception("Simulator output path does not exist: " + outputPath.toString());
		}
		return new FemsSimulatorParameters(outputPath, device, leadWindowSize);
	}

	@Override
	public String toString() {
		return "FemsSimulatorParameters [device=" + device + ", leadWindowSize=" + leadWindowSize + "]";
	}

	public FemsSimulatorParameters(File outputPath, String device, int leadWindowSize) {
		this.outputPath = outputPath;
		this.device = device;
		this.leadWindowSize = leadWindowSize;
	}

	public File getOutputPath() {
		return outputPath;
	}
	
	public String getDevice() {
		return device;
	}
	
	public int getLeadWindowSize() {
		return leadWindowSize;
	}
	
	public static List<String> getCsvHeaders() {
		return new LinkedList<String>(Arrays.asList("Device", "LeadWindowSize"));
	}
	
	public List<String> getCsvLine() {
		return new LinkedList<String>(Arrays.asList(device, leadWindowSize.toString()));
	}
}
