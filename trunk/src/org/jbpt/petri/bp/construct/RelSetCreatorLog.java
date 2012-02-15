package org.jbpt.petri.bp.construct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jbpt.petri.bp.RelSet;
import org.jbpt.petri.bp.RelSetType;
import org.jbpt.petri.log.Log;
import org.jbpt.petri.log.Trace;
import org.jbpt.petri.log.TraceEntry;


public class RelSetCreatorLog extends AbstractRelSetCreator implements RelSetCreator<Log,TraceEntry> {

	private static RelSetCreatorLog eInstance;
	
	public static RelSetCreatorLog getInstance() {
		if (eInstance == null)
			eInstance  = new RelSetCreatorLog();
		return eInstance;
	}
	
	private RelSetCreatorLog() {
		
	}
	
	// captures the base order for labels
	protected boolean[][] baseOrderMatrix; 
	
	// list to have identifiers for the labels in the matrix
	protected List<TraceEntry> entriesForBaseOrderMatrix;
	
	@Override
	public RelSet<Log,TraceEntry> deriveRelationSet(Log log) {
		return deriveRelationSet(log, new ArrayList<TraceEntry>(log.getEntities()));
	}
	
	public RelSet<Log,TraceEntry> deriveRelationSet(Log log, int lookAhead) {
		return deriveRelationSet(log, new ArrayList<TraceEntry>(log.getEntities()),lookAhead);
	}

	@Override
	public RelSet<Log, TraceEntry> deriveRelationSet(Log log,
			Collection<TraceEntry> entries) {
		return deriveRelationSet(log, entries, RelSet.RELATION_FAR_LOOKAHEAD);
	}
	
	public RelSet<Log, TraceEntry> deriveRelationSet(Log log,
			Collection<TraceEntry> entries, int lookAhead) {
		
		this.entriesForBaseOrderMatrix = new ArrayList<TraceEntry>(entries);
		
		RelSet<Log, TraceEntry> rs = new RelSet<Log, TraceEntry>(log,entries,lookAhead);
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
		
	protected void deriveBaseOrderRelation(RelSet<Log, TraceEntry> rs) {
		
		this.baseOrderMatrix = new boolean[this.entriesForBaseOrderMatrix.size()][this.entriesForBaseOrderMatrix.size()];

		for (Trace t1 : rs.getModel().getTraces()) {
			for (int i = 0; i < t1.getLength(); i++) {
				TraceEntry s1 = t1.getTraceAsList().get(i);
				for (int j = i + 1; j < t1.getLength(); j++) {
					TraceEntry s2 = t1.getTraceAsList().get(j);
					if ((j - i) <= rs.getLookAhead())
						addToRelation(this.baseOrderMatrix,s1,s2);
				}
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
