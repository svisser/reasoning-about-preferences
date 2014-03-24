package bienvenu;

import goalplanner.ExecutionSummary;
import goalplanner.TreeNode;

import java.util.ArrayList;

public abstract class PreferenceFormula {

	/** The preferences of the agent. */
	public static AggregatedPreferenceFormula prefs;

	/** Evaluate the node's children for plan selection. */
	public abstract int[] evaluate(TreeNode node, ArrayList<TreeNode> nodes, ExecutionSummary execution);
}
