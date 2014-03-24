package goalplanner;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The metadata M as described in the thesis.
 *
 */
public class ExecutionSummary {

	/** Contains the goals that have succeeded during execution so far. */
	private ArrayList<String> successGoals;
	
	/** Contains the goal that have failed during execution so far. */
	private ArrayList<String> failedGoals;

	/** Contains the results of each plan execution. */
	private ArrayList<ExecutionResult> results;
	
	/** The names and values of properties achieved thus far. */
	private HashMap<String, String> values;
	
	public ExecutionSummary() {
		successGoals = new ArrayList<String>();
		failedGoals  = new ArrayList<String>();
		results = new ArrayList<ExecutionResult>();
		values = new HashMap<String, String>();
	}

	/** Store the name of a property and its received value. */
	public void addProperty(String key, String value) {
		values.put(key, value);
	}
	
	/** Return true iff the goal named 'goal' has succeeded. */
	public boolean hasSucceeded(String goal) {
		return successGoals.contains(goal);
	}
	
	/** Return true iff the goal named 'goal' has failed. */
	public boolean hasFailed(String goal) {
		return failedGoals.contains(goal);
	}
	
	/** Return true iff the goal named 'goal' has finished execution. */
	public boolean hasFinished(String goal) {
		return hasSucceeded(goal) || hasFailed(goal);
	}
	
	public void addExecutionResult(ExecutionResult result) {
		results.add(result);
	}
	
	public void addSuccessGoal(String name) {
		successGoals.add(name);
	}
	
	public void addFailedGoal(String name) {
		failedGoals.add(name);
	}

	public ArrayList<ExecutionResult> getResults() {
		return results;
	}
	
	// return the amount of a particular resource has been used
	// in the execution of plans for the specified goal
	public int getResourceUsage(String goal, String resource) {
		ArrayList<ResourceSet> resources = new ArrayList<ResourceSet>();
		for (ExecutionResult r : results) {
			if (r.isApplicableTo(goal)) {
				resources.add(r.usage.createCopy());	
			}
		}
		ResourceSet set = ResourceSet.mergeResources(resources);
		return set == null ? 0 : set.get(resource);
	}
	
	public String toString() {
		String s = "<";
		s += "succeeded: " + successGoals.toString();
		s += ", ";
		s += "failed: " + failedGoals.toString();
		s += ">";
		return s;
	}

	public void clear() {
		this.results.clear();
		this.successGoals.clear();
		this.failedGoals.clear();
	}

	public static void displayOutcome(GoalPlanTree tree) {
		ExecutionSummary execution = tree.getExecution();
		ArrayList<ExecutionResult> results = execution.getResults();
		System.out.println(tree);
		System.out.println();
		System.out.println("Plans executed: " + results.size() + " - Outcome: " + tree.getRoot().getStatus());
		ArrayList<ResourceSet> resources = new ArrayList<ResourceSet>();
		for (ExecutionResult r : results) {
			System.out.println("- " + r);
			resources.add(r.usage.createCopy());
		}
		System.out.println("Properties:");
		for (ExecutionResult r : results) {
			System.out.println("- " + r.getProperties().toString());
		}
		System.out.println("Resources:");
		System.out.println("- " + ResourceSet.mergeResources(resources));
		System.out.println("Execution summary:");
		System.out.println(execution);
		System.out.println("Root properties:");
		PropertySummary summary = tree.getRoot().propertySummary;
		for (Property p : summary.properties) {
			System.out.println("- " + p.toString());
		}
	}

	public boolean hasPropertyValue(String name, String value) {
		return value.equals(this.values.get(name));
	}
	
}
