package summary;

import jadex.runtime.IGoal;
import jadex.runtime.Plan;

@SuppressWarnings("serial")
public class PingPlanOne extends Plan
{
	public void body()
	{
		MasterPlan.PREFS_TO_USE = 1;
		IGoal rGoal = createGoal("mastergoal");
		this.dispatchSubgoalAndWait(rGoal);
	}
}

