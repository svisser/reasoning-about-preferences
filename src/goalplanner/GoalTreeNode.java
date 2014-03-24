package goalplanner;

import java.util.ArrayList;
import java.util.HashSet;

public final class GoalTreeNode extends TreeNode {
	
	/** Data structure N as described in the thesis. */
	public HashSet<String> names = new HashSet<String>();
	
	/** The current goal should be executed before these goals. */
	public ArrayList<String> orderings = new ArrayList<String>();
	
	/** The human readable name of this goal node. */ 
	protected String humanReadableName;
	
	public GoalTreeNode() {
		super();
	}

	public GoalTreeNode(String name) {
		super(name);
	}
	
	public GoalTreeNode(String name, String humanReadableName) {
		this(name);
		this.humanReadableName = humanReadableName;
	}

	@Override
	public String onPrintNode(int depth) {
		String s = "";
		s += TreeNode.indent(depth) + " " + this.resourceSummary + "\n";
		s += TreeNode.indent(depth) + " " + this.effectSummary + "\n";
		if (!propertySummary.isEmpty()) {
			s += TreeNode.indent(depth) + " " + propertySummary.toString() + "\n";
		}
		return s;
	}

	@Override
	public EffectSummary onComputeEffectSummary(EffectSummary[] children) {
		EffectSummary result = children[0];
		for (int i = 1; i < children.length; i++) {
			result = result.multiply(children[i]);
		}
		this.effectSummary = result;
		return result;
	}

	@Override
	public ResourceSummary onComputeResourceSummary(ResourceSummary[] children) {
		ResourceSummary result = children[0];
		for (int i = 1; i < children.length; i++) {
			result = result.plusCombine(children[i]);
		}
		this.resourceSummary = result;
		return result;
	}

	public void addOrdering(String after) {
		this.orderings.add(after);
	}

	@Override
	public PropertySummary onComputeProperties(PropertySummary[] children) {
		// first, add all property summaries of children together
		PropertySummary result = new PropertySummary(children[0]);
		for (int i = 1; i < children.length; i++) {
			result = result.addGoal(children[i]);
		}
		// find those that occur multiple times but whose raw name is present
		for (Property p : result.properties) {
			if (p.hasRawName()) {
				for (Property q : result.properties) {
					if (!q.hasRawName() && q.getRawName().equals(p.name)) {
						this.names.add(p.name);
						break;
					}
				}
			}
		}
		
		// add new properties for all names to be added
		for (String name : this.names) {
			Property newProp = new Property(name, result.getValuesOfProperty(name));
			if (result.hasProperty(name, false)) {
				result.removeProperty(name);
			}
			// clear null value if needed
			boolean clearNull = true;
			for (int j = 0; j < children.length; j++) {
				if (!children[j].hasProperty(name, true)) {
					clearNull = false;
					break;
				}
			}
			if (clearNull) {
				newProp.clearNull();
			}
			result.addProperty(newProp);
		}
		
		this.propertySummary = result;
		return result;
	}

	public String getHumanReadableName() {
		return humanReadableName;
	}

	public void setHumanReadableName(String humanReadableName) {
		this.humanReadableName = humanReadableName;
	}

	@Override
	public String onPrintName() {
		String name = getName();
		String humanReadableName = getHumanReadableName();
		if (humanReadableName  != null && !name.equals(humanReadableName)) {
			name += " / ";
			name += humanReadableName;
		}
		return name;
	}

	public HashSet<String> getNames() {
		return names;
	}


}
