package goalplanner;

import goalplanner.PlanGenerator.PlanTemplate;
import goalplanner.TreeNode.Status;
import jadex.model.IMAchieveGoal;
import jadex.model.IMParameter;
import jadex.model.IMPlan;
import jadex.model.jibximpl.MExpression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bienvenu.GeneralPreferenceFormula;

public class GoalPlanTree {
	
	public static final boolean CHOICES = false;
	
	public static final boolean EVALUATE = false;

	public static final boolean DEBUG = false;
	
	/** Number of goals in the goal-plan tree. */
	public static int totalGoals = 0;

	/** Number of plans in the goal-plan tree. */
	public static int totalPlans = 0;	
	
	/** The root node of the goal-plan tree. */
	private TreeNode root;
	
	/** Contains the available choices in the goal-plan tree. */
	protected ChoiceList choices;
	
	/** Contains information regarding the execution so far. */
	protected ExecutionSummary execution;
	
	private GoalPlanTree() {
		GoalPlanTree.totalGoals = 0;
		GoalPlanTree.totalPlans = 0;
	}
	
	public GoalPlanTree(HashMap<IMAchieveGoal, ArrayList<IMPlan>> goalToPlans,
			HashMap<IMPlan, ArrayList<IMAchieveGoal>> planToGoals, HashMap<String, PlanTemplate> templates) {
		this();
		this.constructFromJadex(goalToPlans, planToGoals, templates);
		initialize();
	}
	
	public GoalPlanTree(int depth, int planBranchFactor, int goalBranchFactor) {
		this();
		this.root = new GoalTreeNode();
		GoalPlanTree.constructNode(this.root, 1, depth, planBranchFactor, goalBranchFactor, new PlanGenerator());
		initialize();
	}
	
	public void initialize() {
		this.execution = new ExecutionSummary();
		this.root.resetStatuses();
		this.root.computeResourceSummary();
		this.root.computeEffectSummary();
		this.root.computeProperties();
		this.computePreferences();
	}
	
	private static void constructNode(TreeNode parent, int currentDepth, int maxDepth, int planBranchFactor, int goalBranchFactor
			, PlanGenerator planGenerator) {
		if (currentDepth == maxDepth)
			return;
		boolean needsGoals = currentDepth % 2 == 0;
		int branchingFactor = needsGoals ? goalBranchFactor : planBranchFactor;
		for (int i = 0; i < branchingFactor; i++) {
			TreeNode node = null;
			if (needsGoals) {
				node = new GoalTreeNode();
			} else {
				node = new PlanTreeNode(planGenerator.generateTemplate());
			}
			parent.addChild(node);
			GoalPlanTree.constructNode(node, currentDepth + 1, maxDepth
					, planBranchFactor, goalBranchFactor, planGenerator);
		}
	}
	
	public void computePreferences() {
		this.choices = new ChoiceList();
		this.choices.gatherChoices(this.root);
		this.choices.evaluateChoices(this);
	}
	
	/**
	 * @return true if 'node' has 'parent' as ancestor.
	 */
	public boolean hasAncestor(String node, String parent) {
		return getNode(parent).hasDescendant(node);
	}

	/**
	 * Update the status of all nodes in the goal-plan tree
	 * based on the given plan execution.
	 * @param result The results of a plan's execution.
	 */
	public void updateStatuses(ExecutionResult result) {
		this.root.updateStatuses(result);
	}
	
	public void constructFromJadex(
			HashMap<IMAchieveGoal, ArrayList<IMPlan>> goalToPlans,
			HashMap<IMPlan, ArrayList<IMAchieveGoal>> planToGoals,
			HashMap<String, PlanTemplate> templates) {
		// find root goal (one root goal must exist)
		IMAchieveGoal rootGoal = null;
		IMAchieveGoal[] goals = new IMAchieveGoal[goalToPlans.keySet().size()];
		int counter = 0;
		for (IMAchieveGoal goal : goalToPlans.keySet()) {
			goals[counter++] = goal;
		}
		// determine which goals are subgoals
		boolean[] isSubgoal = new boolean[goals.length];
		for (IMPlan plan : planToGoals.keySet()) {
			ArrayList<IMAchieveGoal> subgoals = planToGoals.get(plan);
			for (IMAchieveGoal goal : subgoals) {
				for (int i = 0; i < goals.length; i++) {
					if (goals[i].getName().equals(goal.getName())) {
						isSubgoal[i] = true;
						break;
					}
				}
			}
		}
		for (int i = 0; i < isSubgoal.length; i++) {
			if (!isSubgoal[i]) {
				rootGoal = goals[i];
				break;
			}
		}
		String humanReadableName = getHumanReadableName(rootGoal);
		this.root = new GoalTreeNode(rootGoal.getName(), humanReadableName);
		jadexToTree(this.root, goalToPlans, planToGoals, templates);
	}
	
	private void jadexToTree(TreeNode parent,
			HashMap<IMAchieveGoal, ArrayList<IMPlan>> goalToPlans,
			HashMap<IMPlan, ArrayList<IMAchieveGoal>> planToGoals,
			HashMap<String, PlanTemplate> templates) {
		boolean needsGoals = parent instanceof PlanTreeNode;
		String name = parent.getName();
		if (needsGoals) {
			for (IMPlan plan : planToGoals.keySet()) {
				if (plan.getName().equals(name)) {
					ArrayList<IMAchieveGoal> goals = planToGoals.get(plan);
					for (IMAchieveGoal goal : goals) {
						String humanReadableName = getHumanReadableName(goal);
						TreeNode node = new GoalTreeNode(goal.getName(), humanReadableName);
						parent.addChild(node);
						jadexToTree(node, goalToPlans, planToGoals, templates);
					}
				}	
			}
		} else {
			for (IMAchieveGoal goal : goalToPlans.keySet()) {
				if (goal.getName().equals(name)) {
					ArrayList<IMPlan> plans = goalToPlans.get(goal);
					for (IMPlan plan : plans) {
						PlanTemplate template = templates.get(plan.getName());
						if (template == null) {
							template = new PlanGenerator().new PlanTemplate();
						}
						TreeNode node = new PlanTreeNode(plan.getName(), template);
						parent.addChild(node);
						jadexToTree(node, goalToPlans, planToGoals, templates);
					}
				}
			}
		}
	}
	
	private String getHumanReadableName(IMAchieveGoal goal) {
		String humanReadableName = goal.getName();
		IMParameter hrParam = goal.getParameter("human_readable_name");
		if (hrParam != null) {
			List elems = hrParam.getChildren();
			for (int j = 0; j < elems.size(); j++) {
				MExpression expr = (MExpression) elems.get(j);
				Map map = expr.getEncodableRepresentation();
				try {
					humanReadableName = (String) expr.getTerm().getValue(map);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return humanReadableName;
	}
	
	public void updateExecutionSummary(ExecutionResult result) {
		TreeNode node = this.getNode(result.getName());
		if (node.status.isDone()) {
			TreeNode parent = node.getParent();
			while (parent != null) {
				if (parent instanceof GoalTreeNode) {
					String name = ((GoalTreeNode) parent).getHumanReadableName();
					if (name == null) {
						name = parent.getName();
					}
					if (parent.status == Status.SUCCESS) {
						this.execution.addSuccessGoal(name);
					}
					if (parent.status == Status.FAILURE) {
						this.execution.addFailedGoal(name);
					}
				}
				parent = parent.getParent();
			}
			PropertySummary properties = result.getProperties();
			for (Property p : properties.getProperties()) {
				this.execution.addProperty(p.name, p.values.get(0));
			}
		}
	}

	/**
	 * Perform updates to the goal-plan tree after the results
	 * of the execution of a plan are known.
	 * @param result The result of a plan's execution
	 */
	public void processExecutionResult(ExecutionResult result) {
		addExecutionResult(result);
		updateStatuses(result);
		updateExecutionSummary(result);
		computePreferences();
	}
	
	// algorithm in paper
	public void attachOrderings(ArrayList<GeneralPreferenceFormula> formulas) {
		// compute orderings in general
		ArrayList<Ordering> orderings = new ArrayList<Ordering>();
		for (GeneralPreferenceFormula f : formulas) {
			ArrayList<String> g_cond = f.getConditionGoals();
			ArrayList<String> g_atomic = f.getAtomicFormulaGoals();
			for (String g_c : g_cond) {
				for (String g_a : g_atomic) {
					if (!g_c.equals(g_a)) {
						orderings.add(new Ordering(g_c, g_a));	
					}
				}
			}
		}
		// attach them to the nodes
		attachOrderings(orderings, this.root);
	}
	private void attachOrderings(ArrayList<Ordering> orderings, TreeNode node) {
		if (node instanceof PlanTreeNode) {
			PlanTreeNode plan = (PlanTreeNode) node;
			String[] children = plan.getChildNodes();
			String[] humanReadableChildren = plan.getHumanReadableChildNodes();
			for (Ordering o : orderings) {
				Ordering r = getRelatedOrdering(o, children, humanReadableChildren);
				if (r != null) {
					plan.addOrdering(r);
				}
			}
		}
		for (TreeNode child : node.getChildren()) {
			attachOrderings(orderings, child);
		}
	}
	
	// ordering, real names of nodes, human-readable names of nods
	private Ordering getRelatedOrdering(Ordering ordering
			, String[] nodes, String[] humanNodes) {
		boolean[] bAncestor = new boolean[nodes.length];
		boolean[] aAncestor = new boolean[nodes.length];
		for (int i = 0; i < nodes.length; i++) {
			String realNameBefore = this.root.getNameByHumanName(ordering.before);
			String realNameAfter = this.root.getNameByHumanName(ordering.after);
			bAncestor[i] = hasAncestor(realNameBefore, nodes[i]);
			aAncestor[i] = hasAncestor(realNameAfter, nodes[i]);
		}
		int bIndex = -1;
		int aIndex = -1;
		for (int i = 0; i < nodes.length; i++) {
			if (bAncestor[i]) bIndex = i;
			if (aAncestor[i]) aIndex = i;
		}
		if (bIndex != aIndex && bIndex >= 0 && aIndex >= 0) {
			return new Ordering(humanNodes[bIndex], humanNodes[aIndex]);
		}
		return null; 
	}
	
	public TreeNode getRoot() {
		return this.root;
	}
	
	public TreeNode getNode(String name) {
		return this.root.getNode(name);
	}
	
	public ChoiceList getChoices() {
		return this.choices;
	}

	public ExecutionSummary getExecution() {
		return this.execution;
	}
	
	public void addExecutionResult(ExecutionResult result) {
		this.execution.addExecutionResult(result);
	}

	public String toString() {
		return this.root.printNode();
	}
	
}