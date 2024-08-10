package org.jbpt.pm.gen.bootstrap;

public class BSite {
	
	public BSite(int p1, int p2) {
		super();
		this.p1 = p1;
		this.p2 = p2;
	}
	
	public int p1;
	public int p2;
	
	@Override
	public String toString() {
		return "(" + p1 + "," + p2 + ")";
	}
}
