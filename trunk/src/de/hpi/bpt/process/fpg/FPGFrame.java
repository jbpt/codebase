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
package de.hpi.bpt.process.fpg;

import javax.swing.JFrame;

public class FPGFrame {
	private JFrame frame = new JFrame("Hypergraph");
	
	public FPGFrame() {
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	}
	
	public void show(FPG fpg) {
		/*Hypergraph<String, String> hg = new SetHypergraph<String, String>();
		int z = 0;
		Collection<Edge> es =  fpg.getEdges();
		Iterator<Edge> i = es.iterator();
		while (i.hasNext()) {
			Edge e = i.next();
			
			Collection<Activity> as = e.getVertices();
			Iterator<Activity> j = as.iterator();
			Collection<String> ns = new ArrayList<String>();
			while (j.hasNext()) {
				Activity a = j.next();
				
				hg.addVertex(a.toString());
				
				if (a.isEnabled() || a.getBlackTokens()>0)
					ns.add(a.toString());
			}
			
			if (ns.size()>0)
				hg.addEdge((new Integer(++z)).toString(), ns);
		}

		HypergraphLayout<String, String> l = new HypergraphLayout<String, String>(hg, edu.uci.ics.jung.algorithms.layout.FRLayout.class);
		VisualizationViewer<String, String> v = new VisualizationViewer<String, String>(l, new Dimension(1000, 1000));
		v.setRenderer(new BasicHypergraphRenderer<String, String>());
		
		frame.getContentPane().removeAll();
		frame.getContentPane().add(v);
		frame.pack();
		frame.setVisible(true);*/
	}
}
