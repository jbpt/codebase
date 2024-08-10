package org.jbpt.pm.gen.bootstrap.dfg;

import java.util.List;

public class GraphData {
    private List<EdgeData> edges;
    private List<NodeData> nodes;

    public List<EdgeData> getEdges() {
        return edges;
    }

    public void setEdges(List<EdgeData> edges) {
        this.edges = edges;
    }

    public List<NodeData> getNodes() {
        return nodes;
    }

    public void setNodes(List<NodeData> nodes) {
        this.nodes = nodes;
    }
}
