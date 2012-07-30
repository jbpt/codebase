package org.jbpt.petri.wft;

/**
 * WF-tree classification of bonds. 
 *
 * @author Artem Polyvyanyy
 */
public enum WFTreeBondType {
	PLACE_BORDERED, 
	TRANSITION_BORDERED, 
	LOOP,
	UNDEFINED;
}
