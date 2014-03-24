package bienvenu;

import goalplanner.ExecutionSummary;
import goalplanner.TreeNode;

import java.util.ArrayList;
import java.util.Arrays;

public class GeneralPreferenceFormulaCondition extends GeneralPreferenceFormula {

	/** The condition in the general preference formula. */
	private ConditionPreferenceFormula condition;
	
	/** The atomic preference part of this general preference formula. */
	private AtomicPreferenceFormula formula;
	
	public GeneralPreferenceFormulaCondition(ConditionPreferenceFormula condition
			, AtomicPreferenceFormula formula) {
		this.type = GPFType.CONDITIONAL;
		this.condition = condition;
		this.formula = formula;
	}
	
	@Override
	public int[] onEvaluate(TreeNode node, ArrayList<TreeNode> nodes, ExecutionSummary execution) {
		int value = condition.evaluate(execution);
		if (value == BasicDesireFormula.V_MAX) {
			int[] scores = new int[node.getNumberOfChildren()];
			Arrays.fill(scores, BasicDesireFormula.V_MIN);
			return scores;
		}
		return formula.evaluate(node, nodes, execution);
	}

	@Override
	public String onToString() {
		return condition.toString() + " : " + formula.toString();
	}
	
	public ConditionPreferenceFormula getCondition() {
		return condition;
	}

	@Override
	public ArrayList<String> getConditionGoals() {
		return condition.getApplicableGoals();
	}

	@Override
	public ArrayList<String> getAtomicFormulaGoals() {
		return formula.getAtomicFormulaGoals();
	}

}
