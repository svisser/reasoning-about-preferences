package goalplanner;

import goalplanner.PlanGenerator.PlanTemplate;
import goalplanner.ResourceSet.ResourceType;

import java.util.ArrayList;
import java.util.HashMap;

public final class PlanTreeNode extends TreeNode {
	
	/** The programmer-specified resource usage of this plan. */
	protected ResourceSummary planResourceSummary;
	
	/** The programmer-specified effects of this plan. */
	protected EffectSummary planEffectSummary;
	
	/** Programmer-specified properties of this node. */
	protected PropertySummary planPropertySummary;
	
	/** The order in which subgoals should be performed. */
	protected HashMap<Ordering, Integer> orderings = new HashMap<Ordering, Integer>();
	
	public PlanTreeNode(PlanTemplate template) {
		this(null, template);
	}
	
	public PlanTreeNode(String name, PlanTemplate t) {
		this(name, t.resources, t.types, t.amounts, t.effects, t.properties);
	}
	
	public PlanTreeNode(String name, String[] resources, ResourceType[] types
			, int[] amounts, String[] effects, ArrayList<Property> properties) {
		if (name != null) { 
			this.name = name;			
		}
		this.planResourceSummary = new ResourceSummary(resources, types, amounts);
		this.planEffectSummary = new EffectSummary(this.name, effects);
		this.planPropertySummary = new PropertySummary(properties);
	}

	public ResourceSummary getPlanSummary() {
		return planResourceSummary;
	}

	public EffectSummary getPlanEffectSummary() {
		return planEffectSummary;
	}

	public String[] getParentGoals() {
		ArrayList<String> goals = new ArrayList<String>();
		TreeNode parent = getParent();
		while (parent != null) {
			if (parent instanceof GoalTreeNode) {
				goals.add(parent.getName());				
			}
			parent = parent.getParent();
		}
		String[] goalsArray = new String[goals.size()];
		for (int i = 0; i < goalsArray.length; i++) {
			goalsArray[i] = goals.get(i);
		}
		return goalsArray;
	}

	@Override
	public String onPrintNode(int depth) {
		String s = "";
		ResourceSummary planSummary = ((PlanTreeNode) this).planResourceSummary;
		EffectSummary planEffectSummary = ((PlanTreeNode) this).planEffectSummary;
		if (!this.children.isEmpty()) {
			s += TreeNode.indent(depth) + " " + ResourceSummary.toCombinedString(this.resourceSummary, planSummary) + "\n";
			s += TreeNode.indent(depth) + " " + this.effectSummary + "\n";
		} else {
			s += TreeNode.indent(depth) + " " + planSummary.necessary.toString() + "\n";
		}
		s += TreeNode.indent(depth) + " " + planEffectSummary.definite.toString(false) + "\n";
		if (!planPropertySummary.isEmpty()) {
			s += TreeNode.indent(depth) + " " + planPropertySummary.toString() + "\n";	
		}
		if (!propertySummary.isEmpty()) {
			s += TreeNode.indent(depth) + " " + propertySummary.toString() + "\n";
		}
		return s;
	}

	@Override
	public EffectSummary onComputeEffectSummary(EffectSummary[] children) {
		EffectSummary result = children[0];
		for (int i = 1; i < children.length; i++) {
			result = result.plus(children[i]);
		}
		result = result.plus(this.planEffectSummary);
		this.effectSummary = result;
		return result;
	}

	@Override
	public ResourceSummary onComputeResourceSummary(ResourceSummary[] children) {
		ResourceSummary result = ((PlanTreeNode) this).planResourceSummary;
		for (int i = 0; i < children.length; i++) {
			result = result.add(children[i]);
		}
		this.resourceSummary = result;
		return result;
	}

	@Override
	public PropertySummary onComputeProperties(PropertySummary[] children) {
		PropertySummary result = new PropertySummary(planPropertySummary);
		for (int i = 0; i < children.length; i++) {
			PropertySummary cs = new PropertySummary(children[i]);
			cs.prefixByGoal(getHumanReadableName(i));
			result = result.addPlan(cs);
		}
		this.propertySummary = result;
		return result;
	}	
	
	public void addOrdering(Ordering ordering) {
		int count = 1;
		for (Ordering o : this.orderings.keySet()) {
			if (o.isSameOrdering(ordering)) {
				count++;
			}
		}
		this.orderings.put(ordering, count);
	}
	
	public String getHumanReadableName(int index) {
		return ((GoalTreeNode) children.get(index)).getHumanReadableName();
	}

	public HashMap<Ordering, Integer> getOrderings() {
		return this.orderings;
	}
	
	public String[] getHumanReadableChildNodes() {
		String[] nodes = new String[children.size()];
		for (int i = 0; i < children.size(); i++) {
			GoalTreeNode goal = (GoalTreeNode) children.get(i);
			String name = goal.humanReadableName;
			if (name == null) {
				name = goal.name;
			}
			nodes[i] = name;
		}
		return nodes;
	}

	@Override
	public String onPrintName() {
		return getName();
	}

	public PropertySummary getPlanPropertySummary() {
		return planPropertySummary;
	}

}
