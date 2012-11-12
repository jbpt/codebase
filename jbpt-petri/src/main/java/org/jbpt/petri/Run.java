package org.jbpt.petri;

/**
 * Run (occurrence sequence) of a net system.
 * 
 * @author Artem Polyvyanyy
 */
public class Run extends AbstractRun<Flow,Node,Place,Transition,Marking> {

	private static final long serialVersionUID = -7873810995440121311L;

	public Run(INetSystem<Flow, Node, Place, Transition, Marking> sys) {
		super(sys);
	}

}
