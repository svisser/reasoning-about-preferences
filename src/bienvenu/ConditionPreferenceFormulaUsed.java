package bienvenu;

import goalplanner.ExecutionSummary;

import java.util.ArrayList;

/**
 * The predicate used(..) as described in the paper, although the paper
 * only describes < whereas this class implements, <, <=, =, >=, and >.
 *
 */
public class ConditionPreferenceFormulaUsed extends ConditionPreferenceFormula {
	
	protected enum Comparator {
		LT("<"),
		LTE("<="),
		E("="),
		GTE(">="),
		GT(">");
		
		private String name;
		
		Comparator(String name) {
			this.name = name;
		}
		
		public static Comparator determineComparison(String name) {
			for (Comparator c : Comparator.values()) {
				if (c.name.equals(name)) {
					return c;
				}
			}
			return null;
		}
		
		public boolean compare(int left, int right) {
			if (this == LT) return left < right;
			if (this == LTE) return left <= right;
			if (this == E) return left == right;
			if (this == GTE) return left >= right;
			if (this == GT) return left > right;
			return false;
		}

		public String toString() {
			return this.name;
		}		
	}
	
	/** The name of the goal to which this condition applies. */
	protected String goal;
	
	/** The name of the resource for which the usage should be checked. */
	protected String resource;
	
	/** The amount of usage of the resource that we should compare with. */
	protected int amount;
	
	/** The comparative operator used to compare the amount with. */
	protected Comparator comparator;
	
	public ConditionPreferenceFormulaUsed(String goal, String resource, int amount, String comparison) {
		super(CPFType.USED);
		this.goal = goal;
		this.resource = resource;
		this.amount = amount;
		this.comparator = Comparator.determineComparison(comparison);
	}
	
	@Override
	public int evaluate(ExecutionSummary execution) {
		int usage = execution.getResourceUsage(this.goal, this.resource);
		return this.comparator.compare(usage, this.amount) ?
				BasicDesireFormula.V_MIN : BasicDesireFormula.V_MAX;
	}

	@Override
	public String toString() {
		return this.goal + ", " + this.resource + ", " + this.amount + ", " + this.comparator;
	}

	@Override
	public ArrayList<String> getApplicableGoals() {
		ArrayList<String> result = new ArrayList<String>();
		result.add(this.goal);
		return result;
	}

}
