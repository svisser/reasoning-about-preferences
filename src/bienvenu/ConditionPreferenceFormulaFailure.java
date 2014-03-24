package bienvenu;

import goalplanner.ExecutionSummary;

import java.util.ArrayList;

public class ConditionPreferenceFormulaFailure extends ConditionPreferenceFormula {

	/** The name of the goal that should fail to let the condition become true. */
	private String goal;
	
	public ConditionPreferenceFormulaFailure(String goal) {
		super(CPFType.FAILURE);
		this.goal = goal;
	}

	@Override
	public int evaluate(ExecutionSummary execution) {
		return execution.hasFailed(this.goal) ?
				BasicDesireFormula.V_MAX : BasicDesireFormula.V_MIN;
	}

	@Override
	public ArrayList<String> getApplicableGoals() {
		ArrayList<String> result = new ArrayList<String>();
		result.add(this.goal);
		return result;
	}

}
