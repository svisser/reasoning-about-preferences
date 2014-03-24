package holiday;

import summary.PreferencePlan;

public class BookFiveStarHotelPlan extends PreferencePlan {

	public boolean isFailure() {
		return summary.MasterPlan.PREFS_TO_USE == 3;
	}
}
