package bienvenu;

import goalplanner.ExecutionSummary;

import java.util.ArrayList;

public class ConditionPreferenceFormulaSuccess extends ConditionPreferenceFormula {
	
	/** The name of the goal that should succeed to let the condition become true. */
	private String goal;
	
	public ConditionPreferenceFormulaSuccess(String goal) {
		super(CPFType.SUCCESS);
		this.goal = goal;
	}

	@Override
	public int evaluate(ExecutionSummary execution) {
		return execution.hasSucceeded(this.goal) ? 
				BasicDesireFormula.V_MIN : BasicDesireFormula.V_MAX;
	}

	@Override
	public ArrayList<String> getApplicableGoals() {
		ArrayList<String> result = new ArrayList<String>();
		result.add(this.goal);
		return result;
	}

}
