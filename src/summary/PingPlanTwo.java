package summary;

import jadex.runtime.IGoal;
import jadex.runtime.Plan;

@SuppressWarnings("serial")
public class PingPlanTwo extends Plan
{
	public void body()
	{
		MasterPlan.PREFS_TO_USE = 2;
		IGoal rGoal = createGoal("mastergoal");
		this.dispatchSubgoalAndWait(rGoal);
	}
}

