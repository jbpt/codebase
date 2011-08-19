package de.hpi.bpt.process.petri.unf;

/**
 * Configure unfolding
 * 
 * @author Artem Polyvyanyy
 */
public class Setup {
	public int MAX_BOUND = 1;
	public int MAX_EVENTS = Integer.MAX_VALUE;
	public AdequateOrder ADEQUATE_ORDER = new McMillanAdequateOrder();
}
