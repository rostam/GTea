package graphtea.extensions.reports.numberings;

import graphtea.extensions.algs4.Graph;
import graphtea.extensions.generators.*;
import graphtea.graph.graph.Edge;
import graphtea.graph.graph.GraphModel;
import graphtea.graph.graph.Vertex;
import org.junit.Test;

import java.util.*;

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
                System.out.println(mapping.get(gm.getVertex(i-1)) + " < " + mapping.get(gm.getVertex(i)) +
                        " : " + gm.getVertex(i-1) + ", " + gm.getVertex(i));
                //assertTrue(mapping.get(gm.getVertex(i - 1)) < mapping.get(gm.getVertex(i)));
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

    @Test
    public void Bi_stNumberTest(){
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
            HashMap<Vertex, Integer> stMapping = st.stNumbering();

            stNumberingTestFunction(stMapping, gm);

        } catch (NotBiconnectedException e) {
            System.err.println(e);
        }


    }

    @Test
    public void Bi_stNumberTest2() {
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

        assertTrue(genericStNumberingTest(gm));
    }

    private boolean genericStNumberingTest(GraphModel gm){
        try {
            StNumbering st = new StNumbering(gm);
            HashMap<Vertex, Integer> stMapping = st.stNumbering();

            stNumberingTestFunction(stMapping, gm);
            return true;

        } catch (NotBiconnectedException e) {
            System.err.println(e);
            return false;
        }
    }

    private boolean stNumberingTestFunction(HashMap<Vertex, Integer> stMapping, GraphModel gm){

        if(gm.getVerticesCount() <= 2){
            for (Vertex v : stMapping.keySet()) {
                assertTrue(v.getId() == stMapping.get(v));
            }
            return true;
        }

    int maxId = 0;
        Vertex t = null;
        for(Vertex v : stMapping.keySet()){
            maxId++;
            Vertex _t = null;
            int lowerCount = 0, higherCount = 0;
            for(Vertex n : gm.directNeighbors(v)){
                //if(stMapping.get(n) == stMapping.get(v)-1){
                if(stMapping.get(n) == 0){
                    _t = n;
                    t = n;
                }
                if(stMapping.get(n) < stMapping.get(v)){
                    lowerCount++;
                }
                //else if(stMapping.get(n) == stMapping.get(v)+1){
                else if(stMapping.get(n) > stMapping.get(v)){
                    higherCount++;
                }
                /*else if(stMapping.get(n) == 0){
                    _t = n;
                    t = n;
                }*/
            }

            //System.out.println(gm.getVerticesCount() + " vertex: " + stMapping.get(v));

            assertFalse(lowerCount < 1 && stMapping.get(v) != 0);
            assertFalse(higherCount < 1 && stMapping.get(v) != gm.getVerticesCount()-1);

            //assertFalse(lowerCount < 1 && _t == null && (stMapping.get(v) != 0 || stMapping.get(v) != gm.getVerticesCount()));
            //assertFalse(higherCount < 1 && _t == null && (stMapping.get(v) != 0 || stMapping.get(v) != gm.getVerticesCount()));
            _t = null;
        }

        assertFalse(t == null);
        boolean found = false;
        for(Vertex v : gm.directNeighbors(t)){
            if(stMapping.get(v) == gm.getVerticesCount()-1){
                found = true;
            }
        }
        assertTrue(found);

        return false;
    }


    /** This is the example from the paper */
    @Test
    public void Bi_biconnectedGraph(){
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

        assertTrue(genericStNumberingTest(gm));

    }


    //@Test
    public void Bi_k33stNumbering(){
        GraphModel gm = new GraphModel();

        Vertex v0 = new Vertex();
        Vertex v1 = new Vertex();
        Vertex v2 = new Vertex();
        Vertex v3 = new Vertex();
        Vertex v4 = new Vertex();
        Vertex v5 = new Vertex();

        Edge e0 = new Edge(v0, v1);
        Edge e1 = new Edge(v0, v3);
        Edge e2 = new Edge(v0, v5);

        Edge e3 = new Edge(v2, v1);
        Edge e4 = new Edge(v2, v3);
        Edge e5 = new Edge(v2, v5);

        Edge e6 = new Edge(v4, v1);
        Edge e7 = new Edge(v4, v3);
        Edge e8 = new Edge(v4, v5);

        gm.addVertex(v0);
        gm.addVertex(v1);
        gm.addVertex(v2);
        gm.addVertex(v3);
        gm.addVertex(v4);
        gm.addVertex(v5);

        gm.addEdge(e0);
        gm.addEdge(e1);
        gm.addEdge(e2);
        gm.addEdge(e3);
        gm.addEdge(e4);
        gm.addEdge(e5);
        gm.addEdge(e6);
        gm.addEdge(e7);
        gm.addEdge(e8);

        assertTrue(genericStNumberingTest(gm));
    }

    @Test
    public void Bi_k33Test(){
        KmnGenerator gen = new KmnGenerator();
        GraphModel gm = gen.generateGraph();

        assertTrue(genericStNumberingTest(gm));
    }

    @Test
    public void Bi_k5Test(){
        CompleteGraphGenerator gen = new CompleteGraphGenerator();
        GraphModel gm = gen.generateCompleteGraph(5);

        assertTrue(genericStNumberingTest(gm));

    }

    @Test
    public void Bi_kTest(){
        CompleteGraphGenerator gen = new CompleteGraphGenerator();
        GraphModel gm = gen.generateCompleteGraph(20);

        assertTrue(genericStNumberingTest(gm));

    }


    // Not biconnected
    @Test
    public void NotBi_TreeTest(){
        TreeGenerator gen = new TreeGenerator();
        GraphModel gm = gen.generateGraph();

        // Assert false
        assertFalse(genericStNumberingTest(gm));
    }

    @Test
    public void Bi_CompleteGraphMultipleTest() {
        CompleteGraphGenerator gen = new CompleteGraphGenerator();
        for (int i = 1; i < 35; i++) {
            System.out.println("|n| = " + i);
            GraphModel gm = gen.generateCompleteGraph(i);

            assertTrue(genericStNumberingTest(gm));
        }


    }

    @Test
    public void Bi_CircleGraphTest() {
        CircleGenerator gen = new CircleGenerator();
        GraphModel gm = gen.generateCircle(4);

        assertTrue(genericStNumberingTest(gm));

    }

    @Test
    public void Bi_CircleGraphMutltipleTest() {
        CircleGenerator gen = new CircleGenerator();
        for (int i = 1; i < 13; i++) {
            System.out.println("Testing for n = " + i);
            GraphModel gm = gen.generateCircle(i);

            assertTrue(genericStNumberingTest(gm));

        }
    }

    // Not biconnected
    @Test
    public void NotBi_BananaTreeTest(){
        BananaTreeGenerator gen = new BananaTreeGenerator();
        for(int i=0; i<100; i++) {
            GraphModel gm = gen.generateGraph();

            // Assert false
            assertFalse(genericStNumberingTest(gm));

        }
    }

    // biconnected
    @Test
    public void Bi_GearGraphTest_n2() {
        GearGenerator gen = new GearGenerator();
        GraphModel gm = gen.generateGear(2);

        // Assert false
        assertTrue(genericStNumberingTest(gm));
    }

    // Not biconnected
    @Test
    public void Bi_GearGraphTest_n3() {
        GearGenerator gen = new GearGenerator();
        GraphModel gm = gen.generateGear(3);

        // Assert false
        assertTrue(genericStNumberingTest(gm));
    }

    // Not biconnected
    @Test
    public void NotBi_GearGraphTest() {
        GearGenerator gen = new GearGenerator();
        for (int i = 1; i < 5; i++) {
            System.out.println("Testing for n = " + i);
            GraphModel gm = gen.generateGear(i);

            if(i == 1){
                assertFalse(genericStNumberingTest(gm));
            }
            else if(i <= 4){
                // Assert true
                assertTrue(genericStNumberingTest(gm));
            }
            else {
                // Assert false
                assertFalse(genericStNumberingTest(gm));
            }
        }
    }

    // biconnected
    @Test
    public void NotBi_PrismGraphTest() {
        PrismGraph gen = new PrismGraph();
        GraphModel gm = gen.generateGraph();

        // Assert true
        assertTrue(genericStNumberingTest(gm));
    }

    // bi-connected
    @Test
    public void Bi_AntiPrismGraphTest() {
        AntiprismGraph gen = new AntiprismGraph();
        GraphModel gm = gen.generateGraph();

        assertTrue(genericStNumberingTest(gm));
    }

    // Not biconnected
    @Test
    public void NotBi_StarGraphTest() {
        StarGenerator gen = new StarGenerator();
        GraphModel gm = gen.generateStar(10);

        // Assert false
        assertFalse(genericStNumberingTest(gm));
    }

    // Not biconnected
    @Test
    public void NotBi_TadPoleTest() {
        TadpoleGenerator gen = new TadpoleGenerator();
        GraphModel gm = gen.generateTadpole(5, 6);

        // Assert false
        assertFalse(genericStNumberingTest(gm));
    }

    // Not biconnected
    @Test
    public void NotBi_HelmTest1() {
        HelmGraph gen = new HelmGraph();
        GraphModel gm = gen.generateHelm(1);

        // Assert false
        assertFalse(genericStNumberingTest(gm));
    }

    // Not biconnected
    @Test
    public void NotBi_HelmTest2() {
        HelmGraph gen = new HelmGraph();
        GraphModel gm = gen.generateHelm(2);

        // Assert false
        assertFalse(genericStNumberingTest(gm));

    }

    // Not biconnected
    @Test
    public void NotBi_HelmTest3() {
        HelmGraph gen = new HelmGraph();
        GraphModel gm = gen.generateHelm(3);

        // Assert false
        assertFalse(genericStNumberingTest(gm));

    }

    // Not biconnected
    @Test
    public void NotBi_HelmTest4() {
        HelmGraph gen = new HelmGraph();
        GraphModel gm = gen.generateHelm(10);

        // Assert false
        assertFalse(genericStNumberingTest(gm));

    }

    @Test
    public void Bi_PetersenTest() {
        GeneralizedPetersonGenerator gen = new GeneralizedPetersonGenerator();
        GraphModel gm = gen.generateGeneralizedPeterson(5, 2);

        assertTrue(genericStNumberingTest(gm));

    }

    // Not biconnected
    @Test
    public void NotBi_RandomBinaryTreeTest1() {
        RandomTreeGenerator gen = new RandomTreeGenerator();
        GraphModel gm = gen.generateRandomTree(10, 10, 3);

        // Assert false
        assertFalse(genericStNumberingTest(gm));

    }
    // Not biconnected
    @Test
    public void NotBi_RandomBinaryTreeTest2() {
        RandomTreeGenerator gen = new RandomTreeGenerator();
        GraphModel gm = gen.generateRandomTree(20, 30, 3);

        // Assert false
        assertFalse(genericStNumberingTest(gm));
    }

    // Not biconnected
    @Test
    public void NotBi_RandomBinaryTreeTest3() {
        RandomTreeGenerator gen = new RandomTreeGenerator();
        GraphModel gm = gen.generateRandomTree(50, 30, 3);

        // Assert false
        assertFalse(genericStNumberingTest(gm));
    }

    // Not biconnected
    @Test
    public void NotBi_RandomTreeTest1() {
        RandomTreeGenerator gen = new RandomTreeGenerator();
        GraphModel gm = gen.generateRandomTree(20, 50, 300);

        // Assert false
        assertFalse(genericStNumberingTest(gm));
    }

    // Not biconnected
    @Test
    public void NotBi_RandomTreeTest2() {
        RandomTreeGenerator gen = new RandomTreeGenerator();
        GraphModel gm = gen.generateRandomTree(50, 50, 300);

        // Assert false
        assertFalse(genericStNumberingTest(gm));
    }

    @Test
    public void Bi_ManualGraphTest1() {
        GraphModel gm = new GraphModel();
        ArrayList<Vertex> vertices = new ArrayList<Vertex>();
        for (int i = 0; i < 8; i++) {
            Vertex v = new Vertex();
            vertices.add(v);
            gm.addVertex(v);
        }
        gm.addEdge(new Edge(vertices.get(0), vertices.get(1)));
        gm.addEdge(new Edge(vertices.get(0), vertices.get(2)));
        gm.addEdge(new Edge(vertices.get(0), vertices.get(3)));
        gm.addEdge(new Edge(vertices.get(0), vertices.get(4)));
        gm.addEdge(new Edge(vertices.get(0), vertices.get(5)));

        gm.addEdge(new Edge(vertices.get(1), vertices.get(2)));
        gm.addEdge(new Edge(vertices.get(1), vertices.get(3)));
        gm.addEdge(new Edge(vertices.get(1), vertices.get(5)));
        gm.addEdge(new Edge(vertices.get(1), vertices.get(6)));

        gm.addEdge(new Edge(vertices.get(2), vertices.get(3)));
        gm.addEdge(new Edge(vertices.get(2), vertices.get(6)));
        gm.addEdge(new Edge(vertices.get(2), vertices.get(7)));

        gm.addEdge(new Edge(vertices.get(3), vertices.get(4)));
        gm.addEdge(new Edge(vertices.get(3), vertices.get(7)));

        gm.addEdge(new Edge(vertices.get(4), vertices.get(5)));
        gm.addEdge(new Edge(vertices.get(4), vertices.get(7)));

        gm.addEdge(new Edge(vertices.get(5), vertices.get(6)));

        gm.addEdge(new Edge(vertices.get(6), vertices.get(7)));

        assertTrue(genericStNumberingTest(gm));
    }

    // Biconnected
    @Test
    public void Bi_ManualGraphTest2() {
        GraphModel gm = new GraphModel();
        ArrayList<Vertex> vertices = new ArrayList<Vertex>();
        for (int i = 0; i < 8; i++) {
            Vertex v = new Vertex();
            vertices.add(v);
            gm.addVertex(v);
        }
        gm.addEdge(new Edge(vertices.get(0), vertices.get(1)));
        gm.addEdge(new Edge(vertices.get(0), vertices.get(3)));
        gm.addEdge(new Edge(vertices.get(0), vertices.get(4)));
        gm.addEdge(new Edge(vertices.get(0), vertices.get(5)));

        gm.addEdge(new Edge(vertices.get(1), vertices.get(2)));
        gm.addEdge(new Edge(vertices.get(1), vertices.get(5)));
        gm.addEdge(new Edge(vertices.get(1), vertices.get(6)));

        gm.addEdge(new Edge(vertices.get(2), vertices.get(3)));
        gm.addEdge(new Edge(vertices.get(2), vertices.get(6)));
        gm.addEdge(new Edge(vertices.get(2), vertices.get(7)));

        gm.addEdge(new Edge(vertices.get(3), vertices.get(4)));
        gm.addEdge(new Edge(vertices.get(3), vertices.get(7)));

        gm.addEdge(new Edge(vertices.get(4), vertices.get(5)));
        gm.addEdge(new Edge(vertices.get(4), vertices.get(7)));

        gm.addEdge(new Edge(vertices.get(5), vertices.get(6)));

        gm.addEdge(new Edge(vertices.get(6), vertices.get(7)));

        assertTrue(genericStNumberingTest(gm));
    }

    // Not biconnected
    //@Test
    public void NotBi_ManualGraphTest3(){
        GraphModel gm = new GraphModel();
        ArrayList<Vertex> vertices = new ArrayList<Vertex>();
        for (int i = 0; i < 10; i++) {
            Vertex v = new Vertex();
            vertices.add(v);
            gm.addVertex(v);
        }

        gm.addEdge(new Edge(vertices.get(0), vertices.get(1)));

        gm.addEdge(new Edge(vertices.get(1), vertices.get(4)));
        gm.addEdge(new Edge(vertices.get(1), vertices.get(5)));
        gm.addEdge(new Edge(vertices.get(1), vertices.get(6)));

        gm.addEdge(new Edge(vertices.get(2), vertices.get(4)));
        gm.addEdge(new Edge(vertices.get(2), vertices.get(5)));
        gm.addEdge(new Edge(vertices.get(2), vertices.get(6)));

        gm.addEdge(new Edge(vertices.get(3), vertices.get(4)));
        gm.addEdge(new Edge(vertices.get(3), vertices.get(5)));
        gm.addEdge(new Edge(vertices.get(3), vertices.get(6)));
        gm.addEdge(new Edge(vertices.get(3), vertices.get(7)));

        gm.addEdge(new Edge(vertices.get(7), vertices.get(6)));

        gm.addEdge(new Edge(vertices.get(5), vertices.get(8)));

        // Assert false
        assertFalse(genericStNumberingTest(gm));
    }

    // Not biconnected
    @Test
    public void NotBi_ManualGraphTest4() {
        GraphModel gm = new GraphModel();
        ArrayList<Vertex> vertices = new ArrayList<Vertex>();
        for (int i = 0; i < 8; i++) {
            Vertex v = new Vertex();
            vertices.add(v);
            gm.addVertex(v);
        }

        gm.addEdge(new Edge(vertices.get(0), vertices.get(1)));
        gm.addEdge(new Edge(vertices.get(0), vertices.get(7)));
        gm.addEdge(new Edge(vertices.get(0), vertices.get(6)));

        gm.addEdge(new Edge(vertices.get(1), vertices.get(2)));
        gm.addEdge(new Edge(vertices.get(1), vertices.get(3)));
        gm.addEdge(new Edge(vertices.get(1), vertices.get(5)));

        gm.addEdge(new Edge(vertices.get(3), vertices.get(2)));
        gm.addEdge(new Edge(vertices.get(3), vertices.get(4)));
        gm.addEdge(new Edge(vertices.get(3), vertices.get(5)));

        gm.addEdge(new Edge(vertices.get(2), vertices.get(5)));
        gm.addEdge(new Edge(vertices.get(2), vertices.get(7)));

        gm.addEdge(new Edge(vertices.get(5), vertices.get(6)));

        gm.addEdge(new Edge(vertices.get(7), vertices.get(6)));

        // Assert false
        assertFalse(genericStNumberingTest(gm));

    }

    // Biconnected
    @Test
    public void Bi_ManualGraphTest5() {
        GraphModel gm = new GraphModel();
        ArrayList<Vertex> vertices = new ArrayList<Vertex>();
        for (int i = 0; i < 8; i++) {
            Vertex v = new Vertex();
            vertices.add(v);
            gm.addVertex(v);
        }

        gm.addEdge(new Edge(vertices.get(0), vertices.get(1)));
        gm.addEdge(new Edge(vertices.get(0), vertices.get(7)));
        gm.addEdge(new Edge(vertices.get(0), vertices.get(6)));

        gm.addEdge(new Edge(vertices.get(1), vertices.get(2)));
        gm.addEdge(new Edge(vertices.get(1), vertices.get(3)));
        gm.addEdge(new Edge(vertices.get(1), vertices.get(5)));

        gm.addEdge(new Edge(vertices.get(3), vertices.get(2)));
        gm.addEdge(new Edge(vertices.get(3), vertices.get(4)));
        gm.addEdge(new Edge(vertices.get(3), vertices.get(5)));

        gm.addEdge(new Edge(vertices.get(4), vertices.get(7)));

        gm.addEdge(new Edge(vertices.get(2), vertices.get(5)));
        gm.addEdge(new Edge(vertices.get(2), vertices.get(7)));

        gm.addEdge(new Edge(vertices.get(5), vertices.get(6)));

        gm.addEdge(new Edge(vertices.get(7), vertices.get(6)));

        assertTrue(genericStNumberingTest(gm));

    }

}