package goalplanner;

import goalplanner.EffectSet.EffectItem;
import goalplanner.ResourceSet.ResourceType;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class GenGP {
	
	public static void main(String[] args) {
		if (args.length < 4) {
			System.out.println("Four arguments required: filename depth planBranchFactor goalBranchFactor");
			return;
		}
		String filename = args[0];
		int depth = Integer.valueOf(args[1]);
		int planBranchFactor = Integer.valueOf(args[2]);
		int goalBranchFactor = Integer.valueOf(args[3]);
		GoalPlanTree tree = new GoalPlanTree(depth, planBranchFactor, goalBranchFactor);
		String output = "";
		output += getHeader(filename);
		output += getImports();
		output += getGoals(tree);
		output += getPlans(tree);
		output += getEvents();
		output += getConfigurations();
		output += getFooter();
		try {
			FileWriter writer = new FileWriter(filename);
			BufferedWriter out = new BufferedWriter(writer);
			out.write(output);
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String getHeader(String filename) {
		String agentName = filename.substring(0, filename.indexOf("."));
		return "<agent xmlns=\"http://jadex.sourceforge.net/jadex\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://jadex.sourceforge.net/jadex http://jadex.sourceforge.net/jadex-0.96.xsd\" name=\"" + agentName + "\" package=\"" +filename + "\">\n";
	}
	
	private static String getImports() {
		return "<imports>\n\t<import>jadex.runtime.*</import>\n\t<import>summary.*</import>\n</imports>\n";
	}
	
	private static String getGoals(GoalPlanTree tree) {
		String output = "";
		output += "<goals>\n";
		ArrayList<GoalTreeNode> goals = getGoalNodes(tree.getRoot());
		for (GoalTreeNode goal : goals) {
			output += "\t<achievegoal name=\"" + goal.name + "\"/>\n";
		}
		output += "\t<achievegoal name=\"mastergoal\"/>\n";
		output += "\t<metagoal name=\"metagoal\">\n";
		output += "\t\t<parameterset name=\"applicables\" class=\"ICandidateInfo\"/>\n";
		output += "\t\t<parameterset name=\"result\" class=\"ICandidateInfo\" direction=\"out\"/>\n";
		output += "\t\t<trigger>\n";
		for (GoalTreeNode goal : goals) {
			output += "\t\t\t<goal ref=\"" + goal.name + "\"/>\n";
		}
		output += "\t\t</trigger>\n";
		output += "\t</metagoal>\n";
		output += "</goals>\n";
		return output;
	}
	
	private static ArrayList<GoalTreeNode> getGoalNodes(TreeNode node) {
		ArrayList<GoalTreeNode> nodes = new ArrayList<GoalTreeNode>();
		if (node instanceof GoalTreeNode) {
			nodes.add(((GoalTreeNode) node));
		}
		for (TreeNode child : node.children) {
			nodes.addAll(getGoalNodes(child));	
		}
		return nodes;
	}
	
	private static String getPlans(GoalPlanTree tree) {
		String output = "";
		output += "<plans>\n";
		output += "\t<plan name=\"masterplan\">\n";
		output += "\t\t<parameter name=\"preferences\" class=\"String\">\n";
		output += "\t\t\t<value>\"meta\"</value>\n";
		output += "\t\t</parameter>\n";
		output += "\t\t<body class=\"MasterPlan\"/>\n";
		output += "\t\t<trigger>\n";
		output += "\t\t\t<goal ref=\"mastergoal\"/>\n";
		output += "\t\t</trigger>\n";
		output += "\t</plan>\n";
		output += "\t<plan name=\"pingplan\">\n";
		output += "\t\t<body class=\"PingPlan\"/>\n";
		output += "\t\t<trigger>\n";
		output += "\t\t\t<messageevent ref=\"any_message\"/>\n";
		output += "\t\t</trigger>\n";
		output += "\t</plan>\n";
		output += "\t<plan name=\"metaplan\">\n";
		output += "\t\t<parameterset name=\"applicables\" class=\"ICandidateInfo\">\n";
		output += "\t\t\t<goalmapping ref=\"metagoal.applicables\"/>\n";
		output += "\t\t</parameterset>\n";
		output += "\t\t<parameterset name=\"result\" class=\"ICandidateInfo\" direction=\"out\">\n";
		output += "\t\t\t<goalmapping ref=\"metagoal.result\"/>\n";
		output += "\t\t</parameterset>\n";
		output += "\t\t<body class=\"MetaPlan\"/>\n";
		output += "\t\t<trigger>\n";
		output += "\t\t\t<goal ref=\"metagoal\"/>\n";
		output += "\t\t</trigger>\n";
		output += "\t</plan>\n";
		ArrayList<PlanTreeNode> plans = getPlanNodes(tree.getRoot());
		for (PlanTreeNode plan : plans) {
			output += "\t<plan name=\"" + plan.name + "\">\n";
			EffectSet effects = plan.planEffectSummary.definite;
			if (!effects.isEmpty()) {
				output += "\t\t<parameterset name=\"effects\" class=\"String\">\n";
				for (EffectItem e : effects) {
					output += "\t\t\t<value>\"" + e.effect + "\"</value>\n";
				}
				output += "\t\t</parameterset>\n";
			}
			ResourceSet resources = plan.planResourceSummary.necessary;
			if (!resources.isEmpty()) {
				output += "\t\t<parameterset name=\"resources\" class=\"Triple\">\n";
				for (int i = 0; i < resources.resources.length; i++) {
					String r = resources.resources[i];
					String type = resources.types[i] == ResourceType.CONSUMABLE ? "0" : "1";
					output += "\t\t\t<value>new Triple(\"" + r + "\", " + resources.get(r) + ", " + type + ")</value>\n";
				}
				output += "\t\t</parameterset>\n";
			}
			if (!plan.getChildren().isEmpty()) {
				output += "\t\t<parameterset name=\"subgoals\" class=\"String\">\n";
				for (TreeNode goal : plan.getChildren()) {
					output += "\t\t\t<value>\"" + goal.name + "\"</value>\n";
				}
				output += "\t\t</parameterset>\n";
			}
			output += "\t\t<body class=\"StandardPlan\"/>\n";
			output += "\t\t<trigger>\n";
			output += "\t\t\t<goal ref=\"" + plan.getParent().name + "\"/>\n";
			output += "\t\t</trigger>\n";
			output += "\t</plan>\n";
		}
		output += "</plans>\n";
		return output;
	}
	
	private static ArrayList<PlanTreeNode> getPlanNodes(TreeNode node) {
		ArrayList<PlanTreeNode> nodes = new ArrayList<PlanTreeNode>();
		if (node instanceof PlanTreeNode) {
			nodes.add(((PlanTreeNode) node));
		}
		for (TreeNode child : node.children) {
			nodes.addAll(getPlanNodes(child));	
		}
		return nodes;
	}

	private static String getEvents() {
		String output = "";
		output += "<events>\n";
		output += "\t<messageevent name=\"any_message\" type=\"fipa\" direction=\"send_receive\"/>\n";
		output += "</events>\n";
		return output;
	}
	
	private static String getConfigurations() {
		String output = "";
		output += "<configurations>\n";
		output += "\t<configuration name=\"default\">\n";
		/*output += "\t\t<plans>\n";
		output += "\t\t\t<initialplan ref=\"masterplan\"/>\n";
		output += "\t\t</plans>\n";*/
		output += "\t</configuration>\n";
		output += "</configurations>\n";
		return output;
	}
	
	private static String getFooter() {
		return "</agent>";
	}
}
