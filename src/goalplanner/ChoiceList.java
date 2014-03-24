package goalplanner;

import java.util.ArrayList;
import java.util.Iterator;

public class ChoiceList implements Iterable<Choice> {

	/** The choices that need to be made in the goal-plan tree. */
	private ArrayList<Choice> choices = new ArrayList<Choice>();
	
	/** Add a Choice to the list of choices to be made. */
	public void add(Choice choice) {
		this.choices.add(choice);
	}
	
	/** Return the Choice that is related to the given names of nodes. */
	public Choice get(String[] alternatives) {
		for (Choice c : this.choices) {
			if (c.hasAlternatives(alternatives)) {
				return c;
			}
		}
		return null;
	}
	
	/** Return the Choice for the node with the given name. */
	public Choice get(String name) {
		for (Choice c : this.choices) {
			if (c.name.equals(name)) {
				return c;
			}
		}
		return null;
	}
	
	public ChoiceList getChildChoices(GoalPlanTree tree, String parent) {
		ChoiceList list = new ChoiceList();
		for (Choice c : this.choices) {
			if (c.isOption() && tree.hasAncestor(c.name, parent)) {
				list.add(c);
			}
		}
		return list;
	}
	
	/**
	 * Add all choices of the tree rooted at node to the choice list.
	 * @param node Root of the part of the goal-plan tree.
	 */
	public void gatherChoices(TreeNode node) {
		if (node.getChildren().isEmpty())
			return;
		for (TreeNode n : node.getChildren())
			gatherChoices(n);
		Choice c = Choice.createChoice(node);
		this.add(c);
		if (GoalPlanTree.CHOICES) {
			System.out.println(c);
		}
	}
	
	/** Evaluate all choices that need to be made for the goal-plan tree. */
	public void evaluateChoices(GoalPlanTree tree) {
		for (Choice c : choices) {
			if (!c.isDone()) {
				c.evaluate(tree);
			}	
		}
	}
	
	public String toString() {
		String s = "";
		for (Choice c : this.choices) {
			s += c.toString() + "\n";
		}
		return s;
	}

	@Override
	public Iterator<Choice> iterator() {
		return this.choices.iterator();
	}
}
