package de.hpi.bpt.process.petri.bp.construct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.hpi.bpt.process.petri.bp.RelSet;
import de.hpi.bpt.process.petri.bp.RelSetType;
import de.hpi.bpt.process.petri.log.Log;
import de.hpi.bpt.process.petri.log.Trace;

public class RelSetCreatorLog extends AbstractRelSetCreator implements RelSetCreator<Log,String> {

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
	protected List<String> labelsForBaseOrderMatrix;
	
	@Override
	public RelSet<Log,String> deriveRelationSet(Log log) {
		return deriveRelationSet(log, new ArrayList<String>(log.getLabelsOfLog()));
	}
	
	public RelSet<Log,String> deriveRelationSet(Log log, int lookAhead) {
		return deriveRelationSet(log, new ArrayList<String>(log.getLabelsOfLog()),lookAhead);
	}

	@Override
	public RelSet<Log, String> deriveRelationSet(Log log,
			Collection<String> labels) {
		return deriveRelationSet(log, labels, RelSet.RELATION_FAR_LOOKAHEAD);
	}
	
	public RelSet<Log, String> deriveRelationSet(Log log,
			Collection<String> labels, int lookAhead) {
		
		this.labelsForBaseOrderMatrix = new ArrayList<String>(labels);
		
		RelSet<Log, String> rs = new RelSet<Log, String>(log,labels,lookAhead);
		RelSetType[][] matrix = rs.getMatrix();
		
		this.deriveBaseOrderRelation(rs);

		for(String s1 : rs.getEntities()) {
			int index1 = rs.getEntities().indexOf(s1);
			for(String s2 : rs.getEntities()) {
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
		
	protected void deriveBaseOrderRelation(RelSet<Log, String> rs) {
		
		this.baseOrderMatrix = new boolean[this.labelsForBaseOrderMatrix.size()][this.labelsForBaseOrderMatrix.size()];

		for (Trace t1 : rs.getModel().getTraces()) {
			for (int i = 0; i < t1.getLength(); i++) {
				String s1 = t1.getTraceAsList().get(i);
				for (int j = i + 1; j < t1.getLength(); j++) {
					String s2 = t1.getTraceAsList().get(j);
					if ((j - i) <= rs.getLookAhead())
						addToRelation(this.baseOrderMatrix,s1,s2);
				}
			}
		}
	}
	
	private boolean isBaseOrder(String s1, String s2) {
		return this.baseOrderMatrix[this.labelsForBaseOrderMatrix.indexOf(s1)][this.labelsForBaseOrderMatrix.indexOf(s2)];
	}

	private void addToRelation(boolean[][] matrix, String s1, String s2) {
		matrix[this.labelsForBaseOrderMatrix.indexOf(s1)][this.labelsForBaseOrderMatrix.indexOf(s2)] = true;
	}


}
