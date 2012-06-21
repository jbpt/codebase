package org.jbpt.algo.tree.tctree;

/**
 * Structural types of triconnected components.
 * 
 * @author Artem Polyvyanyy
 * 
 * T - trivial (edge)
 * P - polygon (sequence of components)
 * B - bond (set of components that share boundary vertices)
 * R - rigid (neither trivial, nor polygon, bond)
 */
public enum TCType {
	T, 
	P, 
	B, 
	R, 
	UNDEFINED
}
