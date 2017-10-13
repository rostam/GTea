package graphtea.extensions.reports.planarity.planaritypq;

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
     * - Returns null */
    @Test
    public void bubbleSEmptyTest(){
        PQNode _root = new PQNode();
        PQNode A = new PQNode();
        PQNode B = new PQNode();
        PQNode C = new PQNode();

        // Create tree
        _root.nodeType = PQNode.PNODE;
        A.nodeType = PQNode.PSEUDO_NODE;
        B.nodeType = PQNode.PSEUDO_NODE;
        C.nodeType = PQNode.PSEUDO_NODE;

        _root.children = Arrays.asList(A, B, C);
        A.parent = _root;
        B.parent = _root;
        C.parent = _root;

        // Constraint set S := {B,C}
        List<PQNode> S = new ArrayList<>();

        // Test PQTree
        PQ PQTree = new PQ();
        PQNode tree = PQTree.bubble(_root, S);

        assertTrue(tree == null);
    }

    /** Tests bubbling up
     * Checks:
     * - All nodes processed
     * - Marks all nodes in PRUNED(T,S)*/
    @Test
    public void bubbleTest1(){

        PQNode _root = new PQNode();
        PQNode A = new PQNode();
        PQNode B = new PQNode();
        PQNode C = new PQNode();

        // Create tree
        _root.nodeType = PQNode.PNODE;
        A.nodeType = PQNode.PSEUDO_NODE;
        B.nodeType = PQNode.PSEUDO_NODE;
        C.nodeType = PQNode.PSEUDO_NODE;

        _root.children = Arrays.asList(A, B, C);
        A.parent = _root;
        B.parent = _root;
        C.parent = _root;

        // Constraint set S := {B,C}
        List<PQNode> S = Arrays.asList(B, C);

        // Test PQTree
        PQ PQTree = new PQ();
        PQNode tree = PQTree.bubble(_root, S);

        // ASSERT
        assertTrue(!A.marked);
        assertTrue(B.marked);
        assertTrue(C.marked);
        assertTrue(_root.partialChildren() == Arrays.asList(B, C)
                ||_root.partialChildren() == Arrays.asList(C, B));
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

