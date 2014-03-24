package goalplanner;


public class Ordering {

	/** The goal that comes first in the goal ordering. */
	public String before;
	
	/** The goal that comes second in the goal ordering. */
	public String after;
	
	/** The number of preferences formulas that have given rise to this ordering. */
	public int weight;
	
	public Ordering(String before, String after) {
		this.before = before;
		this.after = after;
		this.weight = 1;
	}
	
	public String toString() {
		return before + " < " + after;
	}
	
	public boolean isSameOrdering(Ordering other) {
		return this.before.equals(other.before) && this.after.equals(other.after);
	}

	public String getBefore() {
		return before;
	}

	public String getAfter() {
		return after;
	}
}
