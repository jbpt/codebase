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
package de.hpi.bpt.oryx.erdf;

import org.w3c.dom.Node;

/**
 * Interface to the eRDF object (node or edge)
 * eRDF object is <name,value> pair collection
 * 
 * @author Artem Polyvyanyy
 */
public interface IERDFObject {

	/**
	 * Get eRDF object property 
	 * @param name Property name
	 * @return Property value, <code>null</code> if value for the property does not exist
	 */
	public String getProperty(String name);
	
	/**
	 * Set eRDF object property
	 * @param name Property name
	 * @param value New property value
	 * @return Old property value, <code>null</code> if value was not previously set
	 */
	public String setProperty(String name, String value);
	
	/**
	 * Parse eRDF object form eRDF DOM Node
	 * @param erdfNode Object eRDF DOM Node
	 */
	public void parseERDF(Node erdfNode);

	/**
	 * Get eRDF object serialization DOM Node
	 * @return eRDF serialization string of the model
	 */
	public Node serializeERDF();
}
