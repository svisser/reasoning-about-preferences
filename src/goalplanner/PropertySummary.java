package goalplanner;

import java.util.ArrayList;

// raw name = property name
// non-raw name = property name with goal prefixes
public class PropertySummary {

	/** Property objects of this property set. */
	protected ArrayList<Property> properties;
	
	/**
	 * Create an empty property summary.
	 */
	public PropertySummary() {
		this(new ArrayList<Property>());
	}
	
	/**
	 * Create a property summary with the given properties.
	 * @param properties
	 */
	public PropertySummary(ArrayList<Property> properties) {
		this.properties = properties;
	}
	
	/**
	 * Copy constructor.
	 * @param summary
	 */
	public PropertySummary(PropertySummary summary) {
		this.properties = new ArrayList<Property>();
		for (Property p : summary.properties) {
			this.properties.add(new Property(p));
		}
	}

	/**
	 * Merge property summaries as if they were children of a plan node.
	 * @param other
	 * @return
	 */
	public PropertySummary addPlan(PropertySummary other) {
		PropertySummary result = new PropertySummary();
		for (Property p : this.properties) {
			result.addProperty(new Property(p));
		}
		for (Property q : other.properties) {
			result.addProperty(new Property(q));
		}
		return result;
	}
	
	/**
	 * Merge property summaries as if they were children of a goal node.
	 * @param other
	 * @return
	 */
	public PropertySummary addGoal(PropertySummary other) {
		PropertySummary result = new PropertySummary();
		ArrayList<Property> thisMatches = new ArrayList<Property>();
		ArrayList<Property> otherMatches = new ArrayList<Property>();
		// merge the ones that have same name
		for (Property p : this.properties) {
			for (Property q : other.properties) {
				if (p.hasSameNameAs(q)) {
					thisMatches.add(p);
					otherMatches.add(q);
					result.addProperty(new Property(p.name, p, q));
				}
			}
		}
		// add the remaining properties from each with null values
		for (Property p : this.properties) {
			if (!thisMatches.contains(p)) {
				result.addProperty(new Property(p, true));
			}
		}
		for (Property q : other.properties) {
			if (!otherMatches.contains(q)) {
				result.addProperty(new Property(q, true));
			}
		}
		return result;
	}
	
	/**
	 * Prefix all properties in this summary with the given goal name.
	 * @param goalName
	 */
	public void prefixByGoal(String goalName) {
		for (Property p : this.properties) {
			this.prefixByGoal(goalName, p);
		}
	}
	
	public void prefixByGoal(String goalName, Property p) {
		p.name = goalName + Property.SEPARATOR + p.name;
	}
	
	// get all values of the properties with rawName = given name
	public ArrayList<String> getValuesOfProperty(String name) {
		ArrayList<String> values = new ArrayList<String>();
		for (Property p : properties) {
			if (p.getRawName().equals(name)) {
				for (String value : p.values) {
					if (!values.contains(value)) {
						values.add(value);
					}
				}
			}
		}
		return values;
	}
	
	/**
	 * Return true if the summary contains a property with the given name.
	 * @param name
	 * @param raw If true, use raw name of property to compare.
	 * @return
	 */
	public boolean hasProperty(String name, boolean raw) {
		for (Property p : properties) {
			if (raw && p.getRawName().equals(name)) {
				return true;
			}
			if (!raw && p.name.equals(name)) {
				return true;
			}	
		}
		return false;
	}
	
	public Property getProperty(String name) {
		for (Property p : properties) {
			if (p.getName().equals(name)) {
				return p;
			}
		}
		return null;
	}
	
	// return true if a property exists with a non-raw name whose
	// raw name part is equal to 
	public boolean hasNonRawProperty(String name) {
		for (Property p : properties) {
			if (!p.hasRawName() && p.getRawName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public void removeProperty(String name) {
		Property r = null;
		for (Property p : properties) {
			if (p.name.equals(name)) {
				r = p;
				break;
			}
		}
		if (r != null) {
			properties.remove(r);
		}
	}
	
	public void addProperty(Property property) {
		this.properties.add(property);
	}
	
	public boolean isEmpty() {
		return this.properties.isEmpty();
	}
	
	public String toString() {
		return this.properties.toString();
	}

	public PropertySummary createCopy() {
		PropertySummary copy = new PropertySummary();
		for (Property p : properties) {
			copy.addProperty(new Property(p));	
		}
		return copy;
	}

	public ArrayList<Property> getProperties() {
		return properties;
	}
	
}
