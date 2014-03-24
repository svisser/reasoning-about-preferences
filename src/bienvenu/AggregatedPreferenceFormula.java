package bienvenu;

import goalplanner.ExecutionSummary;
import goalplanner.TreeNode;

import java.util.ArrayList;

// this is our algorithm as described in the paper
public class AggregatedPreferenceFormula extends PreferenceFormula {

	/** The set of user preference formulas. */
	protected ArrayList<GeneralPreferenceFormula> formulas;
	
	public AggregatedPreferenceFormula(ArrayList<GeneralPreferenceFormula> formulas) {
		this.formulas = formulas;
	}
	
	@Override
	public int[] evaluate(TreeNode node, ArrayList<TreeNode> nodes,
			ExecutionSummary execution) {
		int[] scores = new int[nodes.size()];
		for (int j = 0; j < formulas.size(); j++) {
			int[] fScores = formulas.get(j).evaluate(node, nodes, execution);
			for (int i = 0; i < nodes.size(); i++) {
				scores[i] += fScores[i];
			}
		}
		return scores;
	}
}
