package summary;

import jadex.util.Tuple;

public class Triple extends Tuple {

	public Triple(Object o1, Object o2, Object o3) {
		super(new Object[] { o1, o2, o3 });
	}
	
	public Triple(Object o1, Object o2) {
		super(o1, o2);
	}
}
