package org.jbpt.petri.conform;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jbpt.alignment.LabelEntity;
import org.jbpt.bp.BehaviouralProfile;
import org.jbpt.bp.CausalBehaviouralProfile;
import org.jbpt.bp.RelSetType;
import org.jbpt.petri.NetSystem;
import org.jbpt.petri.behavior.AbstractAnalysis;
import org.jbpt.petri.log.Trace;

public class ConformanceAnalysis extends AbstractAnalysis {
	
	protected static Set<String> IGNORED_LABEL_SUBSTRINGS = new HashSet<String>();
	
	static {
//		IGNORED_LABELS.add("start");
//		IGNORED_LABELS.add("end");
	}
	
	
	protected Set<TraceAnalysisTask> traceAnalysisTasks = new HashSet<TraceAnalysisTask>();
	
	protected CausalBehaviouralProfile<NetSystem, LabelEntity> baseProfile;
	
	protected ConformanceRootCauseAnalysis rootCauseAnalysis = new ConformanceRootCauseAnalysis();
	
	public class TraceAnalysisTask {
		
		protected BehaviouralProfile<Trace, LabelEntity> traceProfile;

		protected float constraintRelativeBehaviouralProfileConformance = -1;
		protected float modelRelativeBehaviouralProfileConformance = -1;

		protected float constraintRelativeBehaviouralProfileConformanceTop = -1;
		protected float constraintRelativeBehaviouralProfileConformanceBottom = -1;
		protected float modelRelativeBehaviouralProfileConformanceTop = -1;
		protected float modelRelativeBehaviouralProfileConformanceBottom = -1;

		protected float constraintRelativeCooccurrenceConformance = -1;
		protected float modelRelativeCooccurrenceConformance = -1;

		protected float constraintRelativeOverallConformance = -1;
		protected float modelRelativeOverallConformance = -1;

		protected float constraintRelativeCooccurrenceConformanceTop = -1;
		protected float constraintRelativeCooccurrenceConformanceBottom = -1;
		protected float modelRelativeCooccurrenceConformanceTop = -1;
		protected float modelRelativeCooccurrenceConformanceBottom = -1;

		protected List<String> traceLabelsAsList = new ArrayList<String>();
		
		public TraceAnalysisTask(BehaviouralProfile<Trace, LabelEntity> traceProfile) {
			this.traceProfile = traceProfile;
			
			for (String s : this.traceProfile.getModel().getLabelsOfTrace()) {
				if (!s.equals(""))
					if (baseProfile.getEntities().contains(new LabelEntity(s)))
						traceLabelsAsList.add(s);
			}
		}

		public List<String> getTraceLabelsAsList() {
			return traceLabelsAsList;
		}

		public BehaviouralProfile<Trace, LabelEntity> getTraceProfile() {
			return traceProfile;
		}

		public void setTraceProfile(BehaviouralProfile<Trace, LabelEntity> traceProfile) {
			this.traceProfile = traceProfile;
		}

		public float getConstraintRelativeCooccurrenceConformance() {
			return constraintRelativeCooccurrenceConformance;
		}


		public void setConstraintRelativeCooccurrenceConformance(
				float constraintRelativeCooccurrenceConformance) {
			this.constraintRelativeCooccurrenceConformance = constraintRelativeCooccurrenceConformance;
		}


		public float getModelRelativeCooccurrenceConformance() {
			return modelRelativeCooccurrenceConformance;
		}


		public void setModelRelativeCooccurrenceConformance(
				float modelRelativeCooccurrenceConformance) {
			this.modelRelativeCooccurrenceConformance = modelRelativeCooccurrenceConformance;
		}


		public float getConstraintRelativeConformance() {
			return constraintRelativeOverallConformance;
		}


		public void setConstraintRelativeConformance(float constraintRelativeConformance) {
			this.constraintRelativeOverallConformance = constraintRelativeConformance;
		}


		public float getModelRelativeConformance() {
			return modelRelativeOverallConformance;
		}


		public void setModelRelativeConformance(float modelRelativeConformance) {
			this.modelRelativeOverallConformance = modelRelativeConformance;
		}

		public float getConstraintRelativeCooccurrenceConformanceTop() {
			return constraintRelativeCooccurrenceConformanceTop;
		}

		public void setConstraintRelativeCooccurrenceConformanceTop(
				float constraintRelativeCooccurrenceConformanceTop) {
			this.constraintRelativeCooccurrenceConformanceTop = constraintRelativeCooccurrenceConformanceTop;
		}

		public float getConstraintRelativeCooccurrenceConformanceBottom() {
			return constraintRelativeCooccurrenceConformanceBottom;
		}

		public void setConstraintRelativeCooccurrenceConformanceBottom(
				float constraintRelativeCooccurrenceConformanceBottom) {
			this.constraintRelativeCooccurrenceConformanceBottom = constraintRelativeCooccurrenceConformanceBottom;
		}

		public float getModelRelativeCooccurrenceConformanceTop() {
			return modelRelativeCooccurrenceConformanceTop;
		}

		public void setModelRelativeCooccurrenceConformanceTop(
				float modelRelativeCooccurrenceConformanceTop) {
			this.modelRelativeCooccurrenceConformanceTop = modelRelativeCooccurrenceConformanceTop;
		}

		public float getModelRelativeCooccurrenceConformanceBottom() {
			return modelRelativeCooccurrenceConformanceBottom;
		}

		public void setModelRelativeCooccurrenceConformanceBottom(
				float modelRelativeCooccurrenceConformanceBottom) {
			this.modelRelativeCooccurrenceConformanceBottom = modelRelativeCooccurrenceConformanceBottom;
		}

		public float getConstraintRelativeBehaviouralProfileConformance() {
			return constraintRelativeBehaviouralProfileConformance;
		}

		public void setConstraintRelativeBehaviouralProfileConformance(
				float constraintRelativeBehaviouralProfileConformance) {
			this.constraintRelativeBehaviouralProfileConformance = constraintRelativeBehaviouralProfileConformance;
		}

		public float getModelRelativeBehaviouralProfileConformance() {
			return modelRelativeBehaviouralProfileConformance;
		}

		public void setModelRelativeBehaviouralProfileConformance(
				float modelRelativeBehaviouralProfileConformance) {
			this.modelRelativeBehaviouralProfileConformance = modelRelativeBehaviouralProfileConformance;
		}

		public float getConstraintRelativeBehaviouralProfileConformanceTop() {
			return constraintRelativeBehaviouralProfileConformanceTop;
		}

		public void setConstraintRelativeBehaviouralProfileConformanceTop(
				float constraintRelativeBehaviouralProfileConformanceTop) {
			this.constraintRelativeBehaviouralProfileConformanceTop = constraintRelativeBehaviouralProfileConformanceTop;
		}

		public float getConstraintRelativeBehaviouralProfileConformanceBottom() {
			return constraintRelativeBehaviouralProfileConformanceBottom;
		}

		public void setConstraintRelativeBehaviouralProfileConformanceBottom(
				float constraintRelativeBehaviouralProfileConformanceBottom) {
			this.constraintRelativeBehaviouralProfileConformanceBottom = constraintRelativeBehaviouralProfileConformanceBottom;
		}

		public float getModelRelativeBehaviouralProfileConformanceTop() {
			return modelRelativeBehaviouralProfileConformanceTop;
		}

		public void setModelRelativeBehaviouralProfileConformanceTop(
				float modelRelativeBehaviouralProfileConformanceTop) {
			this.modelRelativeBehaviouralProfileConformanceTop = modelRelativeBehaviouralProfileConformanceTop;
		}

		public float getModelRelativeBehaviouralProfileConformanceBottom() {
			return modelRelativeBehaviouralProfileConformanceBottom;
		}

		public void setModelRelativeBehaviouralProfileConformanceBottom(
				float modelRelativeBehaviouralProfileConformanceBottom) {
			this.modelRelativeBehaviouralProfileConformanceBottom = modelRelativeBehaviouralProfileConformanceBottom;
		}

	}
	
	public ConformanceAnalysis(CausalBehaviouralProfile<NetSystem, LabelEntity> profile) {
		super();
		this.baseProfile = profile;
	}
	
	public void addTrace(BehaviouralProfile<Trace,LabelEntity> traceProfile) {
		TraceAnalysisTask task = new TraceAnalysisTask(traceProfile);
		this.traceAnalysisTasks.add(task);
		this.rootCauseAnalysis.addTrace(traceProfile);
	}
	
	public void computeBPConformance() {
		for (TraceAnalysisTask p : this.traceAnalysisTasks) {
			computeBPConformance(p);
		}
	}

	protected boolean firstCompatibleWithSecondAsSelfRelation(RelSetType rel1, RelSetType rel2) {
		if (rel1.equals(rel2))
			return true;
		
		if (rel1.equals(RelSetType.Interleaving))
			return true;
		
		return false;
	}

	protected boolean firstCompatibleWithSecond(RelSetType rel1, RelSetType rel2) {
		if (rel1.equals(rel2))
			return true;
		
		if (rel1.equals(RelSetType.Interleaving))
			return true;
		
		if (rel1.equals(RelSetType.Order))
			if (rel2.equals(RelSetType.Exclusive))
				return true;
		
		if (rel1.equals(RelSetType.ReverseOrder))
			if (rel2.equals(RelSetType.Exclusive))
				return true;
		
		return false;
	}
	
	protected void computeBPConformance(TraceAnalysisTask pair) {	

		float consistentPairs = 0;
		float consistentNonInterleavingPairs = 0;
		float nonInterleavingPairs = 0;
		
		try{
			BehaviouralProfile<Trace, LabelEntity> traceProfile = pair.getTraceProfile();
				
			for (int i = 0; i < pair.getTraceLabelsAsList().size(); i++) {
				for (int j = i; j < pair.getTraceLabelsAsList().size(); j++) {
					String label1 = pair.getTraceLabelsAsList().get(i);
					String label2 = pair.getTraceLabelsAsList().get(j);
					
					RelSetType rel1 = this.baseProfile.getRelationForEntities(new LabelEntity(label1), new LabelEntity(label2));
					RelSetType rel2 = traceProfile.getRelationForEntities(new LabelEntity(label1), new LabelEntity(label2));
															
					/*
					 * Self-relation?
					 */
					if (i == j) {
						if (!rel1.equals(RelSetType.Interleaving))
							nonInterleavingPairs++;
						
						boolean checkResult = firstCompatibleWithSecondAsSelfRelation(rel1, rel2);					
						if (checkResult)
							consistentPairs++;
						else
							this.rootCauseAnalysis.addBPViolation(label1, label2, pair.getTraceProfile(), rel1, rel2);
						
						if (checkResult && !rel1.equals(RelSetType.Interleaving))
							consistentNonInterleavingPairs++;
						
					}
					else {
						if (!rel1.equals(RelSetType.Interleaving))
							nonInterleavingPairs += 2;

						boolean checkResult = firstCompatibleWithSecond(rel1, rel2);
						if (checkResult) {
							consistentPairs += 2;
						}
						else { 
							this.rootCauseAnalysis.addBPViolation(label1, label2, pair.getTraceProfile(),rel1,rel2);
							this.rootCauseAnalysis.addBPViolation(label2, label1, pair.getTraceProfile(),BehaviouralProfile.getComplementRelation(rel1),BehaviouralProfile.getComplementRelation(rel2));
						}
							
						if (checkResult && !rel1.equals(RelSetType.Interleaving))
							consistentNonInterleavingPairs += 2;
					}
				}
			}
		
			
			float countPairs = pair.getTraceLabelsAsList().size() * pair.getTraceLabelsAsList().size();
			
			pair.setModelRelativeBehaviouralProfileConformanceTop(consistentPairs);
			pair.setModelRelativeBehaviouralProfileConformanceBottom(countPairs);
			pair.setConstraintRelativeBehaviouralProfileConformanceTop(consistentNonInterleavingPairs);
			pair.setConstraintRelativeBehaviouralProfileConformanceBottom(nonInterleavingPairs);

			pair.setModelRelativeBehaviouralProfileConformance(consistentPairs / countPairs);
			pair.setConstraintRelativeBehaviouralProfileConformance(consistentNonInterleavingPairs / nonInterleavingPairs);

			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void saveResults(String fileName) {
		
		String[] captions = new String[]{
				"Trace ID", 
				"Number of Matched Tasks", 
				"Constraint-Rel BP Conformance",
				"Model-Rel BP Conformance",
				"Constraint-Rel Co-occurrence Conformance",
				"Model-Rel Co-occurrence Conformance",
				"Constraint-Rel Overall Conformance",
				"Model-Rel Overall Conformance"
				};
		
		Set<String[]> rows = new HashSet<String[]>();
		
		for (TraceAnalysisTask pair : this.traceAnalysisTasks) {
			String[] row = new String[8];
			row[0] = String.valueOf(pair.getTraceProfile().getModel().getId());
			row[1] = String.valueOf(pair.getTraceLabelsAsList().size());
			row[2] = String.valueOf(pair.getConstraintRelativeBehaviouralProfileConformance());
			row[3] = String.valueOf(pair.getModelRelativeBehaviouralProfileConformance());
			row[4] = String.valueOf(pair.getConstraintRelativeCooccurrenceConformance());
			row[5] = String.valueOf(pair.getModelRelativeCooccurrenceConformance());
			row[6] = String.valueOf(pair.getConstraintRelativeConformance());
			row[7] = String.valueOf(pair.getModelRelativeConformance());
			rows.add(row);
		}
		super.writeResultsToFile(fileName,captions,rows);
		
		this.rootCauseAnalysis.computeGlobalSupport();
		this.rootCauseAnalysis.computeConfidenceForViolationPairs();
		this.rootCauseAnalysis.saveResults(fileName);
	}	

	private boolean isExpected(List<String> trace, String expectedTraceTask) {
		
		for (String traceTask1 : trace) {
			for (String traceTask2 : trace) {
				if (traceTask1!=expectedTraceTask && traceTask2!=expectedTraceTask) {
					
					RelSetType relExpTrace2 = this.baseProfile.getRelationForEntities(
							new LabelEntity(expectedTraceTask),new LabelEntity(traceTask2));
					RelSetType relObs1Trace2 = this.baseProfile.getRelationForEntities(
							new LabelEntity(traceTask1),new LabelEntity(traceTask2));
					
					if (relExpTrace2.equals(RelSetType.Order)
							&& this.baseProfile.areCooccurring(
									new LabelEntity(traceTask1),new LabelEntity(expectedTraceTask))
							&& (traceTask1.equals(traceTask2) || relObs1Trace2.equals(RelSetType.Order)))
						return true;
				}
			}
		}
		
		return false;
	}

	public void computeCooccurrenceConformance() {
		for (TraceAnalysisTask p : this.traceAnalysisTasks) {
			
			BehaviouralProfile<Trace, LabelEntity> traceProfile = p.getTraceProfile();
			
			Set<String> eA = new HashSet<String>();

			for (LabelEntity e : this.baseProfile.getEntities()) {
				if (IGNORED_LABEL_SUBSTRINGS.contains(e.getLabel()))
						continue;
				if (traceProfile.getEntities().contains(e)
						|| isExpected(p.getTraceLabelsAsList(),e.getLabel()))
					eA.add(e.getLabel());
			}

			int topConstraintRel = 0;
			int topModelRel = 0;

			int bottomConstraintRel = 0;
			int bottomModelRel = eA.size() * eA.size() - eA.size();
			
			for (String s1 : eA) {
				for (String s2 : eA) {
					if (s1.equals(s2))
						continue;
					
					if (IGNORED_LABEL_SUBSTRINGS.contains(s1) || IGNORED_LABEL_SUBSTRINGS.contains(s2))
							continue;
					
					if (this.baseProfile.areCooccurring(new LabelEntity(s1),new LabelEntity(s2))) {
						bottomConstraintRel++;
						if (traceProfile.getEntities().contains(new LabelEntity(s2))) {
							topConstraintRel++;
							topModelRel++;
						}
						else {
							this.rootCauseAnalysis.addCooccurrenceViolation(s1, s2, traceProfile);
						}
					}
					else {
						topModelRel++;
					}
				}
			}
			
			p.setModelRelativeCooccurrenceConformanceTop(topModelRel);
			p.setModelRelativeCooccurrenceConformanceBottom(bottomModelRel);
			p.setConstraintRelativeCooccurrenceConformanceTop(topConstraintRel);
			p.setConstraintRelativeCooccurrenceConformanceBottom(bottomConstraintRel);
			
			p.setModelRelativeCooccurrenceConformance((float) topModelRel / bottomModelRel);
			
			if (bottomConstraintRel==0)
				p.setConstraintRelativeCooccurrenceConformance(1.0f);
			else
				p.setConstraintRelativeCooccurrenceConformance((float) topConstraintRel / bottomConstraintRel);
		}
	}

	public void computeOverallConformance() {
		for (TraceAnalysisTask p : this.traceAnalysisTasks) {
			float constraintRelativeConformance = (p.getConstraintRelativeBehaviouralProfileConformanceTop()  + p.getConstraintRelativeCooccurrenceConformanceTop())
			/(p.getConstraintRelativeBehaviouralProfileConformanceBottom() + p.getConstraintRelativeCooccurrenceConformanceBottom());
			float modelRelativeConformance = (p.getModelRelativeBehaviouralProfileConformanceTop()  + p.getModelRelativeCooccurrenceConformanceTop())
			/(p.getModelRelativeBehaviouralProfileConformanceBottom() + p.getModelRelativeCooccurrenceConformanceBottom());
			
			p.setConstraintRelativeConformance(constraintRelativeConformance);
			p.setModelRelativeConformance(modelRelativeConformance);
		}
	}

	public Set<TraceAnalysisTask> getAnalysisTasks() {
		return this.traceAnalysisTasks;
	}
	
}

