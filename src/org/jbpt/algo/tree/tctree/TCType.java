package org.jbpt.algo.tree.tctree;

/**
 * Structural types of triconnected components.
 * 
 * @author Artem Polyvyanyy
 * 
 * P - polygon (sequence of components)
 * B - bond (set of components that share boundary vertices)
 * R - rigid (neither trivial, nor polygon, nor bond)
 */
public enum TCType {
	P, 
	B, 
	R, 
	UNDEFINED
}
