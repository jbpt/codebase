package org.jbpt.pm.bparc;

import org.jbpt.pm.IEvent;

/**
 * @author rami.eidsabbagh
 *
 */
public abstract class Event extends org.jbpt.pm.Event implements IEvent {
	
private int[] multiplicity;
private BparcProcess enclosingProcess;

//protected static Map<Event, Integer> ids = new HashMap<Event, Integer>();
//protected static Integer maxId = 0;


public Event clone() {
	//TODO: implement
	throw new UnsupportedOperationException();
}

public Event(String label, int[] mult){
	super(label);
//	this.owner = bpid;
	this.multiplicity = mult;
}

/**
 * Constructor with trivial multiplicity.
 * @param bpid
 * @param label
 */
public Event (String label) {
	this(label, new int[]{1});
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

public BparcProcess getEnclosingProcess() {
	return enclosingProcess;
}

public void setEnclosingProcess(BparcProcess enclosingProcess) {
	this.enclosingProcess = enclosingProcess;
}


}
