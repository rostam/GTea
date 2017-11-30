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
    public void preOrderManualTest1() {
        GraphModel gm = new GraphModel(false);

        gm.addVertex(new Vertex());
        gm.addVertex(new Vertex());
        gm.addVertex(new Vertex());
        gm.addVertex(new Vertex());
        gm.addVertex(new Vertex());

        gm.addEdge(new Edge(gm.getVertex(0), gm.getVertex(1)));
        gm.addEdge(new Edge(gm.getVertex(0), gm.getVertex(3)));
        gm.addEdge(new Edge(gm.getVertex(1), gm.getVertex(2)));
        gm.addEdge(new Edge(gm.getVertex(2), gm.getVertex(3)));
        gm.addEdge(new Edge(gm.getVertex(2), gm.getVertex(4)));
        gm.addEdge(new Edge(gm.getVertex(3), gm.getVertex(4)));

        try {
            StNumbering st = new StNumbering(gm);
            HashMap<Vertex, Integer> mapping = st.preOrderNumbering();
            for (int i = 1; i < gm.numOfVertices(); i++) {
                assertTrue(mapping.get(gm.getVertex(i - 1)) < mapping.get(gm.getVertex(i)));
            }
        }
        catch(NotBiconnectedException e) {
            System.err.println(e);
        }
    }

    @Test
    public void preOrderModifiedK4Test() {
        GraphModel gm = new GraphModel(false);

        gm.addVertex(new Vertex());
        gm.addVertex(new Vertex());
        gm.addVertex(new Vertex());
        gm.addVertex(new Vertex());
        gm.addVertex(new Vertex());

        gm.addEdge(new Edge(gm.getVertex(0), gm.getVertex(1)));
        gm.addEdge(new Edge(gm.getVertex(0), gm.getVertex(2)));
        gm.addEdge(new Edge(gm.getVertex(0), gm.getVertex(3)));
        gm.addEdge(new Edge(gm.getVertex(1), gm.getVertex(2)));
        gm.addEdge(new Edge(gm.getVertex(1), gm.getVertex(3)));
        gm.addEdge(new Edge(gm.getVertex(1), gm.getVertex(4)));
        gm.addEdge(new Edge(gm.getVertex(2), gm.getVertex(3)));
        gm.addEdge(new Edge(gm.getVertex(3), gm.getVertex(4)));

        try {
            StNumbering st = new StNumbering(gm);
            HashMap<Vertex, Integer> mapping = st.preOrderNumbering();
            for (int i = 1; i < gm.numOfVertices(); i++) {
                assertTrue(mapping.get(gm.getVertex(i - 1)) < mapping.get(gm.getVertex(i)));
            }
        }
        catch(NotBiconnectedException e) {
            System.err.println(e);
        }
    }

    @Test
    public void computeLManualTest1() {
        GraphModel gm = new GraphModel(false);

        gm.addVertex(new Vertex());
        gm.addVertex(new Vertex());
        gm.addVertex(new Vertex());
        gm.addVertex(new Vertex());
        gm.addVertex(new Vertex());

        gm.addEdge(new Edge(gm.getVertex(0), gm.getVertex(1)));
        gm.addEdge(new Edge(gm.getVertex(0), gm.getVertex(3)));
        gm.addEdge(new Edge(gm.getVertex(1), gm.getVertex(2)));
        gm.addEdge(new Edge(gm.getVertex(2), gm.getVertex(3)));
        gm.addEdge(new Edge(gm.getVertex(2), gm.getVertex(4)));
        gm.addEdge(new Edge(gm.getVertex(3), gm.getVertex(4)));

        try {
            StNumbering st = new StNumbering(gm);
            HashMap<Vertex, Integer> mapping = st.preOrderNumbering();
            for (Vertex v : gm.vertices()) {
                System.out.println(st.computeL(v));
            }
            assertTrue(st.computeL(gm.getVertex(0)) == mapping.get(gm.getVertex(0)));
            assertTrue(st.computeL(gm.getVertex(1)) == mapping.get(gm.getVertex(0)));
            assertTrue(st.computeL(gm.getVertex(2)) == mapping.get(gm.getVertex(0)));
            assertTrue(st.computeL(gm.getVertex(3)) == mapping.get(gm.getVertex(0)));
            assertTrue(st.computeL(gm.getVertex(4)) == mapping.get(gm.getVertex(2)));
        }
        catch(NotBiconnectedException e) {
            System.err.println(e);
        }
    }

    @Test
    public void computeLManualTest2() {
        GraphModel gm = new GraphModel(false);

        gm.addVertex(new Vertex());
        gm.addVertex(new Vertex());
        gm.addVertex(new Vertex());
        gm.addVertex(new Vertex());
        gm.addVertex(new Vertex());
        gm.addVertex(new Vertex());

        gm.addEdge(new Edge(gm.getVertex(0), gm.getVertex(1)));
        gm.addEdge(new Edge(gm.getVertex(0), gm.getVertex(2)));
        gm.addEdge(new Edge(gm.getVertex(0), gm.getVertex(3)));
        gm.addEdge(new Edge(gm.getVertex(1), gm.getVertex(2)));
        gm.addEdge(new Edge(gm.getVertex(2), gm.getVertex(3)));
        gm.addEdge(new Edge(gm.getVertex(2), gm.getVertex(5)));
        gm.addEdge(new Edge(gm.getVertex(3), gm.getVertex(4)));
        gm.addEdge(new Edge(gm.getVertex(4), gm.getVertex(5)));

        try {
            StNumbering st = new StNumbering(gm);
            HashMap<Vertex, Integer> mapping = st.preOrderNumbering();
            for (Vertex v : gm.vertices()) {
                System.out.println(st.computeL(v));
            }
            assertTrue(st.computeL(gm.getVertex(0)) == mapping.get(gm.getVertex(0)));
            assertTrue(st.computeL(gm.getVertex(1)) == mapping.get(gm.getVertex(0)));
            assertTrue(st.computeL(gm.getVertex(2)) == mapping.get(gm.getVertex(0)));
            assertTrue(st.computeL(gm.getVertex(3)) == mapping.get(gm.getVertex(0)));
            assertTrue(st.computeL(gm.getVertex(4)) == mapping.get(gm.getVertex(2)));
            assertTrue(st.computeL(gm.getVertex(5)) == mapping.get(gm.getVertex(2)));
        } catch (NotBiconnectedException e) {
            System.err.println(e);
        }
    }
}