package bienvenu;

import goalplanner.ExecutionSummary;

import java.util.ArrayList;

/**
 * Represents a conjunction or disjunction of conditional preference
 * formulas.
 *
 */
public class ConditionPreferenceFormulaMultiple extends
		ConditionPreferenceFormula {

	/** The conditional preference formulas in the conjunction or disjunction. */
	private ArrayList<ConditionPreferenceFormulaProperty> formulas;
	
	/**
	 * Construct a conditional preference formula.
	 * @param type Must be AND or OR.
	 * @param formulas The formulas in the conjunction or disjunction.
	 */
	public ConditionPreferenceFormulaMultiple(CPFType type, ArrayList<ConditionPreferenceFormulaProperty> formulas) {
		super(type);
		this.formulas = formulas;
	}

	@Override
	public int evaluate(ExecutionSummary execution) {
		int score = this.type == CPFType.AND ? BasicDesireFormula.V_MIN
				: BasicDesireFormula.V_MAX;
		for (ConditionPreferenceFormulaProperty f : formulas) {
			if (this.type == CPFType.AND) {
				score = Math.max(score, f.evaluate(execution));
			} else if (this.type == CPFType.OR) {
				score = Math.min(score, f.evaluate(execution));
			}
		}
		return score;
	}

	@Override
	public ArrayList<String> getApplicableGoals() {
		ArrayList<String> goals = new ArrayList<String>();
		for (ConditionPreferenceFormulaProperty f : formulas) {
			goals.addAll(f.getApplicableGoals());
		}
		return goals;
	}

	@Override
	public String toString() {
		String s = super.toString() + " ";
		for (int i = 0; i < formulas.size(); i++) {
			s += "[" + formulas.get(i).toString() + "]";
			s += " ";
		}
		return s;
	}

}
