package org.jbpt.algo.tree.tctree;

/**
 * Structural types of triconnected components.<br/><br/>
 * 
 * - POLYGON (sequence of components)<br/>
 * - BOND (set of components that share boundary vertices)<br/>
 * - RIGID (neither trivial, nor polygon, nor bond)<br/>
 * 
 * @author Artem Polyvyanyy
 */
public enum TCType {
	POLYGON, 
	BOND, 
	RIGID, 
	UNDEFINED
}
