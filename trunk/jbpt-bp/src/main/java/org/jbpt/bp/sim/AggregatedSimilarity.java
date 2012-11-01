package org.jbpt.bp.sim;

import org.jbpt.alignment.Alignment;
import org.jbpt.bp.RelSet;
import org.jbpt.hypergraph.abs.IEntity;
import org.jbpt.hypergraph.abs.IEntityModel;

/**
 * Scores two models by the weighted sum of the single similarities.
 * 
 * @author matthias.weidlich
 *
 */
public class AggregatedSimilarity<R extends RelSet<M, N>, M extends IEntityModel<N>, N extends IEntity> extends AbstractRelSetSimilarity<R,M,N> {

	public double weightExSim = 0; //1.0/6.0; // 1
	public double weightSoSim = 0; //3.0/6.0; // 3
	public double weightInSim = 0;
	public double weightESSim = 0; //2.0/6.0; // 2
	public double weightEISim = 0;
	
	private ExclusivenessSimilarity<R,M,N> ex = new ExclusivenessSimilarity<R,M,N>();
	private OrderSimilarity<R,M,N> so = new OrderSimilarity<R,M,N>();
	private InterleavingSimilarity<R,M,N> in = new InterleavingSimilarity<R,M,N>();
	private ExtendedOrderSimilarity<R,M,N> eso = new ExtendedOrderSimilarity<R,M,N>();
	private ExtendedInterleavingSimilarity<R,M,N> ein = new ExtendedInterleavingSimilarity<R,M,N>();
	
	@Override
	public double score(Alignment<R,N> alignment) {
		
		double weightSum = weightExSim + weightSoSim + weightInSim + weightESSim + weightEISim;
		
		return (
		(weightExSim > 0 ? weightExSim/weightSum * ex.score(alignment) : 0) + 
		(weightSoSim > 0 ? weightSoSim/weightSum * so.score(alignment) : 0) + 
		(weightInSim > 0 ? weightInSim/weightSum * in.score(alignment) : 0) + 
		(weightESSim > 0 ? weightESSim/weightSum * eso.score(alignment) : 0) + 
		(weightEISim > 0 ? weightEISim/weightSum * ein.score(alignment) : 0) );
	}

	@Override
	public double scoreDice(Alignment<R,N> alignment) {
		
		double weightSum = weightExSim + weightSoSim + weightInSim + weightESSim + weightEISim;
		
		return (
		(weightExSim > 0 ? weightExSim/weightSum * ex.scoreDice(alignment) : 0) + 
		(weightSoSim > 0 ? weightSoSim/weightSum * so.scoreDice(alignment) : 0) + 
		(weightInSim > 0 ? weightInSim/weightSum * in.scoreDice(alignment) : 0) + 
		(weightESSim > 0 ? weightESSim/weightSum * eso.scoreDice(alignment) : 0) + 
		(weightEISim > 0 ? weightEISim/weightSum * ein.scoreDice(alignment) : 0) );
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
	
	/**
	 * Set weights for aggregation of similarities.
	 * 
	 * @param weightExSim weight for @ExclusivenessSimilarity
	 * @param weightSoSim weight for @OrderSimilarity
	 * @param weightInSim weight for @InterleavingSimilarity
	 * @param weightESSim weight for @ExtendedOrderSimilarity
	 * @param weightEISim weight for @ExtendedInterleavingSimilarity
	 */
	public void setWeights(double weightExSim, double weightSoSim, double weightInSim, double weightESSim, double weightEISim) {
		this.weightExSim = weightExSim;
		this.weightSoSim = weightSoSim;
		this.weightInSim = weightInSim;
		this.weightESSim = weightESSim;
		this.weightEISim = weightEISim;
	}

}
