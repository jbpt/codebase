package org.jbpt.pm.gen.bootstrap.dfg;

public class EdgeData {
    private int source;
    private int target;
    private int freq;

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public int getFreq() {
        return freq;
    }

    public void setFreq(int weight) {
        this.freq = weight;
    }
}
