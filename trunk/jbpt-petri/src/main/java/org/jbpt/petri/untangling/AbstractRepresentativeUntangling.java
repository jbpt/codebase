package org.jbpt.petri.untangling;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INetSystem;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.IRun;
import org.jbpt.petri.IStep;
import org.jbpt.petri.ITransition;
import org.jbpt.petri.Run;
import org.jbpt.petri.unfolding.IBPNode;
import org.jbpt.petri.unfolding.ICondition;
import org.jbpt.petri.unfolding.IEvent;
import org.jbpt.utils.IOUtils;

/**
 * An abstract implementation of a technique for constructing representative untanglings of net system (@link {@link INetSystem}).
 * 
 * @author Artem Polyvyanyy
 */
public abstract class AbstractRepresentativeUntangling<BPN extends IBPNode<N>, C extends ICondition<BPN,C,E,F,N,P,T,M>, E extends IEvent<BPN,C,E,F,N,P,T,M>, 
														F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>>
{
	protected INetSystem<F,N,P,T,M> sys = null;
	protected INetSystem<F,N,P,T,M> reducedSys = null;
	
	protected Set<IRun<F,N,P,T,M>> runs = null;
	
	protected Collection<IProcess<BPN,C,E,F,N,P,T,M>> processes = null;
	protected Collection<IProcess<BPN,C,E,F,N,P,T,M>> reducedProcesses = null;
	
	protected int significantRunCounter = 0; // number of visited runs
	protected boolean safe = true;
	
	protected UntanglingSetup setup;
	
	protected long time = 0;
	
	public AbstractRepresentativeUntangling(INetSystem<F,N,P,T,M> sys) {
		this(sys, new UntanglingSetup());
	}
	
	/**
	 * Constructor of a representative untangling.
	 * 
	 * @param sys Net system to untangle.
	 */
	public AbstractRepresentativeUntangling(INetSystem<F,N,P,T,M> sys, UntanglingSetup setup) {
		if (sys==null) return;
		
		this.setup = setup;
		
		this.sys = sys;
		this.runs = new HashSet<>();
		
		long start = System.nanoTime();
		this.constructRuns(this.sys);
		long stop = System.nanoTime();
		this.time = stop - start;
		this.constructProcesses();
	}
	
	public Set<IRun<F,N,P,T,M>> getMaximalSignificantRuns() {
		return this.runs;
	}
	
	public boolean isSafe() {
		return this.safe;
	}
	
	public Collection<IProcess<BPN,C,E,F,N,P,T,M>> getProcesses() {
		return this.processes;
	}
	
	public Collection<IProcess<BPN,C,E,F,N,P,T,M>> getReducedProcesses() {
		return this.reducedProcesses;
	}
	
	public INetSystem<F,N,P,T,M> getReducedNetSystem() {
		return this.reducedSys;
	}
	
	protected void constructProcesses() {		
		this.processes = new ArrayList<>();
		
		for (IRun<F,N,P,T,M> run : this.runs) {
			IProcess<BPN,C,E,F,N,P,T,M> pi = this.createProcess(this.sys);	

			for (IStep<F,N,P,T,M> step : run) {
				pi.appendTransition(step.getTransition());
			}
			
			this.processes.add(pi);
		}
	}
	
	@SuppressWarnings("unchecked")
	protected IProcess<BPN,C,E,F,N,P,T,M> createProcess(INetSystem<F,N,P,T,M> sys) {
		IProcess<BPN,C,E,F,N,P,T,M> pi = null;
		try {
			pi = (IProcess<BPN,C,E,F,N,P,T,M>) Process.class.newInstance();
			pi.setNetSystem(sys);
			pi.constructInitialBranchingProcess();
			return pi;
		} catch (InstantiationException | IllegalAccessException exception) {
			exception.printStackTrace();
		}
		
		return pi;
	}
	
	@SuppressWarnings("unchecked")
	protected IRun<F,N,P,T,M> createRun(INetSystem<F,N,P,T,M> sys) {
		IRun<F,N,P,T,M> run = null;
		try {
			run = (IRun<F,N,P,T,M>) Run.class.newInstance();
			run.setNetSystem(sys);
			return run;
		} catch (InstantiationException | IllegalAccessException exception) {
			exception.printStackTrace();
		}
		
		return run;
	}
	
	public void serializeProcesses() {
		int i=1;
		for (IProcess<BPN,C,E,F,N,P,T,M> pi : this.getProcesses()) {
			try {
				IOUtils.invokeDOT("./", "p"+(i++)+".png", pi.getOccurrenceNet().toDOT());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void serializeReducedProcesses() {
		int i=1;
		for (IProcess<BPN,C,E,F,N,P,T,M> pi : this.getReducedProcesses()) {
			try {
				IOUtils.invokeDOT("./", "rp"+(i++)+".png", pi.getOccurrenceNet().toDOT());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Construct maximal repetition significant runs of a given net system.
	 *  
	 * @param system A net system.
	 */
	protected void constructRuns(INetSystem<F,N,P,T,M> system) {
	}

	/**
	 * Check if a run of a net system is significant.
	 * 
	 * @param run A run of a net system
	 * @return <tt>true</tt> if the given run is significant; otherwise <tt><false/tt>.
	 */
	protected boolean isSignificant(IRun<F,N,P,T,M> run) {
		for (int i=0; i<run.size(); i++) {
			for (int j=i+1; j<run.size(); j++) {
				if (!run.get(i).equals(run.get(j))) continue;
				
				boolean flag = false; // there is no significant occurrence between i and j
				for (int k=i+1; k<j; k++) {
					boolean flagK = true; // is position k significant 
					for (int m=0; m<run.size(); m++) {
						if (m>=i && m<=j) continue;
						
						if (run.get(k).equals(run.get(m))) {
							flagK = false;
							break;
						}
					}
					
					flag |= flagK;
					if (flag) break;
				}
				
				if (!flag) return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Prune a given run, i.e., iteratively remove every last step of the given run 
	 * if this last step occurs at least twice in this run.
	 * 
	 * @param run Run to prune.
	 */
	protected void prune(IRun<F,N,P,T,M> run) {
		int k = run.size()-1; 
		
		for (int i=run.size()-1; i>0; i--) {
			boolean flag = false;
			for (int j=0; j<i; j++) {
				if (run.get(i).equals(run.get(j))) {
					flag = true;
					break;
				}
			}
			
			if (flag) k=i-1;
			else break;
		}
		
		for (int m=run.size()-1; m>k; m--)
			run.remove(m);
	}
	
	public int getNumberOfSignificantRuns() {
		return this.significantRunCounter;
	}
	
	public boolean isDeadlockFree() {
		for (IRun<F,N,P,T,M> run : this.runs) {
			if (run.isEmpty()) return false;
			
			IStep<F,N,P,T,M> step = run.get(run.size()-1);
			if (this.sys.getEnabledTransitionsAtMarking(step.getOutputMarking()).isEmpty())
				return false;
		}
		
		return true;
	}
	
	public boolean isNonSinkDeadlockFree() {
		for (IRun<F,N,P,T,M> run : this.runs) {
			if (run.isEmpty()) return false;
			
			IStep<F,N,P,T,M> step = run.get(run.size()-1);
			if (this.sys.getEnabledTransitionsAtMarking(step.getOutputMarking()).isEmpty()) {
				for (P p : step.getOutputMarking().toMultiSet()) {
					if (!this.sys.getPostset(p).isEmpty())
						return false;
				}
			}
		}
		
		return true;
	}
	
	protected void reduceSubruns() {
		boolean flag = true;
		
		IRun<F,N,P,T,M> run = null;
		
		while (flag) {
			flag = false;
			
			for (IRun<F,N,P,T,M> run1 : this.runs) {
				for (IRun<F,N,P,T,M> run2 : this.runs) {
					if (run1.equals(run2)) continue;
					
					if (run1.containsAll(run2)) {
						run = run2;
						flag = true;
						break;
					}
					
					if (run1.containsAll(run2) && run2.containsAll(run1) && run1.size()<run2.size()) {
						run = run2;
						flag = true;
						break;
					}
				}
				if (flag) break;
			}
			
			if (flag)
				this.runs.remove(run);
		}
	}
	
	public long getTime() {
		return this.time;
				
	}
}
