package goalplanner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class ChoiceOrder extends Choice {
	
	/** Contains all the inferrable orderings from the formulas. */
	public static ArrayList<Ordering> orderings;

	public ChoiceOrder(TreeNode node) {
		super(node);
	}

	@Override
	public void evaluate(GoalPlanTree tree) {
		// get orderings
		PlanTreeNode plan = (PlanTreeNode) this.node;
		HashMap<Ordering, Integer> orderings = plan.getOrderings();
		
		// use related orderings to reorder goals
		ArrayList<Integer> answer = new ArrayList<Integer>();
		for (int i = 0; i < this.node.getNumberOfChildren(); i++) {
			answer.add(i);
		}
		Collections.shuffle(answer); // start with arbitrary ordering
		
		// order the goal orderings by their weight
		HashSet<Integer> uniqueWeights = new HashSet<Integer>();
		uniqueWeights.addAll(orderings.values());
		ArrayList<Integer> sortedWeights = new ArrayList<Integer>();
		sortedWeights.addAll(uniqueWeights);
		Collections.sort(sortedWeights);
		for (Integer w : sortedWeights) {
			for (Ordering o : orderings.keySet()) {
				if (orderings.get(o) == w) {
					String name = this.node.getNameByHumanName(o.getAfter());
					int i_after = this.node.getIndex(name);
					for (int i = 0; i < answer.size(); i++) {
						if (answer.get(i) == i_after) {
							answer.remove(i);
							answer.add(i_after);
						}
					}					
				}
			}
		}
		
		// convert to array
		int[] result = new int[answer.size()];
		for (int i = 0; i < answer.size(); i++) {
			result[i] = answer.get(i);
		}
		this.chosen = result;
	}

}