package org.jbpt.alignment;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jbpt.hypergraph.abs.IEntity;
import org.jbpt.hypergraph.abs.IEntityModel;

/**
 * Class that stores an alignment between two entity models using a
 * correspondence relation.
 * 
 * See <code>initCorrespondenceRelation</code> for a simple method 
 * that initialises the alignment with 
 * correspondences that are derived based on the names of the 
 * entities(string equality).
 * 
 * @author matthias.weidlich
 *
 * @param <M>, a model type
 * @param <N>, an entity type
 */
public class Alignment<M extends IEntityModel<N>, N extends IEntity>  {

		/**
		 * The first model of the alignment.
		 */
		protected M model1;
		
		/**
		 * The second model of the alignment.
		 */
		protected M model2;

		/**
		 * Correspondence relation to capture elementary and complex correspondences
		 * between the first and the second model.
		 */
		protected Map<N,Set<N>> correspondenceRelation = new HashMap<N, Set<N>>();
		
		/**
		 * The reverse correspondence relation is maintained as well.
		 */
		protected Map<N,Set<N>> reversedCorrespondenceRelation = new HashMap<N, Set<N>>();

		/**
		 * Constructor that takes just the two models as input, for which
		 * we want to define correspondences.
		 * 
		 * @param model1, the first model of the correspondences
		 * @param model2, the second model of the correspondences
		 */
		public Alignment(M model1, M model2){
			this.model1 = model1;
			this.model2 = model2;
		}
		
		/**
		 * Constructor that copies the alignment given as input.
		 * 
		 * @param alignment, the alignment that shall be copied
		 */
		public Alignment(Alignment<M,N> alignment) {
			this.model1 = alignment.getFirstModel();
			this.model2 = alignment.getSecondModel();
			
			for (N v1 : alignment.getAlignedEntitiesOfFirstModel())
				for (N v2 : alignment.getCorrespondingEntitiesForEntityOfFirstModel(v1))
					this.addElementaryCorrespondence(v1, v2);
		}
		
		public void addElementaryCorrespondence(N n1, N n2) {
			if (!this.correspondenceRelation.containsKey(n1))
				this.correspondenceRelation.put(n1,new HashSet<N>());
			if (!this.reversedCorrespondenceRelation.containsKey(n2))
				this.reversedCorrespondenceRelation.put(n2,new HashSet<N>());
			
			this.correspondenceRelation.get(n1).add(n2);
			this.reversedCorrespondenceRelation.get(n2).add(n1);
		}
		
		public void addComplexCorrespondence(Set<N> n1, N n2) {
			for (N n : n1)
				addElementaryCorrespondence(n, n2);
		}
		
		public void addComplexCorrespondence(N n1, Set<N> n2) {
			for (N n : n2)
				addElementaryCorrespondence(n1, n);
		}
		
		public void addComplexCorrespondence(Set<N> n1, Set<N> n2) {
			for (N n : n1)
				addComplexCorrespondence(n,n2);
		}
		
		public void removeElementaryCorrespondence(N n1, N n2) {
			if (this.correspondenceRelation.containsKey(n1))
				this.correspondenceRelation.get(n1).remove(n2);
			if (this.correspondenceRelation.get(n1).isEmpty())
				this.correspondenceRelation.remove(n1);
			
			if (this.reversedCorrespondenceRelation.containsKey(n2))
				this.reversedCorrespondenceRelation.get(n2).remove(n1);
			if (this.reversedCorrespondenceRelation.get(n2).isEmpty())
				this.reversedCorrespondenceRelation.remove(n2);
		}
		
		public void removeComplexCorrespondence(Set<N> n1, N n2) {
			for (N n : n1)
				removeElementaryCorrespondence(n, n2);
		}
		
		public void removeComplexCorrespondence(N n1, Set<N> n2) {
			for (N n : n2)
				removeElementaryCorrespondence(n1, n);
		}
		
		public void removeComplexCorrespondence(Set<N> n1, Set<N> n2) {
			for (N n : n1)
				removeComplexCorrespondence(n,n2);
		}
		
		public M getFirstModel() {
			return this.model1;
		}

		public M getSecondModel() {
			return this.model2;
		}

		public Collection<N> getAlignedEntitiesOfFirstModel() {
			return new HashSet<N>(this.correspondenceRelation.keySet());
		}

		public Collection<N> getAlignedEntitiesOfSecondModel() {
			return new HashSet<N>(this.reversedCorrespondenceRelation.keySet());
		}

		public Collection<N> getCorrespondingEntitiesForEntityOfFirstModel(N n) {
			return new HashSet<N>(this.correspondenceRelation.get(n));
		}
		
		public Collection<N> getCorrespondingEntitiesForEntityOfSecondModel(N n) {
			return new HashSet<N>(this.reversedCorrespondenceRelation.get(n));
		}
		
		public boolean isOverlapping(){
			for (N n1 : this.correspondenceRelation.keySet()) {
				for (N n2 : this.correspondenceRelation.keySet()) {
					/* 
					 * not using apache java collections to avoid 
					 * yet another dependency
					 */
					boolean containsAny = false;
					for (N c1 : this.correspondenceRelation.get(n1))
						containsAny |= this.correspondenceRelation.get(n2).contains(c1);
					
					if (containsAny)
						if (!this.correspondenceRelation.get(n1).containsAll(
								this.correspondenceRelation.get(n2)))
							return true;
				}
			}
			return false;
		}
		
		public boolean isLeftTotal() {
			return (this.correspondenceRelation.keySet().containsAll(this.model1.getEntities()));
		}
		
		public boolean isRightTotal() {
			return (this.reversedCorrespondenceRelation.keySet().containsAll(this.model2.getEntities()));
		}
		
		public boolean isFunctional() {
			for (N n : this.correspondenceRelation.keySet())
				if (this.correspondenceRelation.get(n).size() > 1)
					return false;
			
			return true;
		}
		
		public boolean isInjective() {
			for (N n : this.reversedCorrespondenceRelation.keySet())
				if (this.reversedCorrespondenceRelation.get(n).size() > 1)
					return false;
			
			return true;
		}
		
		/**
		 * Normalise a label for comparison in the course of alignment initialisation:
		 * <ul>
		 * <li>set all characters in lower case</li>
		 * <li>trim leading and trailing whitespaces</li>
		 * </ul>
		 * 
		 * @param s, the label that shall be normalised
		 * @return the normalised label
		 */
		public static String normaliseLabel(String s) {
			String result = s.toLowerCase();
			result = result.trim();
			return result;
		}
		
		/**
		 * Init the alignment with correspondences between entities that
		 * carry equal labels.
		 */
		public void initCorrespondenceRelation() {
			for (N v1 : getFirstModel().getEntities()) {
				for (N v2 : getSecondModel().getEntities()) {
					
					String s1 = normaliseLabel(v1.getLabel());
					String s2 = normaliseLabel(v2.getLabel());
					
					if (s1.equals(s2) && !s1.equals(""))
						addElementaryCorrespondence(v1,v2);
				}
			}
		}
		
		/**
		 * Returns the given alignment as a set of correspondences. This method is 
		 * provided for convenience only since the actual alignment is stored in the 
		 * correspondence relation. 
		 * 
		 * The result is well-defined only in case the correspondence relation is 
		 * non-overlapping. 
		 * 
		 * @return a set of correspondences, i.e., pairs of sets of entities
		 */
		public Set<Correspondence<N>> getAlignmentAsCorrespondences() {
			Set<Correspondence<N>> result = new HashSet<Correspondence<N>>();
			
			// not well-defined if the correspondences are overlapping
			assert(!this.isOverlapping());
			
			Set<N> checked = new HashSet<N>();
			for (N n1 : this.correspondenceRelation.keySet()) {
				if (checked.contains(n1))
					continue;
				
				checked.add(n1);
				Correspondence<N> c = new Correspondence<N>();
				c.firstSet.add(n1);
				c.secondSet.addAll(this.correspondenceRelation.get(n1));
				for (N n2 : this.correspondenceRelation.get(n1)) {
					c.firstSet.addAll(this.reversedCorrespondenceRelation.get(n2));
					checked.addAll(this.reversedCorrespondenceRelation.get(n2));
				}
				result.add(c);
			}			
			return result;
		}
			
}
