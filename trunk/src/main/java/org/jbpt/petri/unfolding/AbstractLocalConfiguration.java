package org.jbpt.petri.unfolding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jbpt.petri.IFlow;
import org.jbpt.petri.IMarking;
import org.jbpt.petri.INode;
import org.jbpt.petri.IPlace;
import org.jbpt.petri.ITransition;
import org.jbpt.petri.Marking;


public class AbstractLocalConfiguration<BPN extends IBPNode<N>, C extends ICondition<BPN,C,E,F,N,P,T,M>, E extends IEvent<BPN,C,E,F,N,P,T,M>, F extends IFlow<N>, N extends INode, P extends IPlace, T extends ITransition, M extends IMarking<F,N,P,T>>
		extends HashSet<E> 
		implements ILocalConfiguration<BPN,C,E,F,N,P,T,M>
{
	private static final long serialVersionUID = 1L;
	
	private E e = null;							// event
	private ICut<BPN,C,E,F,N,P,T,M> cut = null;	// cut
	private M marking = null;					// marking of cut
	private List<T> vec = null;					// quasi Parikh vector
	private List<Set<E>> foata = null;			// Foata normal form
	private ICompletePrefixUnfolding<BPN,C,E,F,N,P,T,M> CPU = null;
	
	public AbstractLocalConfiguration() {
	}
	
	public AbstractLocalConfiguration(ICompletePrefixUnfolding<BPN,C,E,F,N,P,T,M> cpu, E e) {
		this.setEvent(e);
		this.setCompletePrefixUnfolding(cpu);
		this.construct();
	}
	
	@Override
	public ICut<BPN,C,E,F,N,P,T,M> getCut() {
		if (this.cut == null) {
			this.cut = (ICut<BPN,C,E,F,N,P,T,M>)this.CPU.createCut();
			this.cut.addAll(this.CPU.getInitialCut());
			for (E e : this) this.cut.addAll(e.getPostConditions());
			for (E e : this) this.cut.removeAll(e.getPreConditions());
		}
		
		return this.cut;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public M getMarking() {
		if (this.marking == null) {
			try {
				this.marking = (M) Marking.class.newInstance();
				this.marking.setPetriNet(this.CPU.getOriginativeNetSystem());
			} catch (InstantiationException | IllegalAccessException e) {
				return null;
			}			

			for (C c : this.getCut()) {
				if (c.getPlace() == null) this.marking.put(c.getPlace(), 1);
				this.marking.put(c.getPlace(), this.marking.get(c.getPlace())+1);
			}
		}
		
		return this.marking;
	}
	
	class ParikhComparator implements Comparator<T> {
		
		private List<T> totalOrderTs = null;
		
		public ParikhComparator(List<T> totalOrderTs) {
			this.totalOrderTs = totalOrderTs;
		}
		
		@Override
		public int compare(T t1, T t2) {
			int i1 = this.totalOrderTs.indexOf(t1);
			int i2 = this.totalOrderTs.indexOf(t2);
			if (i1<i2) return -1;
			if (i1>i2) return 1;
			
			return 0;
		}
	}
	
	@Override
	public List<T> getQuasiParikhVector() {
		if (this.vec == null) {
			this.vec = new ArrayList<T>();
			for (E e : this) this.vec.add(e.getTransition());
			Collections.sort(this.vec, new ParikhComparator(this.CPU.getTotalOrderOfTransitions()));
		}
		
		return this.vec;
	}
	
	// TODO cache this
	@Override
	public List<T> getQuasiParikhVector(Collection<E> es) {
		List<T> result = new ArrayList<T>();
		for (E e : es) result.add(e.getTransition());
		Collections.sort(result, new ParikhComparator(this.CPU.getTotalOrderOfTransitions()));
		return result;
	}	
	
	@Override
	public List<Set<E>> getFoataNormalForm() {
		if (this.foata == null) {
			this.foata = new ArrayList<Set<E>>();
			Collection<E> lc = new ArrayList<E>(this);
			while (lc.size()>0) {
				Set<E> min = this.getMin(lc);
				this.foata.add(min);
				lc.removeAll(min);
			}
		}
		
		return this.foata;
	}
	
	@SuppressWarnings("unchecked")
	private Set<E> getMin(Collection<E> lc) {
		Set<E> result = new HashSet<E>();
		for (E e1 : lc) {
			boolean flag = true;
			for (E e2 : lc) {
				if (this.CPU.areCausal((BPN)e2,(BPN)e1)) {
					flag = false;
					break;
				}
			}
			
			if (flag) result.add(e1);
		}
		return result;
	}

	@Override
	public Integer compareTransitions(T t1, T t2) {
		int i1 = this.CPU.getTotalOrderOfTransitions().indexOf(t1);
		int i2 = this.CPU.getTotalOrderOfTransitions().indexOf(t2);
		if (i1<0 || i2<0) return null;
		
		if (i1<i2) return -1;
		if (i1>i2) return 1;
		
		return 0;
	}

	@Override
	public void setEvent(E e) {
		this.e = e;
	}

	@Override
	public void setCompletePrefixUnfolding(ICompletePrefixUnfolding<BPN,C,E,F,N,P,T,M> cpu) {
		this.CPU = cpu;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void construct() {
		this.add(this.e);
		
		for (C c : this.e.getPreConditions()) {
			for (BPN n : this.CPU.getCausalPredecessors((BPN)c)) {
				if (n.isEvent())
					this.add((E)n);
			}
		}
	}
}
