package goalplanner;


public class EffectSummary {
	
	/** Contains the definite effects of this summary's node. */
	protected EffectSet definite;
	
	/** Contains the potential effects of this summary's node. */
	protected EffectSet potential;
	
	public EffectSummary(EffectSet definite, EffectSet potential) {
		this.definite = definite;
		this.potential = potential;
	}
	
	public EffectSummary(String planName, String[] effects) {
		this(new EffectSet(planName, effects), new EffectSet());	
	}
	
	public EffectSummary multiply(EffectSummary other) {
		EffectSet definite = this.definite.intersect(other.definite); 
		EffectSet potential = this.potential.union(other.potential);
		EffectSet p1 = this.definite.union(other.definite);
		EffectSet p2 = this.definite.intersect(other.definite);
		potential = potential.union(p1.subtract(p2));
		return new EffectSummary(definite, potential);
	}
	
	public EffectSummary plus(EffectSummary other) {
		EffectSet definite = this.definite.union(other.definite);
		EffectSet p1 = this.potential.union(other.potential);
		EffectSet p2 = this.definite.union(other.definite);
		EffectSet potential = p1.subtract(p2);
		return new EffectSummary(definite, potential);
	}
	
	public boolean hasEffect(String effect) {
		return this.definite.hasEffect(effect) || this.potential.hasEffect(effect);
	}
	
	public String toString() {
		return "<" + definite.toString() + ", " + potential.toString() + ">"; 
	}
	
	public EffectSet getDefinite() {
		return definite;
	}

	public EffectSet getPotential() {
		return potential;
	}

}
