package org.jbpt.bp.construct;

import java.util.Collection;

import org.jbpt.bp.BehaviouralProfile;
import org.jbpt.bp.CausalBehaviouralProfile;
import org.jbpt.petri.log.Trace;
import org.jbpt.petri.log.TraceEntry;


public class CBPCreatorTrace extends AbstractRelSetCreator implements CBPCreator<Trace,TraceEntry> {

	private static CBPCreatorTrace eInstance;
	
	public static CBPCreatorTrace getInstance() {
		if (eInstance == null)
			eInstance  = new CBPCreatorTrace();
		return eInstance;
	}
	
	private CBPCreatorTrace() {
		
	}

	@Override
	public CausalBehaviouralProfile<Trace, TraceEntry> deriveCausalBehaviouralProfile(
			Trace model) {
		return deriveCausalBehaviouralProfile(model,model.getEntities());
	}

	@Override
	public CausalBehaviouralProfile<Trace, TraceEntry> deriveCausalBehaviouralProfile(
			Trace model, Collection<TraceEntry> entities) {

		CausalBehaviouralProfile<Trace, TraceEntry> profile = new CausalBehaviouralProfile<Trace, TraceEntry>(model, entities);
		profile.setMatrix(RelSetCreatorTrace.getInstance().deriveRelationSet(model).getMatrix());	

		/*
		 * Fill the co-occurrence relation
		 */
		fillCooccurrence(model, profile);

		return profile;
	}

	@Override
	public CausalBehaviouralProfile<Trace, TraceEntry> deriveCausalBehaviouralProfile(
			BehaviouralProfile<Trace, TraceEntry> profile) {
		
		Trace model = profile.getModel();

		/*
		 * Get the behavioural profile
		 */
		CausalBehaviouralProfile<Trace, TraceEntry> result = new CausalBehaviouralProfile<Trace, TraceEntry>(model, profile.getEntities());
		result.setMatrix(profile.getMatrix());	
			
		/*
		 * Fill the co-occurrence relation
		 */
		fillCooccurrence(model, result);
	
		return result;
	}
			
	protected void fillCooccurrence(Trace model, CausalBehaviouralProfile<Trace, TraceEntry> profile) {		
		for(TraceEntry n1 : profile.getEntities()) {
			int index1 = profile.getEntities().indexOf(n1);
			for(TraceEntry n2 : profile.getEntities()) {
				int index2 = profile.getEntities().indexOf(n2);
				profile.getCooccurrenceMatrix()[index1][index2] = true;
			}
		}
		
	}

}
