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
	
	protected BehaviouralProfile<PetriNet, Node> bp1;
	protected BehaviouralProfile<PetriNet, Node> bp2;
	
	public BPAlignment(PetriNet net1, PetriNet net2) {
		super(net1,net2);
	}

	public BPAlignment(BehaviouralProfile<PetriNet, Node> bp1, BehaviouralProfile<PetriNet, Node> bp2) {
		super(bp1.getModel(),bp2.getModel());
		this.bp1 = bp1;
		this.bp2 = bp2;
	}
	
	public BehaviouralProfile<PetriNet, Node> getFirstProfile() {
		return this.bp1;
	}
	
	public BehaviouralProfile<PetriNet, Node> getSecondProfile() {
		return this.bp2;
	}
	
	/**
	 * Init the alignment with correspondences between nodes that
	 * carry equal labels.
	 * 
	 * We overwrite the method from the superclass in order to avoid
	 * that tau-transitions are all mapped to each other.
	 */
	@Override
	public void initCorrespondenceRelation() {
		for (Node v1 : getFirstGraph().getVertices()) {
			for (Node v2 : getSecondGraph().getVertices()) {
				
				String s1 = normaliseLabel(v1.getName());
				String s2 = normaliseLabel(v2.getName());
				
				if (s1.equals(s2) && !s1.equals(PetriNet.SILENT_LABEL) && !s2.equals(PetriNet.SILENT_LABEL))
					addElementaryCorrespondence(v1,v2);
			}
		}
	}


}
