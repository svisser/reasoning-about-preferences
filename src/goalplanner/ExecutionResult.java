package goalplanner;


public class ExecutionResult {
	
	/** The name of the plan to which this execution result belongs to. */
	protected String name;
	
	/** If true, the plan was executed successfully. */
	private boolean success = false;
	
	private PropertySummary properties;
	
	/** The resources that were used during this plan's execution. */
	protected ResourceSet usage;
	
	/** The goals to which this plan's execution is related. */
	protected String[] goals;
	
	public ExecutionResult(String name, String[] goals
			, PropertySummary properties, ResourceSet usage, boolean success) {
		this.name = name;
		this.goals = goals;
		this.setProperties(properties);
		this.usage = usage;
		this.success = success;
	}
	
	/**
	 * Return true if this execution result is related to the execution
	 * of the specified goal.
	 * 
	 * @param goal: The goal for which applicability needs to be checked.
	 * @return: true if this plan is part of the execution of the given goal.
	 */
	public boolean isApplicableTo(String goal) {
		for (String node : goals) {
			if (node.equals(goal)) {
				return true;
			}
		}
		return false;
	}
	
	public String toString() {
		String s = name;
		s += " (" + (success ? "success" : "failure") + "): ";
		s += usage.toString();
		s += " ";
		s += getProperties().toString();
		return s;
	}

	public boolean isSuccess() {
		return success;
	}

	public ResourceSet getUsage() {
		return usage;
	}

	public void setUsage(ResourceSet usage) {
		this.usage = usage;
	}

	public String getName() {
		return name;
	}

	public void setProperties(PropertySummary properties) {
		this.properties = properties;
	}

	public PropertySummary getProperties() {
		return properties;
	}

}
