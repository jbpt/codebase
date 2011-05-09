package de.hpi.bpt.process;

public class Pair implements Comparable<Pair> {
	Node first;
	Node second;
	
	Pair() {
	}
	
	public Pair(Node f, Node s) {
		first = f;
		second = s;
	}
	
	Pair(Pair e) { 
		first = e.first;
		second = e.second;
	}
	
	public Node getFirst() {
		return first;
	}
	
	public Node getSecond() {
		return second;
	}
	
	public void setFirst(Node val) {
		first = val;
	}
	
	public void setSecond(Node val) {
		second = val;
	}
	
	public int compareTo(Pair o) {
		int compare = first.compareTo(o.first);
		if (compare != 0) return compare;
		return (second).compareTo(o.second);
	}
	
	@Override
	public String toString() {
		return String.format("(%s,%s)", first, second);
    }

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
        if (!(obj instanceof Pair)) return false;
        Pair that = (Pair) obj;
        
        return this.first.equals(that.first) && this.second.equals(that.second);
	}

	@Override
	public int hashCode() {
		return (first == null ? 0 : first.hashCode()) + (second == null ? 0 : second.hashCode() * 37);
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		Pair clone = new Pair(this);
		return clone;
	}
}
