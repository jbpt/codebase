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
package de.hpi.bpt.process.epc;

import de.hpi.bpt.graph.abs.IDirectedEdge;


/**
 * The connection in a process model which is the part of the control flow.
 * 
 * @author Artem Polyvyanyy
 */
public interface IControlFlow<V extends IFlowObject> extends IDirectedEdge<V> {

	/**
	 * Get the probability of the transition from the flow object which is the source of this connection to the target flow object. 
	 * 
	 * @return transition probability value.
	 */
	double getProbability();

	/**
	 * Set the probability of the transition from the flow object which is the source of this connection to the target flow object.
	 * 
	 * @param p the transition probability value.
	 */
	void setProbability(double p);
}