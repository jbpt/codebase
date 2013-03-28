package org.jbpt.petri.untangling;

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jbpt.algo.graph.DirectedGraphAlgorithms;
import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INetSystem;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.IRun;
import org.jbpt.petri.ITransition;
import org.jbpt.petri.structure.PetriNetStructuralChecks;
import org.jbpt.petri.unfolding.IBPNode;
import org.jbpt.petri.unfolding.ICondition;
import org.jbpt.petri.unfolding.IEvent;

/**
 * An abstract implementation of the baseline algorithm for computing representative untanglings of net systems ({@link INetSystem}). 
 * 
 * @author Artem Polyvyanyy
 */
public class AbstractBaselineRepresentativeUntangling<BPN extends IBPNode<N>, C extends ICondition<BPN,C,E,F,N,P,T,M>, E extends IEvent<BPN,C,E,F,N,P,T,M>, 
													F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>> 
		extends AbstractRepresentativeUntangling<BPN,C,E,F,N,P,T,M>
{
	public AbstractBaselineRepresentativeUntangling(INetSystem<F,N,P,T,M> sys, UntanglingSetup setup) {
		super(sys, setup);
	}

	/**
	 * Constructor of a representative untangling.
	 * 
	 * @param sys Net system to untangle.
	 */
	public AbstractBaselineRepresentativeUntangling(INetSystem<F,N,P,T,M> sys) {
		super(sys);
	}
	
	@Override
	protected void constructRuns(INetSystem<F,N,P,T,M> system) {
		Queue<IRun<F,N,P,T,M>> queue = new ConcurrentLinkedQueue<>();
		IRun<F,N,P,T,M> ini = this.createRun(system);
		
		// if system has no conflicts and is acyclic its untangling is trivial
		PetriNetStructuralChecks<F,N,P,T> sc = new PetriNetStructuralChecks<>();
		DirectedGraphAlgorithms<F,N> dga = new DirectedGraphAlgorithms<>();
		if (sc.isConflictFree(system) && dga.isAcyclic(system)) {
			Set<T> PE = ini.getPossibleExtensions();
			while (!PE.isEmpty()) {
				ini.append(PE.iterator().next());
			}
			this.significantRunCounter++;
			this.runs.add(ini);
			return;
		}
		
		// perform complex stuff
		queue.add(ini);
		this.significantRunCounter++;
		
		while (!queue.isEmpty()) {
			IRun<F,N,P,T,M> run = queue.poll();
			
			// safeness check (extra)
			if (run.size()>0 && !run.get(run.size()-1).getOutputMarking().isSafe()) {
				this.safe = false;
				return;
			}
			
			// get possible extensions of the run
			Set<T> PE = run.getPossibleExtensions();
			
			if (PE.isEmpty()) {
				if (setup.REDUCE) this.prune(run);
				this.runs.add(run);
			}
			else {
				boolean allExtensionsInsignificant = true;
				for (T t : PE) {
					IRun<F,N,P,T,M> freshRun = run.clone();
					freshRun.append(t);
					
					if (this.isSignificant(freshRun)) {
						queue.add(freshRun);
						this.significantRunCounter++;
						allExtensionsInsignificant = false;
					}
				}
				
				if (allExtensionsInsignificant) {
					if (setup.REDUCE) this.prune(run);
					this.runs.add(run);
				}
			}
		}
		
		if (setup.REDUCE) {
			this.reduceSubruns();
		}
	}
}
