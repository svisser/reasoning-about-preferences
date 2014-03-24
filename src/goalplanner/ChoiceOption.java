package goalplanner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import bienvenu.PreferenceFormula;

public class ChoiceOption extends Choice {

	public ChoiceOption(TreeNode node) {
		super(node);
	}

	@Override
	public void evaluate(GoalPlanTree tree) {
		// determine satisfaction of preference formulas
		if (GoalPlanTree.CHOICES) {
			System.out.println("-----");
			System.out.println(this.node.resourceSummary);
			System.out.println(this.node.effectSummary);
		}		
		int[] scores = PreferenceFormula.prefs.evaluate(this.node, this.node.getChildren(), tree.getExecution());
		
		// order nodes according to preference satisfaction
		boolean[] used = new boolean[scores.length];
		int[] answer = new int[scores.length];
		Arrays.fill(answer, -1);
		int count = 0;
		Random random = new Random();
		while (count < answer.length) {
			// find minValue
			int index = -1;
			int minValue = Integer.MAX_VALUE;
			for (int i = 0; i < scores.length; i++) {
				if (used[i])
					continue;
				if (scores[i] < minValue) {
					index = i;
					minValue = scores[i];
				}
			}
			
			// get indices with that minValue
			ArrayList<Integer> indices = new ArrayList<Integer>();
			for (int i = 0; i < scores.length; i++) {
				if (used[i])
					continue;
				if (scores[i] == minValue) {
					indices.add(i);
				}
			}
			
			// choose randomly among those with same minValue
			int randomIndex = random.nextInt(indices.size());
			index = indices.get(randomIndex);
			answer[count] = index;
			used[index] = true;
			count++;
		}
		this.chosen = answer;
	}

}
