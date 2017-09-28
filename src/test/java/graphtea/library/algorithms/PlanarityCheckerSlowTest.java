package graphtea.library.algorithms;

import graphtea.extensions.generators.*;
import graphtea.graph.graph.Edge;
import graphtea.graph.graph.GraphModel;
import graphtea.graph.graph.Vertex;
import graphtea.library.algorithms.planarity.CustomEdge;
import graphtea.library.algorithms.planarity.CustomGraph;
import graphtea.library.algorithms.planarity.CustomVertex;
import graphtea.library.algorithms.planarity.PlanarityCheckerSlow;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class PlanarityCheckerSlowTest {

    PlanarityCheckerSlow pc = new PlanarityCheckerSlow();

    /*********************
     * Helper Functions
     *********************/

    /**
     * Creates a graph for testing purposes
     *
     * @Returns a CustomGraph object */
    private CustomGraph CreateCustomGraph(GraphModel gm){
        ArrayList<CustomVertex> vertices = new ArrayList<>(gm.getVerticesCount());
        for (Vertex v : gm) {
            vertices.add(new CustomVertex(v.getId()));
        }

        ArrayList<CustomEdge> edges = new ArrayList<>(gm.getEdgesCount());
        for (Edge e : gm.edges()) {
            edges.add(new CustomEdge(vertices.get(e.source.getId()), vertices.get(e.target.getId())));
        }

        return new CustomGraph(edges, vertices);
    }

    /**
     * Checks if the edge is subdivided in a certain direction
     *
     * @Returns true if subdivision worked, false otherwise */
    private boolean checkSubdivided(CustomGraph g, CustomVertex src, CustomVertex trg, CustomVertex addedVertex){
        for(CustomEdge e : g.getEdgeList()) {
            if(e.source == addedVertex && e.target == src){
                for(CustomEdge eb : g.getEdgeList()) {
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
        PlanarityCheckerSlow pc = new PlanarityCheckerSlow();

        CustomGraph graph = CreateCustomGraph(gm);
        CustomEdge edge = graph.getEdgeList().get(0);

        assertTrue(graph.getEdgeList().contains(edge));
        CustomGraph g = pc.removeEdge(edge, graph);
        assertFalse(g.getEdgeList().contains(edge));
    }

    @Test
    public void contractEdgeTest(){
        CompleteGraphGenerator gen = new CompleteGraphGenerator();
        GraphModel gm = gen.generateCompleteGraph(10);
        PlanarityCheckerSlow pc = new PlanarityCheckerSlow();

        CustomGraph graph = CreateCustomGraph(gm);

        CustomEdge edge = graph.getEdgeList().get(0);
        CustomVertex src = edge.source;
        CustomVertex trg = edge.target;

        ArrayList<CustomEdge> srcNeighbours = new ArrayList<>();

        for(CustomEdge e : graph.getEdgeList()){
            if( (e.source == src || e.target == src) && (e.target != trg || e.source != trg) )
                srcNeighbours.add(e);
        }

        assertTrue(graph.getEdgeList().contains(edge)); // edge exists
        CustomGraph g = pc.contractEdge(edge, graph); // Run function we are testing
        assertFalse(g.getEdgeList().contains(edge)); // edge deleted
        for(CustomEdge e : srcNeighbours){ // check if neighbours switched from being connected to src to trg
            assertTrue(e.source == trg || e.target == trg);
        }

    }

    @Test
    public void subdivideEdgeTest(){
        CompleteGraphGenerator gen = new CompleteGraphGenerator();
        GraphModel gm = gen.generateCompleteGraph(10);
        PlanarityCheckerSlow pc = new PlanarityCheckerSlow();
        CustomGraph graph = CreateCustomGraph(gm);

        CustomEdge edge = graph.getEdgeList().get(0);
        CustomVertex src = edge.source;
        CustomVertex trg = edge.target;

        assertTrue(graph.getEdgeList().contains(edge)); // edge exists
        CustomGraph g = pc.subdivideEdge(edge, graph); // Run function we are testing
        assertFalse(g.getEdgeList().contains(edge)); // edge deleted
        assertTrue(g.getVerticesList().contains(src));
        assertTrue(g.getVerticesList().contains(trg));


        CustomVertex addedVertex = g.getVerticesList().get(g.getVerticesList().size()-1);
        assertNotNull(addedVertex);


        if(checkSubdivided(g, src, trg, addedVertex)){
            // Checks: src -> u -> trg
            assertTrue(true);
        } else if (checkSubdivided(g, trg, src, addedVertex)){
            // Checks: trg -> u -> src
            assertTrue(true);
        } else {
            // Failed
            assertTrue(false);
        }

    }


    @Test
    public void removeVertexTest(){
        CompleteGraphGenerator gen = new CompleteGraphGenerator();
        GraphModel gm = gen.generateCompleteGraph(10);
        PlanarityCheckerSlow pc = new PlanarityCheckerSlow();
        CustomGraph graph = CreateCustomGraph(gm);

        CustomVertex v = graph.getVerticesList().get(5);
        assertTrue(graph.getVerticesList().contains(v));
        CustomGraph g = pc.removeVertex(v, graph); // Run function we are testing
        assertFalse(g.getVerticesList().contains(v));
    }

    @Test
    public void isNotPlanarK5Test(){
        CompleteGraphGenerator gen = new CompleteGraphGenerator();
        GraphModel gm = gen.generateCompleteGraph(5);
        PlanarityCheckerSlow pc = new PlanarityCheckerSlow();
        CustomGraph graph = CreateCustomGraph(gm);

        assertTrue(pc.isNotPlanar(graph));
    }

    @Test public void isNotPlanarK33Test(){
        KmnGenerator gen = new KmnGenerator();
        GraphModel gm = gen.generateGraph();
        PlanarityCheckerSlow pc = new PlanarityCheckerSlow();
        CustomGraph graph = CreateCustomGraph(gm);

        assertTrue(pc.isNotPlanar(graph));
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
        PlanarityCheckerSlow pc = new PlanarityCheckerSlow();
        CustomGraph graph = CreateCustomGraph(gm);

        assertFalse(pc.isPlanar(graph));
    }

    @Test
    public void k33Test(){
        KmnGenerator gen = new KmnGenerator();
        GraphModel gm = gen.generateGraph();
        PlanarityCheckerSlow pc = new PlanarityCheckerSlow();
        CustomGraph graph = CreateCustomGraph(gm);

        assertFalse(pc.isPlanar(graph));
    }

    /**
     * */
    @Test
    public void kTest(){
        CompleteGraphGenerator gen = new CompleteGraphGenerator();
        GraphModel gm = gen.generateCompleteGraph(12);
        PlanarityCheckerSlow pc = new PlanarityCheckerSlow();
        CustomGraph graph = CreateCustomGraph(gm);
        assertFalse( pc.isPlanar(graph) );
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
                PlanarityCheckerSlow pc = new PlanarityCheckerSlow();
                CustomGraph graph = CreateCustomGraph(gm);
                assertTrue( pc.isPlanar(graph) );
            }
            else {
                PlanarityCheckerSlow pc = new PlanarityCheckerSlow();
                CustomGraph graph = CreateCustomGraph(gm);
                assertFalse( pc.isPlanar(graph) );
            }
        }
    }

    @Test
    public void CircleGraphTest() {
        CircleGenerator gen = new CircleGenerator();
        GraphModel gm = gen.generateCircle(10);
        CustomGraph graph = CreateCustomGraph(gm);
        assertTrue( pc.isPlanar(graph) );

    }

    @Test
    public void CircleGraphMutltipleTest() {
        CircleGenerator gen = new CircleGenerator();
        for (int i = 1; i < 13; i++) {
            System.out.println("Testing for n = " + i);

            GraphModel gm = gen.generateCircle(i);
            PlanarityCheckerSlow pc = new PlanarityCheckerSlow();
            CustomGraph graph = CreateCustomGraph(gm);
            assertTrue( pc.isPlanar(graph) );
        }
    }

    @Test
    public void BananaTreeGraphTest() {
        GearGenerator gen = new GearGenerator();
        for (int i = 1; i < 5; i++) {
            System.out.println("Testing for n = " + i);
            GraphModel gm = gen.generateGear(i);
            PlanarityCheckerSlow pc = new PlanarityCheckerSlow();
            CustomGraph graph = CreateCustomGraph(gm);
            assertTrue( pc.isPlanar(graph) );
        }
    }


}