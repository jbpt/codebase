package org.jbpt.bp.construct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jbpt.bp.RelSet;
import org.jbpt.bp.RelSetType;
import org.jbpt.petri.log.Trace;
import org.jbpt.petri.log.TraceEntry;


public class RelSetCreatorTrace extends AbstractRelSetCreator implements RelSetCreator<Trace,TraceEntry> {

	private static RelSetCreatorTrace eInstance;
	
	public static RelSetCreatorTrace getInstance() {
		if (eInstance == null)
			eInstance  = new RelSetCreatorTrace();
		return eInstance;
	}
	
	private RelSetCreatorTrace() {
		
	}
	
	// captures the base order for labels
	protected boolean[][] baseOrderMatrix; 
	
	// list to have identifiers for the labels in the matrix
	protected List<TraceEntry> entriesForBaseOrderMatrix;
	
	@Override
	public RelSet<Trace,TraceEntry> deriveRelationSet(Trace trace) {
		return deriveRelationSet(trace, new ArrayList<TraceEntry>(trace.getEntities()));
	}
	
	public RelSet<Trace,TraceEntry> deriveRelationSet(Trace trace, int lookAhead) {
		return deriveRelationSet(trace, new ArrayList<TraceEntry>(trace.getEntities()),lookAhead);
	}

	@Override
	public RelSet<Trace, TraceEntry> deriveRelationSet(Trace trace,
			Collection<TraceEntry> entries) {
		return deriveRelationSet(trace, entries, RelSet.RELATION_FAR_LOOKAHEAD);
	}
	
	public RelSet<Trace, TraceEntry> deriveRelationSet(Trace trace,
			Collection<TraceEntry> entries, int lookAhead) {
		
		this.entriesForBaseOrderMatrix = new ArrayList<TraceEntry>(entries);
		
		RelSet<Trace, TraceEntry> rs = new RelSet<Trace, TraceEntry>(trace,entries,lookAhead);
		RelSetType[][] matrix = rs.getMatrix();
		
		this.deriveBaseOrderRelation(rs);

		for(TraceEntry s1 : rs.getEntities()) {
			int index1 = rs.getEntities().indexOf(s1);
			for(TraceEntry s2 : rs.getEntities()) {
				int index2 = rs.getEntities().indexOf(s2);
				
				/*
				 * The behavioural profile matrix is symmetric. Therefore, we 
				 * need to traverse only half of the entries.
				 */
				if (index2 > index1)
					continue;
				
				if (this.isBaseOrder(s1,s2) && this.isBaseOrder(s2,s1))
					super.setMatrixEntry(matrix, index1, index2, RelSetType.Interleaving);
				else if (this.isBaseOrder(s1,s2))
					super.setMatrixEntryOrder(matrix, index1, index2);
				else if (this.isBaseOrder(s2,s1))
					super.setMatrixEntryOrder(matrix, index2, index1);
				else
					super.setMatrixEntry(matrix, index1, index2, RelSetType.Exclusive);
			}
		}		
		
		return rs;
	}
		
	protected void deriveBaseOrderRelation(RelSet<Trace, TraceEntry> rs) {
		
		this.baseOrderMatrix = new boolean[this.entriesForBaseOrderMatrix.size()][this.entriesForBaseOrderMatrix.size()];

		for (int i = 0; i < rs.getEntities().size(); i++) {
			TraceEntry s1 = rs.getEntities().get(i);
			for (int j = i + 1; j < rs.getEntities().size(); j++) {
				TraceEntry s2 = rs.getEntities().get(j);
				if ((j - i) <= rs.getLookAhead())
					addToRelation(this.baseOrderMatrix,s1,s2);
			}
		}
	}
	
	private boolean isBaseOrder(TraceEntry s1, TraceEntry s2) {
		return this.baseOrderMatrix[this.entriesForBaseOrderMatrix.indexOf(s1)][this.entriesForBaseOrderMatrix.indexOf(s2)];
	}

	private void addToRelation(boolean[][] matrix, TraceEntry s1, TraceEntry s2) {
		matrix[this.entriesForBaseOrderMatrix.indexOf(s1)][this.entriesForBaseOrderMatrix.indexOf(s2)] = true;
	}


}
