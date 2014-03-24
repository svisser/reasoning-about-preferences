package bienvenu;

import goalplanner.ExecutionSummary;
import goalplanner.TreeNode;

import java.util.ArrayList;

public class ConditionPreferenceFormulaProperty extends ConditionPreferenceFormula {
	
	/** The name of the property. */
	private String name;
	
	/** The value of the property. */
	private String value;

	/**
	 * Construct a conditional preference formula for name = value
	 * and name =/= value conditions.
	 * @param type Must be PROP_EQUALS or PROP_NOT_EQUALS
	 * @param name The name of the property
	 * @param value The desired or undesired name of the property
	 */
	public ConditionPreferenceFormulaProperty(CPFType type, String name, String value) {
		super(type);
		this.name = name;
		this.value = value;
	}

	@Override
	public int evaluate(ExecutionSummary execution) {
		boolean hasIt = execution.hasPropertyValue(this.name, this.value);
		if (this.type.equals(CPFType.PROP_EQUALS)) {
			return hasIt ? BasicDesireFormula.V_MIN : BasicDesireFormula.V_MAX; 
		} else if (this.type.equals(CPFType.PROP_NOT_EQUALS)) {
			return !hasIt ? BasicDesireFormula.V_MIN : BasicDesireFormula.V_MAX;
		}
		// should not occur
		return BasicDesireFormula.V_MAX;
	}

	@Override
	public String toString() {
		return this.name + (type.equals(CPFType.PROP_EQUALS) ? " = " : " =/= ") + this.value;
	}

	@Override
	public ArrayList<String> getApplicableGoals() {
		ArrayList<String> result = new ArrayList<String>();
		result.add(ConditionPreferenceFormulaProperty.extractGoal(name));
		return result;
	}
	
	public static String extractGoal(String name) {
		int endIndex = name.lastIndexOf(TreeNode.PATH_SEPARATOR);
		if (endIndex < 0) endIndex = name.length() - 1;
		int startIndex = name.lastIndexOf(TreeNode.PATH_SEPARATOR, endIndex - 1);
		if (startIndex < 0) startIndex = 0;
		return name.substring(startIndex, endIndex);
	}

}
