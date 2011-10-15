package de.hpi.bpt.process.petri.bp.sim;

import de.hpi.bpt.alignment.Alignment;
import de.hpi.bpt.alignment.IEntity;
import de.hpi.bpt.alignment.IEntityModel;
import de.hpi.bpt.process.petri.bp.RelSet;

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
		return (
		(weightExSim > 0 ? weightExSim * ex.score(alignment) : 0) + 
		(weightSoSim > 0 ? weightSoSim * so.score(alignment) : 0) + 
		(weightInSim > 0 ? weightInSim * in.score(alignment) : 0) + 
		(weightESSim > 0 ? weightESSim * eso.score(alignment) : 0) + 
		(weightEISim > 0 ? weightEISim * ein.score(alignment) : 0) );
	}

	@Override
	public double scoreDice(Alignment<R,N> alignment) {
		return (
		(weightExSim > 0 ? weightExSim * ex.scoreDice(alignment) : 0) + 
		(weightSoSim > 0 ? weightSoSim * so.scoreDice(alignment) : 0) + 
		(weightInSim > 0 ? weightInSim * in.scoreDice(alignment) : 0) + 
		(weightESSim > 0 ? weightESSim * eso.scoreDice(alignment) : 0) + 
		(weightEISim > 0 ? weightEISim * ein.scoreDice(alignment) : 0) );
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
