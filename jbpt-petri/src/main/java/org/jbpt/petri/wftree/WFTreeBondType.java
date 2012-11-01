package org.jbpt.petri.wftree;

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
