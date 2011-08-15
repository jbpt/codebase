package de.hpi.bpt.process.petri.bp;

import de.hpi.bpt.alignment.Alignment;
import de.hpi.bpt.process.petri.Flow;
import de.hpi.bpt.process.petri.Node;
import de.hpi.bpt.process.petri.PetriNet;

/**
 * Class that captures an alignment between two Petri nets
 * along with their behavioural profiles.
 * 
 * @author matthias.weidlich
 *
 */
public class BPAlignment extends Alignment<Flow, Node> {
	
	protected BehaviouralProfile bp1;
	protected BehaviouralProfile bp2;
	
	public BPAlignment(PetriNet net1, PetriNet net2) {
		super(net1,net2);
	}

	public BPAlignment(BehaviouralProfile bp1, BehaviouralProfile bp2) {
		super(bp1.getNet(),bp2.getNet());
		this.bp1 = bp1;
		this.bp2 = bp2;
	}
	
	public BehaviouralProfile getFirstProfile() {
		return this.bp1;
	}
	
	public BehaviouralProfile getSecondProfile() {
		return this.bp2;
	}

}
