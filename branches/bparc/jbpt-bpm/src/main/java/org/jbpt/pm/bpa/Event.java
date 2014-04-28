package org.jbpt.pm.bpa;

import org.jbpt.pm.IEvent;

/**
 * @author rami.eidsabbagh
 *
 */
public abstract class Event extends org.jbpt.pm.Event implements IEvent {
	
	private int[] multiplicity;
	private BpaProcess enclosingProcess;
	
	//protected static Map<Event, Integer> ids = new HashMap<Event, Integer>();
	//protected static Integer maxId = 0;
	
	
	public Event clone() {
		Event clone = (Event) super.clone();
		clone.setMultiplicity(this.multiplicity);
		clone.setEnclosingProcess((BpaProcess) this.enclosingProcess.clone());
		return clone;
	}
	
	public void setEnclosingProcess(BpaProcess enclosingProcess) {
		this.enclosingProcess = enclosingProcess;
	}
	
	public Event(String label, BpaProcess enclosingProcess, int[] mult){
		super(label);
		this.enclosingProcess = enclosingProcess;
		this.multiplicity = mult;
	}
	
	/**
	 * Constructor with trivial multiplicity.
	 * @param bpid
	 * @param label
	 */
	public Event (String label, BpaProcess enclosingProcess) {
		this(label, enclosingProcess, new int[]{1});
	}
	
	// replaced by setName() in GObject, so far not called
	public void setLabel(String newlabel){
		super.setName(newlabel);
	}
	
	public String getLabel(){
		return super.getName();
	}
	
	public void setMultiplicity(int[] mult){
		this.multiplicity = mult;
	}
	
	public int[] getMultiplicity(){
		return this.multiplicity;
	}
	
	public boolean hasTrivialMultiplicity() {
		return (multiplicity.length == 1 && multiplicity[0] == 1);
		
	}
	
	public BpaProcess getEnclosingProcess() {
		return enclosingProcess;
	}
}
