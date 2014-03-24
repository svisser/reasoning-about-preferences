package summary;

import goalplanner.Choice;
import goalplanner.ExecutionResult;
import goalplanner.GoalTreeNode;
import goalplanner.PlanGenerator.PlanTemplate;
import goalplanner.PlanTreeNode;
import goalplanner.Property;
import goalplanner.PropertySummary;
import goalplanner.ResourceSet;
import jadex.runtime.IGoal;
import jadex.runtime.Plan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

@SuppressWarnings("serial")
public class PreferencePlan extends Plan {
	
	/** The failure rate of a plan when no failure rate has been specified, */
	public static final int PLAN_FAILURE_RATE = 0;
	
	/** Contains the subgoals of a plan. */ 
	public static final HashMap<String, String[]> children;
	
	/** Contains the plan details of a plan. */
	public static final HashMap<String, PlanTemplate> templates;

	/** Random number generator for determining plan failure.*/
	public static final Random generator = new Random();
	
	/** The failure rate of this plan when the value is >= 0. */
	public int customPlanFailureRate = -1;
	
	static {
		children = new HashMap<String, String[]>();
		templates = new HashMap<String, PlanTemplate>();
	}
	
	protected int getPlanFailureRate() {
		return customPlanFailureRate >= 0 
		? customPlanFailureRate : PLAN_FAILURE_RATE;
	}
	
	@Override
	public void body() {
		boolean success = true;
		if (PreferencePlan.generator.nextInt(100) < getPlanFailureRate()) {
			success = false;
		}
		String[] children = PreferencePlan.children.get(getRawName());
		Choice choice = MasterPlan.tree.getChoices().get(getRawName());
		if (choice != null) {
			for (Integer i : choice.getChosen()) {
				String goal = children[i]; 
				IGoal rGoal = createGoal(goal);
				this.dispatchSubgoalAndWait(rGoal);
			}	
		}
		if (!success || this.isFailure()) {
			this.fail();
		}
	}

	@Override
	public void failed() {
		onPlanFinished(false);
	}

	@Override
	public void passed() {
		onPlanFinished(true);
	}
	
	/**
	 * Update the data structures (as described in thesis) when the plan is finished.
	 * @param success True if the plan completed successfully.
	 */
	private void onPlanFinished(boolean success) {
		String rawName = getRawName();
		PlanTreeNode node = (PlanTreeNode) MasterPlan.tree.getNode(rawName);
		HashSet<String> names = ((GoalTreeNode) node.getParent()).getNames();
		String[] goals = node.getParentGoals();
		PropertySummary properties = null;
		if (success) {
			properties = node.getPlanPropertySummary().createCopy();
			properties.prefixByGoal(node.getParentGoalPath());
			
			for (Property p : properties.getProperties()) {
				int numValues = p.getValues().size(); 
				if (numValues > 1) {
					Random random = new Random();
					String value = p.getValues().get(random.nextInt(numValues));
					ArrayList<String> values = new ArrayList<String>();
					values.add(value);
					p.setValues(values);
				}
			}
			
			// for processing N as described in thesis
			PropertySummary compProperties = node.getPropertySummary();
			ArrayList<Property> add = new ArrayList<Property>();
			for (String name : names) {
				for (Property p : compProperties.getProperties()) {
					if (p.getRawName().equals(name)) {
						Property n = new Property(p.getRawName(), determineNValue(p));
						properties.prefixByGoal(node.getParentGoalPath(), n);
						if (!properties.hasProperty(n.getName(), false)) {
							add.add(n);	
						}
					}
				}
			}
			for (Property m : add) {
				properties.addProperty(m);
			}
		} else {
			properties = new PropertySummary();
		}		
		ResourceSet usage = (ResourceSet) node.getPlanSummary().getNecessary().createCopy(); 
		ExecutionResult result = new ExecutionResult(rawName, goals, properties, usage, success);
		MasterPlan.tree.processExecutionResult(result);
	}
	
	// find value obtained earlier for related property (i.e., a property
	// from which the current one is derived using N, see thesis).
	protected String determineNValue(Property p) {
		ArrayList<ExecutionResult> results = MasterPlan.tree.getExecution().getResults();
		for (ExecutionResult r : results) {
			PropertySummary ps = r.getProperties();
			for (Property q : ps.getProperties()) {
				if (q.getName().endsWith(p.getName())) {
					return q.getValues().get(0);
				}
			}
		}
		return p.getValues().get(0);
	}
	
	protected String getRawName() {
		return PreferencePlan.getRawName(getName());
	}
	public static String getRawName(String name) {
		int hashIndex = name.indexOf("#");
		return name.substring(0, hashIndex);
	}
	public boolean isFailure() {
		return false;
	}


}
