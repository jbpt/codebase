package org.jbpt.algo;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Generate combinations systematically.
 * 
 * @author Michael Gilleland
 * @author Artem Polyvyanyy
 *
 * @param <X> Template for elements to use when generating combinations.
 */
public class CombinationGenerator<X> {

	private X[] arr;
	private int[] a;
	private int n;
	private int r;
	private BigInteger numLeft;
	private BigInteger total;
	
	
	/**
	 * Constructor.
	 * 
	 * @param objs Collection of objects to generate combinations.
	 * @param r Size of combinations to generate.
	 */
	@SuppressWarnings("unchecked")
	public CombinationGenerator (Collection<X> objs, int r) {
		if (r > objs.size() || objs.size() < 1 || r < 1)
			throw new IllegalArgumentException ();
	 
		this.n		= objs.size();
		this.r		= r;
		this.arr	= (X[]) objs.toArray(); 
		this.a		= new int[r];
		BigInteger nFact = getFactorial(n);
		BigInteger rFact = getFactorial(r);
		BigInteger nminusrFact = getFactorial(n - r);
		this.total = nFact.divide (rFact.multiply (nminusrFact));
		reset();
	}
	
	/**
	 * Reset the generator.
	 */
	public void reset () {
		for (int i = 0; i < this.a.length; i++) a[i] = i;
		
		this.numLeft = new BigInteger (this.total.toString ());
	}
	
	/**
	 * Return number of combinations not yet generated.
	 */
	public BigInteger getNumLeft () {
		return this.numLeft;
	}
	
	/**
	 * Are there more combinations?
	 */
	public boolean hasMore () {
		return this.numLeft.compareTo(BigInteger.ZERO) == 1;
	}
	
	/**
	 * Return total number of combinations.
	 */
	public BigInteger getTotal () {
		return this.total;
	}
	
	/**
	 * Compute factorial
	 */
	private static BigInteger getFactorial (int n) {
		BigInteger fact = BigInteger.ONE;
		for (int i = n; i > 1; i--)
			fact = fact.multiply (new BigInteger (Integer.toString (i)));

		return fact;
	}
	
	/**
	 * Generate next combination (algorithm from Rosen p. 286).
	 */
	public Collection<X> getNextCombination () {
		if (this.numLeft.equals(total)) {
			this.numLeft = this.numLeft.subtract (BigInteger.ONE);
			return this.indicesToArray(a);
		}
		
		int i = r - 1;
		while (a[i] == n - r + i) i--;
		a[i] = a[i] + 1;
		for (int j = i + 1; j < r; j++) a[j] = a[i] + j - i;
		
		numLeft = numLeft.subtract(BigInteger.ONE);
		return this.indicesToArray(a);
	}
	
	private Collection<X> indicesToArray(int[] a) {
		Collection<X> result = new ArrayList<X>();
		for (int i = 0; i < this.r; i++) {
			result.add(arr[a[i]]);
		}
		
		return result;
	}
}
