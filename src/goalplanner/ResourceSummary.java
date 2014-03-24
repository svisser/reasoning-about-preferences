package goalplanner;

import goalplanner.ResourceSet.ResourceType;

public class ResourceSummary {

	/** Contains the necessary resources of this summary's node. */
	protected ResourceSet necessary;
	
	/** Contains the possible resources of this summary's node. */
	protected ResourceSet possible;
	
	public ResourceSummary(ResourceSet necessary, ResourceSet possible) {
		this.necessary = necessary;
		this.possible = possible;
	}
	
	public ResourceSummary(String[] resources, ResourceType[] types, int[] amounts) {
		this.necessary = new ResourceSet(resources, types, amounts);
		this.possible = new ResourceSet(resources, types, amounts);
	}
	
	public ResourceSummary plusCombine(ResourceSummary other) {
		ResourceSet necessary = this.necessary.getLowerBound(other.necessary);
		ResourceSet possible = this.possible.multiply(other.possible);
		return new ResourceSummary(necessary, possible);
	}
	
	public ResourceSummary getLowerBound(ResourceSummary other) {
		ResourceSet necessary = this.necessary.getLowerBound(other.necessary);
		ResourceSet possible = this.possible.getLowerBound(other.possible); 
		return new ResourceSummary(necessary, possible);
	}
	
	public ResourceSummary getUpperBound(ResourceSummary other) {
		ResourceSet necessary = this.necessary.getUpperBound(other.necessary);
		ResourceSet possible = this.possible.getUpperBound(other.possible);
		return new ResourceSummary(necessary, possible);
	}
	
	public ResourceSummary add(ResourceSummary other) {
		ResourceSet necessary = this.necessary.add(other.necessary);
		ResourceSet possible = this.possible.add(other.possible); 
		return new ResourceSummary(necessary, possible);
	}
	
	public ResourceSummary multiply(ResourceSummary other) {
		ResourceSet necessary = this.necessary.multiply(other.necessary);
		ResourceSet possible = this.possible.multiply(other.possible);
		return new ResourceSummary(necessary, possible);
	}
	
	public boolean hasResource(String resource) {
		return this.necessary.hasResource(resource) || this.possible.hasResource(resource);
	}
	
	public static String toCombinedString(ResourceSummary a, ResourceSummary b) {
		String s = "<";
		s += ResourceSet.toCombinedString(a.necessary, b.necessary);
		s += ", ";
		s += ResourceSet.toCombinedString(a.possible, b.possible);
		s += ">";
		return s;
	}
	
	public String toString() {
		return "<" + necessary.toString() + ", " + possible.toString() + ">";
	}

	public ResourceSet getNecessary() {
		return necessary;
	}

	public ResourceSet getPossible() {
		return possible;
	}
}
