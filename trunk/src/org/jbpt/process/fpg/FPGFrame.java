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
