package de.hpi.bpt.process.petri.bp.sim;

import java.util.HashMap;
import java.util.Map;

import de.hpi.bpt.process.petri.bp.RelSet;
import de.hpi.bpt.process.petri.bp.RelSetType;

/**
 * 
 * Cache for relation sizes of relation sets. 
 * Cache may be emptied by calling <code>invalidateCache()</code>.
 * 
 * @author matthias.weidlich
 *
 */
public class RelSetSizeCache {

	private static RelSetSizeCache eInstance;
	
	public static RelSetSizeCache getInstance() {
		if (eInstance == null)
			eInstance  = new RelSetSizeCache();
		return eInstance;
	}
	
	private RelSetSizeCache() {}

	@SuppressWarnings("rawtypes")
	private Map<RelSet,Map<RelSetType,Integer>> relationChache = new HashMap<RelSet,Map<RelSetType,Integer>>();

	
	/**
	 * Resets the internal cache that stores the sizes of relations for relation sets.
	 */
	@SuppressWarnings("rawtypes")
	public void invalidateCache() {
		relationChache = new HashMap<RelSet,Map<RelSetType,Integer>>();		
	}

	@SuppressWarnings("rawtypes")
	public boolean containsEntry(RelSet rs, RelSetType type) {
		if (!relationChache.containsKey(rs))
			return false;
		
		return (relationChache.get(rs).containsKey(type));
	}
	
	@SuppressWarnings("rawtypes")
	public void addEntry(RelSet rs, RelSetType type, int size) {
		if (!relationChache.containsKey(rs))
			relationChache.put(rs, new HashMap<RelSetType, Integer>());
		
		relationChache.get(rs).put(type,size);
	}


	@SuppressWarnings("rawtypes")
	public int getRelationSize(RelSet rs, RelSetType type) {
		if (!containsEntry(rs,type))
			return -1;

		return relationChache.get(rs).get(type);
	}

	
}
