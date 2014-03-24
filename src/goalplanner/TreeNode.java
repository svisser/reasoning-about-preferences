package goalplanner;

import java.util.ArrayList;

public abstract class TreeNode {
	
	public static final String PATH_SEPARATOR = ".";
	
	public enum Status {
		SUCCESS("success"),
		FAILURE("failure"),
		ACTIVE("active"),
		DEFAULT("default");
		
		private String name;
		
		private Status(String name) {
			this.name = name;
		}
		
		public boolean isDone() {
			return this == SUCCESS || this == FAILURE;
		}

		@Override
		public String toString() {
			return this.name;
		}
	};
	
	/** The parent node in the goal-plan tree. */
	protected TreeNode parent;
	
	/** The unique identifier of this node in the goal-plan tree. */
	protected String name;
	
	/** Contains the execution status of this node. */
	protected Status status;
	
	/** The child nodes of this node in the goal-plan tree. */
	protected ArrayList<TreeNode> children;
	
	/** The computed effect summary of this node. */
	protected EffectSummary effectSummary;
	
	/** The computed resource summary of this node. */
	protected ResourceSummary resourceSummary;
	
	/** The computed property summary of this node. */
	protected PropertySummary propertySummary;
	
	/** Print the information of this node indented at specified depth. */
	public abstract String onPrintNode(int depth);
	
	/** Print the name of this node (internal name or human readable name). */
	public abstract String onPrintName();
	
	/** Compute and store the effect summary of this node, based on child summaries. */
	public abstract EffectSummary onComputeEffectSummary(EffectSummary[] children);
	
	/** Compute and store the resource summary of this node, based on child summaries. */
	public abstract ResourceSummary onComputeResourceSummary(ResourceSummary[] children);
	
	/** Compute and store the property summary of this node, based on child summaries. */
	public abstract PropertySummary onComputeProperties(PropertySummary[] properties);
	
	public TreeNode() {
		this.status = Status.DEFAULT;
		this.children = new ArrayList<TreeNode>();
		if (this instanceof PlanTreeNode) {
			this.name = "P" + String.valueOf(GoalPlanTree.totalPlans++);
		} else if (this instanceof GoalTreeNode) {
			this.name = "G" + String.valueOf(GoalPlanTree.totalGoals++);
		}
	}
	
	public TreeNode(String name) {
		this();
		this.name = name;
	}
	
	public void addChild(TreeNode child) {
		child.parent = this;
		this.children.add(child);
	}
	
	public boolean hasAncestor(String name) {
		return this.name.equals(name) || hasParent(name);
	}

	public boolean hasDescendant(String name) {
		if (this.name.equals(name))
			return true;
		for (TreeNode child : children) {
			if (child.hasDescendant(name))
				return true;
		}
		return false;
	}
	
	public boolean isDone() {
		int nDone = 0;
		for (TreeNode child : children) {
			if (child.status.isDone()){ 
				nDone++;
			}				
		}
		return nDone == children.size();
	}
	
	public String[] getChildNodes() {
		String[] nodes = new String[children.size()];
		for (int i = 0; i < children.size(); i++) {
			nodes[i] = children.get(i).name;
		}
		return nodes;
	}
	
	public int getNumberOfChildren() {
		return this.children.size();
	}
	
	public boolean hasEffect(String effect) {
		return this.effectSummary.hasEffect(effect);
	}
	
	public boolean hasParent(String parentName) {
		TreeNode parent = this.getParent();
		while (parent != null) {
			if (parent.getName().equals(parentName)) {
				return true;
			}
			parent = parent.getParent();
		}
		return false;
	}
	
	public boolean hasParent() {
		return getParent() != null;
	}
	
	public int getIndex(String name) {
		for (int i = 0; i < children.size(); i++) {
			if (children.get(i).getName().equals(name)) {
				return i;
			}
		}
		return -1;
	}

	protected static String indent(int depth) {
		final int INDENTATION = 6;
		String s = "";
		for (int i = 0; i < depth * INDENTATION; i++) {
			s += " ";
		}
		return s;
	}
	
	public String printNode() {
		return printNode(0);
	}

	private String printNode(int depth) {
		String s = TreeNode.indent(depth) + " ";
		s += "(" + onPrintName() + (this.status != Status.DEFAULT ? ", " + this.status : "") + ")";
		s += "\n";
		s += onPrintNode(depth + 1);
		s += "\n";
		for (TreeNode c : this.children) {
			s += c.printNode(depth + 1);
		}
		return s;
	}

	public PropertySummary computeProperties() {
		if (this.children.isEmpty()) {
			PropertySummary ps = ((PlanTreeNode) this).planPropertySummary; 
			this.propertySummary = ps;
			return ps;
		}
		PropertySummary[] ps = new PropertySummary[this.children.size()];
		for (int i = 0; i < ps.length; i++) {
			ps[i] = this.children.get(i).computeProperties();
		}
		return onComputeProperties(ps);
	}	
	
	public EffectSummary computeEffectSummary() {
		if (this.children.isEmpty()) {
			EffectSummary planEffectSummary = ((PlanTreeNode) this).planEffectSummary;
			this.effectSummary = planEffectSummary;
			return planEffectSummary;
		}
		EffectSummary[] es = new EffectSummary[this.children.size()];
		for (int i = 0; i < es.length; i++) {
			es[i] = this.children.get(i).computeEffectSummary();
		}
		return onComputeEffectSummary(es);
	}

	public ResourceSummary computeResourceSummary() {
		if (this.children.isEmpty()) {
			ResourceSummary planSummary = ((PlanTreeNode) this).planResourceSummary;
			this.resourceSummary = planSummary;
			return planSummary;
		}
		ResourceSummary[] rs = new ResourceSummary[this.children.size()];
		for (int i = 0; i < rs.length; i++) {
			rs[i] = this.children.get(i).computeResourceSummary();
		}
		return onComputeResourceSummary(rs);
	}
	
	public Status updateStatuses(ExecutionResult result) {
		if (!this.children.isEmpty()) {
			Status[] statuses = new Status[this.children.size()];
			for (int i = 0; i < statuses.length; i++) {
				statuses[i] = this.children.get(i).updateStatuses(result);
			}
			int totalSuccess = 0;
			int totalFailure = 0;
			int totalNotDone = 0;
			boolean hasFinishedNode = false;
			boolean hasActiveNode = false;
			boolean hasNotDoneNode = false;
			for (int i = 0; i < statuses.length; i++) {
				if (statuses[i] == Status.SUCCESS)
					totalSuccess++;
				if (statuses[i] == Status.FAILURE)
					totalFailure++;
				if (statuses[i] == Status.DEFAULT)
					totalNotDone++;
				if (statuses[i].isDone())
					hasFinishedNode = true;
				if (statuses[i] == Status.ACTIVE)
					hasActiveNode = true;
				if (statuses[i] == Status.DEFAULT)
					hasNotDoneNode = true;
			}
			if (this instanceof PlanTreeNode) {
				if (this.status != Status.DEFAULT) {
				} else if (hasActiveNode || hasFinishedNode) {
					this.status = Status.ACTIVE;
				} else if (hasNotDoneNode) {
					this.status = Status.DEFAULT;
				}
			} else if (this instanceof GoalTreeNode) {
				if (totalSuccess > 0) {
					this.status = Status.SUCCESS;
				} else if (totalFailure == statuses.length) {
					this.status = Status.FAILURE;
				} else if (hasActiveNode || (totalFailure > 0 && totalFailure < statuses.length && hasNotDoneNode)) {
					this.status = Status.ACTIVE;
				} else if (hasNotDoneNode) {
					this.status = Status.DEFAULT;
				}
			}
		} else {
			// must be a plan
			if (this.status != Status.DEFAULT) {
			} else {
				this.status = Status.DEFAULT;
			}
		}
		// process the result of a plan's execution
		if (result.name.equals(this.getName())) {
			this.status = result.isSuccess() ? Status.SUCCESS : Status.FAILURE;
		}
		return this.status;
	}
	
	public void resetStatuses() {
		this.status = Status.DEFAULT;
		for (TreeNode c : this.children) {
			c.resetStatuses();
		}
	}
	
	/**
	 * Return the TreeNode in the tree rooted at the current tree node
	 * with the specified name. 
	 * @param name
	 * @return
	 */
	public TreeNode getNode(String name) {
		if (this.getName().equals(name))
			return this;
		for (int i = 0; i < this.children.size(); i++) {
			TreeNode n = this.children.get(i).getNode(name);
			if (n != null)
				return n;
		}
		return null;
	}
	
	// return the actual technical name of the goal with the given
	// human-readable name
	public String getNameByHumanName(String humanReadableName) {
		if (this instanceof GoalTreeNode) {
			if (((GoalTreeNode) this).humanReadableName.equals(humanReadableName)) {
				return this.getName();
			}
		}
		for (int i = 0; i < this.children.size(); i++) {
			String n = this.children.get(i).getNameByHumanName(humanReadableName);
			if (n != null)
				return n;
		}
		return null;
	}
	
	// get goal path of this node, including current goal name
	// but excluding goal name of root node
	public String getParentGoalPath() {
		boolean needSep = false;
		String result = "";
		if (this instanceof GoalTreeNode) {
			result += ((GoalTreeNode) this).humanReadableName;
			needSep = true;
		}
		TreeNode parent = getParent();
		while (parent != null) {
			if (parent instanceof GoalTreeNode && parent.hasParent()) {
				GoalTreeNode goal = (GoalTreeNode) parent;
				if (needSep) {
					result = goal.getHumanReadableName() + TreeNode.PATH_SEPARATOR + result;	
				} else {
					result = goal.getHumanReadableName();
					needSep = true;
				}
			}
			parent = parent.getParent();
		}
		return result;
	}
	
	public String getName() {
		return name;
	}

	public TreeNode getParent() {
		return parent;
	}

	public ArrayList<TreeNode> getChildren() {
		return children;
	}

	public Status getStatus() {
		return status;
	}

	public EffectSummary getEffectSummary() {
		return effectSummary;
	}

	public ResourceSummary getResourceSummary() {
		return resourceSummary;
	}

	public String toString() {
		return this.name;
	}

	public PropertySummary getPropertySummary() {
		return propertySummary;
	}

}
