package bienvenu;

import goalplanner.ExecutionSummary;
import goalplanner.TreeNode;

import java.util.ArrayList;

public abstract class GeneralPreferenceFormula extends PreferenceFormula {

	/** The type of general preference formula. */
	public GPFType type;
	
	/** The names of goals in a conditional preference formula in this formula. */
	public abstract ArrayList<String> getConditionGoals();

	/** The names of goals in the atomic preference part of this formula. */
	public abstract ArrayList<String> getAtomicFormulaGoals();
	
	/**
	 * The function w(G, P, M) as described in the paper. The implemented
	 * function computes w(G, P, M) for each plan P and returns an int[]
	 * with the values.
	 */
	public abstract int[] onEvaluate(TreeNode node, ArrayList<TreeNode> nodes, ExecutionSummary execution);
	
	public abstract String onToString();
	
	public enum GPFType {
		ATOMIC("atomic"),
		CONDITIONAL("conditional");
		
		private String name;
		
		private GPFType(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return this.name;
		}
	}
	
	@Override
	public int[] evaluate(TreeNode node, ArrayList<TreeNode> nodes, ExecutionSummary execution) {
		return onEvaluate(node, nodes, execution);
	}
	
	public String toString() {
		return onToString();
	}

}
