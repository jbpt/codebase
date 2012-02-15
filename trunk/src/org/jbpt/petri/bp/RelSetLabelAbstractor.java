package org.jbpt.petri.bp;

import java.util.ArrayList;
import java.util.List;

import org.jbpt.alignment.IEntity;
import org.jbpt.alignment.LabelEntity;


/**
 * Class that provides a method to abstract a relation set given for the
 * entities of a certain model to the labels of all entities.
 * 
 * Functionality is provided by a types static method:
 * <code>abstractRelSetToLabels</code>
 * 
 * @author matthias.weidlich
 */
public class RelSetLabelAbstractor {
	
	public static <M,N extends IEntity> RelSet<M,LabelEntity> abstractRelSetToLabels(RelSet<M,N> rs) {
		List<LabelEntity> labels = new ArrayList<LabelEntity>();
		
		for (N e : rs.getEntities())
			if (!labels.contains(e.getLabel()))
				labels.add(new LabelEntity(e.getLabel()));
		
		RelSet<M,LabelEntity> result = new RelSet<M, LabelEntity>(rs.getModel(), labels);

		for (N t1 : rs.getEntities()) {
			String s1 = t1.getLabel();
			int index1 = labels.indexOf(new LabelEntity(s1));
			for (N t2 : rs.getEntities()) {
				String s2 = t2.getLabel();
				int index2 = labels.indexOf(new LabelEntity(s2));
				
				RelSetType rel = rs.getRelationForEntities(t1, t2);
				
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
			}
		}
		
		return result;
	}
	
}
