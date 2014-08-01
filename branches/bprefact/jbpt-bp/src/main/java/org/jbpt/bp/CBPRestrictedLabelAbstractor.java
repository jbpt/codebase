package org.jbpt.bp;

import java.util.ArrayList;
import java.util.List;

import org.jbpt.alignment.LabelEntity;
import org.jbpt.hypergraph.abs.IEntity;


/**
 * Class that provides a method to abstract a casual behavioural profile 
 * given for the entities of a certain model to the labels of all entities.
 * 
 * In general, cooccurrence for labels may not be derived from 
 * cooccurrence of the entities of a model. Hence, this class works only
 * for the restricted case that all entities have unique labels.
 * 
 * Functionality is provided by a types static method:
 * <code>abstractCBPToLabels</code>
 * 
 * @author matthias.weidlich
 */
public class CBPRestrictedLabelAbstractor {
	
	public static <M,N extends IEntity> CausalBehaviouralProfile<M,LabelEntity> abstractCBPToLabels(CausalBehaviouralProfile<M,N> cbp) {
		List<LabelEntity> labels = new ArrayList<LabelEntity>();
		
		for (N e : cbp.getEntities()) {
			if (e.getLabel().equals(""))
				continue;
			if (labels.contains(new LabelEntity(e.getLabel()))) {
				/* we are in trouble: entities do not have unique labels, 
				 * so this class is not applicable
				 */
				throw new IllegalArgumentException("Tried to abstract a CBP to labels, but the entities of the CBP do not have unique labels.");
			}
			labels.add(new LabelEntity(e.getLabel()));
		}
		
		CausalBehaviouralProfile<M,LabelEntity> result = new CausalBehaviouralProfile<M, LabelEntity>(cbp.getModel(), labels);

		for (N t1 : cbp.getEntities()) {
			if (t1.getLabel().equals(""))
				continue;
			String s1 = t1.getLabel();
			int index1 = labels.indexOf(new LabelEntity(s1));
			for (N t2 : cbp.getEntities()) {
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
				 * Second, abstract BP
				 */
				if (cbp.areCooccurring(t1, t2))
					result.getCooccurrenceMatrix()[index1][index2] = true;
			}
		}
		
		
		return result;
	}
	
}
