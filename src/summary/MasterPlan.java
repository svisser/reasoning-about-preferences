package summary;
import goalplanner.ExecutionSummary;
import goalplanner.GoalPlanTree;
import goalplanner.PlanGenerator;
import goalplanner.PlanGenerator.PlanTemplate;
import goalplanner.Property;
import goalplanner.ResourceSet.ResourceType;
import jadex.model.IMAchieveGoal;
import jadex.model.IMGoalbase;
import jadex.model.IMParameterSet;
import jadex.model.IMPlan;
import jadex.model.IMPlanbase;
import jadex.model.IMReference;
import jadex.model.jibximpl.MExpression;
import jadex.runtime.IGoal;
import jadex.runtime.Plan;
import jadex.util.Tuple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bienvenu.AggregatedPreferenceFormula;
import bienvenu.AtomicPreferenceFormula;
import bienvenu.BasicDesireFormula;
import bienvenu.BasicDesireFormula.BDFType;
import bienvenu.BasicDesireFormulaMinimize;
import bienvenu.BasicDesireFormulaProperty;
import bienvenu.ConditionPreferenceFormula;
import bienvenu.ConditionPreferenceFormula.CPFType;
import bienvenu.ConditionPreferenceFormulaProperty;
import bienvenu.GeneralPreferenceFormula;
import bienvenu.GeneralPreferenceFormulaCondition;
import bienvenu.PreferenceFormula;

@SuppressWarnings("serial")
public class MasterPlan extends Plan {
	
	/** The names of goals in an agent specification that should be ignored. */
	public static final ArrayList<String> ignoreGoals;
	
	/** The names of plans in an agent specification that should be ignored. */
	public static final ArrayList<String> ignorePlans;
	
	/** The goal-plan tree as extracted from the agent specification. */
	public static GoalPlanTree tree;
	
	/** A plan generator, used to create the plan templates. */
	public static final PlanGenerator planGenerator = new PlanGenerator();
	
	public static int PREFS_TO_USE = 1;
	
	static {
		ignorePlans = new ArrayList<String>();
		ignorePlans.add("masterplan");
		ignorePlans.add("metaplan");
		ignorePlans.add("pingplan");
		ignoreGoals = new ArrayList<String>();
		ignoreGoals.add("mastergoal");
		ignoreGoals.add("metagoal");
	}
	
	private ArrayList<GeneralPreferenceFormula> createExampleOne() {
		ArrayList<GeneralPreferenceFormula> prefs = new ArrayList<GeneralPreferenceFormula>();
		BasicDesireFormula e1b1 = new BasicDesireFormulaProperty(BDFType.PROP_EQUALS, "accommodation.type", "backpacker");
		BasicDesireFormula e1b2 = new BasicDesireFormulaProperty(BDFType.PROP_EQUALS, "accommodation.type", "hotel");
		BasicDesireFormula[] e1ba1 = new BasicDesireFormula[] { e1b1, e1b2 };
		int[] e1va1 = new int[] { 0, 100 };
		GeneralPreferenceFormula e1g1 = new AtomicPreferenceFormula(e1ba1, e1va1);
		prefs.add(e1g1);
		
		BasicDesireFormula e1b3 = new BasicDesireFormulaProperty(BDFType.PROP_EQUALS, "transport.primary_transport", "train");
		BasicDesireFormula e1b4 = new BasicDesireFormulaProperty(BDFType.PROP_EQUALS, "transport.primary_transport", "plane");
		BasicDesireFormula[] e1ba2 = new BasicDesireFormula[] { e1b3, e1b4 };
		int[] e1va2 = new int[] { 0, 100 };
		GeneralPreferenceFormula e1g2 = new AtomicPreferenceFormula(e1ba2, e1va2);
		prefs.add(e1g2);
		
		BasicDesireFormula e1b5 = new BasicDesireFormulaMinimize("money");
		BasicDesireFormula[] e1ba3 = new BasicDesireFormula[] { e1b5 };
		int[] e1va3 = new int[] { 0 };
		GeneralPreferenceFormula e1g3 = new AtomicPreferenceFormula(e1ba3, e1va3);
		prefs.add(e1g3);
		
		BasicDesireFormula e1b6 = new BasicDesireFormulaProperty(BDFType.PROP_EQUALS, "transport.book_flight.airline", "jetstar");
		ConditionPreferenceFormula e1c1 = new ConditionPreferenceFormulaProperty(CPFType.PROP_EQUALS, "transport.primary_transport", "plane");
		BasicDesireFormula[] e1ba4 = new BasicDesireFormula[] { e1b6 };
		int[] e1va4 = new int[] { 0 };
		GeneralPreferenceFormula e1g4 = new GeneralPreferenceFormulaCondition(e1c1, new AtomicPreferenceFormula(e1ba4, e1va4));
		prefs.add(e1g4);
		
		BasicDesireFormula e1b7 = new BasicDesireFormulaProperty(BDFType.PROP_EQUALS, "transport.book_airport_transport.secondary_transport", "public_transport");
		ConditionPreferenceFormula e1c2 = new ConditionPreferenceFormulaProperty(CPFType.PROP_EQUALS, "transport.primary_transport", "plane");
		BasicDesireFormula[] e1ba5 = new BasicDesireFormula[] { e1b7 };
		int[] e1va5 = new int[] { 0 };
		GeneralPreferenceFormula e1g5 = new GeneralPreferenceFormulaCondition(e1c2, new AtomicPreferenceFormula(e1ba5, e1va5));
		prefs.add(e1g5);
		
		BasicDesireFormula e1b8 = new BasicDesireFormulaProperty(BDFType.PROP_EQUALS, "luggage.type", "backpack");
		BasicDesireFormula[] e1ba6 = new BasicDesireFormula[] { e1b8 };
		int[] e1va6 = new int[] { 0 };
		GeneralPreferenceFormula e1g6 = new AtomicPreferenceFormula(e1ba6, e1va6);
		prefs.add(e1g6);
		
		BasicDesireFormula e1b9 = new BasicDesireFormulaProperty(BDFType.PROP_EQUALS, "luggage.size", "50liter");
		BasicDesireFormula e1b10 = new BasicDesireFormulaProperty(BDFType.PROP_EQUALS, "luggage.size", "40liter");
		BasicDesireFormula e1b11 = new BasicDesireFormulaProperty(BDFType.PROP_EQUALS, "luggage.size", "30liter");
		BasicDesireFormula[] e1ba7 = new BasicDesireFormula[] { e1b9, e1b10, e1b11 };
		int[] e1va7 = new int[] { 0, 50, 100 };
		GeneralPreferenceFormula e1g7 = new AtomicPreferenceFormula(e1ba7, e1va7);
		prefs.add(e1g7);
		return prefs;
	}
	
	private ArrayList<GeneralPreferenceFormula> createExampleTwo() {
		ArrayList<GeneralPreferenceFormula> prefs = new ArrayList<GeneralPreferenceFormula>();
		BasicDesireFormula e2b1 = new BasicDesireFormulaProperty(BDFType.PROP_EQUALS, "transport.primary_transport", "plane");
		BasicDesireFormula e2b2 = new BasicDesireFormulaProperty(BDFType.PROP_EQUALS, "transport.primary_transport", "train");
		BasicDesireFormula[] e2ba1 = new BasicDesireFormula[] { e2b1, e2b2 };
		int[] e2va1 = new int[] { 0, 100 };
		GeneralPreferenceFormula e2g1 = new AtomicPreferenceFormula(e2ba1, e2va1);
		prefs.add(e2g1);
		
		BasicDesireFormula e2b3 = new BasicDesireFormulaProperty(BDFType.PROP_EQUALS, "transport.book_flight.airline", "qantas");
		BasicDesireFormula e2b4 = new BasicDesireFormulaProperty(BDFType.PROP_EQUALS, "transport.book_flight.airline", "jetstar");
		BasicDesireFormula[] e2ba2 = new BasicDesireFormula[] { e2b3, e2b4 };
		int[] e2va2 = new int[] { 0, 100 };
		GeneralPreferenceFormula e2g2 = new AtomicPreferenceFormula(e2ba2, e2va2);
		prefs.add(e2g2);
		
		BasicDesireFormulaProperty e2b6 = new BasicDesireFormulaProperty(BDFType.PROP_EQUALS, "accommodation.quality", "5*");
		BasicDesireFormula[] e2ba3 = new BasicDesireFormula[] { e2b6 };
		int[] e2va3 = new int[] { 0 };
		GeneralPreferenceFormula e2g3 = new AtomicPreferenceFormula(e2ba3, e2va3);
		prefs.add(e2g3);
		
		//ConditionPreferenceFormulaProperty e2cp1 = new ConditionPreferenceFormulaProperty(CPFType.PROP_EQUALS, "accommodation.type", "hotel");
		ConditionPreferenceFormulaProperty e2cp2 = new ConditionPreferenceFormulaProperty(CPFType.PROP_EQUALS, "accommodation.quality", "3*");
		ArrayList<ConditionPreferenceFormulaProperty > e2al2 = new ArrayList<ConditionPreferenceFormulaProperty>();
		//e2al2.add(e2cp1);
		e2al2.add(e2cp2);
		//ConditionPreferenceFormulaMultiple e2c1 = new ConditionPreferenceFormulaMultiple(CPFType.AND, e2al2);
		BasicDesireFormula e2b8 = new BasicDesireFormulaProperty(BDFType.PROP_EQUALS, "luggage.size", "40liter");
		BasicDesireFormula[] e2ba4 = new BasicDesireFormula[] { e2b8 };
		int[] e2va4 = new int[] { 0 };
		GeneralPreferenceFormula e2g4 = new GeneralPreferenceFormulaCondition(e2cp2, new AtomicPreferenceFormula(e2ba4, e2va4));
		prefs.add(e2g4);
		
		BasicDesireFormulaProperty e2b9 = new BasicDesireFormulaProperty(BDFType.PROP_EQUALS, "transport.book_airport_transport.secondary_transport", "taxi");
		BasicDesireFormula[] e2ba5 = new BasicDesireFormula[] { e2b9 };
		int[] e2va5 = new int[] { 0 };
		GeneralPreferenceFormula e2g5 = new AtomicPreferenceFormula(e2ba5, e2va5);
		prefs.add(e2g5);		
		BasicDesireFormulaProperty e2b10 = new BasicDesireFormulaProperty(BDFType.PROP_EQUALS, "transport.book_airport_transport.payment", "credit");
		BasicDesireFormula[] e2ba6 = new BasicDesireFormula[] { e2b10 };
		int[] e2va6 = new int[] { 0 };
		GeneralPreferenceFormula e2g6 = new AtomicPreferenceFormula(e2ba6, e2va6);
		prefs.add(e2g6);
		
		BasicDesireFormula e2b11 = new BasicDesireFormulaProperty(BDFType.PROP_EQUALS, "luggage.type", "suitcase");
		BasicDesireFormula[] e1ba7 = new BasicDesireFormula[] { e2b11 };
		int[] e1va7 = new int[] { 50 };
		GeneralPreferenceFormula e2g7 = new AtomicPreferenceFormula(e1ba7, e1va7);
		prefs.add(e2g7);
		return prefs;
	}
	
	@Override
	public void body() {
		ArrayList<GeneralPreferenceFormula> prefs = null;
		if (PREFS_TO_USE == 1) {
			prefs = createExampleOne();
		} else if (PREFS_TO_USE == 2) {
			prefs = createExampleTwo();
		} else if (PREFS_TO_USE == 3) {
			prefs = createExampleTwo();
		}
		
		System.out.println("PREFERENCES:");
		for (GeneralPreferenceFormula f : prefs) {
			System.out.println("- " + f.toString());	
		}
		PreferenceFormula.prefs = new AggregatedPreferenceFormula(prefs);
		MasterPlan.tree = jadexToGoalPlanTree();
		MasterPlan.tree.attachOrderings(prefs);
		MasterPlan.tree.computePreferences();

		IGoal rGoal = createGoal(MasterPlan.tree.getRoot().getName());
		this.dispatchSubgoalAndWait(rGoal);
	}
	
	/**
	 * Convert the Jadex data structures to a goal-plan tree.
	 * @return
	 */
	private GoalPlanTree jadexToGoalPlanTree() {
		// gather goals & plans
		IMGoalbase goalModel = (IMGoalbase) this.getGoalbase().getModelElement();
		IMAchieveGoal[] goals = goalModel.getAchieveGoals();
		IMPlanbase planModel = (IMPlanbase) this.getPlanbase().getModelElement();
		IMPlan[] plans = planModel.getPlans();
		
		// gather relationships for tree structure
		HashMap<IMAchieveGoal, ArrayList<IMPlan>> goalToPlans = new HashMap<IMAchieveGoal, ArrayList<IMPlan>>();
		for (IMAchieveGoal goal : goals) {
			String goalName = goal.getName();
			if (ignoreGoals.contains(goalName))
				continue;
			ArrayList<IMPlan> gPlans = this.getPlans(goalName, plans);
			goalToPlans.put(goal, gPlans);
		}
		HashMap<IMPlan, ArrayList<IMAchieveGoal>> planToGoals = new HashMap<IMPlan, ArrayList<IMAchieveGoal>>();
		for (IMPlan plan : plans) {
			if (ignorePlans.contains(plan.getName()))
				continue;
			ArrayList<IMAchieveGoal> pGoals = this.getGoals(plan, goals);
			planToGoals.put(plan, pGoals);
		}
		
		// assign resource/effect summaries
		for (IMPlan plan : plans) {
			if (ignorePlans.contains(plan.getName()))
				continue;
			PreferencePlan.templates.put(plan.getName(), constructPlanTemplate(plan));
			PreferencePlan.children.put(plan.getName(), getSubgoalNames(planToGoals.get(plan)));
		}
		return new GoalPlanTree(goalToPlans, planToGoals, PreferencePlan.templates);
	}
	
	private String[] getSubgoalNames(ArrayList<IMAchieveGoal> subgoals) {
		String[] subgoalNames = new String[subgoals.size()];
		for (int i = 0; i < subgoals.size(); i++) {
			subgoalNames[i] = subgoals.get(i).getName();
		}
		return subgoalNames;
	}
	
	/**
	 * Convert a Jadex data structure to a PlanTemplate containing the plan's information.
	 * @param plan
	 * @return
	 */
	private PlanTemplate constructPlanTemplate(IMPlan plan) {
		String[] effects = getStringParameterSet(plan.getParameterSet("effects"));
		Tuple[] tuples = getTupleParameterSet(plan.getParameterSet("resources"));
		String[] resources = new String[tuples.length];
		ResourceType[] types = new ResourceType[tuples.length];
		Arrays.fill(types, ResourceType.CONSUMABLE);
		int[] amounts = new int[tuples.length];
		for (int i = 0; i < tuples.length; i++) {
			resources[i] = (String) tuples[i].get(0);
			amounts[i] = (Integer) tuples[i].get(1);
			if (tuples[i].size() == 3) {
				int value = (Integer) tuples[i].get(2);
				types[i] = value == 0 ? ResourceType.CONSUMABLE : ResourceType.REUSABLE;
			}
		}
		ArrayList<Property> properties = getProperties(plan);
		return MasterPlan.planGenerator.new PlanTemplate(resources, types, amounts, effects, properties);
	}
	
	/**
	 * Construct a list of properties of the given plan.
	 * @param plan
	 * @return
	 */
	private ArrayList<Property> getProperties(IMPlan plan) {
		ArrayList<Property> result = new ArrayList<Property>();
		IMParameterSet set = plan.getParameterSet("properties");
		if (set == null) return result;
		List elems = set.getChildren();
		for (int i = 0; i < elems.size(); i++) {
			MExpression expr = (MExpression) elems.get(i);
			Map map = expr.getEncodableRepresentation();
			try {
				result.add((Property) expr.getTerm().getValue(map));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	// Various methods for converting a parameter set into a data structure.
	
	private Tuple[] getTupleParameterSet(IMParameterSet parameterSet) {
		if (parameterSet == null)
			return new Tuple[0];
		List elems = parameterSet.getChildren();
		Tuple[] result = new Tuple[elems.size()];
		for (int i = 0; i < elems.size(); i++) {
			MExpression expr = (MExpression) elems.get(i);
			Map map = expr.getEncodableRepresentation();
			try {
				result[i] = (Tuple) expr.getTerm().getValue(map);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	private String[] getStringParameterSet(IMParameterSet parameterSet) {
		if (parameterSet == null)
			return new String[0];
		List elems = parameterSet.getChildren();
		String[] result = new String[elems.size()];
		for (int i = 0; i < elems.size(); i++) {
			MExpression expr = (MExpression) elems.get(i);
			Map map = expr.getEncodableRepresentation();
			try {
				result[i] = (String) expr.getTerm().getValue(map); 
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}	
	private int[] getIntParameterSet(IMParameterSet parameterSet) {
		if (parameterSet == null)
			return new int[0];
		List elems = parameterSet.getChildren();
		int[] result = new int[elems.size()];
		for (int i = 0; i < elems.size(); i++) {
			MExpression expr = (MExpression) elems.get(i);
			Map map = expr.getEncodableRepresentation();
			try {
				result[i] = (Integer) expr.getTerm().getValue(map); 
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	/**
	 * Obtain the plans of the given goal, based on the Jadex data structures.
	 * @param goalName
	 * @param plans
	 * @return
	 */
	private ArrayList<IMPlan> getPlans(String goalName, IMPlan[] plans) {
		ArrayList<IMPlan> result = new ArrayList<IMPlan>();
		for (IMPlan plan : plans) {
			if (ignorePlans.contains(plan.getName()))
				continue;
			if (plan.getTrigger() != null) {
				IMReference ref = plan.getTrigger().getGoals()[0];
				if (ref.getReference().equals(goalName)) {
					result.add(plan);
				}
			}
		}
		return result;
	}
	
	/**
	 * Obtain the subgoals of the given plan, based on the Jadex data structures.
	 * @param plan
	 * @param goals
	 * @return
	 */
	private ArrayList<IMAchieveGoal> getGoals(IMPlan plan, IMAchieveGoal[] goals) {
		ArrayList<IMAchieveGoal> result = new ArrayList<IMAchieveGoal>();
		IMParameterSet set = plan.getParameterSet("subgoals");
		if (set == null)
			return result;
		String[] subgoals = getStringParameterSet(set);
		for (int i = 0; i < subgoals.length; i++) {
			for (IMAchieveGoal goal : goals) {
				if (ignoreGoals.contains(goal.getName()))
					continue;
				if (goal.getName().equals(subgoals[i])) {
					result.add(goal);
				}
			}
		}
		return result;
	}
	
	private void onFinish() {
		ExecutionSummary.displayOutcome(tree);
		MasterPlan.tree.getExecution().clear();
	}

	@Override
	public void passed() {
		onFinish();
	}
	
	@Override
	public void failed() {
		onFinish();
	}

}
