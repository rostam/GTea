package graphtea.extensions.reports.numberings;

import graphtea.graph.graph.Edge;
import graphtea.graph.graph.GraphModel;
import graphtea.graph.graph.Vertex;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class StNumberingTest {
    @Test
    public void preOrderPathTest() {
        GraphModel gm = new GraphModel();
        for (int i = 0; i < 1000; i++) {
            Vertex v = new Vertex();
            gm.insertVertex(v);
        }
        for (int i = 1; i < 1000; i++) {
            gm.addEdge(new Edge(gm.getVertex(i-1), gm.getVertex(i)));
        }
        PreOrderNumbering preo = new PreOrderNumbering();
        HashMap<Vertex, Integer> mapping = preo.preOrderNumbering(gm, gm.getVertex(0));
        for (int i = 1; i < gm.numOfVertices(); i++) {
            assertTrue(mapping.get(gm.getVertex(i-1)) + 1 == mapping.get(gm.getVertex(i)));
        }
    }

}