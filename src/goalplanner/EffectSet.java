package goalplanner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class EffectSet implements Iterable<EffectSet.EffectItem> {
	
	public class EffectItem {
		
		public String effect;
		
		public HashSet<String> plans;
		
		public EffectItem() {
			this.plans = new HashSet<String>();
		}
		
		public EffectItem(String planName, String effect) {
			this();
			this.effect = effect;
			this.plans.add(planName);
		}
		
		public String toString(boolean includePlans) {
			String effectString = effect;
			if (plans.size() > 1) {
				effectString += " (" + plans.size() + "x)";
			}
			if (includePlans) {
				return "(" + effectString + ", " + plans + ")";	
			}
			return effectString;
		}
		
		public String toString() {
			return toString(true);
		}
	}

	protected ArrayList<EffectItem> items;
	
	public EffectSet() {
		this.items = new ArrayList<EffectItem>();
	}
	
	public EffectSet(String planName, String[] effects) {
		this();
		for (int i = 0; i < effects.length; i++) {
			this.items.add(new EffectItem(planName, effects[i]));
		}
	}
	
	public EffectSet intersect(EffectSet other) {
		EffectSet es = new EffectSet();
		for (EffectItem item : this.items) {
			for (EffectItem otherItem : other.items) {
				if (item.effect.equals(otherItem.effect)) {
					EffectItem newItem = new EffectItem();
					newItem.effect = item.effect;
					newItem.plans.addAll(item.plans);
					newItem.plans.addAll(otherItem.plans);
					es.items.add(newItem);
				}
			}
		}
		return es;
	}
	
	public EffectSet union(EffectSet other) {
		EffectSet es = this.intersect(other);
		for (EffectItem item : this.items) {
			if (!other.hasEffect(item.effect)) {
				es.items.add(item);
			}
		}
		for (EffectItem otherItem : other.items) {
			if (!this.hasEffect(otherItem.effect)) {
				es.items.add(otherItem);
			}
		}
		return es;
	}
	
	public EffectSet subtract(EffectSet other) {
		EffectSet es = new EffectSet();
		for (EffectItem item : this.items) {
			if (!other.hasEffect(item.effect)) {
				es.items.add(item);
			}
		}
		return es;
	}
	
	public boolean hasEffect(String effect) {
		for (EffectItem item : items) {
			if (item.effect.equals(effect)) {
				return true;
			}
		}
		return false;
	}
	
	public EffectSet createCopy() {
		EffectSet es = new EffectSet();
		for (EffectItem item : this.items) {
			EffectItem i = new EffectItem();
			i.effect = item.effect;
			HashSet<String> s = (HashSet<String>) item.plans.clone();
			i.plans.addAll(s);
			es.items.add(i);
		}
		return es;
	}
	
	public String toString(boolean includePlans) {
		String s = "{";
		for (int i = 0; i < items.size(); i++) {
			s += items.get(i).toString(includePlans);
			if (i < items.size() - 1) {
				s += ", ";
			}
		}
		s += "}";
		return s;
	}
	
	public EffectItem get(String effect) {
		for (EffectItem item : this.items) {
			if (item.effect.equals(effect)) {
				return item;
			}
		}
		return null;
	}
	
	public static EffectSet mergeEffects(ArrayList<EffectSet> effects) {
		EffectSet result = new EffectSet();
		for (EffectSet e : effects) {
			result = result.union(e);
		}
		return result;
	}
	
	public int size() {
		return items.size();
	}
	
	public boolean isEmpty() {
		return items.isEmpty();
	}	
	
	public String toString() {
		return toString(true);
	}

	@Override
	public Iterator<EffectItem> iterator() {
		return items.iterator();
	}
	
}
