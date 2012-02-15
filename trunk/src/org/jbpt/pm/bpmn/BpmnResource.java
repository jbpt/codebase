/**
 * 
 */
package org.jbpt.pm.bpmn;

import org.jbpt.pm.IResource;
import org.jbpt.pm.Resource;

/**
 * Class for BPMN Resources, such as Pools or Lanes.
 * @author Tobias Hoppe
 *
 */
public class BpmnResource extends Resource implements IBpmnResource {

	/**
	 * says whether the resource is a Lane or Pool
	 */
	private String type;
	
	/**
	 * Constructor 
	 */
	public BpmnResource() {
		super();
	}

	/**
	 * Constructor with setting the parent of this BPMN Resource
	 * @param parent
	 */
	public BpmnResource(IResource parent) {
		super(parent);
	}

	/**
	 * Constructor
	 * @param parent the parent of this BPMN Resource
	 * @param label the label/name of this BPMN Resource
	 */
	public BpmnResource(IResource parent, String label) {
		super(parent, label);
	}
	
	@Override
	public BpmnResource clone() {
		BpmnResource clone = (BpmnResource) super.clone();
		if (this.type != null) {
			clone.type = new String(this.type);
		}
		return clone;
	}
	
	/**
	 * Sets the BPMN Resource type of this Resource
	 * @param type "Lane" or "Pool"
	 */
	public void setType(String type){
		this.type = type;
	}
	/**
	 * Returns the BPMN Resource type of this Resource
	 * @return type "Lane" or "Pool"
	 */
	public String getType(){
		return this.type;
	}

}
