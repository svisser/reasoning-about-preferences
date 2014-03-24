package goalplanner;

import java.util.ArrayList;
import java.util.HashMap;

public class ResourceSet extends HashMap<String, Integer> {
	
	public enum ResourceType {
		CONSUMABLE, REUSABLE;
	}
	
	/** The names of the resources in the resource set. */
	protected final String[] resources;
	
	/** The type of each resource in the resource set. */
	protected final ResourceType[] types;

	public ResourceSet(String[] resources, ResourceType[] types) {
		this(resources, types, new int[resources.length]);
	}
	
	public ResourceSet(String[] resources, ResourceType[] types, int[] amounts) {
		this.resources = resources;
		this.types = types;
		for (int i = 0; i < amounts.length; i++) {
			this.put(resources[i], amounts[i]);
		}
	}
	
	public boolean isEmpty() {
		int total = 0;
		for (String r : this.resources) {
			total += this.get(r);
		}
		return total == 0;
	}
	
	public ResourceSet createEmpty() {
		return new ResourceSet(this.resources, this.types);
	}
	
	public ResourceSet createCopy() {
		ResourceSet rs = new ResourceSet(this.resources, this.types);
		for (String r : this.resources) {
			rs.put(r, this.get(r));
		}
		return rs;
	}
	
	public ResourceSet getUpperBound(ResourceSet other) {
		ResourceSet rs = mergeResourceSetsEmpty(this, other);
		for (String r : rs.resources) {
			rs.put(r, Math.max(this.get(r), other.get(r)));
		}
		return rs;
	}
	
	public ResourceSet getLowerBound(ResourceSet other) {
		ResourceSet rs = mergeResourceSetsEmpty(this, other);
		for (String r : rs.resources) {
			rs.put(r, Math.min(this.get(r), other.get(r)));
		}
		return rs;
	}
	
	public ResourceSet add(ResourceSet other) {
		ResourceSet rs = mergeResourceSetsEmpty(this, other);
		for (String r : rs.resources) {
			rs.put(r, this.get(r) + other.get(r));
		}
		return rs;
	}
	
	public ResourceSet multiply(ResourceSet other) {
		ResourceSet rs = mergeResourceSetsEmpty(this, other);
		for (int i = 0; i < rs.resources.length; i++) {
			String r = rs.resources[i];
			ResourceType type = rs.types[i];
			if (type == ResourceType.CONSUMABLE) {
				rs.put(r, this.get(r) + other.get(r));
			} else if (type == ResourceType.REUSABLE) {
				rs.put(r, Math.max(this.get(r), other.get(r)));
			}
		}
		return rs;
	}
	
	private ResourceSet mergeResourceSetsEmpty(ResourceSet a, ResourceSet b) {
		HashMap<String, ResourceType> resources = mergeResourceSetTypes(a, b);
		String[] nResources = computeResourceArray(resources);
		ResourceType[] nTypes = computeTypesArray(resources);
		return new ResourceSet(nResources, nTypes);
	}
	
	private String[] computeResourceArray(HashMap<String, ResourceType> resources) {
		int size = resources.keySet().size();
		String[] nResources = new String[size];
		int index = 0;
		for (String r : resources.keySet()) {
			nResources[index++] = r;
		}
		return nResources;
	}
	
	private ResourceType[] computeTypesArray(HashMap<String, ResourceType> resources) {
		int size = resources.keySet().size();
		ResourceType[] nTypes = new ResourceType[size];
		int index = 0;
		for (String r : resources.keySet()) {
			nTypes[index] = resources.get(r);
			index++;
		}
		return nTypes;
	}
	
	private HashMap<String, ResourceType> mergeResourceSetTypes(ResourceSet a, ResourceSet b) {
		HashMap<String, ResourceType> resources = new HashMap<String, ResourceType>();
		for (int i = 0; i < a.resources.length; i++) {
			resources.put(a.resources[i], a.types[i]);
		}
		for (int i = 0; i < b.resources.length; i++) {
			resources.put(b.resources[i], b.types[i]);
		}
		return resources;
	}
	
	public boolean hasResource(String key) {
		return this.get(key) > 0;
	}
	
	public String toString() {
		return ResourceSet.toCombinedString(this, null);
	}

	public static ResourceSet mergeResources(ArrayList<ResourceSet> resources) {
		if (resources.isEmpty()) {
			return null;	
		}	
		ResourceSet usage = resources.get(0);
		for (int i = 1; i < resources.size(); i++) {
			usage = usage.add(resources.get(i));
		}
		return usage;
	}

	public static String toCombinedString(ResourceSet a, ResourceSet b) {
		String s = "{";
		for (int i = 0; i < a.resources.length; i++) {
			s += "(";
			s += a.resources[i];
			s += ", ";
			int aValue = a.get(a.resources[i]);
			s += aValue;
			if (b != null) {
				int bValue = b.get(a.resources[i]);
				if (bValue > 0 && bValue != aValue) {
					s += "/";
					s += bValue;
				}				
			}
			s += ")";
			if (i < a.resources.length - 1) {
				s += ", ";
			}
		}
		s += "}";
		return s;
	}

	@Override
	public Integer get(Object o) {
		if (this.containsKey(o))
			return super.get(o);
		return 0;
	}
	
}
