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

import de.hpi.bpt.hypergraph.abs.IVertex;

/**
 * An element of an EPC diagram which is a node of a graph.
 * 
 * @author Artem Polyvyanyy
 */
public interface INode extends IVertex {

	/**
	 * Get vertex coordinate X
	 * @return x
	 */
	int getX();
	
	/**
	 * Set vertex coordinate X
	 * @param x
	 */
	void setX(int x);
	
	/**
	 * Get vertex coordinate Y
	 * @return y
	 */
	int getY();
	
	/**
	 * Set vertex coordinate Y
	 * @param y
	 */
	void setY(int y);
	
	/**
	 * Get vertex width
	 * @return Width
	 */
	int getWidth();
	
	/**
	 * Set vertex width
	 * @param w Width
	 */
	void setWidth(int w);
	
	/**
	 * Get vertex height
	 * @return Height
	 */
	int getHeight();
	
	/**
	 * Set vertex height
	 * @param h Height
	 */
	void setHeight(int h);
	
	/**
	 * Set vertex location
	 * @param x
	 * @param y
	 */
	void setLocation(int x, int y);
	
	/**
	 * Set vertex size
	 * @param w Width
	 * @param h Height
	 */
	void setSize(int w, int h);
	
	/**
	 * Set vertex layout information
	 * @param x
	 * @param y
	 * @param w Width
	 * @param h Height
	 */
	void setLayout(int x, int y, int w, int h);

}