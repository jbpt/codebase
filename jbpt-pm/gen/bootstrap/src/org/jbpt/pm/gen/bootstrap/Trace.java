package org.jbpt.pm.gen.bootstrap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class Trace extends ArrayList<String> {

	public Trace() {
		super();
	}

	public Trace(Collection<? extends String> c) {
		super(c);
	}

	private static final long serialVersionUID = 8117097524302408331L;

	public static Trace crossover(Trace t1, Trace t2, int p1, int p2, int k) {
		Trace result = new Trace();
		Iterator<String> it1 = t1.iterator();
		Iterator<String> it2 = t2.iterator();
		
		for (int i=0; i<(p1+k); i++) result.add(it1.next());		
		for (int i=0; i<(p2+k); i++) it2.next();		
		while (it2.hasNext()) result.add(it2.next());
		
		return result;
	}

}
