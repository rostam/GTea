package extensions.reports.planarity.extensions.reports.planarity.planaritypq;

import graphtea.extensions.generators.CompleteGraphGenerator;
import graphtea.extensions.reports.planarity.planaritypq.PQ;
import graphtea.extensions.reports.planarity.planaritypq.PQNode;
import graphtea.extensions.reports.planarity.planaritypq.PQTree;
import graphtea.graph.graph.GraphModel;
import graphtea.graph.graph.Vertex;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.TestCase.assertTrue;

public class PQTest {

    @Test
    public void createPQNode(){
        PQNode node = new PQNode();
        assertNotNull(node);
    }

    @Test
    public void unionTest(){

        List<PQNode> initialiser = new ArrayList<>();
        for(int i=0; i<10; i++){
            PQNode n = new PQNode();
            initialiser.add(n);

        }
        List<PQNode> list1 = new LinkedList<>(initialiser.subList(0, 4));
        List<PQNode> list2 = new LinkedList<>(initialiser.subList(2, 7));

        List<PQNode> tester = PQ.union(list1, list2);

        assertTrue(tester.size() == 7);
    }

    @Test
    public void intersectionTest(){
        List<PQNode> initialiser = new ArrayList<>();
        for(int i=0; i<10; i++){
            initialiser.add(new PQNode());

        }
        List<PQNode> list1 = new LinkedList<>(initialiser.subList(0, 5));
        List<PQNode> list2 = new LinkedList<>(initialiser.subList(3, 8));

        List<PQNode> tester = PQ.intersection(list1, list2);

        // Checks if test has elements from indices 3,4 and no others.
        assertTrue(tester.size() == 2);
    }

    @Test
    public void subsetTest(){
        List<PQNode> initialiser = new ArrayList<>();
        for(int i=0; i<10; i++){
            initialiser.add(new PQNode());

        }
        List<PQNode> list1 = new LinkedList<>(initialiser.subList(0, 5));
        List<PQNode> list2 = new LinkedList<>(initialiser.subList(0, 8));

        assertTrue(PQ.subset(list1, list2));
        assertTrue(!PQ.subset(list2, list1));

    }

    /** Tests bubbling up
     * Checks:
     * - All nodes processed
     * - Marks all nodes in PRUNED(T,S)*/
    @Test
    public void bubbleTest(){

        PQNode root = new PQNode();
        List<PQNode> S = new ArrayList<>();

        // Create tree to PQ-ify


        // Test PQTree

        PQ PQTree = new PQ();
        PQNode treeRoot = PQTree.bubble(root, S);


    }

    @Test
    public void reduceTest(){

    }

    @Test
    public void templateP1Test(){

    }

    @Test
    public void templateP2Test(){

    }

    @Test
    public void templateP3Test(){

    }

    @Test
    public void templateP4Test(){

    }

    @Test
    public void templateP5Test(){

    }

    @Test
    public void templateP6Test(){

    }

    @Test
    public void templateQ1Test(){

    }

    @Test
    public void templateQ2Test(){

    }

    @Test
    public void templateL1Test(){

    }

}
