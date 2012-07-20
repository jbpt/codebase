package org.jbpt.petri.behavior;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jbpt.alignment.LabelEntity;
import org.jbpt.bp.BehaviouralProfile;
import org.jbpt.bp.CausalBehaviouralProfile;
import org.jbpt.bp.RelSetType;
import org.jbpt.petri.Node;
import org.jbpt.petri.PetriNet;
import org.jbpt.petri.log.Trace;

public class ConformanceAnalysis extends AbstractAnalysis {
	
	protected static Set<String> IGNORED_LABELS = new HashSet<String>();
	
	static {
//		IGNORED_LABELS.add("start");
//		IGNORED_LABELS.add("end");
	}
	
	
	protected Set<TraceAnalysisTask> traceAnalysisTasks = new HashSet<TraceAnalysisTask>();
	
	protected CausalBehaviouralProfile<PetriNet, LabelEntity> baseProfile;
	protected List<String> modelLabels = new ArrayList<String>(); 
	protected boolean[][] modelCooccurrencesForLabels;
	
	protected ConformanceRootCauseAnalysis rootCauseAnalysis = new ConformanceRootCauseAnalysis();
	
	protected class TraceAnalysisTask {
		
		protected BehaviouralProfile<Trace, LabelEntity> traceProfile;

		protected float constraintRelativeBehaviouralProfileCompliance = -1;
		protected float modelRelativeBehaviouralProfileCompliance = -1;

		protected float constraintRelativeBehaviouralProfileComplianceTop = -1;
		protected float constraintRelativeBehaviouralProfileComplianceBottom = -1;
		protected float modelRelativeBehaviouralProfileComplianceTop = -1;
		protected float modelRelativeBehaviouralProfileComplianceBottom = -1;

		protected float constraintRelativeCooccurrenceCompliance = -1;
		protected float modelRelativeCooccurrenceCompliance = -1;

		protected float constraintRelativeOverallCompliance = -1;
		protected float modelRelativeOverallCompliance = -1;

		protected float constraintRelativeCooccurrenceComplianceTop = -1;
		protected float constraintRelativeCooccurrenceComplianceBottom = -1;
		protected float modelRelativeCooccurrenceComplianceTop = -1;
		protected float modelRelativeCooccurrenceComplianceBottom = -1;

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

		public float getConstraintRelativeCooccurrenceCompliance() {
			return constraintRelativeCooccurrenceCompliance;
		}


		public void setConstraintRelativeCooccurrenceCompliance(
				float constraintRelativeCooccurrenceCompliance) {
			this.constraintRelativeCooccurrenceCompliance = constraintRelativeCooccurrenceCompliance;
		}


		public float getModelRelativeCooccurrenceCompliance() {
			return modelRelativeCooccurrenceCompliance;
		}


		public void setModelRelativeCooccurrenceCompliance(
				float modelRelativeCooccurrenceCompliance) {
			this.modelRelativeCooccurrenceCompliance = modelRelativeCooccurrenceCompliance;
		}


		public float getConstraintRelativeCompliance() {
			return constraintRelativeOverallCompliance;
		}


		public void setConstraintRelativeCompliance(float constraintRelativeCompliance) {
			this.constraintRelativeOverallCompliance = constraintRelativeCompliance;
		}


		public float getModelRelativeCompliance() {
			return modelRelativeOverallCompliance;
		}


		public void setModelRelativeCompliance(float modelRelativeCompliance) {
			this.modelRelativeOverallCompliance = modelRelativeCompliance;
		}

		public float getConstraintRelativeCooccurrenceComplianceTop() {
			return constraintRelativeCooccurrenceComplianceTop;
		}

		public void setConstraintRelativeCooccurrenceComplianceTop(
				float constraintRelativeCooccurrenceComplianceTop) {
			this.constraintRelativeCooccurrenceComplianceTop = constraintRelativeCooccurrenceComplianceTop;
		}

		public float getConstraintRelativeCooccurrenceComplianceBottom() {
			return constraintRelativeCooccurrenceComplianceBottom;
		}

		public void setConstraintRelativeCooccurrenceComplianceBottom(
				float constraintRelativeCooccurrenceComplianceBottom) {
			this.constraintRelativeCooccurrenceComplianceBottom = constraintRelativeCooccurrenceComplianceBottom;
		}

		public float getModelRelativeCooccurrenceComplianceTop() {
			return modelRelativeCooccurrenceComplianceTop;
		}

		public void setModelRelativeCooccurrenceComplianceTop(
				float modelRelativeCooccurrenceComplianceTop) {
			this.modelRelativeCooccurrenceComplianceTop = modelRelativeCooccurrenceComplianceTop;
		}

		public float getModelRelativeCooccurrenceComplianceBottom() {
			return modelRelativeCooccurrenceComplianceBottom;
		}

		public void setModelRelativeCooccurrenceComplianceBottom(
				float modelRelativeCooccurrenceComplianceBottom) {
			this.modelRelativeCooccurrenceComplianceBottom = modelRelativeCooccurrenceComplianceBottom;
		}

		public float getConstraintRelativeBehaviouralProfileCompliance() {
			return constraintRelativeBehaviouralProfileCompliance;
		}

		public void setConstraintRelativeBehaviouralProfileCompliance(
				float constraintRelativeBehaviouralProfileCompliance) {
			this.constraintRelativeBehaviouralProfileCompliance = constraintRelativeBehaviouralProfileCompliance;
		}

		public float getModelRelativeBehaviouralProfileCompliance() {
			return modelRelativeBehaviouralProfileCompliance;
		}

		public void setModelRelativeBehaviouralProfileCompliance(
				float modelRelativeBehaviouralProfileCompliance) {
			this.modelRelativeBehaviouralProfileCompliance = modelRelativeBehaviouralProfileCompliance;
		}

		public float getConstraintRelativeBehaviouralProfileComplianceTop() {
			return constraintRelativeBehaviouralProfileComplianceTop;
		}

		public void setConstraintRelativeBehaviouralProfileComplianceTop(
				float constraintRelativeBehaviouralProfileComplianceTop) {
			this.constraintRelativeBehaviouralProfileComplianceTop = constraintRelativeBehaviouralProfileComplianceTop;
		}

		public float getConstraintRelativeBehaviouralProfileComplianceBottom() {
			return constraintRelativeBehaviouralProfileComplianceBottom;
		}

		public void setConstraintRelativeBehaviouralProfileComplianceBottom(
				float constraintRelativeBehaviouralProfileComplianceBottom) {
			this.constraintRelativeBehaviouralProfileComplianceBottom = constraintRelativeBehaviouralProfileComplianceBottom;
		}

		public float getModelRelativeBehaviouralProfileComplianceTop() {
			return modelRelativeBehaviouralProfileComplianceTop;
		}

		public void setModelRelativeBehaviouralProfileComplianceTop(
				float modelRelativeBehaviouralProfileComplianceTop) {
			this.modelRelativeBehaviouralProfileComplianceTop = modelRelativeBehaviouralProfileComplianceTop;
		}

		public float getModelRelativeBehaviouralProfileComplianceBottom() {
			return modelRelativeBehaviouralProfileComplianceBottom;
		}

		public void setModelRelativeBehaviouralProfileComplianceBottom(
				float modelRelativeBehaviouralProfileComplianceBottom) {
			this.modelRelativeBehaviouralProfileComplianceBottom = modelRelativeBehaviouralProfileComplianceBottom;
		}

	}
	
	public ConformanceAnalysis() {
		super();
	}
	
	public void init(CausalBehaviouralProfile<PetriNet, LabelEntity> profile) {
		
		this.baseProfile = profile;
		
		for (Node t : this.baseProfile.getModel().getObservableTransitions())
			modelLabels.add(t.getLabel());
		
//		int count = profile.getModel().getObservableTransitions().size();
//		this.modelCooccurrencesForLabels = new boolean[count][count];
//
//		/*
//		 * Set co-occurrences between labels for model
//		 */
//		for (int i=0; i<count; i++)
//			for (int j=0; j<count; j++)
//				modelCooccurrencesForLabels[i][j] = false;
//		
//		
//		for(Transition n1 : profile.getModel().getObservableTransitions()) {
//			for(Transition n2 : profile.getModel().getObservableTransitions()) {
//				String label1 = n1.getLabel();
//				String label2 = n2.getLabel();
//				if (profile.areCooccurring(n1, n2))
//					setCooccurrence(label1,label2,true);
//			}
//		}
	}
	
	protected void setCooccurrence(String a1, String a2, boolean value) {
		modelCooccurrencesForLabels[modelLabels.indexOf(a1)][modelLabels.indexOf(a2)] = value;
	}
	
	protected boolean getCooccurrence(String a1, String a2) {
		return modelCooccurrencesForLabels[modelLabels.indexOf(a1)][modelLabels.indexOf(a2)];
	}
	
	public void addLogAnalysisTask(BehaviouralProfile<Trace,LabelEntity> logProfile) {
		TraceAnalysisTask task = new TraceAnalysisTask(logProfile);
		this.traceAnalysisTasks.add(task);
		this.rootCauseAnalysis.addTrace(logProfile);
	}
	
	public void setRelationsForLogAnalysisTasksAndComputeBPCompliance() {
		for (TraceAnalysisTask p : this.traceAnalysisTasks) {
			setRelationsForAnalysisTaskAndComputeBPCompliance(p);
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
	
	protected void setRelationsForAnalysisTaskAndComputeBPCompliance(TraceAnalysisTask pair) {	

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
			
			pair.setModelRelativeBehaviouralProfileComplianceTop(consistentPairs);
			pair.setModelRelativeBehaviouralProfileComplianceBottom(countPairs);
			pair.setConstraintRelativeBehaviouralProfileComplianceTop(consistentNonInterleavingPairs);
			pair.setConstraintRelativeBehaviouralProfileComplianceBottom(nonInterleavingPairs);

			pair.setModelRelativeBehaviouralProfileCompliance(consistentPairs / countPairs);
			pair.setConstraintRelativeBehaviouralProfileCompliance(consistentNonInterleavingPairs / nonInterleavingPairs);

			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void saveResults(String fileName) {
		
		String[] captions = new String[]{
				"Log ID", 
				"Number of Matched Tasks", 
				"Constraint-Rel BP Compliance",
				"Model-Rel BP Compliance",
				"Constraint-Rel Co-occurrence Compliance",
				"Model-Rel Co-occurrence Compliance",
				"Constraint-Rel Overall Compliance",
				"Model-Rel Overall Compliance"
				};
		
		Set<String[]> rows = new HashSet<String[]>();
		
		for (TraceAnalysisTask pair : this.traceAnalysisTasks) {
			String[] row = new String[8];
			row[0] = String.valueOf(pair.getTraceProfile().getModel().getId());
			row[1] = String.valueOf(pair.getTraceLabelsAsList().size());
			row[2] = String.valueOf(pair.getConstraintRelativeBehaviouralProfileCompliance());
			row[3] = String.valueOf(pair.getModelRelativeBehaviouralProfileCompliance());
			row[4] = String.valueOf(pair.getConstraintRelativeCooccurrenceCompliance());
			row[5] = String.valueOf(pair.getModelRelativeCooccurrenceCompliance());
			row[6] = String.valueOf(pair.getConstraintRelativeCompliance());
			row[7] = String.valueOf(pair.getModelRelativeCompliance());
			rows.add(row);
		}
		super.writeResultsToFile(fileName,captions,rows);
		
		this.rootCauseAnalysis.computeGlobalSupport();
		this.rootCauseAnalysis.computeConfidenceForViolationPairs();
		this.rootCauseAnalysis.saveResults(fileName);
	}	

	private boolean isExpected(List<String> log, String expectedLogTask) {
		
		for (String logTask1 : log) {
			for (String logTask2 : log) {
				if (logTask1!=expectedLogTask && logTask2!=expectedLogTask) {
					
					RelSetType relExpLog2 = this.baseProfile.getRelationForEntities(
							new LabelEntity(expectedLogTask),new LabelEntity(logTask2));
					RelSetType relLog1Log2 = this.baseProfile.getRelationForEntities(
							new LabelEntity(logTask1),new LabelEntity(logTask2));
					
					if (relExpLog2.equals(RelSetType.Order)
							&& this.baseProfile.areCooccurring(new LabelEntity(logTask1), new LabelEntity(expectedLogTask))
							&& (logTask1.equals(logTask2) || relLog1Log2.equals(RelSetType.Order)))
						return true;
				}
			}
		}
		
		return false;
	}

	public void computeCooccurrenceCompliance() {
		for (TraceAnalysisTask p : this.traceAnalysisTasks) {
			
			BehaviouralProfile<Trace, LabelEntity> traceProfile = p.getTraceProfile();
			
			/*
			 * Build EA_L
			 */
			Set<String> EA_L = new HashSet<String>();

			for (int i=0; i<modelLabels.size(); i++) {
				if (IGNORED_LABELS.contains(modelLabels.get(i)))
						continue;
				if (traceProfile.getEntities().contains(new LabelEntity(modelLabels.get(i)))
						|| isExpected(p.getTraceLabelsAsList(),modelLabels.get(i)))
					EA_L.add(modelLabels.get(i));
			}

			int topConstraintRel = 0;
			int topModelRel = 0;

			int bottomConstraintRel = 0;
			int bottomModelRel = EA_L.size() * EA_L.size() - EA_L.size();
			
			for (String s1 : EA_L) {
				for (String s2 : EA_L) {
					if (s1.equals(s2))
						continue;
					
					if (IGNORED_LABELS.contains(s1) || IGNORED_LABELS.contains(s2))
							continue;
					
					if (getCooccurrence(s1,s2)) {
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
			
			p.setModelRelativeCooccurrenceComplianceTop(topModelRel);
			p.setModelRelativeCooccurrenceComplianceBottom(bottomModelRel);
			p.setConstraintRelativeCooccurrenceComplianceTop(topConstraintRel);
			p.setConstraintRelativeCooccurrenceComplianceBottom(bottomConstraintRel);
			
			p.setModelRelativeCooccurrenceCompliance((float) topModelRel / bottomModelRel);
			
			if (bottomConstraintRel==0)
				p.setConstraintRelativeCooccurrenceCompliance(1.0f);
			else
				p.setConstraintRelativeCooccurrenceCompliance((float) topConstraintRel / bottomConstraintRel);
		}
	}

	public void computeOverallCompliance() {
		for (TraceAnalysisTask p : this.traceAnalysisTasks) {
			float constraintRelativeCompliance = (p.getConstraintRelativeBehaviouralProfileComplianceTop()  + p.getConstraintRelativeCooccurrenceComplianceTop())
			/(p.getConstraintRelativeBehaviouralProfileComplianceBottom() + p.getConstraintRelativeCooccurrenceComplianceBottom());
			float modelRelativeCompliance = (p.getModelRelativeBehaviouralProfileComplianceTop()  + p.getModelRelativeCooccurrenceComplianceTop())
			/(p.getModelRelativeBehaviouralProfileComplianceBottom() + p.getModelRelativeCooccurrenceComplianceBottom());
			
			p.setConstraintRelativeCompliance(constraintRelativeCompliance);
			p.setModelRelativeCompliance(modelRelativeCompliance);
		}
	}

}

