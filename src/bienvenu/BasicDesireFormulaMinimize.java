package bienvenu;

import goalplanner.ExecutionSummary;
import goalplanner.ResourceSummary;
import goalplanner.TreeNode;

import java.util.ArrayList;
import java.util.Arrays;

public class BasicDesireFormulaMinimize extends BasicDesireFormula {
	
	public enum BDFHeuristic {
		MINNECPOS, KESTIMATE // as described in the paper
	}
	
	/** The heuristic to use to compute the set S. */
	public BDFHeuristic heuristic = BDFHeuristic.MINNECPOS;

	public BasicDesireFormulaMinimize(String resource) {
		super(BDFType.MINIMIZE, resource);
	}
	
	public BasicDesireFormulaMinimize(String goal, String resource) {
		super(BDFType.MINIMIZE, resource);
		this.goal = goal;
	}
	
	@Override
	public int[] onEvaluate(TreeNode node, ArrayList<TreeNode> nodes, ExecutionSummary execution) {
		if (this.heuristic == BDFHeuristic.MINNECPOS) {
			return computeMinNecPos(node, nodes);
		} else if (this.heuristic == BDFHeuristic.KESTIMATE) {
			return computeKEstimate(node, nodes);
		}
		return null; // should not occur
	}
	
	private int[] computeMinNecPos(TreeNode node, ArrayList<TreeNode> nodes) {
		int[] scores = new int[nodes.size()];
		Arrays.fill(scores, V_MAX);
		int[] nec = new int[nodes.size()];
		int[] pos = new int[nodes.size()];
		int minNec = Integer.MAX_VALUE;
		for (int i = 0; i < nodes.size(); i++) {
			if (node.getChildren().get(i).getStatus().equals(TreeNode.Status.FAILURE)) {
				continue;
			}
			ResourceSummary resourceSummary = node.getChildren().get(i).getResourceSummary();
			nec[i] = resourceSummary.getNecessary().get(this.value);
			pos[i] = resourceSummary.getPossible().get(this.value);
			if (nec[i] < minNec) {
				minNec = nec[i];
			}
		}
		int count = 0;
		for (int i = 0; i < nec.length; i++) {
			if (nec[i] == minNec) {
				scores[i] = V_MIN;
				count++;
			}
		}
		if (count > 1) {
			int minPos = Integer.MAX_VALUE;
			for (int i = 0; i < scores.length; i++) {
				if (scores[i] == V_MIN && pos[i] < minPos) {
					minPos = pos[i];
				}
			}
			for (int i = 0; i < scores.length; i++) {
				if (scores[i] == V_MIN && pos[i] != minPos) {
					scores[i] = V_MAX;
				}
			}
		}
		return scores;
	}
	
	private int[] computeKEstimate(TreeNode node, ArrayList<TreeNode> nodes) {
		int[] values = BasicDesireFormulaMinimize.computeKValues(nodes, this.value, 0);
		int[] scores = new int[nodes.size()];
		Arrays.fill(scores, V_MAX);
		double minValue = Double.MAX_VALUE;
		for (int i = 0; i < nodes.size(); i++) {
			if (values[i] < minValue) {
				minValue = values[i];
			}
		}
		for (int i = 0; i < scores.length; i++) {
			if (values[i] == minValue) {
				scores[i] = V_MIN;
			}
		}
		return scores;
	}
	
	public static int[] computeKValues(ArrayList<TreeNode> nodes, String resource, double k) {
		int[] values = new int[nodes.size()];
		Arrays.fill(values, -1);
		for (int i = 0; i < nodes.size(); i++) {
			ResourceSummary resourceSummary = nodes.get(i).getResourceSummary();
			int nec = resourceSummary.getNecessary().get(resource);
			int pos = resourceSummary.getPossible().get(resource);
			values[i] = (int) (nec + k * (pos - nec));
		}
		return values;
	}
	
	@Override
	public String toString() {
		return (goal != null ? goal + "." : "") + "minimize(" + value + ")";
	}

	@Override
	public String onToString() {
		return null;
	}

}
