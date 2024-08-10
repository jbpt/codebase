package org.jbpt.pm.gen.bootstrap.dfg;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import org.jbpt.graph.DirectedGraph;
import org.jbpt.hypergraph.abs.Vertex;
import org.jbpt.petri.NetSystem;
import org.jbpt.petri.io.PNMLSerializer;
import org.jbpt.pm.gen.bootstrap.ProcessDiscovery;
import org.jbpt.pm.models.FDAGraph;
import org.jbpt.throwable.SerializationException;
import org.jbpt.utils.IOUtils;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DfgUtils {

    public static DirectedGraph parseJsonToDfg(String filePath) {
        GraphData graphData = parseJsonFile(filePath);
        DirectedGraph directedGraph = new DirectedGraph();
        Map<Integer, Vertex> vertexMap = new HashMap<>();

        for (NodeData nodeData : graphData.getNodes()) {
            Vertex vertex = new Vertex(nodeData.getLabel());
            directedGraph.addVertex(vertex);
            vertexMap.put(nodeData.getId(), vertex);
        }

        for (EdgeData edgeData : graphData.getEdges()) {
            Vertex source = vertexMap.get(edgeData.getSource());
            Vertex target = vertexMap.get(edgeData.getTarget());
            directedGraph.addEdge(source, target);
        }

        return directedGraph;
    }

    private static GraphData parseJsonFile(String filePath) throws JsonParseException {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, GraphData.class);
        } catch (JsonSyntaxException | IOException e) {
            throw new JsonParseException("Invalid JSON format or unable to read file: " + filePath, e);
        }
    }

    public static void dfgToPnml(DirectedGraph directedGraph, String dfgFilePath) throws SerializationException {
        NetSystem M = ProcessDiscovery.graphToNetSystem(directedGraph);
        String mString = PNMLSerializer.serializePetriNet(M);
        IOUtils.toFile(dfgFilePath, mString);
    }

    public static void fdagToPnml(FDAGraph fdaGraph, String fdagFilePath) throws SerializationException {
        NetSystem M = ProcessDiscovery.fdagToNetSystem(fdaGraph);
        String mString = PNMLSerializer.serializePetriNet(M);
        IOUtils.toFile(fdagFilePath, mString);
    }

}
