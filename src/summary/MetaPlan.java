package summary;

import goalplanner.Choice;
import jadex.runtime.ICandidateInfo;
import jadex.runtime.Plan;

import java.util.Arrays;

public class MetaPlan extends Plan {
	
	@Override
	public void body() {
		// gather names of applicable children
		ICandidateInfo[] applicables = (ICandidateInfo[]) getParameterSet("applicables").getValues();
		String[] names = new String[applicables.length];
		for (int i = 0; i < applicables.length; i++) {
			names[i] = PreferencePlan.getRawName(applicables[i].getPlan().getName());
		}
		// make decision and store it in result
		Choice choice = MasterPlan.tree.getChoices().get(names);
		int decision = choice.makeDecision();
		ICandidateInfo result = findCandidate(applicables, choice.getName(decision));
		getParameterSet("result").addValue(result);
	}
	/**
	 * Find the candidate from the applicables that corresponds to the given String name.
	 * @param applicables
	 * @param name
	 * @return
	 */
	private ICandidateInfo findCandidate(ICandidateInfo[] applicables, String name) {
		for (ICandidateInfo app : applicables) {
			String rawName = PreferencePlan.getRawName(app.getPlan().getName());
			if (rawName.equals(name)) {
				return app;
			}
		}
		return null;
	}

}
