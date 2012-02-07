/**
 * 
 */
package de.hpi.bpt.process.bpmn;

import de.hpi.bpt.process.IDataNode;

/**
 * Interface class for BPMN Document.
 * 
 * @author Cindy F�hnrich
 */
public interface IDocument extends IDataNode {
	
	/**
	 * Marks this Document as list.
	 */
	public void markAsList();
	
	/**
	 * Unmarks this Document as list.
	 */
	public void unmarkAsList();
	
	/**
	 * Checks whether the current Document is a list.
	 * @return
	 */
	public boolean isList();

}
