package org.jbpt.pm.mspd.utilities;
public class DoublePair {
    private double first;
    private double second;
    private String name;
    private int dominatecounts;

    public DoublePair(String first, String second,String name) {
        this.first = Double.parseDouble(first);
        this.second = Double.parseDouble(second);
        this.name = name;
        this.dominatecounts = 0;
    }

    public void setFirst(double first) {
        this.first = first;
    }

    public void setSecond(double second) {
    	 this.second = second;
    }
    public double getFirst() {
        return first;
    }

    public double getSecond() {
        return second;
    }
    public int getDominateCounts() {
    	return dominatecounts;
    }
    public void incDominatecounts() {
    	dominatecounts++;
    }
    public String getName() {
    	return name;
    }
    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}