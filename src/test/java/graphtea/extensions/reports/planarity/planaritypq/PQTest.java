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

        // todo: Check if the output tree is the same as the input tree
        assertTrue(tree == _root);
    }

    /** Tests bubbling up
     * Checks:
     * - All nodes processed
     * - Marks all nodes in PRUNED(T,S)*/
    @Test
    public void bubbleTest1_PNode(){

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
        A.circularLink_prev = C;
        A.circularLink_next = B;
        B.circularLink_prev = A;
        B.circularLink_next = C;
        C.circularLink_prev = B;
        C.circularLink_next = A;

        // Constraint set S := {B,C}
        List<PQNode> S = Arrays.asList(B, C);

        // Test PQTree
        PQ PQTree = new PQ();
        PQNode r = PQTree.bubble(_root, S);

        // ASSERT
        assertTrue(r.partialChildren().contains(B));
        assertTrue(r.partialChildren().contains(C));
        assertTrue(r.fullChildren().contains(B));
        assertTrue(r.fullChildren().contains(C));
        assertTrue(r.children.contains(A));
        assertTrue(r.children.contains(B));
        assertTrue(r.children.contains(C));
        assertTrue(r.nodeType(PQNode.PNODE));
        //assertTrue(B.marked);
        //assertTrue(C.marked);
        //assertTrue(_root.fullChildren() == Arrays.asList(B, C)
        //        ||_root.fullChildren() == Arrays.asList(C, B));
    }

    /*
    @Test
    public void bubbleTest2_QNodePNode(){

        PQNode _root = new PQNode();
        PQNode A = new PQNode();
        PQNode B = new PQNode();
        PQNode C = new PQNode();
        PQNode qNode = new PQNode();
        PQNode D = new PQNode();
        PQNode E = new PQNode();
        PQNode F = new PQNode();


        // Create tree
        _root.nodeType = PQNode.PNODE;
        A.nodeType = PQNode.PSEUDO_NODE;
        B.nodeType = PQNode.PSEUDO_NODE;
        C.nodeType = PQNode.PSEUDO_NODE;
        qNode.nodeType = PQNode.QNODE;
        D.nodeType = PQNode.PSEUDO_NODE;
        E.nodeType = PQNode.PSEUDO_NODE;
        F.nodeType = PQNode.PSEUDO_NODE;

        _root.children = Arrays.asList(A, B, C, qNode);
        A.parent = _root;
        B.parent = _root;
        C.parent = _root;
        qNode.parent = _root;
        D.parent = qNode;
        E.parent = qNode;
        F.parent = qNode;


        // Constraint set S
        List<PQNode> S = Arrays.asList(D, E, F);

        // Test PQTree
        PQ PQTree = new PQ();
        PQNode tree = PQTree.bubble(_root, S);

        // ASSERT
        assertTrue(!A.marked && !B.marked && !C.marked && D.marked && E.marked && F.marked);

    }

    @Test
    public void bubbleTest3_QNodePNode(){

        PQNode _root = new PQNode();
        PQNode A = new PQNode();
        PQNode B = new PQNode();
        PQNode C = new PQNode();
        PQNode qNode = new PQNode();
        PQNode D = new PQNode();
        PQNode E = new PQNode();
        PQNode F = new PQNode();


        // Create tree
        _root.nodeType = PQNode.PNODE;
        A.nodeType = PQNode.PSEUDO_NODE;
        B.nodeType = PQNode.PSEUDO_NODE;
        C.nodeType = PQNode.PSEUDO_NODE;
        qNode.nodeType = PQNode.QNODE;
        D.nodeType = PQNode.PSEUDO_NODE;
        E.nodeType = PQNode.PSEUDO_NODE;
        F.nodeType = PQNode.PSEUDO_NODE;

        _root.children = Arrays.asList(A, B, C, qNode);
        A.parent = _root;
        B.parent = _root;
        C.parent = _root;
        qNode.parent = _root;
        D.parent = qNode;
        E.parent = qNode;
        F.parent = qNode;


        // Constraint set S
        List<PQNode> S = Arrays.asList(F, E, D);

        // Test PQTree
        PQ PQTree = new PQ();
        PQNode tree = PQTree.bubble(_root, S);

        // ASSERT
        assertTrue(!A.marked && !B.marked && !C.marked && D.marked && E.marked && F.marked);
    }

    @Test
    public void bubbleTest4_QNodePNode(){

        PQNode _root = new PQNode();
        PQNode A = new PQNode();
        PQNode B = new PQNode();
        PQNode C = new PQNode();
        PQNode qNode = new PQNode();
        PQNode D = new PQNode();
        PQNode E = new PQNode();
        PQNode F = new PQNode();


        // Create tree
        _root.nodeType = PQNode.PNODE;
        A.nodeType = PQNode.PSEUDO_NODE;
        B.nodeType = PQNode.PSEUDO_NODE;
        C.nodeType = PQNode.PSEUDO_NODE;
        qNode.nodeType = PQNode.QNODE;
        D.nodeType = PQNode.PSEUDO_NODE;
        E.nodeType = PQNode.PSEUDO_NODE;
        F.nodeType = PQNode.PSEUDO_NODE;

        _root.children = Arrays.asList(A, B, C, qNode);
        A.parent = _root;
        B.parent = _root;
        C.parent = _root;
        qNode.parent = _root;
        D.parent = qNode;
        E.parent = qNode;
        F.parent = qNode;


        // Constraint set S
        List<PQNode> S = Arrays.asList(D, E, F, B);

        // Test PQTree
        PQ PQTree = new PQ();
        PQNode tree = PQTree.bubble(_root, S);

        // ASSERT
        assertTrue(!A.marked && B.marked && !C.marked && D.marked && E.marked && F.marked);
    }

    @Test
    public void bubbleTest5_QNodePNode(){

        PQNode _root = new PQNode();
        PQNode A = new PQNode();
        PQNode B = new PQNode();
        PQNode C = new PQNode();
        PQNode qNode = new PQNode();
        PQNode D = new PQNode();
        PQNode E = new PQNode();
        PQNode F = new PQNode();


        // Create tree
        _root.nodeType = PQNode.PNODE;
        A.nodeType = PQNode.PSEUDO_NODE;
        B.nodeType = PQNode.PSEUDO_NODE;
        C.nodeType = PQNode.PSEUDO_NODE;
        qNode.nodeType = PQNode.QNODE;
        D.nodeType = PQNode.PSEUDO_NODE;
        E.nodeType = PQNode.PSEUDO_NODE;
        F.nodeType = PQNode.PSEUDO_NODE;

        _root.children = Arrays.asList(A, B, C, qNode);
        A.parent = _root;
        B.parent = _root;
        C.parent = _root;
        qNode.parent = _root;
        D.parent = qNode;
        E.parent = qNode;
        F.parent = qNode;


        // Constraint set S
        List<PQNode> S = Arrays.asList(D, E, F, B, A);

        // Test PQTree
        PQ PQTree = new PQ();
        PQNode tree = PQTree.bubble(_root, S);

        // ASSERT
        assertTrue(A.marked && B.marked && !C.marked && D.marked && E.marked && F.marked);
    }

    @Test
    public void bubbleTest6_QNodePNode(){

        PQNode _root = new PQNode();
        PQNode A = new PQNode();
        PQNode B = new PQNode();
        PQNode C = new PQNode();
        PQNode qNode = new PQNode();
        PQNode D = new PQNode();
        PQNode E = new PQNode();
        PQNode F = new PQNode();


        // Create tree
        _root.nodeType = PQNode.PNODE;
        A.nodeType = PQNode.PSEUDO_NODE;
        B.nodeType = PQNode.PSEUDO_NODE;
        C.nodeType = PQNode.PSEUDO_NODE;
        qNode.nodeType = PQNode.QNODE;
        D.nodeType = PQNode.PSEUDO_NODE;
        E.nodeType = PQNode.PSEUDO_NODE;
        F.nodeType = PQNode.PSEUDO_NODE;

        _root.children = Arrays.asList(A, B, C, qNode);
        A.parent = _root;
        B.parent = _root;
        C.parent = _root;
        qNode.parent = _root;
        D.parent = qNode;
        E.parent = qNode;
        F.parent = qNode;


        // Constraint set S
        List<PQNode> S = Arrays.asList(D, E, F, B, A, C);

        // Test PQTree
        PQ PQTree = new PQ();
        PQNode tree = PQTree.bubble(_root, S);

        // ASSERT
        assertTrue(A.marked && B.marked && C.marked && D.marked && E.marked && F.marked);
    }

    @Test
    public void bubbleTest7_QNodePNode(){

        PQNode _root = new PQNode();
        PQNode A = new PQNode();
        PQNode B = new PQNode();
        PQNode C = new PQNode();
        PQNode qNode = new PQNode();
        PQNode D = new PQNode();
        PQNode E = new PQNode();
        PQNode F = new PQNode();


        // Create tree
        _root.nodeType = PQNode.PNODE;
        A.nodeType = PQNode.PSEUDO_NODE;
        B.nodeType = PQNode.PSEUDO_NODE;
        C.nodeType = PQNode.PSEUDO_NODE;
        qNode.nodeType = PQNode.QNODE;
        D.nodeType = PQNode.PSEUDO_NODE;
        E.nodeType = PQNode.PSEUDO_NODE;
        F.nodeType = PQNode.PSEUDO_NODE;

        _root.children = Arrays.asList(A, B, C, qNode);
        A.parent = _root;
        B.parent = _root;
        C.parent = _root;
        qNode.parent = _root;
        D.parent = qNode;
        E.parent = qNode;
        F.parent = qNode;


        // Constraint set S
        List<PQNode> S = Arrays.asList(E, D, F, B, A, C);

        // Test PQTree
        PQ PQTree = new PQ();
        PQNode tree = PQTree.bubble(_root, S);

        // ASSERT
        assertTrue(tree == null);
    }*/

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

