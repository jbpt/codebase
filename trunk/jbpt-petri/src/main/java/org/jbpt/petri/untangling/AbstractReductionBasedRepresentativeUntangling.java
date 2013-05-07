package org.jbpt.petri.untangling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INetSystem;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.IRun;
import org.jbpt.petri.IStep;
import org.jbpt.petri.ITransition;
import org.jbpt.petri.unfolding.IBPNode;
import org.jbpt.petri.unfolding.ICondition;
import org.jbpt.petri.unfolding.IEvent;

/**
 * An abstract implementation of the reduction-based algorithm for computing representative untanglings of net systems ({@link INetSystem}).
 * 
 * @author Artem Polyvyanyy
 */
public class AbstractReductionBasedRepresentativeUntangling<BPN extends IBPNode<N>, C extends ICondition<BPN,C,E,F,N,P,T,M>, E extends IEvent<BPN,C,E,F,N,P,T,M>, 
													F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>> 
		extends AbstractBaselineRepresentativeUntangling<BPN,C,E,F,N,P,T,M>
{

	private Map<F,List<N>> abs = null;
	private Map<N,N> map = null;
	private Map<N,N> map2 = null;
	
	/**
	 * Constructor of a representative untangling.
	 * 
	 * @param sys A net system to untangle.
	 */
	public AbstractReductionBasedRepresentativeUntangling(INetSystem<F,N,P,T,M> sys) {
		super(sys);
	}
	
	public AbstractReductionBasedRepresentativeUntangling(INetSystem<F,N,P,T,M> sys, UntanglingSetup setup) {
		super(sys, setup);
	}
	
	@Override
	protected void constructRuns(INetSystem<F,N,P,T,M> system) {
		map2 = new HashMap<>();
		this.reducedSys = system.clone(map2);
		
		map = new HashMap<>();
		for (Map.Entry<N,N> entry : map2.entrySet())
			map.put(entry.getValue(), entry.getKey());
		
		try {		
			abs = this.reduce(this.reducedSys);
		}
		catch(Exception e) {
			System.err.println(e.getMessage());
		}
		
		super.constructRuns(this.reducedSys);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void constructProcesses() {
		// construct reduced processes
		this.reducedProcesses = new ArrayList<>();
		for (IRun<F,N,P,T,M> run : this.runs) {
			IProcess<BPN,C,E,F,N,P,T,M> pi = this.createProcess(this.reducedSys);	

			for (IStep<F,N,P,T,M> step : run) {
				pi.appendTransition(step.getTransition());
			}
			
			this.reducedProcesses.add(pi);
		}
		
		// construct processes of the original net
		this.processes = new ArrayList<>();
		for (IRun<F,N,P,T,M> run : this.runs) {
			IProcess<BPN,C,E,F,N,P,T,M> pi = this.createProcess(this.sys);	

			for (IStep<F,N,P,T,M> step : run) {
				// process preset
				for (P p : step.getInputMarking().toMultiSet()) {
					List<N> list = this.getAbstractedNodes((N)p,(N)step.getTransition());
					if (list!=null) {
						for (int i=0; i<list.size(); i+=2) {
							pi.appendTransition((T)this.map.get(list.get(i)));
						}
					}
				}
				
				// process transition
				pi.appendTransition((T)this.map.get(step.getTransition()));
				
				// process postset
				for (P p : step.getOutputMarking().toMultiSet()) {
					List<N> list = this.getAbstractedNodes((N)step.getTransition(),(N)p);
					if (list!=null) {
						for (int i=1; i<list.size(); i+=2) {
							pi.appendTransition((T)this.map.get(list.get(i)));
						}
					}
				}
			}
			
			for (C c : pi.getMax()) {
				for (F f : this.reducedSys.getOutgoingEdges(map2.get(c.getPlace()))) {
					if (this.abs.containsKey(f)) {
						List<N> list = this.abs.get(f);
						for (int i=0; i<list.size(); i+=2) {
							pi.appendTransition((T)this.map.get(list.get(i)));
						}
					}
				}
			}
			
			this.processes.add(pi);
		}

	}
	
	private List<N> getAbstractedNodes(N n1, N n2) {
		for (Map.Entry<F,List<N>> entry : this.abs.entrySet()) {
			F f = entry.getKey();
			if (f.getSource().equals(n1) && f.getTarget().equals(n2))
				return entry.getValue();
		}
		
		return null;
	}

	@SuppressWarnings({ "unchecked" })
	protected Map<F,List<N>> reduce(INetSystem<F,N,P,T,M> system) {
		Map<F,List<N>> result = new HashMap<>();
		
		// compute maximal sequences of trivial fragments
		List<List<F>> maxSequences = new ArrayList<>();
		Set<F> flow = new HashSet<>(system.getFlow());
		List<F> seq = new ArrayList<F>();
		boolean flag = false;
		N entry = null;
		N exit = null;
		while (!flow.isEmpty()) {
			if (!flag) {
				F f = flow.iterator().next();
				flow.remove(f);
				seq.add(f);
				entry = f.getSource();
				exit = f.getTarget();
				flag = true;
			}
			else {
				boolean done = true;
				
				P p = null; if (entry instanceof IPlace) p = (P) entry;
				if (system.getDirectPredecessors(entry).size()==1  && system.getDirectSuccessors(entry).size()==1 && (p==null || system.getTokens(p)==0)) {
					F nf = system.getFirstIncomingEdge(entry);
					seq.add(0,nf);
					entry = nf.getSource();
					done = false;
					flow.remove(nf);
				}
				
				p = null; if (exit instanceof IPlace) p = (P) exit;
				if (system.getDirectPredecessors(exit).size()==1  && system.getDirectSuccessors(exit).size()==1 && (p==null || system.getTokens(p)==0)) {
					F nf = system.getFirstOutgoingEdge(exit);
					seq.add(nf);
					exit = nf.getTarget();
					done = false;
					flow.remove(nf);
				}
				
				if (done) {
					flag = false;
					maxSequences.add(new ArrayList<F>(seq));
					seq = new ArrayList<F>();
					entry = null;
					exit = null;
				}
			}
		}
		maxSequences.add(seq);
				
		// reduce maximal sequences
		for (List<F> sequence : maxSequences) {
			if (sequence.size()<3) continue;
			N start	= sequence.get(0).getSource();
			N end	= sequence.get(sequence.size()-1).getTarget();
			
			if (start instanceof IPlace) {
				if (end instanceof IPlace) { // O->...->O
					P en = (P) start;
					List<N> list = this.getOrderedNodes(sequence);
					T last = (T) list.get(list.size()-1);
					list.remove(list.size()-1);
					system.removeNodes(list);
					F f = system.addFlow(en, last);
					result.put(f,list);
				}
				else { // O->...->[] !!! can lead to deadlocks
					// Option 1: remove everything between  entry and exit
					/*P en	= (P) start;
					T ex	= (T) end;
					List<N> list = this.getOrderedNodes(sequence);
					system.removeNodes(list);
					F f = system.addFlow(en,ex);
					result.put(f,list);*/
					
					// Option 2: avoid potential deadlocks
					P en	= (P) start;
					T ex	= (T) end;
					
					if (system.getPostset(en).size()==1 || system.getPreset(ex).size()==1) { // not a join
						List<N> list = this.getOrderedNodes(sequence);
						system.removeNodes(list);
						F f = system.addFlow(en,ex);
						result.put(f,list);
					}
					else { // a join
						if (sequence.size()<5) continue;
						List<N> list = this.getOrderedNodes(sequence);
						T last = (T) list.get(list.size()-2);
						list.remove(list.size()-1);
						list.remove(list.size()-1);
						system.removeNodes(list);
						F f = system.addFlow(en, last);
						result.put(f,list);
					}
				}
			}
			else {
				if (end instanceof IPlace) { // []->...->O
					T en	= (T) start;
					P ex	= (P) end;
					List<N> list = this.getOrderedNodes(sequence);
					system.removeNodes(list);
					F f = system.addFlow(en, ex);
					result.put(f,list);
				}
				else { // []->...->[]
					T en = (T) start;
					List<N> list = this.getOrderedNodes(sequence);
					P last = (P) list.get(list.size()-1);
					list.remove(list.size()-1);
					system.removeNodes(list);
					F f = system.addFlow(en, last);
					result.put(f,list);
				}
			}
		}
		
		return result;
	}
	
	private List<N> getOrderedNodes(List<F> polygon) {
		List<N> result = new ArrayList<N>();
				
		for (int i=1; i<polygon.size(); i++)
			result.add(polygon.get(i).getSource());
		
		return result;
	}
	
	@Override
	public boolean isNonSinkDeadlockFree() {
		for (IRun<F,N,P,T,M> run : this.runs) {
			if (run.isEmpty()) return false;
			
			IStep<F,N,P,T,M> step = run.get(run.size()-1);
			if (this.reducedSys.getEnabledTransitionsAtMarking(step.getOutputMarking()).isEmpty()) {
				for (P p : step.getOutputMarking().toMultiSet()) {
					if (!this.reducedSys.getPostset(p).isEmpty())
						return false;
				}
			}
		}
		
		return true;
	}
}
