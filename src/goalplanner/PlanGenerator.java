package goalplanner;

import goalplanner.ResourceSet.ResourceType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class PlanGenerator {
	
	public class PlanTemplate {
		public String[] resources;
		
		public ResourceType[] types;
		
		public int[] amounts;
		
		public String[] effects;
		
		public ArrayList<Property> properties;
		
		public PlanTemplate() {
			this(new String[0], new ResourceType[0], new int[0], new String[0], new ArrayList<Property>());
		}
		
		public PlanTemplate(String[] resources, ResourceType[] types, int[] amounts, String[] effects, ArrayList<Property> properties) {
			this.resources = resources;
			this.types = types;
			this.amounts = amounts;
			this.effects = effects;
			this.properties = properties;
		}
		
		public String toString() {
			String s = "PlanTemplate: " + Arrays.toString(resources) + " "
					+ Arrays.toString(types) + " "
					+ Arrays.toString(amounts) + " "
					+ Arrays.toString(effects) + " "
					+ properties.toString();
			return s;
		}
	}

	public static final int GENERATE_RANDOM = 0;
	
	protected int method;
	
	protected String[] resources;
	
	protected ResourceType[] types;

	protected HashMap<String, Integer> violations;
	
	public PlanGenerator() {
		this.method = GENERATE_RANDOM;
		this.resources = new String[]{"money","energy"};
		this.types = new ResourceType[]{ResourceType.CONSUMABLE ,ResourceType.CONSUMABLE};
		this.violations = new HashMap<String, Integer>();
		this.violations.put("money", 100);
		this.violations.put("energy", 5);
	}
	
	public PlanTemplate generateTemplate() {
		ArrayList<String> listResources = new ArrayList<String>();
		ArrayList<ResourceType> listTypes = new ArrayList<ResourceType>();
		for (String r : resources) {
			listResources.add(r);
		}
		for (ResourceType t : types) {
			listTypes.add(t);
		}
		
		ArrayList<String> chosenResources = new ArrayList<String>();
		ArrayList<ResourceType> chosenTypes = new ArrayList<ResourceType>();
		
		Random random = new Random();
		
		int total = random.nextInt(resources.length + 1);
		int remaining = total;
		for (int i = 0; i < total; i++) {
			int k = (int) Math.floor(random.nextDouble() * remaining);
			String chosenResource = listResources.get(k);
			ResourceType chosenType = listTypes.get(k);
			chosenResources.add(chosenResource);
			chosenTypes.add(chosenType);
			listResources.remove(k);
			listTypes.remove(k);
			remaining--;
		}
		
		String[] planResources = new String[chosenResources.size()];
		ResourceType[] planTypes = new ResourceType[chosenTypes.size()];
		for (int i = 0; i < planResources.length; i++) {
			planResources[i] = chosenResources.get(i);
		}
		for (int i = 0; i < planTypes.length; i++) {
			planTypes[i] = chosenTypes.get(i);
		}
		
		int[] amounts = new int[chosenResources.size()];
		for (int i = 0; i < planResources.length; i++) {
			int violation = this.violations.get(planResources[i]);
			amounts[i] = (int) (random.nextDouble() * 10 * violation);
		}
		
		final int max = 5;
		String[] effects = new String[max];
		boolean[] taken = new boolean[max];
		int totalTaken = 0;
		for (int i = 0; i < effects.length; i++) {
			effects[i] = "E" + (i + 1);
			taken[i] = random.nextBoolean();
			if (taken[i]) {
				totalTaken++;
			}
		}
		int index = 0;
		String[] planEffects = new String[totalTaken];
		for (int i = 0; i < effects.length; i++) {
			if (taken[i]) {
				planEffects[index++] = effects[i];
			}
		}
		ArrayList<Property> properties = new ArrayList<Property>();
		return new PlanTemplate(planResources, planTypes, amounts, planEffects, properties);
	}
	
}
