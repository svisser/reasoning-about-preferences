package goalplanner;

import java.util.ArrayList;

public abstract class Choice {
	
	public enum Type {
		ORDER, // The decision of ordering goals.
		OPTION; // The decision of selecting a plan.
	}
	
	/** The type of decision being made. */
	private Type type;
	
	/** Each of the involved child nodes of this choice. */
	private ArrayList<TreeNode> children;
	
	/** The node to which this choice is applicable. */
	protected String name;
	
	/** The child nodes in preferred order. */
	protected int[] chosen;
	
	/** If true, this decision has been made already. */
	private boolean done;
	
	/** The node to which this choice belongs. */
	protected TreeNode node;
	
	/** Compute the preferred order of the child nodes for this choice. */
	public abstract void evaluate(GoalPlanTree tree);
	
	public Choice(TreeNode node) {
		this.node = node;
		boolean isGoal = node instanceof GoalTreeNode;
		this.name = node.getName();
		this.type = isGoal ? Choice.Type.OPTION : Choice.Type.ORDER;
		this.children = node.getChildren();
		this.done = node.isDone();
	}
	
	public String toString() {
		String s = this.name + " - ";
		if (this.type == Type.ORDER) {
			s += "choose order of";
		} else if (this.type == Type.OPTION) {
			s += "choose one of";
		}
		s += ": {";
		for (int i = 0; i < children.size(); i++) {
			s += children.get(i).name;
			if (i < children.size() - 1) {
				s += ", ";
			}
		}
		s += "} - {";
		if (chosen != null) {
			for (int i = 0; i < chosen.length; i++) {
				s += children.get(chosen[i]).name;
				if (i < chosen.length - 1) {
					s += ", ";
				}
			}	
		} else {
			s += "---";
		}
		s += "}";
		return s;
	}

	/**
	 * Return true if this choice is related to choosing
	 * between the alternatives in String[]. We assume each node
	 * in the goal-plan tree has a unique name.
	 */
	public boolean hasAlternatives(String[] alternatives) {
		for (String s : alternatives) {
			for (TreeNode node : children) {
				if (node.getName().equals(s)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Compute the decision to make for a plan of a goal, using
	 * the preferred ordering of plans and the status information
	 * of the nodes.
	 * @return the index of the plan to choose
	 */
	public int makeDecision() {
		int decision = -1;
		for (int i = 0; i < chosen.length; i++) {
			if (chosen[i] >= 0) {
				TreeNode.Status status = children.get(chosen[i]).status;
				if (status.isDone())
					continue;
				decision = chosen[i];
				break;
			}
		}
		return decision;
	}
	
	/**
	 * Return the name of the plan with the given index.
	 * @param index The index of the plan (0, ..., n - 1)
	 * @return The name of the plan
	 */
	public String getName(int index) {
		return this.children.get(index).name;
	}
	
	/**
	 * @return true iff this choice/decision is about choosing a plan for a goal.
	 */
	public boolean isOption() {
		return this.type == Type.OPTION;
	}
	
	/**
	 * @return true iff this choice/decision is about ordering subgoals of a plan.
	 */
	public boolean isOrder() {
		return this.type == Type.ORDER;
	}
	
	/**
	 * @return true iff this choice has already been made during execution.
	 */
	public boolean isDone() {
		return this.done;
	}

	/**
	 * @return the preferred order to attempt plans or to instantiate subgoals.
	 */
	public int[] getChosen() {
		return chosen;
	}

	/** Create a Choice depending on the type of node. */
	public static Choice createChoice(TreeNode node) {
		Choice choice = null;
		if (node instanceof PlanTreeNode) {
			choice = new ChoiceOrder(node);
		} else if (node instanceof GoalTreeNode) {
			choice = new ChoiceOption(node);
		}
		return choice;
	}

}
