package org.jbpt.petri.conform;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jbpt.alignment.LabelEntity;
import org.jbpt.bp.BehaviouralProfile;
import org.jbpt.bp.CausalBehaviouralProfile;
import org.jbpt.bp.RelSetType;
import org.jbpt.petri.behavior.AbstractAnalysis;
import org.jbpt.petri.log.Trace;

public class ConformanceRootCauseAnalysis extends AbstractAnalysis {
	
	protected final static int CONSTANT_HIGH_SUPPORT = 30;
	
	protected final static float CONSTANT_HIGH_CONFIDENCE = 0.6f;	

//	protected final static String CONSTANT_COOCCURRENCE_CONSTRAINT = ">>";	

	protected class ViolationImplicationTupel {
		
		public ViolationTupleInclSupport violation1;
		public ViolationTupleInclSupport violation2;

		public float confidence = 0;
		
		public ViolationImplicationTupel(ViolationTupleInclSupport violation1, ViolationTupleInclSupport violation2, float confidence) {
			this.violation1 = violation1;
			this.violation2 = violation2;
			this.confidence = confidence;
		}
	}

	protected class ViolationTuple {
		
		public String label1;
		public String label2;
		public String expectedRelation;

		public ViolationTuple(String label1, String label2, String expectedRelation) {
			this.label1 = label1;
			this.label2 = label2;
			this.expectedRelation = expectedRelation;
		}
		
		@Override
		public String toString() {
			return "(" + label1 + "," + label2 + ":" + expectedRelation + ")";
		}
		
		@Override
		public boolean equals(Object o) {
			if (! (o instanceof ViolationTuple))
				return false;
			return (this.label1.equals(((ViolationTuple)o).label1) &&
					this.label2.equals(((ViolationTuple)o).label2) &&
					this.expectedRelation.equals(((ViolationTuple)o).expectedRelation));
		}
		
	}

	protected class ViolationTupleInclSupport extends ViolationTuple {
		
		public int globalSupport = 0;
		
		public ViolationTupleInclSupport(String label1, String label2, String expectedRelation) {
			super(label1,label2,expectedRelation);
		}
		
		public ViolationTupleInclSupport(ViolationTuple violation) {
			super(violation.label1,violation.label2,violation.expectedRelation);
		}
		
		@Override
		public String toString() {
			return "(" + label1 + "," + label2 + ":" + expectedRelation + "[" + globalSupport + "])";
		}
	}

	/**
	 * Class to capture a single BP violation in a trace.
	 *
	 */
	protected class BPViolationTuple extends ViolationTuple {
		
		public String foundRelation;

		public BPViolationTuple(String label1, String label2, String expectedRelation, String foundRelation) {
			super(label1,label2,expectedRelation);
			this.foundRelation = foundRelation;
		}
		
	}


	/**
	 * Class that captures all analysis information related to a single trace.
	 *
	 */
	protected class RootCauseAnalysisForTrace {
		
		private int traceId;
		
		private List<BPViolationTuple> bpViolations = new ArrayList<BPViolationTuple>();
		private List<ViolationTuple> coViolations = new ArrayList<ViolationTuple>();
		
		private Map<String,Integer> numberOfBPViolationsRelatedToTask = new HashMap<String,Integer>();
		private Map<String,Integer> numberOfCoViolationsRelatedToTask = new HashMap<String,Integer>();
				
		public RootCauseAnalysisForTrace(int traceId) {
			this.traceId = traceId;
		}
		
		public void addBPViolation(String s1, String s2, String expectedRelation, String foundRelation ) {
			bpViolations.add(new BPViolationTuple(s1,s2,expectedRelation,foundRelation));
			
			if (!this.numberOfBPViolationsRelatedToTask.containsKey(s1))
				this.numberOfBPViolationsRelatedToTask.put(s1, 0);
			if (!this.numberOfBPViolationsRelatedToTask.containsKey(s2))
				this.numberOfBPViolationsRelatedToTask.put(s2, 0);
			
			this.numberOfBPViolationsRelatedToTask.put(s1,this.numberOfBPViolationsRelatedToTask.get(s1) + 1);
			if (!s1.equals(s2))
				this.numberOfBPViolationsRelatedToTask.put(s2,this.numberOfBPViolationsRelatedToTask.get(s2) + 1);
		}

		public void addCooccurrenceViolation(String s1, String s2) {
			coViolations.add(new ViolationTuple(s1,s2,CausalBehaviouralProfile.COOCCURRENCE_SYMBOL));
			
			if (!this.numberOfCoViolationsRelatedToTask.containsKey(s1))
				this.numberOfCoViolationsRelatedToTask.put(s1, 0);
			if (!this.numberOfCoViolationsRelatedToTask.containsKey(s2))
				this.numberOfCoViolationsRelatedToTask.put(s2, 0);

			this.numberOfCoViolationsRelatedToTask.put(s1,this.numberOfCoViolationsRelatedToTask.get(s1) + 1);
			if (!s1.equals(s2))
				this.numberOfCoViolationsRelatedToTask.put(s2,this.numberOfCoViolationsRelatedToTask.get(s2) + 1);
		}
		
		public Set<String[]> getResultsOnConstraintViolations() {
			Set<String[]> result = new HashSet<String[]>();
			
			for (BPViolationTuple v : this.bpViolations)
				result.add(new String[]{
						String.valueOf(this.traceId),
						v.label1,
						v.label2,
						v.expectedRelation.toString(),
						v.foundRelation.toString()
						});

			for (ViolationTuple v : this.coViolations)
				result.add(new String[]{
						String.valueOf(this.traceId),
						v.label1,
						v.label2,
						v.expectedRelation,
						"Violation"
						});

			return result;
		}
		
		public Set<String[]> getResultsOnTaskViolations() {
			Set<String[]> result = new HashSet<String[]>();
			
			Set<String> keys = new HashSet<String>(this.numberOfBPViolationsRelatedToTask.keySet());
			keys.addAll(this.numberOfCoViolationsRelatedToTask.keySet());
			
			for (String t : keys) {
				int nuBPViolations = this.numberOfBPViolationsRelatedToTask.containsKey(t) ? this.numberOfBPViolationsRelatedToTask.get(t) : 0;
				int nuCoViolations = this.numberOfCoViolationsRelatedToTask.containsKey(t) ? this.numberOfCoViolationsRelatedToTask.get(t) : 0;
				
				float impact = ((float)(nuBPViolations + nuCoViolations))/((float)(bpViolations.size() + coViolations.size()));
				
				result.add(new String[]{
						String.valueOf(this.traceId),
						t,
						String.valueOf(impact)
						});
			}
			
			return result;
		}
	}
	
	protected List<Integer> traceIds = new ArrayList<Integer>(); 
	
	protected List<ViolationTupleInclSupport> violations = new ArrayList<ViolationTupleInclSupport>();
	protected List<ViolationImplicationTupel> violationImplicationsAboveThreshold = new ArrayList<ViolationImplicationTupel>();

	protected List<Set<Integer>> violationsForTraces = new ArrayList<Set<Integer>>();

	
	protected Map<Integer, RootCauseAnalysisForTrace> traceAnalyses = new HashMap<Integer, RootCauseAnalysisForTrace>();
	
	public final static List<String> constraintList = Arrays.asList(
			RelSetType.Exclusive.toString(),
			RelSetType.Order.toString(),
			RelSetType.ReverseOrder.toString(),
			RelSetType.Interleaving.toString(),
			CausalBehaviouralProfile.COOCCURRENCE_SYMBOL
	);
	
	protected int getNumberOfConstraints() {
		return constraintList.size();
	}
	
	protected int getIDForConstraint(String constraint) {
		return constraintList.indexOf(constraint);
	}
	
	public void addTrace(BehaviouralProfile<Trace,LabelEntity> rs) {
		this.traceIds.add(rs.getModel().getId());
		this.traceAnalyses.put(this.traceIds.indexOf(rs.getModel().getId()), new RootCauseAnalysisForTrace(rs.getModel().getId()));
	}
	
	public void addBPViolation(String s1, String s2, BehaviouralProfile<Trace,LabelEntity> rs, RelSetType expectedRelation, RelSetType  foundRelation) {
		ViolationTupleInclSupport v = new ViolationTupleInclSupport(s1,s2,BehaviouralProfile.getSymbolForRelation(expectedRelation));
		
		addViolation(rs,v);
		this.traceAnalyses.get(this.traceIds.indexOf(rs.getModel().getId())).addBPViolation(s1, s2, expectedRelation.toString(), foundRelation.toString());
	}
	
	protected void addViolation(BehaviouralProfile<Trace,LabelEntity> rs, ViolationTupleInclSupport v) {
		if (!this.violations.contains(v)) {
			this.violations.add(v);
			this.violationsForTraces.add(this.violations.indexOf(v), new HashSet<Integer>());
		}
		
		this.violationsForTraces.get(this.violations.indexOf(v)).add(this.traceIds.indexOf(rs.getModel().getId()));
	}
	
	public void addCooccurrenceViolation(String s1, String s2, BehaviouralProfile<Trace,LabelEntity> rs) {
		ViolationTupleInclSupport v = new ViolationTupleInclSupport(s1,s2,CausalBehaviouralProfile.COOCCURRENCE_SYMBOL);
		
		addViolation(rs, v);
		this.traceAnalyses.get(this.traceIds.indexOf(rs.getModel().getId())).addCooccurrenceViolation(s1, s2);
	}
	
	public void computeGlobalSupport() {
		for (ViolationTupleInclSupport v : this.violations)
			v.globalSupport = this.violationsForTraces.get(this.violations.indexOf(v)).size();
	}
	
	public void computeConfidenceForViolationPairs() {
		
		for (ViolationTupleInclSupport v1 : this.violations) {
			if (v1.globalSupport < CONSTANT_HIGH_SUPPORT)
				continue;
			for (ViolationTupleInclSupport v2 : this.violations) {
				if (v2.globalSupport < CONSTANT_HIGH_SUPPORT)
					continue;
				if (v1.equals(v2))
					continue;
				
				float supportForBothPairs = 0;
				for (Integer trace : this.traceIds) {
					 if (this.violationsForTraces.get(this.violations.indexOf(v1)).contains(this.traceIds.indexOf(trace))
							 && this.violationsForTraces.get(this.violations.indexOf(v2)).contains(this.traceIds.indexOf(trace)))
							supportForBothPairs++;
				}
				
				float confidence = (v1.globalSupport == 0) ? 0 : supportForBothPairs / (float) v1.globalSupport;

				if (confidence > CONSTANT_HIGH_CONFIDENCE) 
					this.violationImplicationsAboveThreshold.add(new ViolationImplicationTupel(v1,v2,confidence));
			}
		}
	}
		
	public void saveResults(String fileName) {
		
		String fileNameTracePairs = fileName.replace(".csv", "") + "_trace_pairs.csv";

		String[] captions = new String[]{
				"Trace", 
				"First Task", 
				"Second Task", 
				"Expected Relation",
				"Found Relation"
				};
		
		Set<String[]> rows = new HashSet<String[]>();
		
		for (RootCauseAnalysisForTrace a : this.traceAnalyses.values())
			rows.addAll(a.getResultsOnConstraintViolations());
		
		super.writeResultsToFile(fileNameTracePairs,captions,rows);
		
		String fileNameTraceTasks = fileName.replace(".csv", "") + "_trace_act.csv";

		captions = new String[]{
				"Trace", 
				"Task", 
				"Compliance Violation Impact of Task"
				};
		
		rows = new HashSet<String[]>();
		
		for (RootCauseAnalysisForTrace a : this.traceAnalyses.values())
			rows.addAll(a.getResultsOnTaskViolations());
		
		super.writeResultsToFile(fileNameTraceTasks,captions,rows);

		String fileNameGlobalSupport = fileName.replace(".csv", "") + "_global_support.csv";
		
		captions = new String[]{
				"First Task", 
				"Second Task", 
				"Violated Relation",
				"Support"
				};
		
		rows = new HashSet<String[]>();
		
		for (ViolationTupleInclSupport v : this.violations) {
			String[] row = new String[4];
			row[0] = v.label1;
			row[1] = v.label2;
			row[2] = v.expectedRelation;
			row[3] = String.valueOf(v.globalSupport);
			rows.add(row);
		}
		
		super.writeResultsToFile(fileNameGlobalSupport,captions,rows);

		String fileNameGlobalConfidence = fileName.replace(".csv", "") + "_global_confidence.csv";
		
		captions = new String[]{
				"First Task (First Pair)", 
				"Second Task (First Pair)", 
				"Violated Relation (First Pair)",
				"First Task (Second Pair)", 
				"Second Task (Second Pair)", 
				"Violated Relation (Second Pair)",
				"Confidence (Thresholds:" + CONSTANT_HIGH_SUPPORT  + "/" + CONSTANT_HIGH_CONFIDENCE + ")"
				};
		
		rows = new HashSet<String[]>();
		
		for (ViolationImplicationTupel v : this.violationImplicationsAboveThreshold) {
			String[] row = new String[7];
			row[0] = v.violation1.label1;
			row[1] = v.violation1.label2;
			row[2] = v.violation1.expectedRelation;
			row[3] = v.violation2.label1;
			row[4] = v.violation2.label2;
			row[5] = v.violation2.expectedRelation;
			row[6] = String.valueOf(v.confidence);
			rows.add(row);
		}
		
		super.writeResultsToFile(fileNameGlobalConfidence,captions,rows);

		/*
		 * Build up matrix for transitive reduction
		 */
		boolean[][] matrix = new boolean[this.violations.size()][this.violations.size()];
		
		for (ViolationImplicationTupel vi : this.violationImplicationsAboveThreshold) {
			ViolationTuple v1 = vi.violation1;
			ViolationTuple v2 = vi.violation2;
			matrix[this.violations.indexOf(v1)][this.violations.indexOf(v2)] = true;
		}

		/*
		 * Simple reduction, will not find minimal reduction in case of cycles
		 */
	    for (int i = 0; i < matrix.length; i++) {
	    	for (int j = 0; j < matrix.length; j++) {
		    	for (int k = 0; k < matrix.length; k++) {
		    		if (matrix[i][j] && matrix[j][k])
		    			matrix[i][k] = false;
		    	}
	    	}
	    }
	    
		String fileNameConfidenceGraph = fileName.replace(".csv", "") + "_global_confidence_graph.csv";

		try {
			FileOutputStream stream = new FileOutputStream(new File(fileNameConfidenceGraph));
			PrintWriter out = new PrintWriter(stream);

		
			for (ViolationImplicationTupel vi : this.violationImplicationsAboveThreshold) {
				ViolationTupleInclSupport v1 = vi.violation1;
				ViolationTupleInclSupport v2 = vi.violation2;
				if (matrix[this.violations.indexOf(v1)][this.violations.indexOf(v2)])
					out.println(v1.label1+v1.expectedRelation+v1.label2+"\t"+v1.globalSupport+"\t"+v2.label1+v2.expectedRelation+v2.label2+"\t"+v2.globalSupport+"\t"+String.valueOf(vi.confidence));
			}
			
			out.flush();
			stream.close();
			System.out.println("Saved results to " + fileNameConfidenceGraph);
			
		} catch (Exception e) {
			System.out.println("Failed to write the results to " + fileNameConfidenceGraph);
			e.printStackTrace();
		}

//		try {
//			FileOutputStream stream = new FileOutputStream(new File(fileNameConfidenceGraph));
//			PrintWriter out = new PrintWriter(stream);
//
//			out.println("nodedef>name VARCHAR,label VARCHAR, support INTEGER");
//
//			for (ViolationTupleInclSupport v : this.violations)
//				if (v.globalSupport >= CONSTANT_HIGH_SUPPORT)
//					out.println(this.violations.indexOf(v) + ",(" + v.label1 + "." + v.label2 + "." + v.expectedRelation + "),"+ String.valueOf(v.globalSupport));
//		
//			out.println("edgedef>node1 VARCHAR,node2 VARCHAR, label VARCHAR, conf DOUBLE");
//		
//			for (ViolationImplicationTupel vi : this.violationImplicationsAboveThreshold) {
//				ViolationTuple v1 = vi.violation1;
//				ViolationTuple v2 = vi.violation2;
//				if (matrix[this.violations.indexOf(v1)][this.violations.indexOf(v2)])
//					out.println(this.violations.indexOf(v1) + "," + this.violations.indexOf(v2) + ","+String.valueOf(vi.confidence));
//					
//			}
//			
//			out.flush();
//			stream.close();
//			System.out.println("Saved results to " + fileNameConfidenceGraph);
//			
//		} catch (Exception e) {
//			System.out.println("Failed to write the results to " + fileNameConfidenceGraph);
//			e.printStackTrace();
//		}

	}	
	
}
