package org.jbpt.pm.gen.bootstrap;

public class Generalization {
	public double pre; 
	public double rec;
	public double prestddev;
	public double recstddev;
	
	public Generalization(double pre, double rec) {
		this.pre = pre;
		this.rec = rec;
	}
	
	public Generalization(double pre, double rec, double preStdDev, double recStdDev) {
		this.pre = pre;
		this.rec = rec;
		this.prestddev = preStdDev;
		this.recstddev = recStdDev;
	}

	public Generalization(String pre, String rec) {
		this.pre = Double.parseDouble(pre);
		this.rec = Double.parseDouble(rec);
	}
	
	@Override
	public String toString() {
		return "["+this.pre+","+this.rec+"]";
	}

}
