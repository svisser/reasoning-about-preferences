package bienvenu;

import goalplanner.ExecutionSummary;

import java.util.ArrayList;

public abstract class ConditionPreferenceFormula {
	
	/** The type of conditional preference formula. */
	protected CPFType type;
	
	public enum CPFType {
		SUCCESS("success"),
		FAILURE("failure"),
		PROP_EQUALS("prop_equal"),
		PROP_NOT_EQUALS("prop_not_equal"),
		USED("used"),
		AND("and"),
		OR("or");
		
		private String name;
		
		private CPFType(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return this.name;
		}
	}
	
	/**
	 * Construct a conditional preference formula with the given type.
	 * @param type The type of conditional preference formula
	 */
	public ConditionPreferenceFormula(CPFType type) {
		this.type = type;
	}

	/** Evaluate this conditional preference formula using metadata M. */
	public abstract int evaluate(ExecutionSummary execution);
	
	/**
	 * Return the set of goal names that this conditional preference formula
	 * applies to.
	 */
	public abstract ArrayList<String> getApplicableGoals();

	public String toString() {
		return this.type.toString();
	}
	
}
