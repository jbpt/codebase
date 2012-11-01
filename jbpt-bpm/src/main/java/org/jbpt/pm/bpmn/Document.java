/**
 * 
 */
package org.jbpt.pm.bpmn;

import org.jbpt.pm.DataNode;

/**
 * Class for BPMN Document. This also encapsulates Lists.
 * 
 * @author Cindy Fähnrich
 *
 */
public class Document extends DataNode implements IDocument{

	/**
	 * Attribute to determine whether this is a data list.
	 */
	private boolean isList = false;
	
	/**
	 * 
	 */
	public Document() {
		super();
	}

	/**
	 * @param name
	 * @param desc
	 */
	public Document(String name, String desc) {
		super(name, desc);
	}

	/**
	 * @param name
	 */
	public Document(String name) {
		super(name);
	}

	@Override
	public void markAsList() {
		this.isList = true;		
	}

	@Override
	public void unmarkAsList() {
		this.isList = false;
		
	}

	@Override
	public boolean isList() {
		return this.isList;
	}

}
