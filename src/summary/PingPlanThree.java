package summary;

import jadex.runtime.IGoal;
import jadex.runtime.Plan;

@SuppressWarnings("serial")
public class PingPlanThree extends Plan
{
	public void body()
	{
		MasterPlan.PREFS_TO_USE = 3;
		IGoal rGoal = createGoal("mastergoal");
		this.dispatchSubgoalAndWait(rGoal);
	}
}

