package bienvenu;

import goalplanner.ExecutionSummary;
import goalplanner.ResourceSummary;
import goalplanner.TreeNode;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Basic desire formula for the usage(resource, amount, comparator)
 * predicate.
 *
 */
public class BasicDesireFormulaUsage extends BasicDesireFormula {
	
	/** The name of the resource. */ 
	protected String resource;
	
	/** The amount to which the usage should be compared. */
	protected int amount;
	
	public enum Comparator {
		L("<"), LEQ("<="), EQ("="), GEQ(">="), G(">");
		
		private String name;
		
		private Comparator(String name) {
			this.name = name;
		}
		
		public boolean performComparison(int v1, int v2) {
			if (this.equals(L)) return v1 < v2;
			if (this.equals(LEQ)) return v1 <= v2;
			if (this.equals(EQ)) return v1 == v2;
			if (this.equals(GEQ)) return v1 >= v2;
			if (this.equals(G)) return v1 > v2;
			return false;
		}
		
		@Override
		public String toString() {
			return this.name;
		}
	}
	
	/** The comparator used to compare the actual to the desire usage. */
	protected Comparator comparator;
	
	public BasicDesireFormulaUsage(String resource, int amount, Comparator comparator) {
		super(BDFType.USAGE, resource);
		this.resource = resource;
		this.amount = amount;
		this.comparator = comparator;
	}
	
	public BasicDesireFormulaUsage(String goal, String resource, int amount, Comparator comparator) {
		super(BDFType.USAGE, resource);
		this.goal = goal;
		this.resource = resource;
		this.amount = amount;
		this.comparator = comparator;
	}
	
	@Override
	public int[] onEvaluate(TreeNode node, ArrayList<TreeNode> nodes, ExecutionSummary execution) {
		int[] nec = new int[nodes.size()];
		int[] pos = new int[nodes.size()];
		for (int i = 0; i < nodes.size(); i++) {
			ResourceSummary resourceSummary = nodes.get(i).getResourceSummary();
			nec[i] = resourceSummary.getNecessary().get(this.value);
			pos[i] = resourceSummary.getPossible().get(this.value);
		}
		int[] scores = new int[nodes.size()];
		Arrays.fill(scores, V_MAX);
		if (this.comparator.equals(Comparator.L)
				|| this.comparator.equals(Comparator.LEQ)) {
			int count = 0;
			for (int i = 0; i < pos.length; i++) {
				if (this.comparator.performComparison(pos[i], amount)) {
					scores[i] = V_MIN;
					count++;
				}
			}
			if (count == 0) {
				for (int i = 0; i < nec.length; i++) {
					if (this.comparator.performComparison(nec[i], amount)) {
						scores[i] = V_MIN;
					}
				}
			}
		} else if (this.comparator.equals(Comparator.EQ)) {
			int[] values = BasicDesireFormulaMinimize.computeKValues(nodes, resource, 0);
			int minDistance = Integer.MAX_VALUE;
			for (int i = 0; i < nodes.size(); i++) {
				if ((values[i] - amount) < minDistance) {
					minDistance = values[i] - amount;
				}
			}
			for (int i = 0; i < nodes.size(); i++) {
				if (values[i] - amount == minDistance) {
					scores[i] = V_MIN;
				}
			}
			
		} else if (this.comparator.equals(Comparator.GEQ)
				|| this.comparator.equals(Comparator.G)) {
			int count = 0;
			for (int i = 0; i < nec.length; i++) {
				if (this.comparator.performComparison(nec[i], amount)) {
					scores[i] = V_MIN;
					count++;
				}
			}
			if (count == 0) {
				int[] values = BasicDesireFormulaMinimize.computeKValues(nodes, resource, 0);
				for (int i = 0; i < scores.length; i++) {
					if (values[i] > amount) {
						scores[i] = V_MIN;
					}
				}
			}
		}
		return scores;
	}

	@Override
	public String onToString() {
		return resource + ", " + amount + ", " + comparator;
	}

}
