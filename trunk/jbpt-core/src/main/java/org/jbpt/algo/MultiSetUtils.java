package org.jbpt.algo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Operations on multi-sets.
 * 
 * @author Artem Polyvyanyy
 */
public class MultiSetUtils {
	
	/**
	 * Check if multi-set is a set (contains no duplicates).
	 * 
	 * @param ms Multi-set.
	 * @return <tt>true</tt> if 'ms' is a set; otherwise <tt>false</tt>.
	 */
	public static boolean isSet(Collection<?> ms) {
		Set<?> set = new HashSet<Object>(ms);
		return ms.size()==set.size();
	}
	
	/**
	 * Check if one multi-set contains all elements of the other multi-set.
	 * 
	 * @param ms1 Multi-set. 
	 * @param ms2 Multi-set.
	 * @return <tt>true</tt> if 'ms1' contains all elements of 'ms2'; otherwise <tt>false</tt>.
	 */
	public static boolean containsAll(Collection<?> ms1, Collection<?> ms2) {
		if (ms2.size()>ms1.size()) return false;
		
		Collection<?> tmp = new ArrayList<Object>(ms1);
		
		Iterator<?> i = ms2.iterator();
		while (i.hasNext()) 
			if (!tmp.remove(i.next()))
				return false;
				
		return true;
	}
	
	/**
	 * Check if two multi-sets are exactly the same.
	 * 
	 * @param ms1 Multi-set.
	 * @param ms2 Multi-set.
	 * @return <tt>true</tt> if 'ms1' and 'ms2' are exactly the same; otherwise <tt>false</tt>.
	 */
	public static boolean areEqual(Collection<?> ms1, Collection<?> ms2) {
		if (ms1.size()!=ms2.size()) return false;
		
		Collection<?> tmp = new ArrayList<Object>(ms1);
		Iterator<?> i = ms2.iterator();
		while (i.hasNext()) 
			if (!tmp.remove(i.next()))
				return false;
		
		return tmp.isEmpty();
	}

}
