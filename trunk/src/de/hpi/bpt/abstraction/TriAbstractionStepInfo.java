/**
 * Copyright (c) 2008 Artem Polyvyanyy
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.hpi.bpt.abstraction;

import java.util.ArrayList;
import java.util.Collection;

import de.hpi.bpt.graph.algo.tctree.TCType;
import de.hpi.bpt.hypergraph.abs.IGObject;

/**
 * 
 * @author Artem Polyvyanyy
 *
 */
public class TriAbstractionStepInfo {
	protected Collection<IGObject> fragment = new ArrayList<IGObject>();
	
	public void setFragment(Collection<IGObject> fragment) {
		this.fragment = fragment;
	}

	protected IGObject entry = null;
	
	public void setEntry(IGObject entry) {
		this.entry = entry;
	}

	protected IGObject exit = null;
	
	public void setExit(IGObject exit) {
		this.exit = exit;
	}

	protected TCType type = TCType.UNDEFINED;
	
	
	public void setType(TCType type) {
		this.type = type;
	}

	public Collection<IGObject> getFragment() {
		return fragment;
	}
	
	public IGObject getEntry() {
		return entry;
	}
	
	public IGObject getExit() {
		return exit;
	}
	
	public TCType getType() {
		return type;
	}
	
	public void addObjects(Collection<IGObject> objs) {
		this.fragment.addAll(objs);
	}
	
	public void removeObjects(Collection<IGObject> objs) {
		this.fragment.removeAll(objs);
	}
	
}
