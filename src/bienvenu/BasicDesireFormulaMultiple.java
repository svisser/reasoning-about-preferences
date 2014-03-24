package bienvenu;

import goalplanner.ExecutionSummary;
import goalplanner.TreeNode;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Represents a conjunction or disjunction of name = value or
 * name =/= value formulas
 */
public class BasicDesireFormulaMultiple extends BasicDesireFormula {

	/** The name = value or name =/= value formulas. */
	private ArrayList<BasicDesireFormulaProperty> formulas;
	
	/**
	 * Construct a basic desire formula for properties
	 * @param type Type of basic desire formula, must be AND or OR
	 * @param formulas The formulas in the conjunction or disjunction
	 */
	public BasicDesireFormulaMultiple(BDFType type, ArrayList<BasicDesireFormulaProperty> formulas) {
		super(type);
		this.formulas = formulas;
	}

	@Override
	public int[] onEvaluate(TreeNode node, ArrayList<TreeNode> nodes,
			ExecutionSummary execution) {
		int[] scores = new int[nodes.size()];
		Arrays.fill(scores, this.type == BDFType.AND ? V_MIN : V_MAX);
		for (BasicDesireFormulaProperty f : formulas) {
			int[] fScores = f.evaluate(node, nodes, execution);
			for (int i = 0; i < fScores.length; i++) {
				if (this.type == BDFType.AND) {
					scores[i] = Math.max(scores[i], fScores[i]);
				} else if (this.type == BDFType.OR) {
					scores[i] = Math.min(scores[i], fScores[i]);
				}
			}
		}
		return scores;
	}

	@Override
	public String onToString() {
		return this.type + " " + formulas.toString();
	}

	@Override
	public ArrayList<String> getGoals() {
		ArrayList<String> goals = new ArrayList<String>();
		for (BasicDesireFormulaProperty f : formulas) {
			goals.addAll(f.getGoals());
		}
		return goals;
	}

}
