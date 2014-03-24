package bienvenu;

import goalplanner.ExecutionSummary;
import goalplanner.TreeNode;

import java.util.ArrayList;

public abstract class BasicDesireFormula extends PreferenceFormula {

	/** The value v_{min} as minimum value of a preference formula. */
	public static final int V_MIN = 0;
	
	/** The value v_{max} as maximum value of a preference formula. */
	public static final int V_MAX = 100;
	
	public abstract int[] onEvaluate(TreeNode node
			, ArrayList<TreeNode> nodes, ExecutionSummary execution);
	
	public abstract String onToString();

	/** The goal to which this basic desire formula applies, if any. */
	public String goal;
	
	public ArrayList<String> getGoals() {
		ArrayList<String> result = new ArrayList<String>();
		if (this.goal != null) {
			result.add(this.goal);	
		}
		return result;
	}
	
	/** The type of basic desire formula. */
	public BDFType type;
	
	/** The value that is stored depends on the type of
	 * basic desire formula, such as a resource name.
	 */
	public String value;
	
	public enum BDFType {
		MINIMIZE("minimize"),
		AND("and"),
		OR("or"),
		USAGE("usage"),
		PROP_EQUALS("prop_equals"),
		PROP_NOT_EQUALS("prop_not_equals");
		
		private String name;
		
		private BDFType(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return this.name;
		}
	}
	
	public BasicDesireFormula(BDFType type) {
		this.type = type;
	}
	
	public BasicDesireFormula(BDFType type, String value) {
		this(type);
		this.value = value;
	}

	@Override
	public int[] evaluate(TreeNode node, ArrayList<TreeNode> nodes, ExecutionSummary execution) {
		return onEvaluate(node, nodes, execution);
	}
	
	public String toString() {
		return type.toString() + "(" + onToString() + ")";
	}

}
