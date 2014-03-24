package bienvenu;

import goalplanner.ExecutionSummary;
import goalplanner.TreeNode;

import java.util.ArrayList;
import java.util.Arrays;

public class AtomicPreferenceFormula extends GeneralPreferenceFormula {
	
	public enum APFSemantics {
		BIENVENU, // assigned value = value of lowest BDF formula
		AVERAGE; // assigned value = average of applicable BDF formulas
	}
	
	/** The ordered list of preference formulas. */
	public BasicDesireFormula[] formulas;

	/** The values associated with each formula. */
	public int[] values;
	
	/** The evaluation strategy in case multiple formulas are applicable. */
	public APFSemantics semantics = APFSemantics.BIENVENU;

	public AtomicPreferenceFormula(BasicDesireFormula[] formulas, int[] values) {
		this.type = GPFType.ATOMIC;
		this.formulas = formulas;
		this.values = values;
	}
	
	public AtomicPreferenceFormula(ArrayList<BasicDesireFormula> formulas, ArrayList<Integer> values) {
		this.type = GPFType.ATOMIC;
		this.formulas = new BasicDesireFormula[formulas.size()];
		for (int i = 0; i < this.formulas.length; i++) {
			this.formulas[i] = formulas.get(i);
		}
		this.values = new int[values.size()];
		for (int i = 0; i < this.values.length; i++) {
			this.values[i] = values.get(i);
		}
	}
	
	@Override
	public int[] onEvaluate(TreeNode node, ArrayList<TreeNode> nodes, ExecutionSummary execution) {
		int[] scores = new int[nodes.size()];
		if (semantics == APFSemantics.BIENVENU) {
			Arrays.fill(scores, BasicDesireFormula.V_MAX);
			boolean[] done = new boolean[nodes.size()];
			for (int i = 0; i < formulas.length; i++) {
				int[] result = formulas[i].evaluate(node, nodes, execution);
				for (int j = 0; j < result.length; j++) {
					if (done[j]) continue;
					if (result[j] == BasicDesireFormula.V_MIN) {
						scores[j] = values[i];
						done[j] = true;
					}
				}
			}
		} else if (semantics == APFSemantics.AVERAGE) {
			Arrays.fill(scores, BasicDesireFormula.V_MIN);
			int[] counts = new int[nodes.size()];
			for (int i = 0; i < formulas.length; i++) {
				int[] result = formulas[i].evaluate(node, nodes, execution);
				for (int j = 0; j < result.length; j++) {
					if (result[j] == BasicDesireFormula.V_MIN) {
						scores[j] += values[i];
						counts[j]++;
					}
				}
			}
			for (int i = 0; i < scores.length; i++) {
				if (counts[i] > 0) {
					scores[i] /= counts[i];
				} else {
					scores[i] = BasicDesireFormula.V_MAX;
				}
			}
		}
		return scores;
	}

	@Override
	public String onToString() {
		String s = "";
		for (int i = 0; i < formulas.length; i++) {
			BasicDesireFormula f = this.formulas[i];
			int value = this.values[i];
			s += f.toString();
			s += " (";
			s += value;
			s += ")";
			if (i < formulas.length - 1) {
				s += " >> ";
			}
		}
		return s;
	}

	@Override
	public ArrayList<String> getConditionGoals() {
		return new ArrayList<String>();
	}

	@Override
	public ArrayList<String> getAtomicFormulaGoals() {
		ArrayList<String> goals = new ArrayList<String>();
		for (BasicDesireFormula f : formulas) {
			goals.addAll(f.getGoals());
		}
		return goals;
	}
	
}
