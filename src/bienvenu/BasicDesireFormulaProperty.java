package bienvenu;

import goalplanner.ExecutionSummary;
import goalplanner.Property;
import goalplanner.PropertySummary;
import goalplanner.TreeNode;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Basic desire formula representing name = value or name =/= value
 * for properties.
 *
 */
public class BasicDesireFormulaProperty extends BasicDesireFormula {

	/** The property name. */
	protected String propName;
	
	/** The desired or undesired name of the property. */
	protected String propValue;
	
	public BasicDesireFormulaProperty(BDFType type, String name, String value) {
		super(type);
		this.propName = name;
		this.propValue = value;
		this.goal = ConditionPreferenceFormulaProperty.extractGoal(name);
	}

	@Override
	public int[] onEvaluate(TreeNode node, ArrayList<TreeNode> nodes,
			ExecutionSummary execution) {
		int[] scores = new int[nodes.size()];
		Arrays.fill(scores, V_MAX);
		for (int i = 0; i < nodes.size(); i++) {
			TreeNode child = nodes.get(i);
			PropertySummary summary = child.getPropertySummary();
			String goalPath = node.getParentGoalPath();
			
			for (Property p : summary.getProperties()) {
				String pName = p.getName();
				String pFullName = goalPath + TreeNode.PATH_SEPARATOR + p.getName();
				boolean cond1 = pName.equals(propName) || pFullName.equals(propName);
				boolean cond2 = checkSecondCondition(p, goalPath, child, nodes);
				boolean cond3 = checkThirdCondition(pName, node);
				if (cond1 || cond2 || cond3) {
					ArrayList<String> values = p.getValues();
					if (this.type == BDFType.PROP_EQUALS) {
						scores[i] = values.contains(propValue) ? V_MIN : V_MAX;
					} else if (this.type == BDFType.PROP_NOT_EQUALS) {
						scores[i] = !values.contains(propValue) ? V_MIN : V_MAX;
					}					
				}
			}
		}
		return scores;
	}
	
	private boolean checkSecondCondition(Property p, String goalPath, TreeNode child
			, ArrayList<TreeNode> children) {
		String pSpecialName = goalPath + TreeNode.PATH_SEPARATOR + p.getRawName();
		boolean condition = false;
		for (TreeNode n : children) {
			if (n.getName().equals(child.getName()))
				continue;
			PropertySummary summary = n.getPropertySummary();
			for (Property q : summary.getProperties()) {
				if (q.hasRawName() && q.getRawName().equals(p.getRawName())) {
					condition = true;
					break;
				}
			}
			if (condition) {
				break;
			}
		}
		return pSpecialName.equals(propName) && condition;
	}
	
	private boolean checkThirdCondition(String pName, TreeNode plan) {
		String goalPath = plan.getParentGoalPath();
		String propGoalPath = propName.substring(0, propName.lastIndexOf("."));
		return propName.endsWith(pName) && goalPath.contains(propGoalPath);
	}

	@Override
	public String toString() {
		return this.propName + (type.equals(BDFType.PROP_EQUALS) ? " = " : " =/= ") + this.propValue;
	}

	@Override
	public String onToString() {
		return null;
	}

}
