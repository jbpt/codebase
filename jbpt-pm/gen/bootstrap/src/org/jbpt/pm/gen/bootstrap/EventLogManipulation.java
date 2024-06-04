package org.jbpt.pm.gen.bootstrap;

import java.util.Random;

public class EventLogManipulation extends EventLogUtils {
	
	public static void swapRandomEvents(EventLog log, int n) {
		if (log==null || n<=0) return;
		
		Random random = new Random(System.currentTimeMillis());
		for (int i=0; i<n; i++) {
			Trace t = EventLogManipulation.random(log);
			int p1 = random.nextInt(t.size());
			int p2 = random.nextInt(t.size());
			if (p1!=p2) {
				String tmp = t.get(p1);
				t.set(p1, t.get(p2));
				t.set(p2, tmp);
			}
		}
	}

}
