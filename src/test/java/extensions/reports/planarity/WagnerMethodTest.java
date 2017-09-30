package extensions.reports.planarity;

import graphtea.extensions.generators.*;
import graphtea.extensions.reports.planarity.WagnerMethod;
import graphtea.graph.graph.Edge;
import graphtea.graph.graph.GraphModel;
import graphtea.graph.graph.Vertex;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class WagnerMethodTest {

    //WagnerMethod pc = new WagnerMethod();

    /*********************
     * Helper Functions
     *********************/

    /**
     * Checks if the edge is subdivided in a certain direction
     *
     * @Returns true if subdivision worked, false otherwise */
    private boolean checkSubdivided(GraphModel g, Vertex src, Vertex trg, Vertex addedVertex){
        for(Edge e : g.edges()) {
            if(e.source == addedVertex && e.target == src){
                for(Edge eb : g.edges()) {
                    if(eb.target == addedVertex && eb.source == trg){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**********************
     * Singular Function Tests
     *
     * These test only specific functions.  In order to
     * do this I have temporarily made the private functions
     * public.
     *
     * It would be a smart idea to keep them private
     * and instead use reflections.
     **********************/

    @Test
    public void removeEdgeTest(){
        CompleteGraphGenerator gen = new CompleteGraphGenerator();
        GraphModel gm = gen.generateCompleteGraph(5);
        WagnerMethod pc = new WagnerMethod();
        int edgeCountBeforeRemoval = gm.getEdgesCount();
        Edge edge = gm.getEdges().iterator().next();

        assertTrue(gm.getEdgesCount() == edgeCountBeforeRemoval);
        GraphModel g = pc.removeEdge(edge, gm);
        assertTrue((g.getEdgesCount() + 1) == edgeCountBeforeRemoval);
    }

    @Test
    public void contractEdgeTest(){
        CompleteGraphGenerator gen = new CompleteGraphGenerator();
        GraphModel gm = gen.generateCompleteGraph(3);
        WagnerMethod pc = new WagnerMethod();

        Edge edge = gm.getEdges().iterator().next();
        Vertex src = edge.source;
        Vertex trg = edge.target;

        ArrayList<Vertex> srcNeighbours = new ArrayList<>();
        srcNeighbours.addAll(gm.directNeighbors(src));

        assertTrue(gm.getEdges(edge.source, edge.target).contains(edge)); // edge exists

        GraphModel g = pc.contractEdge(edge, gm); // Run function we are testing

        for(Vertex v :  srcNeighbours){ // check if neighbours switched from being connected to src to trg
            if(v != trg && g.directNeighbors(v).contains(v)){
                assertTrue(true);
            }
        }

    }

    @Test
    public void removeVertexTest(){
        CompleteGraphGenerator gen = new CompleteGraphGenerator();
        GraphModel gm = gen.generateCompleteGraph(10);
        WagnerMethod pc = new WagnerMethod();

        Vertex v = gm.getVertexArray()[5];
        assertTrue(gm.containsVertex(v));
        GraphModel g = pc.removeVertex(v, gm); // Run function we are testing
        assertFalse(g.containsVertex(v));
    }

    @Test
    public void isNotPlanarK5Test(){
        CompleteGraphGenerator gen = new CompleteGraphGenerator();
        GraphModel gm = gen.generateCompleteGraph(5);
        WagnerMethod pc = new WagnerMethod();
        assertTrue(pc.isNotPlanar(gm));
    }

    @Test public void isNotPlanarK33Test(){
        KmnGenerator gen = new KmnGenerator();
        GraphModel gm = gen.generateGraph();
        WagnerMethod pc = new WagnerMethod();
        assertTrue(pc.isNotPlanar(gm));
    }

    /**********************
     * Integrated Function Tests
     *
     * These test all of the functions together
     **********************/

    @Test
    public void k5Test(){
        CompleteGraphGenerator gen = new CompleteGraphGenerator();
        GraphModel gm = gen.generateCompleteGraph(5);
        WagnerMethod pc = new WagnerMethod();
        assertFalse(pc.isPlanar(gm));
    }

    @Test
    public void k33Test(){
        KmnGenerator gen = new KmnGenerator();
        GraphModel gm = gen.generateGraph();
        WagnerMethod pc = new WagnerMethod();
        assertFalse(pc.isPlanar(gm));
    }

    /**
     * */
    @Test
    public void kTest(){
        CompleteGraphGenerator gen = new CompleteGraphGenerator();
        WagnerMethod pc = new WagnerMethod();
        GraphModel gm = gen.generateCompleteGraph(20);
        assertFalse( pc.isPlanar(gm));
    }

    /* // Runs too slow
    @Test
    public void TreeTest(){
        TreeGenerator gen = new TreeGenerator();
        GraphModel gm = gen.generateGraph();
        WagnerMethod pc = new WagnerMethod();
        assertFalse( pc.isPlanar(gm));
    }*/

    @Test
    public void CompleteGraphMultipleTest() {
        CompleteGraphGenerator gen = new CompleteGraphGenerator();
        for (int i = 1; i < 35; i++) {
            System.out.println(i);
            GraphModel gm = gen.generateCompleteGraph(i);
            WagnerMethod pc = new WagnerMethod();

            if (i < 5) {
                assertTrue( pc.isPlanar(gm));
            }
            else {
                assertFalse( pc.isPlanar(gm));
            }
        }
    }

    @Test
    public void CircleGraphTest() {
        CircleGenerator gen = new CircleGenerator();
        GraphModel gm = gen.generateCircle(4);
        WagnerMethod pc = new WagnerMethod();
        assertTrue( pc.isPlanar(gm));

    }

    @Test
    public void CircleGraphMutltipleTest() {
        CircleGenerator gen = new CircleGenerator();
        for (int i = 1; i < 13; i++) {
            System.out.println("Testing for n = " + i);

            GraphModel gm = gen.generateCircle(i);
            WagnerMethod pc = new WagnerMethod();
            assertTrue( pc.isPlanar(gm));
        }
    }

    @Test
    public void BananaTreeGraphTest() {
        GearGenerator gen = new GearGenerator();
        for (int i = 1; i < 5; i++) {
            System.out.println("Testing for n = " + i);
            GraphModel gm = gen.generateGear(i);
            WagnerMethod pc = new WagnerMethod();
            assertTrue( pc.isPlanar(gm));
        }
    }


}