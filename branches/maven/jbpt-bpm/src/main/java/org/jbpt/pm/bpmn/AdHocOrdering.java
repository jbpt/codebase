/**
 * 
 */
package org.jbpt.pm.bpmn;

/**
 * Different execution orderings for Adhoc subprocesses.
 * "None" indicates, that it is no adhoc subprocess at all or not set.
 * 
 * @author Tobias Hoppe
 */
public enum AdHocOrdering {
	Parallel,
	Sequential,
	None
}
