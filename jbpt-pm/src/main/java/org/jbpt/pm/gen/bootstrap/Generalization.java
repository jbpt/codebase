package org.jbpt.pm.gen.bootstrap;

public class Generalization {
	public double pre; 
	public double rec;
	public double preConfInt;
	public double recConfInt;
	
	public Generalization(double pre, double rec) {
		this.pre = pre;
		this.rec = rec;
	}
	
	public Generalization(double pre, double rec, double preConfInt, double recConfInt) {
		this.pre = pre;
		this.rec = rec;
		this.preConfInt = preConfInt;
		this.recConfInt = recConfInt;
	}

	public Generalization(String pre, String rec) {
		this.pre = Double.parseDouble(pre);
		this.rec = Double.parseDouble(rec);
	}
	
	@Override
	public String toString() {
		return this.pre+", "+this.rec;
	}

}
