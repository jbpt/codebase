package org.jbpt.pm.bparc;

import java.util.*;


public class Relation {
	
//	private static Relation instance;
	 
	private Map<SendingEvent, List<StartEvent>> triggers = new HashMap<SendingEvent, List<StartEvent>>();
	private Map<SendingEvent, List<IntermediateCatchingEvent>> messages = new HashMap<SendingEvent, List<IntermediateCatchingEvent>>();
	
	public Relation(){
	// nothing to do here	
	}
	
//	public static Relation getInstance(){
//		if(Relation.instance == null){
//			Relation.instance = new Relation();
//		}
//		return Relation.instance;
//	}
	
	public void addTriggers(SendingEvent send, List<StartEvent> start){
		
		if(triggers.get(send)!=null){
			List<StartEvent> triggeredevents = triggers.get(send);
			 
			ListIterator<StartEvent> litr = start.listIterator();
			    while (litr.hasNext()) {
			      triggeredevents.add(litr.next());			     
			    }
			
		}else{
			triggers.put(send, start);	
		}
		
	}
	
	public void addTrigger(SendingEvent send, StartEvent start) {
		if (triggers.get(send) != null) {
			triggers.get(send).add(start);
		} else {
			List<StartEvent> triggered = new ArrayList<StartEvent>();
			triggered.add(start);
			triggers.put(send, triggered);	
		}
		//TODO is it better to use addToPreset() instead of getPostset().add()???
		start.addToPreset(send);
		send.getPostset().add(start);
	}
	
	public void addMessage(SendingEvent send, IntermediateCatchingEvent catchint){
		if (messages.get(send) != null) {
			messages.get(send).add(catchint);
		} else {
			List<IntermediateCatchingEvent> receivers = new ArrayList<IntermediateCatchingEvent>();
			receivers.add(catchint);
			messages.put(send, receivers);
		}
		send.getPostset().add(catchint);
		catchint.getPreset().add(send);
	}
	
	public List<StartEvent> getTriggered(SendingEvent send){
		List<StartEvent> triggeredevents = triggers.get(send);
		if(triggeredevents==null) triggeredevents = new ArrayList<StartEvent>();
		return triggers.get(send);
	}
}
