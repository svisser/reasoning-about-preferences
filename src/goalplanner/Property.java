package goalplanner;

import java.util.ArrayList;

public class Property {
	
	/** The name of the property (possibly with goal prefixes). */
	protected String name;
	
	/** The set of values of this property. */
	protected ArrayList<String> values;

	public static final String SEPARATOR = ".";
	
	public static final String NULL = "null";
	
	/**
	 * Construct a property with the given goal, name and values.
	 * @param goal
	 * @param name
	 * @param values
	 */
	public Property(String name, ArrayList<String> values) {
		this.name = name;
		this.values = values;
	}
	
	/**
	 * Construct a property with the given name and values.
	 * @param name
	 * @param values
	 */
	public Property(String name, String[] values) {
		this.name = name;
		this.values = new ArrayList<String>();
		for (String v : values) {
			this.values.add(v);
		}
	}
	
	/**
	 * Construct a property with a value set of cardinality 1.
	 * @param name
	 * @param value
	 */
	public Property(String name, String value) {
		this.name = name;
		this.values = new ArrayList<String>();
		this.values.add(value);
	}

	/**
	 * Copy constructor
	 * @param property
	 */
	public Property(Property property) {
		this(property, false);
	}
	
	/**
	 * Copy constructor with addNull option. If true, add null if needed.
	 * @param property
	 * @param addNull
	 */
	public Property(Property property, boolean addNull) {
		this.name = property.name;
		this.values = new ArrayList<String>();
		this.values.addAll(property.values);
		if (addNull && !this.values.contains(Property.NULL)) {
			this.values.add(Property.NULL);	
		}
	}
	
	/**
	 * Create a property with the given name and the values
	 * of the given properties.
	 * @param name
	 * @param p0
	 * @param p1
	 */
	public Property(String name, Property p0, Property p1) {
		ArrayList<String> values = new ArrayList<String>();
		values.addAll(p0.values);
		for (String s : p1.values) {
			if (!values.contains(s)) {
				values.add(s);
			}
		}
		this.name = name;
		this.values = values;
	}	
	
	public boolean hasSameNameAs(Property property) {
		return this.name.equals(property.name);
	}
	
	public boolean hasRawName() {
		return !this.name.contains(Property.SEPARATOR);
	}

	public String getRawName() {
		int index = this.name.lastIndexOf(Property.SEPARATOR);
		if (index < 0) return this.name;
		return this.name.substring(index + 1);
	}
	
	public void clearNull() {
		values.remove(Property.NULL);
	}
	
	public String toString() {
		String valuesString = "";
		if (values.isEmpty()) {
			valuesString = "{}";
		} else {
			valuesString = "{" + values.toString().substring(1, values.toString().length() - 1) + "}";
		}
		return name + " = " + valuesString; 
	}
	
	public String getName() {
		return this.name;
	}

	public ArrayList<String> getValues() {
		return values;
	}
	
	public void setValues(ArrayList<String> values) {
		this.values = values;
	}
	
}
