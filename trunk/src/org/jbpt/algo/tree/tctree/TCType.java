package org.jbpt.algo.tree.tctree;

/**
 * Enumeration of structural types of the triconnected components.<br/><br/>
 * 
 * Note that every edge of a graph is a trivial component, but is not explicitly computed by {@link TCTree}.<br/><br/>
 * 
 * POLYGON - sequence of triconnected components.<br/>
 * BOND - set of triconnected components that share a split pair.<br/>
 * RIGID - neither a trivial, nor polygon, nor bond component.<br/>
 * 
 * @author Artem Polyvyanyy
 */
public enum TCType {
	POLYGON, 
	BOND, 
	RIGID, 
	UNDEFINED
}
