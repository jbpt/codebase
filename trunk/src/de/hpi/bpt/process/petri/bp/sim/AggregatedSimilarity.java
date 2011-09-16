package de.hpi.bpt.process.petri.bp.sim;

import de.hpi.bpt.process.petri.bp.BPAlignment;

/**
 * Scores two models by the weighted sum of the single similarities.
 * 
 * @author matthias.weidlich
 *
 */
public class AggregatedSimilarity extends AbstractBPSimilarity {

	public double weightExSim = 0; //1.0/6.0; // 1
	public double weightSoSim = 0; //3.0/6.0; // 3
	public double weightInSim = 0;
	public double weightESSim = 0; //2.0/6.0; // 2
	public double weightEISim = 0;
	
	private ExclusivenessSimilarity ex = new ExclusivenessSimilarity();
	private StrictOrderSimilarity so = new StrictOrderSimilarity();
	private InterleavingOrderSimilarity in = new InterleavingOrderSimilarity();
	private ExtendedStrictOrderSimilarity eso = new ExtendedStrictOrderSimilarity();
	private ExtendedInterleavingOrderSimilarity ein = new ExtendedInterleavingOrderSimilarity();
	
	@Override
	public double score(BPAlignment alignment) {
		return (
		(weightExSim > 0 ? weightExSim * ex.score(alignment) : 0) + 
		(weightSoSim > 0 ? weightSoSim * so.score(alignment) : 0) + 
		(weightInSim > 0 ? weightInSim * in.score(alignment) : 0) + 
		(weightESSim > 0 ? weightESSim * eso.score(alignment) : 0) + 
		(weightEISim > 0 ? weightEISim * ein.score(alignment) : 0) );
	}
	
	@Override
	public String getName() {
		return this.getClass().getName() 
		+ "Ex:" + weightExSim + "-" 
		+ "So:" + weightSoSim + "-" 
		+ "In:" + weightInSim + "-" 
		+ "ES:" + weightESSim + "-" 
		+ "EI:" + weightEISim + "-";
	}
}
