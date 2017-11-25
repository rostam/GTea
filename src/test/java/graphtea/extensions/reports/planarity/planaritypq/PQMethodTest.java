package graphtea.extensions.reports.planarity.planaritypq;

import graphtea.extensions.generators.*;
import graphtea.extensions.reports.planarity.WagnerMethod;
import graphtea.graph.graph.Edge;
import graphtea.graph.graph.GraphModel;
import graphtea.graph.graph.Vertex;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static junit.framework.TestCase.assertTrue;
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

    @Test
    public void k5Test(){
        CompleteGraphGenerator gen = new CompleteGraphGenerator();
        GraphModel gm = gen.generateCompleteGraph(5);
        PQMethod pc = new PQMethod();
        assertFalse(pc.isPlanar(gm));
    }

    @Test
    public void kTest(){
        CompleteGraphGenerator gen = new CompleteGraphGenerator();
        GraphModel gm = gen.generateCompleteGraph(20);
        PQMethod pc = new PQMethod();
        assertFalse(pc.isPlanar(gm));
    }

    @Test
    public void TreeTest(){
        TreeGenerator gen = new TreeGenerator();
        GraphModel gm = gen.generateGraph();
        PQMethod pc = new PQMethod();
        assertTrue(pc.isPlanar(gm));
    }

    @Test
    public void CompleteGraphMultipleTest() {
        CompleteGraphGenerator gen = new CompleteGraphGenerator();
        for (int i = 1; i < 35; i++) {
            System.out.println(i);
            GraphModel gm = gen.generateCompleteGraph(i);
            PQMethod pc = new PQMethod();

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
        PQMethod pc = new PQMethod();
        assertTrue( pc.isPlanar(gm));

    }

    @Test
    public void CircleGraphMutltipleTest() {
        CircleGenerator gen = new CircleGenerator();
        for (int i = 1; i < 13; i++) {
            System.out.println("Testing for n = " + i);

            GraphModel gm = gen.generateCircle(i);
            PQMethod pc = new PQMethod();
            assertTrue( pc.isPlanar(gm));
        }
    }

    @Test
    public void BananatreeTest(){
        BananaTreeGenerator gen = new BananaTreeGenerator();
        for(int i=0; i<100; i++) {
            GraphModel gm = gen.generateGraph();
            PQMethod pc = new PQMethod();
            assertTrue( pc.isPlanar(gm));
        }
    }

    @Test
    public void GearGraphTest_n3() {
        GearGenerator gen = new GearGenerator();
        GraphModel gm = gen.generateGear(3);
        PQMethod pc = new PQMethod();
        assertTrue( pc.isPlanar(gm));
    }

    @Test
    public void GearGraphTest() {
        GearGenerator gen = new GearGenerator();
        for (int i = 1; i < 5; i++) {
            System.out.println("Testing for n = " + i);
            GraphModel gm = gen.generateGear(i);
            PQMethod pc = new PQMethod();
            assertTrue( pc.isPlanar(gm));
        }
    }

    @Test
    public void PrismGraphTest() {
        PrismGraph gen = new PrismGraph();
        GraphModel gm = gen.generateGraph();
        PQMethod pc = new PQMethod();
        assertTrue(pc.isPlanar(gm));
    }

    @Test
    public void AntiPrismGraphTest() {
        AntiprismGraph gen = new AntiprismGraph();
        GraphModel gm = gen.generateGraph();
        PQMethod pc = new PQMethod();
        assertTrue(pc.isPlanar(gm));
    }

    @Test
    public void StarGraphTest() {
        StarGenerator gen = new StarGenerator();
        GraphModel gm = gen.generateStar(10);
        PQMethod pc = new PQMethod();
        assertTrue(pc.isPlanar(gm));
    }

    @Test
    public void TadPoleTest() {
        TadpoleGenerator gen = new TadpoleGenerator();
        GraphModel gm = gen.generateTadpole(5, 6);
        PQMethod pc = new PQMethod();
        assertTrue(pc.isPlanar(gm));
    }

    @Test
    public void HelmTest1() {
        HelmGraph gen = new HelmGraph();
        GraphModel gm = gen.generateHelm(1);
        PQMethod pc = new PQMethod();
        assertTrue(pc.isPlanar(gm));
    }

    @Test
    public void HelmTest2() {
        HelmGraph gen = new HelmGraph();
        GraphModel gm = gen.generateHelm(2);
        PQMethod pc = new PQMethod();
        assertTrue(pc.isPlanar(gm));
    }

    @Test
    public void HelmTest3() {
        HelmGraph gen = new HelmGraph();
        GraphModel gm = gen.generateHelm(3);
        PQMethod pc = new PQMethod();
        assertTrue(pc.isPlanar(gm));
    }

    @Test
    public void HelmTest4() {
        HelmGraph gen = new HelmGraph();
        GraphModel gm = gen.generateHelm(10);
        PQMethod pc = new PQMethod();
        assertTrue(pc.isPlanar(gm));
    }

    @Test
    public void PetersenTest() {
        GeneralizedPetersonGenerator gen = new GeneralizedPetersonGenerator();
        GraphModel gm = gen.generateGeneralizedPeterson(5, 2);
        PQMethod pc = new PQMethod();
        assertFalse(pc.isPlanar(gm));
    }

    @Test
    public void RandomBinaryTreeTest1() {
        RandomTreeGenerator gen = new RandomTreeGenerator();
        GraphModel gm = gen.generateRandomTree(10, 10, 3);
        PQMethod pc = new PQMethod();
        assertTrue(pc.isPlanar(gm));
    }

    @Test
    public void RandomBinaryTreeTest2() {
        RandomTreeGenerator gen = new RandomTreeGenerator();
        GraphModel gm = gen.generateRandomTree(20, 30, 3);
        PQMethod pc = new PQMethod();
        assertTrue(pc.isPlanar(gm));
    }

    @Test
    public void RandomBinaryTreeTest3() {
        RandomTreeGenerator gen = new RandomTreeGenerator();
        GraphModel gm = gen.generateRandomTree(50, 30, 3);
        PQMethod pc = new PQMethod();
        assertTrue(pc.isPlanar(gm));
    }

    @Test
    public void RandomTreeTest1() {
        RandomTreeGenerator gen = new RandomTreeGenerator();
        GraphModel gm = gen.generateRandomTree(20, 50, 300);
        PQMethod pc = new PQMethod();
        assertTrue(pc.isPlanar(gm));
    }

    @Test
    public void RandomTreeTest2() {
        RandomTreeGenerator gen = new RandomTreeGenerator();
        GraphModel gm = gen.generateRandomTree(50, 50, 300);
        PQMethod pc = new PQMethod();
        assertTrue(pc.isPlanar(gm));
    }

    @Test
    public void ManualGraphTest1() {
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

        PQMethod pc = new PQMethod();
        assertFalse(pc.isPlanar(gm));
    }

    @Test
    public void ManualGraphTest2() {
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

        PQMethod pc = new PQMethod();
        assertTrue(pc.isPlanar(gm));
    }

    @Test
    public void ManualGraphTest3(){
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

        PQMethod pc = new PQMethod();
        assertFalse(pc.isPlanar(gm));
    }

    @Test
    public void ManualGraphTest4() {
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

        PQMethod pc = new PQMethod();
        assertTrue(pc.isPlanar(gm));

    }

    @Test
    public void ManualGraphTest5() {
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

        PQMethod pc = new PQMethod();
        assertFalse(pc.isPlanar(gm));

    }

}