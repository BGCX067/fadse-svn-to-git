package extended.problems.simulators.msim3.parametermodifiers;

import extended.problems.simulators.msimcommon.Parameter;
import extended.problems.simulators.msimcommon.ParameterModifier;

public class CLIOptionCoreIndexSuffix implements ParameterModifier {
	private int CoreIndex;
	
	public CLIOptionCoreIndexSuffix(int coreindex) {
		CoreIndex = coreindex;
	}
	
	public void modify(Parameter p) {
		String clioption = p.getCliOption();
		p.setCliOption(clioption + "_" + Integer.toString(CoreIndex));
	}

}
