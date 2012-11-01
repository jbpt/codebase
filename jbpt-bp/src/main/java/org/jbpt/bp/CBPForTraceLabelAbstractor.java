package org.jbpt.bp;

import java.util.ArrayList;
import java.util.List;

import org.jbpt.alignment.LabelEntity;
import org.jbpt.petri.log.Trace;
import org.jbpt.petri.log.TraceEntry;


/**
 * Class that provides a method to abstract a casual behavioural profile 
 * given for trace entries of a trace to the labels of all trace entries.
 * 
 * In general, cooccurrence for labels may not be derived from 
 * cooccurrence of the entities of a model. For a trace, however, this is 
 * possible since the cooccurrence relation is trivial.
 * 
 * Functionality is provided by a types static method:
 * <code>abstractCBPForTraceToLabels</code>
 * 
 * @author matthias.weidlich
 */
public class CBPForTraceLabelAbstractor {
	
	public static CausalBehaviouralProfile<Trace,LabelEntity> abstractCBPForTraceToLabels(CausalBehaviouralProfile<Trace,TraceEntry> cbp) {
		List<LabelEntity> labels = new ArrayList<LabelEntity>();
		
		for (TraceEntry e : cbp.getEntities()) {
			if (e.getLabel().equals(""))
				continue;
			LabelEntity le = new LabelEntity(e.getLabel());
			if (!labels.contains(le))
				labels.add(le);
		}
		
		CausalBehaviouralProfile<Trace,LabelEntity> result = new CausalBehaviouralProfile<Trace, LabelEntity>(cbp.getModel(), labels);

		for (TraceEntry t1 : cbp.getEntities()) {
			if (t1.getLabel().equals(""))
				continue;
			String s1 = t1.getLabel();
			int index1 = labels.indexOf(new LabelEntity(s1));
			for (TraceEntry t2 : cbp.getEntities()) {
				if (t2.getLabel().equals(""))
					continue;
				String s2 = t2.getLabel();
				int index2 = labels.indexOf(new LabelEntity(s2));
				
				/*
				 * First, abstract BP
				 */
				RelSetType rel = cbp.getRelationForEntities(t1, t2);
				
				if (result.getMatrix()[index1][index2] == null) {
					result.getMatrix()[index1][index2] = rel;
				}
				else if (result.getMatrix()[index1][index2].equals(RelSetType.Exclusive)) {
					result.getMatrix()[index1][index2] = rel;
				}
				else if (result.getMatrix()[index1][index2].equals(RelSetType.Order)) {
					if (rel.equals(RelSetType.ReverseOrder))
						result.getMatrix()[index1][index2] = RelSetType.Interleaving;
					else if (rel.equals(RelSetType.Interleaving))
						result.getMatrix()[index1][index2] = RelSetType.Interleaving;
				}
				else if (result.getMatrix()[index1][index2].equals(RelSetType.ReverseOrder)) {
					if (rel.equals(RelSetType.Order))
						result.getMatrix()[index1][index2] = RelSetType.Interleaving;
					else if (rel.equals(RelSetType.Interleaving))
						result.getMatrix()[index1][index2] = RelSetType.Interleaving;
				}
				
				/*
				 * Second, trivially abstract BP
				 */
				result.getCooccurrenceMatrix()[index1][index2] = true;
			}
		}
		
		
		return result;
	}
	
}
