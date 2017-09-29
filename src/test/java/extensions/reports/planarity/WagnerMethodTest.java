package extensions.reports.planarity;

import graphtea.extensions.generators.CircleGenerator;
import graphtea.extensions.generators.CompleteGraphGenerator;
import graphtea.extensions.generators.GearGenerator;
import graphtea.extensions.generators.KmnGenerator;
import graphtea.extensions.reports.planarity.Planarity;
import graphtea.extensions.reports.planarity.WagnerMethod;
import graphtea.graph.graph.Edge;
import graphtea.graph.graph.GraphModel;
import graphtea.graph.graph.Vertex;
import graphtea.library.algorithms.planarity.CustomEdge;
import graphtea.library.algorithms.planarity.CustomGraph;
import graphtea.library.algorithms.planarity.CustomVertex;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class WagnerMethodTest {

    WagnerMethod pc = new WagnerMethod();

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

        Edge edge = gm.getEdges().iterator().next();
        Vertex src = edge.source;
        Vertex trg = edge.target;

        ArrayList<Edge> srcNeighbours = new ArrayList<>();

        for(Edge e : gm.edges()){
            if( (e.source == src || e.target == src) && (e.target != trg || e.source != trg) )
                srcNeighbours.add(e);
        }

        assertTrue(gm.getEdges(edge.source, edge.target).contains(edge)); // edge exists
        GraphModel g = pc.contractEdge(edge, gm); // Run function we are testing
        assertFalse(g.getEdges(edge.source, edge.target).contains(edge)); // edge deleted
        for(Edge e : srcNeighbours){ // check if neighbours switched from being connected to src to trg
            assertTrue(e.source == trg || e.target == trg);
        }

    }

    @Test
    public void removeVertexTest(){
        CompleteGraphGenerator gen = new CompleteGraphGenerator();
        GraphModel gm = gen.generateCompleteGraph(10);

        Vertex v = gm.getVertexArray()[5];
        assertTrue(gm.containsVertex(v));
        GraphModel g = pc.removeVertex(v, gm); // Run function we are testing
        assertFalse(g.containsVertex(v));
    }

    @Test
    public void isNotPlanarK5Test(){
        CompleteGraphGenerator gen = new CompleteGraphGenerator();
        GraphModel gm = gen.generateCompleteGraph(5);
        assertTrue(pc.isNotPlanar(gm));
    }

    @Test public void isNotPlanarK33Test(){
        KmnGenerator gen = new KmnGenerator();
        GraphModel gm = gen.generateGraph();
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
        assertFalse(pc.isPlanar(gm));
    }

    @Test
    public void k33Test(){
        KmnGenerator gen = new KmnGenerator();
        GraphModel gm = gen.generateGraph();
        assertFalse(pc.isPlanar(gm));
    }

    /**
     * */
    @Test
    public void kTest(){
        CompleteGraphGenerator gen = new CompleteGraphGenerator();
        GraphModel gm = gen.generateCompleteGraph(6);
        assertFalse( pc.isPlanar(gm));
    }

    /* // Runs too slow
    @Test
    public void TreeTest(){
        TreeGenerator tree = new TreeGenerator();
        GraphModel gm = tree.generateGraph();
        PlanarityCheckerSlow pc = new PlanarityCheckerSlow();
        CustomGraph graph = CreateCustomGraph(gm);
        assertTrue(pc.isPlanar(graph));
    }*/

    @Test
    public void CompleteGraphMultipleTest() {
        CompleteGraphGenerator gen = new CompleteGraphGenerator();
        for (int i = 1; i < 12; i++) {
            GraphModel gm = gen.generateCompleteGraph(i);
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
        GraphModel gm = gen.generateCircle(10);
        assertTrue( pc.isPlanar(gm));

    }

    @Test
    public void CircleGraphMutltipleTest() {
        CircleGenerator gen = new CircleGenerator();
        for (int i = 1; i < 13; i++) {
            System.out.println("Testing for n = " + i);

            GraphModel gm = gen.generateCircle(i);
            assertTrue( pc.isPlanar(gm));
        }
    }

    @Test
    public void BananaTreeGraphTest() {
        GearGenerator gen = new GearGenerator();
        for (int i = 1; i < 5; i++) {
            System.out.println("Testing for n = " + i);
            GraphModel gm = gen.generateGear(i);
            assertTrue( pc.isPlanar(gm));
        }
    }


}