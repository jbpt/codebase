package de.hpi.bpt.process.petri.bp.sim;

import de.hpi.bpt.process.petri.bp.RelSetAlignment;

/**
 * Scores two models by the weighted sum of the single similarities.
 * 
 * @author matthias.weidlich
 *
 */
public class AggregatedSimilarity extends AbstractRelSetSimilarity {

	public double weightExSim = 0; //1.0/6.0; // 1
	public double weightSoSim = 0; //3.0/6.0; // 3
	public double weightInSim = 0;
	public double weightESSim = 0; //2.0/6.0; // 2
	public double weightEISim = 0;
	
	private ExclusivenessSimilarity ex = new ExclusivenessSimilarity();
	private OrderSimilarity so = new OrderSimilarity();
	private InterleavingSimilarity in = new InterleavingSimilarity();
	private ExtendedOrderSimilarity eso = new ExtendedOrderSimilarity();
	private ExtendedInterleavingSimilarity ein = new ExtendedInterleavingSimilarity();
	
	@Override
	public double score(RelSetAlignment alignment) {
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
