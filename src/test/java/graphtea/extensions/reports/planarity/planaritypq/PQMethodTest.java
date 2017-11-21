package graphtea.extensions.reports.planarity.planaritypq;

import graphtea.extensions.generators.KmnGenerator;
import graphtea.extensions.reports.planarity.WagnerMethod;
import graphtea.graph.graph.Edge;
import graphtea.graph.graph.GraphModel;
import graphtea.graph.graph.Vertex;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class PQMethodTest {

    /** This is the example from the paper */
    @Test
    public void biconnectedGraph(){
        GraphModel gm = new GraphModel();

        Vertex v1 = new Vertex();
        Vertex v2 = new Vertex();
        Vertex v3 = new Vertex();
        Vertex v4 = new Vertex();
        Vertex v5 = new Vertex();

        Edge e1 = new Edge(v1, v2);
        Edge e2 = new Edge(v1, v3);
        Edge e3 = new Edge(v1, v5);
        Edge e4 = new Edge(v2, v3);
        Edge e5 = new Edge(v2, v4);
        Edge e6 = new Edge(v2, v5);
        Edge e7 = new Edge(v3, v4);
        Edge e8 = new Edge(v3, v5);
        Edge e9 = new Edge(v4, v5);

        gm.addVertex(v1);
        gm.addVertex(v2);
        gm.addVertex(v3);
        gm.addVertex(v4);
        gm.addVertex(v5);

        gm.addEdge(e1);
        gm.addEdge(e2);
        gm.addEdge(e3);
        gm.addEdge(e4);
        gm.addEdge(e5);
        gm.addEdge(e6);
        gm.addEdge(e7);
        gm.addEdge(e8);
        gm.addEdge(e9);

        PQMethod pc = new PQMethod();
        assertTrue(pc.isPlanar(gm));

    }

    @Test
    public void k33Test(){
        KmnGenerator gen = new KmnGenerator();
        GraphModel gm = gen.generateGraph();
        PQMethod pc = new PQMethod();
        assertFalse(pc.isPlanar(gm));
    }


}