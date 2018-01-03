package graphtea.extensions.reports.planarity.planaritypq.pqtree;

import graphtea.extensions.reports.planarity.planaritypq.pqtree.helpers.PQHelpers;
import graphtea.extensions.reports.planarity.planaritypq.pqtree.pqnodes.LeafNode;
import graphtea.extensions.reports.planarity.planaritypq.pqtree.pqnodes.PNode;
import graphtea.extensions.reports.planarity.planaritypq.pqtree.pqnodes.PQNode;
import graphtea.extensions.reports.planarity.planaritypq.pqtree.pqnodes.QNode;
import org.junit.Test;

import java.util.*;

import static graphtea.extensions.reports.planarity.planaritypq.pqtree.helpers.PQHelpers.setCircularLinks;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;


public class PQTreeTest {

    @Test
    public void createPQNode(){
        PQNode node = new PQNode();
        assertNotNull(node);
    }

     // Tests bubbling up
     // Checks:
     // - Returns null
    @Test
    public void bubbleSEmptyTest(){
        PQNode _root = new PNode();
        PQNode A = new LeafNode();
        PQNode B = new LeafNode();
        PQNode C = new LeafNode();

        // Create tree
        _root.addChildren(Arrays.asList(A, B, C));
        A.setParent(_root);
        B.setParent(_root);
        C.setParent(_root);

        // Constraint set S := {B,C}
        List<PQNode> S = new ArrayList<>();

        //Get preorder before
        List<PQNode> preorderBefore = PQHelpers.preorder(_root);

        // Test PQTree
        PQTree PQTree = new PQTree();
        PQNode tree = PQTree.bubble(((PNode)_root), S);

        //Get preorder afterwards
        List<PQNode> preorderAfter = PQHelpers.preorder(_root);

        assertTrue(tree == _root);
    }

     // Tests bubbling up
     // Checks:
     // - All nodes processed
     // - Marks all nodes in PRUNED(T,S)
     // Increments pertinentChildCount
    @Test
    public void bubbleTest1_PNode(){

        PNode _root = new PNode();
        PQNode A = new LeafNode();
        PQNode B = new LeafNode();
        PQNode C = new LeafNode();

        // Create tree

        _root.addChildren(Arrays.asList(A, B, C));
        A.setParent(_root);
        B.setParent(_root);
        C.setParent(_root);
        List<PQNode> list = new ArrayList<>(Arrays.asList(A,B,C));
        setCircularLinks(list);

        // Constraint set S := {B,C}
        List<PQNode> S = Arrays.asList(B, C);

        //Get preorder before
        List<PQNode> preorderBefore = PQHelpers.preorder(_root);

        // Test PQTree
        PQTree PQTree = new PQTree();
        PQNode r = PQTree.bubble(_root, S);

        //Get preorder afterwards
        List<PQNode> preorderAfter = PQHelpers.preorder(_root);

        // ASSERT
        assertTrue(r.getChildren().contains(A));
        assertTrue(r.getChildren().contains(B));
        assertTrue(r.getChildren().contains(C));
        assertTrue(r.getClass() == PNode.class);
        assertTrue(!B.blocked);
        assertTrue(!C.blocked);

        assertTrue(_root.getPertinentChildCount() == 2);
    }

    @Test
    public void bubbleTest2_QNodePNode(){
        PNode _root = new PNode();
        PQNode A = new LeafNode();
        PQNode B = new LeafNode();
        PQNode C = new LeafNode();
        PQNode qNode = new QNode();
        PQNode D = new LeafNode();
        PQNode E = new LeafNode();
        PQNode F = new LeafNode();


        // Create tree
        _root.addChildren(Arrays.asList(A, B, C, qNode));
        A.setParent(_root);
        B.setParent(_root);
        C.setParent(_root);
        qNode.setParent(_root);
        D.setParent(qNode);
        F.setParent(qNode);

        setCircularLinks(Arrays.asList(qNode,A,B,C));
        setCircularLinks(Arrays.asList(D,E,F));

        qNode.setQNodeEndmostChildren(D, F);
        qNode.setParentQNodeChildren();


        // Constraint set S
        List<PQNode> S = Arrays.asList(D, E, F);

        // Test PQTree
        PQTree PQTree = new PQTree();
        PQNode r = PQTree.bubble(_root, S);

        // ASSERT
        assertTrue(D.getParent() == qNode);
        assertTrue(E.getParent() == qNode);
        assertTrue(F.getParent() == qNode);
        assertTrue(!D.blocked);
        assertTrue(!E.blocked);
        assertTrue(!F.blocked);

    }

    @Test
    public void bubbleTest3_QNodePNode(){
        PNode _root = new PNode();
        PQNode A = new LeafNode();
        PQNode B = new LeafNode();
        PQNode C = new LeafNode();
        PQNode qNode = new QNode();
        PQNode D = new LeafNode();
        PQNode E = new LeafNode();
        PQNode F = new LeafNode();

        // Create tree
        _root.addChildren(Arrays.asList(A, B, C, qNode));
        A.setParent(_root);
        B.setParent(_root);
        C.setParent(_root);
        qNode.setParent(_root);
        D.setParent(qNode);
        F.setParent(qNode);

        setCircularLinks(Arrays.asList(qNode,A,B,C));
        setCircularLinks(Arrays.asList(D,E,F));

        qNode.setQNodeEndmostChildren(D, F);
        qNode.setParentQNodeChildren();

        // Constraint set S
        List<PQNode> S = Arrays.asList(D, E, A);

        // Test PQTree
        PQTree PQTree = new PQTree();
        PQNode r = PQTree.bubble(_root, S);

        // ASSERT
        assertTrue(A.getParent() == r);
        assertTrue(D.getParent() == qNode);
        assertTrue(E.getParent() == qNode);
        assertTrue(!D.blocked);
        assertTrue(!E.blocked);
        assertTrue(!A.blocked);
    }

    @Test
    public void bubbleTest4_QNodePNode(){
        PNode _root = new PNode();
        PQNode A = new LeafNode();
        PQNode B = new LeafNode();
        PQNode C = new LeafNode();
        PQNode qNode = new QNode();
        PQNode D = new LeafNode();
        PQNode E = new LeafNode();
        PQNode F = new LeafNode();
        PQNode G = new LeafNode();
        PQNode H = new LeafNode();

        // Create tree
        _root.setId("root");
        A.setId("A");
        B.setId("B");
        C.setId("C");
        D.setId("D");
        E.setId("E");
        F.setId("F");
        G.setId("G");
        H.setId("H");

        _root.addChildren(Arrays.asList(A, B, C, qNode));
        A.setParent(_root);
        B.setParent(_root);
        C.setParent(_root);
        qNode.setParent(_root);
        D.setParent(qNode);
        E.setParent(qNode);
        F.setParent(qNode);
        G.setParent(qNode);
        H.setParent(qNode);

        setCircularLinks(Arrays.asList(qNode,A,B,C));
        setCircularLinks(Arrays.asList(D,E,F,G,H));

        qNode.setQNodeEndmostChildren(D, H);
        qNode.setParentQNodeChildren();

        // Constraint set S
        List<PQNode> S = Arrays.asList(F, B, C);

        // Test PQTree
        PQTree PQTree = new PQTree();
        PQNode r = PQTree.bubble(_root, S);

        // ASSERT
        assertTrue(F.getParent() == qNode);
        assertTrue(B.getParent() == r);
        assertTrue(C.getParent() == r);
        assertTrue(!F.blocked);
        assertTrue(!B.blocked);
        assertTrue(!C.blocked);

    }

    @Test
    public void reduceTest(){
        PNode _root = new PNode();
        PQNode A = new LeafNode();
        PQNode B = new LeafNode();
        PQNode C = new LeafNode();

        // Create tree
        //A.nodeType = PQNode.PSEUDO_NODE;
        //B.nodeType = PQNode.PSEUDO_NODE;
        //C.nodeType = PQNode.PSEUDO_NODE;

        _root.addChildren(Arrays.asList(A, B, C));
        A.setParent(_root);
        B.setParent(_root);
        C.setParent(_root);
        List<PQNode> list = new ArrayList<>(Arrays.asList(A,B,C));
        setCircularLinks(list);

        // Constraint set S := {B,C}
        List<PQNode> S = Arrays.asList(B, C);

        PQTree PQTree = new PQTree();
        PQNode r = PQTree.reduce(_root, S);

        assertTrue(r != null);
    }

    @Test
    public void reduceTemplateP1Test(){
        List<PQNode> nodes = new ArrayList<>(templateP1Tree());
        PNode _root = (PNode) nodes.get(0);
        PQNode A = nodes.get(1);
        PQNode B = nodes.get(2);

        PQTree PQTree = new PQTree();
        List<PQNode> S = new ArrayList<>(Arrays.asList(A, B));
        PQNode rt = PQTree.reduce(_root, S);

        assertTrue(rt != null);
        assertTrue(rt == _root);
        assertTrue(_root.getLabel().equals(PQNode.FULL));
        assertTrue(A.getLabel().equals(PQNode.FULL));
        assertTrue(B.getLabel().equals(PQNode.FULL));
        assertTrue(_root.getChildren().size() == 2);
        assertTrue(A.getParent() == _root);
        assertTrue(B.getParent() == _root);
    }

    @Test
    public void reduceTemplateP2Test(){
        List<PQNode> nodes = new ArrayList<>(templateP2Tree(1));
        PNode _root = (PNode) nodes.get(0);
        PQNode A = nodes.get(1);
        PQNode B = nodes.get(2);
        PQNode C = nodes.get(3);
        PQNode D = nodes.get(4);
        PQNode E = nodes.get(5);

        PQTree PQTree = new PQTree();
        List<PQNode> S = new ArrayList<>(Arrays.asList(A, B, C));
        PQNode rt = PQTree.reduce(_root, S);

        assertTrue(rt != null);
        assertTrue(rt == _root);
        assertTrue(_root.getChildren().size() == 3);
        assertTrue(_root.fullChildren().size() == 1);
        PQNode pNode = _root.fullChildren().get(0);
        //assertTrue(pNode.nodeType.equals(PQNode.PNODE));
        assertTrue(pNode.getClass() == PNode.class);
        assertTrue(pNode.getChildren().size() == 3);
        for(PQNode n : pNode.getChildren()){
            assertTrue(n.getParent() == pNode);
            assertTrue(n.getLabel().equals(PQNode.FULL));
        }
    }

    @Test
    public void reduceTemplateP3Test(){
        List<PQNode> nodes = new ArrayList<>(templateP3Tree(1));
        PNode _root = (PNode) nodes.get(0);
        PQNode initialParent = nodes.get(1);
        PQNode A = nodes.get(2);
        PQNode B = nodes.get(3);
        PQNode C = nodes.get(4);
        PQNode D = nodes.get(5);
        PQNode E = nodes.get(6);

        PQNode extraNode = new PNode(PQNode.EMPTY);
        //extraNode.getLabel().equals(PQNode.FULL);
        extraNode.setId("extraNode");
        extraNode.setParent(_root);
        _root.addChild(extraNode);

        PQTree PQTree = new PQTree();

        List<PQNode> S = new ArrayList<PQNode>(Arrays.asList(A, B, C, extraNode));
        PQNode rt = PQTree.reduce(_root, S);
        PQNode parent = null;
        if(_root.getChildren().get(0) != extraNode){
            parent = _root.getChildren().get(0);
        }
        else {
            parent = _root.getChildren().get(1);
        }

        assertTrue(parent.getClass() == QNode.class);
        assertTrue(parent.getLabel().equals(PQNode.PARTIAL));
        assertTrue(parent.getChildren().size() == 2);
        for (PQNode notFullChild : parent.getChildren().get(0).getChildren()) {
            assertTrue(!notFullChild.getLabel().equals(PQNode.FULL));
        }
        for (PQNode fullChild : parent.getChildren().get(1).getChildren()) {
            assertTrue(fullChild.getLabel().equals(PQNode.FULL));
        }
    }

    @Test
    public void reduceTemplateP4Test(){
        List<PQNode> nodes = templateP4Tree(1);
        PNode _root = (PNode) nodes.get(0);
        PQNode qNode = nodes.get(1);
        PQNode A = nodes.get(2);
        PQNode B = nodes.get(3);
        PQNode C = nodes.get(4);
        PQNode D = nodes.get(5);
        PQNode E = nodes.get(6);
        PQNode F = nodes.get(7);
        PQNode G = nodes.get(8);
        PQNode H = nodes.get(9);

        PQTree PQTree = new PQTree();
        List<PQNode> S = new ArrayList<>(Arrays.asList(C, D, G, H));
        PQNode rt = PQTree.reduce(_root, S);

        assertTrue(A.getParent() == _root);
        assertTrue(B.getParent() == _root);
        assertTrue(qNode.getParent() == _root);

        assertTrue(C.getParent() != qNode && C.getParent() != _root);
        assertTrue(D.getParent() != qNode && D.getParent() != _root);

        assertTrue(E.getParent() == qNode);
        //assertTrue(F.getParent() == null);
        //assertTrue(G.getParent() == null);
        //assertTrue(H.getParent() == null);
        assertTrue(C.getParent() == D.getParent());
        assertTrue(C.getParent().getParent() == qNode);
        assertTrue(qNode.getChildren().contains(C.getParent()));
        assertTrue(_root.getChildren().contains(qNode));
        assertTrue(C.getParent().getClass() == PNode.class);
        assertTrue(C.getParent().getLabel().equals(PQNode.FULL));
        assertTrue(qNode.getLabel().equals(PQNode.PARTIAL));

    }

    @Test
    public void reduceTemplateP5Test() {
        List<PQNode> nodes = new ArrayList<>(templateP5Tree());
        PNode _root = (PNode) nodes.get(0);
        List<PQNode> empties = new ArrayList<>();
        for(int i=1; i<5; i++){
            empties.add(nodes.get(i));
        }
        List<PQNode> fulls = new ArrayList<>();
        for(int i=5; i<9; i++){
            fulls.add(nodes.get(i));
        }

        // extraNodes in order to make P5Tree non-ROOT(T, S)
        PQNode extraPNode = new PNode();
        extraPNode.setLabel(PQNode.FULL);
        _root.setParent(extraPNode);
        extraPNode.addChild(_root);

        PQNode extraSNode = new PQNode();
        extraSNode.setLabel(PQNode.FULL);
        extraSNode.setParent(extraPNode);
        extraPNode.addChild(extraSNode);
        // end of extra nodes

        List<PQNode> S = new ArrayList<>();
        for(int i=9; i<17; i++){
            S.add(nodes.get(i));
        }
        S.add(extraSNode);

        PQNode head = new PNode();
        head.addChild(_root);
        _root.setParent(head);
        PQTree PQTree = new PQTree();

        PQNode rt = PQTree.reduce(_root, S);

        PQNode _r = head.getChildren().get(0);

        for(PQNode n : empties){
            assertTrue(n.getParent() != _r);
            assertTrue(n.getParent().getParent() == _r);
            assertTrue(n.getParent().getChildren().contains(n));
        }

        for(PQNode n : fulls){
            assertTrue(n.getParent() != _r);
            assertTrue(n.getParent().getParent() == _r);
            assertTrue(n.getParent().getChildren().contains(n));
        }

        PQNode leftmostPNode = (empties.get(0)).getParent();
        PQNode rightmostPNode = (fulls.get(0)).getParent();

        assertTrue(_r.endmostChildren().contains(leftmostPNode));
        assertTrue(_r.endmostChildren().contains(rightmostPNode));

        assertTrue(_r.endmostChildren().size() == 2);

        assertTrue(_r.getLabel().equals(PQNode.PARTIAL));
        assertTrue(_r.getClass() == QNode.class);

        PQNode iter = leftmostPNode;
        int countRootChildren = 1;
        while(iter.getCircularLink_next() != leftmostPNode){
            countRootChildren++;
            iter = iter.getCircularLink_next();
        }
        assertTrue(countRootChildren == 10);
    }

    @Test
    public void reduceTemplateP6Test() {
        List<List<PQNode>> nodesList = new ArrayList<>(templateP6Tree());

        PNode _root = (PNode) nodesList.get(0).get(0);
        List<PQNode> empties = new ArrayList<>(nodesList.get(1));
        List<PQNode> fulls = new ArrayList<>(nodesList.get(2));

        List<PQNode> S = new ArrayList<>(nodesList.get(3));

        // Begin Testing
        PQTree PQTree = new PQTree();
        PQNode rt = PQTree.reduce(_root, S);

        for(PQNode n : empties){
            assertTrue(n.getParent() == _root);
        }

        for(PQNode n : fulls){
            assertTrue(n.getParent() != _root);
            assertTrue(n.getParent().getParent() != _root);
            assertTrue(n.getParent().getChildren().contains(n));
        }

        assertTrue(_root.getClass() == PNode.class);

        int qNodeCount = 0;
        QNode mergingQNode = null;
        for(PQNode n : _root.getChildren()){ //?
            if (n.getClass() == QNode.class) {
                qNodeCount++;
                mergingQNode = (QNode) n;
            }
        }
        assertTrue(qNodeCount == 1);
        assertTrue(mergingQNode.getLabel().equals(PQNode.PARTIAL));
        assertTrue(mergingQNode.endmostChildren().size() == 2);

        PQNode iter = mergingQNode.endmostChildren().get(0);
        PQNode leftMostNode = iter;
        int nodeIndex = 0;
        int interiorPNodeCount = 0;
        PQNode interiorPNode = null;
        while(iter.getCircularLink_next() != leftMostNode){

            // Asserts first 4 nodes are EMPTY
            if(nodeIndex < 4) {
                assertTrue(iter.getLabel().equals(PQNode.EMPTY));
            }
            // Asserts node indices between 4 and 13 and PARTIAL/FULL
            else if(4 <= nodeIndex && nodeIndex <= 12){
                // Asserts if singular interior pNode is FULL
                if (iter.getClass() == PNode.class) {
                    assertTrue(iter.getLabel().equals(PQNode.FULL));
                    interiorPNode = iter;
                    interiorPNodeCount++;
                }
                // Asserts if non-PNodes are full
                else {
                    assertTrue(iter.getLabel().equals(PQNode.FULL));
                }
            }
            else {
                assertTrue(iter.getLabel().equals(PQNode.EMPTY));
            }

            nodeIndex++;
            iter = iter.getCircularLink_next();
        }

        assertTrue(interiorPNodeCount == 1);
        assertTrue(interiorPNode.getChildren().size() == 4);
        for(PQNode n : interiorPNode.getChildren()){
            assertTrue(n.getLabel().equals(PQNode.FULL));
        }
        // Finished testing


    }
    // Todo: test reduce with Q1
    @Test
    public void reduceTemplateQ1Test() {
        List<PQNode> nodes = new ArrayList<>(templateQ1Tree());
        PQNode _root = nodes.get(0);
        PQNode A = nodes.get(1);
        PQNode B = nodes.get(2);

        _root.setId("_root");
        A.setId("A");
        B.setId("B");

        // Extra nodes so Q1 is not ROOT(T, S)
        PNode extraPNode = new PNode();
        extraPNode.addChild(_root);
        _root.setParent(extraPNode);

        PQNode extraSNode = new PQNode();
        extraSNode.setParent(extraPNode);
        extraPNode.getChildren().add(extraSNode);
        extraSNode.setLabel(PQNode.FULL);

        extraPNode.setId("extraPNode");
        extraSNode.setId("extraSNode");
        // end of extra nodes

        List<PQNode> S = new ArrayList<>();
        S.add(A);
        S.add(B);
        S.add(extraSNode);

        PQTree PQTree = new PQTree();

        // All children blocked
        assertTrue(!_root.getLabel().equals(PQNode.FULL));
        //PQNode rt = PQTree.reduce(_root, S);
        PQNode rt = PQTree.reduce(extraPNode, S);
        assertTrue(_root.getLabel().equals(PQNode.FULL));

        // One child not blocked
        PQNode C = new PQNode();
        C.setParent(_root);
        _root.setLabel(PQNode.EMPTY);
        C.setLabel(PQNode.EMPTY);
        C.setId("C");

        _root.clearChildren();
        _root.addChildren(Arrays.asList(A,C));
        B.setParent(null);
        setCircularLinks(Arrays.asList(A,B,C));

        rt = PQTree.reduce(extraPNode, S);
        assertTrue(rt != null);
        assertTrue(!_root.getLabel().equals(PQNode.FULL));
    }
    // Todo: test reduce with Q2
    @Test
    public void reduceTemplateQ2Test() {
        List<PQNode> nodes = new ArrayList<>(templateQ2Tree(1));
        PNode _root = (PNode) nodes.get(0);
        PQNode qNode = nodes.get(1);
        PQNode A = nodes.get(2);
        PQNode B = nodes.get(3);
        PQNode C = nodes.get(4);
        PQNode Ca = nodes.get(5);
        PQNode Cb = nodes.get(6);
        PQNode Cc = nodes.get(7);
        PQNode Cd = nodes.get(8);
        PQNode D = nodes.get(9);
        PQNode E = nodes.get(10);

        PQNode extraSNode = new PQNode();
        extraSNode.setParent(_root);
        _root.addChild(extraSNode);

        List<PQNode> S = new ArrayList<>(Arrays.asList(Cc, Cd, D, E, extraSNode));

        PQTree PQTree = new PQTree();
        PQNode rt = PQTree.reduce(_root, S);

        Set<PQNode> nodesToCheck = new HashSet<PQNode>();

        nodesToCheck.addAll(Arrays.asList(A, B, Ca, Cb, Cc, Cd, D, E));

        PQNode iterNode = qNode.getChildren().get(0);
        int flips = 0;

         // Below, we travel from left to right in the children list and counts
         // the number of children whose siblings's labels are different from theirs.
         // We call this number the 'number of flips'.
         //
         // This number has to be 1 to be valid.

        // While we have not checked the whole circular list
        do {
            if (nodesToCheck.contains(iterNode)) {
                nodesToCheck.remove(iterNode);
            }
            if (iterNode != qNode.getChildren().get(0) && iterNode.getCircularLink_prev().getLabel() != iterNode.getLabel()) {
                flips++;
            }
            iterNode = iterNode.getCircularLink_next();
        } while (iterNode != qNode.getChildren().get(0));

        assert(nodesToCheck.isEmpty());
        assert(flips == 1);


    }
    // Todo: test reduce with Q3
    @Test
    public void reduceTemplateQ3Test() {

    }

    private List<PQNode> templateP1Tree(){
        PQNode _root = new PNode(PQNode.EMPTY);
        PQNode A = new LeafNode();
        PQNode B = new LeafNode();

        //_root.id="_root";
        A.setId("A");
        B.setId("B");

        _root.setPertinentChildCount(2);

        _root.addChildren(Arrays.asList(A,B));
        A.setParent(_root);
        B.setParent(_root);
        _root.setLabel(PQNode.EMPTY);
        A.setLabel(PQNode.FULL);
        B.setLabel(PQNode.FULL);

        return Arrays.asList(_root, A, B);
    }

    @Test
    public void templateP1Test(){
        List<PQNode> nodes = new ArrayList<>(templateP1Tree());
        PQNode _root = nodes.get(0);
        PQNode A = nodes.get(1);
        PQNode B = nodes.get(2);

        PQTree PQTree = new PQTree();

        // All children blocked
        assertTrue(!_root.getLabel().equals(PQNode.FULL));
        boolean rt = PQTree.TEMPLATE_P1(_root);
        assertTrue(rt);
        assertTrue(_root.getLabel().equals(PQNode.FULL));

        // One child not blocked
        PQNode C = new PQNode();
        C.setParent(_root);
        _root.setLabel(PQNode.EMPTY);
        C.setLabel(PQNode.EMPTY);

        _root.addChildren(Arrays.asList(A,B,C));
        rt = PQTree.TEMPLATE_P1(_root);
        assertTrue(!rt);
        assertTrue(!_root.getLabel().equals(PQNode.FULL));
    }

    private List<PQNode> templateP2Tree(int treeNum){
        PQNode _root = new PNode();
        PQNode A = new LeafNode();
        PQNode B = new LeafNode();
        PQNode C = new LeafNode();
        PQNode D = new LeafNode();
        PQNode E = new LeafNode();

        _root.setId("root");
        A.setId("A");
        B.setId("B");
        C.setId("C");
        D.setId("D");
        E.setId("E");

        _root.setPertinentChildCount(3);

        A.setParent(_root);
        B.setParent(_root);
        C.setParent(_root);
        D.setParent(_root);
        E.setParent(_root);

        A.setLabel(PQNode.FULL);
        B.setLabel(PQNode.FULL);
        C.setLabel(PQNode.FULL);

        D.setLabel(PQNode.EMPTY);
        E.setLabel(PQNode.EMPTY);

        List<PQNode> list;
        if(treeNum == 1) {
            list = new ArrayList<>(Arrays.asList(A, B, C, D, E));
        }
        else if(treeNum == 2) {
            list = new ArrayList<>(Arrays.asList(A,C,E,D,B));
        }
        else {
            System.out.println("treeNum not set!");
            return null;
        }

        _root.addChildren(list);
        setCircularLinks(list);

        return Arrays.asList(_root, A, B, C, D, E);
    }

    @Test
    public void templateP2Test1(){
        List<PQNode> nodes = new ArrayList<>(templateP2Tree(1));
        PQNode _root = nodes.get(0);
        PQNode A = nodes.get(1);
        PQNode B = nodes.get(2);
        PQNode C = nodes.get(3);
        PQNode D = nodes.get(4);
        PQNode E = nodes.get(5);

        PQTree PQTree = new PQTree();
        boolean rt = PQTree.TEMPLATE_P2(_root);

        assertTrue(rt);

        assertTrue(A.getParent() != _root);
        assertTrue(B.getParent() != _root);
        assertTrue(C.getParent() != _root);

        assertTrue(D.getParent() == _root);
        assertTrue(E.getParent() == _root);

        assertTrue(A.getParent().getClass() == PNode.class);
        assertTrue(B.getParent().getClass() == PNode.class);
        assertTrue(C.getParent().getClass() == PNode.class);


        assertTrue(A.getParent().getLabel().equals(PQNode.FULL));

        assertTrue(_root.getChildren().size() == 3);

    }

    @Test
    public void templateP2Test2(){

        List<PQNode> nodes = new ArrayList<>(templateP2Tree(2));
        PQNode _root = nodes.get(0);
        PQNode A = nodes.get(1);
        PQNode B = nodes.get(2);
        PQNode C = nodes.get(3);
        PQNode D = nodes.get(4);
        PQNode E = nodes.get(5);

        PQTree PQTree = new PQTree();
        boolean rt = PQTree.TEMPLATE_P2(_root);

        assertTrue(rt);

        assertTrue(A.getParent() != _root);
        assertTrue(B.getParent() != _root);
        assertTrue(C.getParent() != _root);

        assertTrue(D.getParent() == _root);
        assertTrue(E.getParent() == _root);

        assertTrue(A.getParent().getClass() == PNode.class);
        assertTrue(B.getParent().getClass() == PNode.class);
        assertTrue(C.getParent().getClass() == PNode.class);

        assertTrue(A.getParent().getLabel().equals(PQNode.FULL));

    }

    private List<PQNode> templateP3Tree(int treeNum){
        PQNode _root = new PNode();
        PQNode parent = new PNode();
        PQNode A = new LeafNode();
        PQNode B = new LeafNode();
        PQNode C = new LeafNode();
        PQNode D = new LeafNode();
        PQNode E = new LeafNode();

        _root.setId("_root");
        parent.setId("parent");
        A.setId("A");
        B.setId("B");
        C.setId("C");
        D.setId("D");
        E.setId("E");

        _root.addChild(parent);
        //_root.nodeType = PQNode.PNODE;
        parent.setParent(_root);
        //parent.nodeType = PQNode.PNODE;

        parent.setPertinentChildCount(3);

        A.setParent(parent);
        B.setParent(parent);
        C.setParent(parent);
        D.setParent(parent);
        E.setParent(parent);

        A.setLabel(PQNode.FULL);
        B.setLabel(PQNode.FULL);
        C.setLabel(PQNode.FULL);

        D.setLabel(PQNode.EMPTY);
        E.setLabel(PQNode.EMPTY);


        List<PQNode> list;
        if(treeNum == 1) {
                list = new ArrayList<>(Arrays.asList(A, B, C, D, E));
        }
        else if(treeNum == 2){
            list = new ArrayList<>(Arrays.asList(C,B,A,E,D));
        }
        else {
            System.out.println("treeNum not set!");
            return null;
        }
        parent.clearChildren();
        parent.addChildren(list);
        setCircularLinks(list);

        return Arrays.asList(_root, parent, A, B, C, D, E);
    }

    @Test
    public void templateP3Test1(){
        List<PQNode> nodes = new ArrayList<>(templateP3Tree(1));
        PQNode _root = nodes.get(0);
        PQNode initialParent = nodes.get(1);
        PQNode A = nodes.get(2);
        PQNode B = nodes.get(3);
        PQNode C = nodes.get(4);
        PQNode D = nodes.get(5);
        PQNode E = nodes.get(6);

        PQTree PQTree = new PQTree();
        boolean rt = PQTree.TEMPLATE_P3(initialParent);
        PQNode parent = _root.getChildren().get(0);
        assertTrue(parent.getClass() == QNode.class);
        assertTrue(parent.getLabel() == PQNode.PARTIAL);
        assertTrue(parent.getChildren().size() == 2);
        for (PQNode notFullChild : parent.getChildren().get(0).getChildren()) {
            assertTrue(notFullChild.getLabel() != PQNode.FULL);
        }
        for (PQNode fullChild : parent.getChildren().get(1).getChildren()) {
            assertTrue(fullChild.getLabel() == PQNode.FULL);
        }
    }

    @Test
    public void templateP3Test2(){

        List<PQNode> nodes = new ArrayList<>(templateP3Tree(2));
        PQNode _root = nodes.get(0);
        PQNode initialParent = nodes.get(1);
        PQNode A = nodes.get(2);
        PQNode B = nodes.get(3);
        PQNode C = nodes.get(4);
        PQNode D = nodes.get(5);
        PQNode E = nodes.get(6);

        PQTree PQTree = new PQTree();
        boolean rt = PQTree.TEMPLATE_P3(initialParent);
        PQNode parent = _root.getChildren().get(0);
        assertTrue(parent.getClass() == QNode.class);
        assertTrue(parent.getLabel() == PQNode.PARTIAL);
        assertTrue(parent.getChildren().size() == 2);
        for (PQNode notFullChild : parent.getChildren().get(0).getChildren()) {
            assertTrue(notFullChild.getLabel() != PQNode.FULL);
        }
        for (PQNode fullChild : parent.getChildren().get(1).getChildren()) {
            assertTrue(fullChild.getLabel() == PQNode.FULL);
        }
    }

    private List<PQNode> templateP4Tree(int treeNum){
        PQNode _root = new PNode();
        PQNode A = new PQNode();
        PQNode B = new PQNode();
        PQNode C = new PQNode();
        PQNode D = new PQNode();

        PQNode qNode = new QNode();
        PQNode E = new PQNode();
        PQNode F = new PQNode();
        PQNode G = new PQNode();
        PQNode H = new PQNode();

        _root.setId("_root");
        A.setId("A");
        B.setId("B");
        C.setId("C");
        D.setId("D");
        qNode.setId("qNode");
        E.setId("E");
        F.setId("F");
        G.setId("G");
        H.setId("H");

        A.setParent(_root);
        B.setParent(_root);
        C.setParent(_root);
        D.setParent(_root);

        qNode.setParent(_root);
        E.setParent(qNode);
        F.setParent(qNode);
        G.setParent(qNode);
        H.setParent(qNode);

        A.setLabel(PQNode.EMPTY);
        B.setLabel(PQNode.EMPTY);
        C.setLabel(PQNode.FULL);
        D.setLabel(PQNode.FULL);

        qNode.setLabel(PQNode.PARTIAL);
        E.setLabel(PQNode.EMPTY);
        F.setLabel(PQNode.EMPTY);
        G.setLabel(PQNode.FULL);
        H.setLabel(PQNode.FULL);

        _root.setPertinentChildCount(3);
        qNode.setPertinentChildCount(2);

        _root.setLabel(PQNode.PARTIAL);

        List<PQNode> rootChildren;
        List<PQNode> qNodeChildren;
        if(treeNum == 1) {
            rootChildren = new ArrayList<>(Arrays.asList(A, B, qNode, C, D));
            qNodeChildren = new ArrayList<>(Arrays.asList(E, F, G, H));
        }
        else if(treeNum == 2){
            rootChildren = new ArrayList<>(Arrays.asList(qNode,B,C,D,A));
            qNodeChildren = new ArrayList<>(Arrays.asList(F,G,H,E));
        }
        else{
            System.out.println("treeNum not set!");
            return null;
        }

        setCircularLinks(rootChildren);
        setCircularLinks(qNodeChildren);
        _root.addChildren(rootChildren);
        qNode.setChildren(qNodeChildren);

        return Arrays.asList(_root, qNode, A, B, C, D, E, F, G, H);
    }

    @Test
    public void templateP4Test1(){
        List<PQNode> nodes = new ArrayList<>(templateP4Tree(1));
        PQNode _root = nodes.get(0);
        PQNode qNode = nodes.get(1);
        PQNode A = nodes.get(2);
        PQNode B = nodes.get(3);
        PQNode C = nodes.get(4);
        PQNode D = nodes.get(5);
        PQNode E = nodes.get(6);
        PQNode F = nodes.get(7);
        PQNode G = nodes.get(8);
        PQNode H = nodes.get(9);

        PQTree PQTree = new PQTree();
        boolean rt = PQTree.TEMPLATE_P4(_root);

        assertTrue(rt);
        assertTrue(A.getParent() == _root);
        assertTrue(B.getParent() == _root);
        assertTrue(qNode.getParent() == _root);

        assertTrue(C.getParent() != qNode && C.getParent() != _root);
        assertTrue(D.getParent() != qNode && D.getParent() != _root);

        assertTrue(E.getParent() == qNode);
        //assertTrue(F.getParent() == null);
        //assertTrue(G.getParent() == null);
        //assertTrue(H.getParent() == null);
        assertTrue(C.getParent() == D.getParent());
        assertTrue(C.getParent().getParent() == qNode);
        assertTrue(qNode.getChildren().contains(C.getParent()));
        assertTrue(_root.getChildren().contains(qNode));
        assertTrue(C.getParent().getClass() == PNode.class);
        assertTrue(C.getParent().getLabel().equals(PQNode.FULL));
        assertTrue(qNode.getLabel().equals(PQNode.PARTIAL));

    }

    @Test
    public void templateP4Test2(){
        List<PQNode> nodes = new ArrayList<>(templateP4Tree(2));
        PQNode _root = nodes.get(0);
        PQNode qNode = nodes.get(1);
        PQNode A = nodes.get(2);
        PQNode B = nodes.get(3);
        PQNode C = nodes.get(4);
        PQNode D = nodes.get(5);
        PQNode E = nodes.get(6);
        PQNode F = nodes.get(7);
        PQNode G = nodes.get(8);
        PQNode H = nodes.get(9);

        PQTree PQTree = new PQTree();
        boolean rt = PQTree.TEMPLATE_P4(_root);

        assertFalse(rt);
    }

    private List<PQNode> templateP5Tree(){
        PQNode _root = new PNode();
        _root.setId("_root");
        List<PQNode> empties = new ArrayList<PQNode>();
        List<PQNode> emptyChildrenOfQNode = new ArrayList<PQNode>();
        List<PQNode> pertinentChildrenOfQNode = new ArrayList<PQNode>();
        List<PQNode> fulls = new ArrayList<PQNode>();
        List<PQNode> S = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            PQNode empty = new PQNode();
            empty.setId("empty" + Integer.toString(i));
            empty.setParent(_root);
            empty.setLabel(PQNode.EMPTY);
            empties.add(empty);
        }

        PQNode partialQNode = new QNode();
        partialQNode.setLabel(PQNode.PARTIAL);
        partialQNode.setId("partialQNode");
        partialQNode.setParent(_root);

        for (int i = 0; i < 4; i++) {
            PQNode emptyChildOfQNode = new PQNode();
            emptyChildOfQNode.setId("emptyChildOfQNode" + Integer.toString(i));
            emptyChildOfQNode.setLabel(PQNode.EMPTY);
            emptyChildrenOfQNode.add(emptyChildOfQNode);
        }


        for (int i = 0; i < 4; i++) {
            PQNode pertinentChildOfQNode = new PQNode();
            pertinentChildOfQNode.setId("fullChildOfQNode" + Integer.toString(i));
            pertinentChildOfQNode.setLabel(PQNode.FULL);
            pertinentChildrenOfQNode.add(pertinentChildOfQNode);
            S.add(pertinentChildOfQNode);
        }

        List<PQNode> combinedChildrenOfQNode = new ArrayList<PQNode>();
        combinedChildrenOfQNode.addAll(emptyChildrenOfQNode);
        combinedChildrenOfQNode.addAll(pertinentChildrenOfQNode);
        combinedChildrenOfQNode.get(0).setParent(partialQNode);
        combinedChildrenOfQNode.get(combinedChildrenOfQNode.size() - 1).setParent(partialQNode);
        setCircularLinks(combinedChildrenOfQNode);


        for (int i = 0; i < 4; i++) {
            PQNode full = new PQNode();
            full.setId("partial" + Integer.toString(i));
            full.setParent(_root);
            full.setLabel(PQNode.FULL);
            fulls.add(full);
            S.add(full);
        }

        List<PQNode> combinedChildrenOfRoot = new ArrayList<PQNode>();
        combinedChildrenOfRoot.addAll(empties);
        combinedChildrenOfRoot.add(partialQNode);
        combinedChildrenOfRoot.addAll(fulls);
        setCircularLinks(combinedChildrenOfRoot);

        _root.addChildren(combinedChildrenOfRoot);

        _root.setPertinentChildCount(5);
        partialQNode.setPertinentChildCount(4);

        partialQNode.setChildren(combinedChildrenOfQNode);

        List<PQNode> retList = new ArrayList<>();
        retList.add(_root);
        retList.addAll(empties);
        retList.addAll(fulls);
        retList.addAll(S);
        return retList;
    }

    @Test
    public void templateP5Test(){
        List<PQNode> nodes = new ArrayList<>(templateP5Tree());

        PQNode _root = nodes.get(0);

        PQNode T = new PNode();
        T.addChild(_root);

        _root.setParent(T);

        List<PQNode> empties = new ArrayList<>();
        for(int i=1; i<5; i++){
            empties.add(nodes.get(i));
        }
        List<PQNode> fulls = new ArrayList<>();
        for(int i=5; i<9; i++){
            fulls.add(nodes.get(i));
        }


        PQTree PQTree = new PQTree();
        boolean ret = PQTree.TEMPLATE_P5(_root);
        assertTrue(ret);

        for(PQNode n : empties){
            assertTrue(n.getParent() != _root);
            assertTrue(n.getParent().getChildren().contains(n));
        }

        for(PQNode n : fulls){
            assertTrue(n.getParent() != _root);
            assertTrue(n.getParent().getChildren().contains(n));
        }

        PQNode leftmostPNode = (empties.get(0)).getParent();
        PQNode rightmostPNode = (fulls.get(0)).getParent();

        PQNode iter = leftmostPNode;
        int countRootChildren = 1;
        while(iter.getCircularLink_next() != leftmostPNode){
            countRootChildren++;
            iter = iter.getCircularLink_next();
        }
        assertTrue(countRootChildren == 10);

    }

    private List<List<PQNode>> templateP6Tree(){
        PQNode _root = new PNode();
        _root.setId("_root");
        List<PQNode> empties = new ArrayList<PQNode>();
        List<PQNode> emptyChildrenOfQNode1 = new ArrayList<PQNode>();
        List<PQNode> pertinentChildrenOfQNode1 = new ArrayList<PQNode>();
        List<PQNode> emptyChildrenOfQNode2 = new ArrayList<PQNode>();
        List<PQNode> pertinentChildrenOfQNode2 = new ArrayList<PQNode>();
        List<PQNode> fulls = new ArrayList<PQNode>();
        List<PQNode> S = new ArrayList<PQNode>();

        // QNode 1
        PQNode partialQNode1 = new QNode();
        //partialQNode1.nodeType = PQNode.QNODE;
        partialQNode1.setLabel(PQNode.PARTIAL);
        partialQNode1.setId("partialQNode");
        partialQNode1.setParent(_root); // Added
        for (int i = 0; i < 4; i++) {
            PQNode emptyChildOfQNode = new PQNode();
            emptyChildOfQNode.setId("emptyChildOfQNode1" + Integer.toString(i));
            emptyChildOfQNode.setLabel(PQNode.EMPTY);
            emptyChildrenOfQNode1.add(emptyChildOfQNode);
        }
        partialQNode1.addChild(emptyChildrenOfQNode1.get(0));

        for (int i = 0; i < 4; i++) {
            PQNode pertinentChildOfQNode = new PQNode();
            pertinentChildOfQNode.setId("fullChildOfQNode1" + Integer.toString(i));
            pertinentChildOfQNode.setLabel(PQNode.FULL);
            S.add(pertinentChildOfQNode);
            pertinentChildrenOfQNode1.add(pertinentChildOfQNode);
        }
        partialQNode1.addChild(pertinentChildrenOfQNode1.get(pertinentChildrenOfQNode1.size()-1));

        List<PQNode> combinedChildrenOfQNode1 = new ArrayList<PQNode>();
        combinedChildrenOfQNode1.addAll(emptyChildrenOfQNode1);
        combinedChildrenOfQNode1.addAll(pertinentChildrenOfQNode1);
        setCircularLinks(combinedChildrenOfQNode1);
        combinedChildrenOfQNode1.get(0).setParent(partialQNode1);
        combinedChildrenOfQNode1.get(combinedChildrenOfQNode1.size() - 1).setParent(partialQNode1);
        partialQNode1.setPertinentChildCount(pertinentChildrenOfQNode1.size());
        // Finished setting up QNode 1

        // QNode 2
        PQNode partialQNode2 = new QNode();
        partialQNode2.setLabel(PQNode.PARTIAL);
        partialQNode2.setId("partialQNode");
        partialQNode2.setParent(_root); // Added
        for (int i = 0; i < 4; i++) {
            PQNode emptyChildOfQNode = new PQNode();
            emptyChildOfQNode.setId("emptyChildOfQNode2" + Integer.toString(i));
            emptyChildOfQNode.setLabel(PQNode.EMPTY);
            emptyChildrenOfQNode2.add(emptyChildOfQNode);
        }
        partialQNode2.addChild(emptyChildrenOfQNode2.get(0));

        for (int i = 0; i < 4; i++) {
            PQNode pertinentChildOfQNode = new PQNode();
            pertinentChildOfQNode.setId("fullChildOfQNode2" + Integer.toString(i));
            pertinentChildOfQNode.setLabel(PQNode.FULL);
            pertinentChildrenOfQNode2.add(pertinentChildOfQNode);
            S.add(pertinentChildOfQNode);
        }
        partialQNode2.addChild(pertinentChildrenOfQNode2.get(pertinentChildrenOfQNode2.size()-1));

        List<PQNode> combinedChildrenOfQNode2 = new ArrayList<PQNode>();
        combinedChildrenOfQNode2.addAll(emptyChildrenOfQNode2);
        combinedChildrenOfQNode2.addAll(pertinentChildrenOfQNode2);
        setCircularLinks(combinedChildrenOfQNode2);
        combinedChildrenOfQNode2.get(0).setParent(partialQNode2);
        combinedChildrenOfQNode2.get(combinedChildrenOfQNode2.size() - 1).setParent(partialQNode2);
        partialQNode2.setPertinentChildCount(pertinentChildrenOfQNode2.size());
        // Finished setting up QNode 2


        partialQNode1.setQNodeEndmostChildren(emptyChildrenOfQNode1.get(0), pertinentChildrenOfQNode1.get(pertinentChildrenOfQNode1.size()-1));
        partialQNode1.setParentQNodeChildren();
        partialQNode2.setQNodeEndmostChildren(emptyChildrenOfQNode2.get(0), pertinentChildrenOfQNode2.get(pertinentChildrenOfQNode2.size()-1));
        partialQNode2.setParentQNodeChildren();

        // PNode (root)
        for (int i = 0; i < 4; i++) {
            PQNode empty = new PQNode();
            empty.setId("empty" + Integer.toString(i));
            empty.setParent(_root);
            empty.setLabel(PQNode.EMPTY);
            empties.add(empty);
        }
        for (int i = 0; i < 4; i++) {
            PQNode full = new PQNode();
            full.setId("partial" + Integer.toString(i));
            full.setParent(_root);
            full.setLabel(PQNode.FULL);
            S.add(full);
            fulls.add(full);
        }
        List<PQNode> combinedChildrenOfRoot = new ArrayList<>();
        combinedChildrenOfRoot.addAll(empties);
        combinedChildrenOfRoot.add(partialQNode1);
        combinedChildrenOfRoot.addAll(fulls);
        combinedChildrenOfRoot.add(partialQNode2);
        setCircularLinks(combinedChildrenOfRoot);
        _root.addChildren(combinedChildrenOfRoot);
        _root.setPertinentChildCount(fulls.size() + 2);
        // Finished setting up PNode (root)


        List<List<PQNode>> retList = new ArrayList<>();
        retList.add(Arrays.asList(_root));
        retList.add(empties);
        retList.add(fulls);
        retList.add(S);
        return retList;
    }

    @Test
    public void templateP6Test(){

        List<List<PQNode>> nodesList = new ArrayList<>(templateP6Tree());

        PQNode _root = nodesList.get(0).get(0);
        List<PQNode> empties = new ArrayList<>(nodesList.get(1));
        List<PQNode> fulls = new ArrayList<>(nodesList.get(2));

        // Begin Testing
        PQTree PQTree = new PQTree();
        boolean ret = PQTree.TEMPLATE_P6(_root);
        assertTrue(ret);

        for(PQNode n : empties){
            assertTrue(n.getParent() == _root);
        }

        for(PQNode n : fulls){
            assertTrue(n.getParent() != _root);
            assertTrue(n.getParent().getParent() != _root);
            assertTrue(n.getParent().getChildren().contains(n));
        }

        assertTrue(_root.getChildren().size() == empties.size() + 1);
        assertTrue(_root.getClass() == PNode.class);

        int qNodeCount = 0;
        PQNode mergingQNode = null;
        for(PQNode n : _root.getChildren()){
            if (n.getClass() == QNode.class) {
                qNodeCount++;
                mergingQNode = n;
            }
        }
        assertTrue(qNodeCount == 1);
        assertTrue(mergingQNode.getLabel().equals(PQNode.PARTIAL));
        assertTrue(mergingQNode.endmostChildren().size() == 2);

        PQNode iter = mergingQNode.endmostChildren().get(0);
        PQNode leftMostNode = iter;
        int nodeIndex = 0;
        int interiorPNodeCount = 0;
        PQNode interiorPNode = null;
        while(iter.getCircularLink_next() != leftMostNode){

            // Asserts first 4 nodes are EMPTY
            if(nodeIndex < 4) {
                assertTrue(iter.getLabel().equals(PQNode.EMPTY));
            }
            // Asserts node indices between 4 and 13 and PARTIAL/FULL
            else if(4 <= nodeIndex && nodeIndex <= 12){
                // Asserts if singular interior pNode is FULL
                if (iter.getClass() == PNode.class) {
                    assertTrue(iter.getLabel().equals(PQNode.FULL));
                    interiorPNode = iter;
                    interiorPNodeCount++;
                }
                // Asserts if non-PNodes are full
                else {
                    assertTrue(iter.getLabel().equals(PQNode.FULL));
                }
            }
            else {
                assertTrue(iter.getLabel().equals(PQNode.EMPTY));
            }

            nodeIndex++;
            iter = iter.getCircularLink_next();
        }

        assertTrue(interiorPNodeCount == 1);
        assertTrue(interiorPNode.getChildren().size() == 4);
        for(PQNode n : interiorPNode.getChildren()){
            assertTrue(n.getLabel().equals(PQNode.FULL));
        }
        // Finished testing
    }


    private List<PQNode> templateQ1Tree(){
        PQNode T = new PNode(PQNode.EMPTY);
        PQNode _root = new QNode(PQNode.EMPTY);
        PQNode A = new LeafNode();
        PQNode B = new LeafNode();

        T.addChild(_root);
        _root.setParent(T);
        _root.addChildren(Arrays.asList(A,B));
        A.setParent(_root);
        B.setParent(_root);
        A.setLabel(PQNode.FULL);
        B.setLabel(PQNode.FULL);

        _root.setPertinentChildCount(2);

        setCircularLinks(Arrays.asList(A, B));

        _root.setQNodeEndmostChildren(A, B);
        _root.setParentQNodeChildren();

        return Arrays.asList(_root, A, B);
    }

    @Test
    public void templateQ1Test(){
        List<PQNode> nodes = new ArrayList<>(templateQ1Tree());
        PQNode _root = nodes.get(0);
        PQNode A = nodes.get(1);
        PQNode B = nodes.get(2);

        PQTree PQTree = new PQTree();

        // All children blocked
        assertTrue(!_root.getLabel().equals(PQNode.FULL));
        boolean rt = PQTree.TEMPLATE_Q1(_root);
        assertTrue(rt);
        assertTrue(_root.getLabel().equals(PQNode.FULL));

        // One child not blocked
        PQNode C = new PQNode();
        C.setParent(_root);
        _root.setLabel(PQNode.EMPTY);
        C.setLabel(PQNode.EMPTY);

        _root.addChildren(Arrays.asList(A,B,C));
        rt = PQTree.TEMPLATE_P1(_root);
        assertTrue(!rt);
        assertTrue(!_root.getLabel().equals(PQNode.FULL));
    }

    private List<PQNode> templateQ2Tree(int treeNum){
        PQNode _root = new PNode();

        PQNode qNode = new QNode();
        PQNode A = new PQNode();
        PQNode B = new PQNode();

        PQNode C = new QNode();
        PQNode Ca = new PQNode();
        PQNode Cb = new PQNode();
        PQNode Cc = new PQNode();
        PQNode Cd = new PQNode();

        PQNode D = new PQNode();
        PQNode E = new PQNode();

        _root.addChild(qNode);

        qNode.setParent(_root);
        A.setParent(qNode);
        Ca.setParent(C);
        Cd.setParent(C);
        E.setParent(qNode);

        A.setId("A");
        B.setId("B");
        C.setId("C");
        D.setId("D");
        E.setId("E");
        Ca.setId("Ca");
        Cb.setId("Cb");
        Cc.setId("Cc");
        Cd.setId("Cd");
        qNode.setId("qNode");
        _root.setId("_rootQ");

        A.setLabel(PQNode.EMPTY);
        B.setLabel(PQNode.EMPTY);
        C.setLabel(PQNode.PARTIAL);
        Ca.setLabel(PQNode.EMPTY);
        Cb.setLabel(PQNode.EMPTY);
        Cc.setLabel(PQNode.FULL);
        Cd.setLabel(PQNode.FULL);
        D.setLabel(PQNode.FULL);
        E.setLabel(PQNode.FULL);

        C.setPertinentChildCount(2);
        qNode.setPertinentChildCount(1);
        _root.setPertinentChildCount(2);

        //Only A, E as official children, since this is a qnode
        if(treeNum == 1) {
            C.addChildren(Arrays.asList(Ca, Cd));
            qNode.addChildren(Arrays.asList(A, E));
            setCircularLinks(Arrays.asList(A, B, C, D, E));
            setCircularLinks(Arrays.asList(Ca, Cb, Cc, Cd));

            C.setQNodeEndmostChildren(Ca, Cd);
            C.setParentQNodeChildren();

            qNode.setQNodeEndmostChildren(A, E);
            qNode.setParentQNodeChildren();
        }
        else if(treeNum == 2){
            C.addChildren(Arrays.asList(Ca, Cd));
            qNode.addChildren(Arrays.asList(E, A));
            setCircularLinks(Arrays.asList(E, D, C, B, A));
            setCircularLinks(Arrays.asList(Ca, Cb, Cc, Cd));

            C.setQNodeEndmostChildren(Ca, Cd);
            C.setParentQNodeChildren();

            qNode.setQNodeEndmostChildren(E, A);
            qNode.setParentQNodeChildren();
        }
        else {
            System.out.println("treeNum not set!");
        }

        return Arrays.asList(_root, qNode, A, B, C, Ca, Cb, Cc, Cd, D, E);
    }

    @Test
    public void templateQ2Test(){
        List<PQNode> nodes = new ArrayList<>(templateQ2Tree(1));
        PQNode _root = nodes.get(0);
        PQNode qNode = nodes.get(1);
        PQNode A = nodes.get(2);
        PQNode B = nodes.get(3);
        PQNode C = nodes.get(4);
        PQNode Ca = nodes.get(5);
        PQNode Cb = nodes.get(6);
        PQNode Cc = nodes.get(7);
        PQNode Cd = nodes.get(8);
        PQNode D = nodes.get(9);
        PQNode E = nodes.get(10);

        PQTree PQTree = new PQTree();
        boolean rt = PQTree.TEMPLATE_Q2(qNode);

        assertTrue(rt);

        Set<PQNode> nodesToCheck = new HashSet<PQNode>();

        nodesToCheck.addAll(Arrays.asList(A, B, Ca, Cb, Cc, Cd, D, E));

        PQNode iterNode = qNode.getChildren().get(0);
        int flips = 0;

         // Below, we travel from left to right in the children list and counts
         // the number of children whose siblings's labels are different from theirs.
         // We call this number the 'number of flips'.
         //
         // This number has to be 1 to be valid.

        // While we have not checked the whole circular list
        do {
            if (nodesToCheck.contains(iterNode)) {
                nodesToCheck.remove(iterNode);
            }
            if (iterNode != qNode.getChildren().get(0) && iterNode.getCircularLink_prev().getLabel() != iterNode.getLabel()) {
                flips++;
            }
            iterNode = iterNode.getCircularLink_next();
        } while (iterNode != qNode.getChildren().get(0));

        assert(nodesToCheck.isEmpty());
        assert(flips == 1);

    }

    @Test
    public void templateQ2TestRearranged(){
        List<PQNode> nodes = new ArrayList<>(templateQ2Tree(2));
        PQNode _root = nodes.get(0);
        PQNode qNode = nodes.get(1);
        PQNode A = nodes.get(2);
        PQNode B = nodes.get(3);
        PQNode C = nodes.get(4);
        PQNode Ca = nodes.get(5);
        PQNode Cb = nodes.get(6);
        PQNode Cc = nodes.get(7);
        PQNode Cd = nodes.get(8);
        PQNode D = nodes.get(9);
        PQNode E = nodes.get(10);

        PQTree PQTree = new PQTree();
        boolean rt = PQTree.TEMPLATE_Q2(qNode);

        assertTrue(rt);

        Set<PQNode> nodesToCheck = new HashSet<PQNode>();

        nodesToCheck.addAll(Arrays.asList(A, B, Ca, Cb, Cc, Cd, D, E));

        PQNode iterNode = qNode.getChildren().get(0);
        int flips = 0;

         // Below, we travel from left to right in the children list and counts
         // the number of children whose siblings's labels are different from theirs.
         // We call this number the 'number of flips'.
         //
         // This number has to be 1 to be valid.

        // While we have not checked the whole circular list
        do {
            if (nodesToCheck.contains(iterNode)) {
                nodesToCheck.remove(iterNode);
            }
            if (iterNode != qNode.getChildren().get(0) && iterNode.getCircularLink_prev().getLabel() != iterNode.getLabel()) {
                flips++;
            }
            iterNode = iterNode.getCircularLink_next();
        } while (iterNode != qNode.getChildren().get(0));

        assert(nodesToCheck.isEmpty());
        assert(flips == 1);

    }

    @Test
    public void templateQ2TestTwoPartials(){
        PQNode _root = new PNode();

        PQNode qNode = new QNode();
        PQNode A = new PQNode();
        PQNode B = new PQNode();

        PQNode C = new QNode();
        PQNode Ca = new PQNode();
        PQNode Cb = new PQNode();
        PQNode Cc = new PQNode();
        PQNode Cd = new PQNode();
        PQNode D = new PQNode();
        PQNode E = new PQNode();

        _root.addChildren(Arrays.asList(qNode));

        qNode.setParent(_root);
        A.setParent(qNode);
        Ca.setParent(C);
        Cd.setParent(C);
        E.setParent(qNode);

        A.setId("A");
        B.setId("B");
        C.setId("C");
        D.setId("D");
        E.setId("E");
        Ca.setId("Ca");
        Cb.setId("Cb");
        Cc.setId("Cc");
        Cd.setId("Cd");
        qNode.setId("qNode");
        _root.setId("_root");

        A.setLabel(PQNode.EMPTY);
        B.setLabel(PQNode.PARTIAL);
        C.setLabel(PQNode.PARTIAL);
        Ca.setLabel(PQNode.EMPTY);
        Cb.setLabel(PQNode.EMPTY);
        Cc.setLabel(PQNode.FULL);
        Cd.setLabel(PQNode.FULL);
        D.setLabel(PQNode.FULL);
        E.setLabel(PQNode.FULL);

        qNode.setChildren(Arrays.asList(E, D, C, B, A));
        C.setChildren(Arrays.asList(Ca, Cb, Cc, Cd));

        PQTree PQTree = new PQTree();
        boolean rt = PQTree.TEMPLATE_Q2(qNode);

        assertFalse(rt);
    }

    private List<List<PQNode>> templateQ3Tree(){
        PQNode _root = new QNode();
        PQNode mainQNode = new QNode();
        PQNode partialLeftQNode = new QNode(PQNode.PARTIAL);
        PQNode partialRightQNode = new QNode(PQNode.PARTIAL);

        List<PQNode> emptyLeft = new ArrayList<PQNode>();
        List<PQNode> partialLeft = new ArrayList<PQNode>();
        List<PQNode> fullMid = new ArrayList<PQNode>();
        List<PQNode> partialRight = new ArrayList<PQNode>();
        List<PQNode> emptyRight = new ArrayList<PQNode>();

        //Populating emptyLeft
        for (int i = 0; i < 4; i++) {
            PQNode tmp = new PQNode();
            tmp.setLabel(PQNode.EMPTY);
            tmp.setId("emptyLeft" + Integer.toString(i));
            tmp.setParent(mainQNode);
            emptyLeft.add(tmp);
        }

        //Populating partialLeft
        for (int i = 0; i < 4; i++) {
            PQNode tmp = new PQNode();
            tmp.setLabel(PQNode.EMPTY);
            if (i == 0) {
                tmp.setParent(partialLeftQNode);
            }
            tmp.setId("partialLeft" + Integer.toString(i));
            partialLeft.add(tmp);
        }
        for (int i = 0; i < 4; i++) {
            PQNode tmp = new PQNode();
            tmp.setLabel(PQNode.FULL);
            if (i == 3) {
                tmp.setParent(partialLeftQNode);
            }
            tmp.setId("partialLeft" + Integer.toString(i));
            partialLeft.add(tmp);
        }

        //Populating fullMid
        for (int i = 0; i < 4; i++) {
            PQNode tmp = new PQNode();
            tmp.setLabel(PQNode.FULL);
            tmp.setId("fullMid" + Integer.toString(i));
            tmp.setParent(mainQNode);
            fullMid.add(tmp);
        }


        //Populating partialRight
        for (int i = 0; i < 4; i++) {
            PQNode tmp = new PQNode();
            tmp.setLabel(PQNode.EMPTY);
            if (i == 0) {
                tmp.setParent(partialRightQNode);
            }

            tmp.setId("partialRight" + Integer.toString(i));
            partialRight.add(tmp);
        }
        for (int i = 0; i < 4; i++) {
            PQNode tmp = new PQNode();
            tmp.setLabel(PQNode.FULL);
            if (i == 3) {
                tmp.setParent(partialRightQNode);
            }
            tmp.setId("partialRight" + Integer.toString(i));
            partialRight.add(tmp);
        }

        //Populating emptyRight
        for (int i = 0; i < 4; i++) {
            PQNode tmp = new PQNode();
            tmp.setLabel(PQNode.EMPTY);
            tmp.setId("emptyRight" + Integer.toString(i));
            emptyRight.add(tmp);
        }

        //Constructing temporary list for the purpose of setting circular links
        List<PQNode> circular = new ArrayList<PQNode>();
        circular.addAll(emptyLeft);
        circular.add(partialLeftQNode);
        circular.addAll(fullMid);
        circular.add(partialRightQNode);
        circular.addAll(emptyRight);


        setCircularLinks(partialLeft);
        setCircularLinks(partialRight);

        setCircularLinks(circular);

        //Using same list as before to set the children
        mainQNode.setChildren(circular);
        partialLeftQNode.setChildren(partialLeft);
        partialRightQNode.setChildren(partialRight);

        List<PQNode> QNodes = new ArrayList<>();
        QNodes.add(partialLeftQNode);
        QNodes.add(partialRightQNode);

        List<List<PQNode>> retList = new ArrayList<>();

        retList.add(Arrays.asList(mainQNode));
        retList.add(emptyLeft);
        retList.add(partialLeft);
        retList.add(fullMid);
        retList.add(partialRight);
        retList.add(emptyRight);
        retList.add(QNodes);
        return retList;
    }

    @Test
    public void templateQ3Test1() {
        List<List<PQNode>> nodesList = new ArrayList<>(templateQ3Tree());
        PQNode mainQNode = nodesList.get(0).get(0);
        List<PQNode> emptyLeft = new ArrayList<>(nodesList.get(1));
        List<PQNode> partialLeft = new ArrayList<>(nodesList.get(2));
        List<PQNode> fullMid = new ArrayList<>(nodesList.get(3));
        List<PQNode> partialRight = new ArrayList<>(nodesList.get(4));
        List<PQNode> emptyRight = new ArrayList<>(nodesList.get(5));

        PQTree PQTree = new PQTree();
        boolean rt = PQTree.TEMPLATE_Q3(mainQNode);
        assertTrue(rt);
        int sumOfParts = emptyLeft.size() + partialLeft.size() + fullMid.size() + partialRight.size() + emptyRight.size();
        assertTrue(mainQNode.getChildren().size() == sumOfParts);
        assertTrue(PQHelpers.checkIfConsecutive(mainQNode.getChildren()));
    }

    @Test
    public void templateQ3Test2() {
        List<List<PQNode>> nodesList = new ArrayList<>(templateQ3Tree());
        PQNode mainQNode = nodesList.get(0).get(0);
        List<PQNode> emptyLeft = new ArrayList<>(nodesList.get(1));
        List<PQNode> partialLeft = new ArrayList<>(nodesList.get(2));
        List<PQNode> fullMid = new ArrayList<>(nodesList.get(3));
        List<PQNode> partialRight = new ArrayList<>(nodesList.get(4));
        List<PQNode> emptyRight = new ArrayList<>(nodesList.get(5));

        List<PQNode> QNodes = new ArrayList<>(nodesList.get(6));
        QNode leftQNode = (QNode) QNodes.get(0);
        QNode rightQNode = (QNode) QNodes.get(1);

        leftQNode.rotate();

        PQTree PQTree = new PQTree();
        boolean rt = PQTree.TEMPLATE_Q3(mainQNode);
        assertTrue(rt);

        //for(PQNode n : mainQNode.getChildren()){
        //    System.out.print(n.labelType + " ");
        //}

        int sumOfParts = emptyLeft.size() + partialLeft.size() + fullMid.size() + partialRight.size() + emptyRight.size();
        assertTrue(mainQNode.getChildren().size() == sumOfParts);
        assertTrue(PQHelpers.checkIfConsecutive(mainQNode.getChildren()));
    }

    @Test
    public void templateQ3Test3() {
        List<List<PQNode>> nodesList = new ArrayList<>(templateQ3Tree());
        PQNode mainQNode = nodesList.get(0).get(0);
        List<PQNode> emptyLeft = new ArrayList<>(nodesList.get(1));
        List<PQNode> partialLeft = new ArrayList<>(nodesList.get(2));
        List<PQNode> fullMid = new ArrayList<>(nodesList.get(3));
        List<PQNode> partialRight = new ArrayList<>(nodesList.get(4));
        List<PQNode> emptyRight = new ArrayList<>(nodesList.get(5));

        List<PQNode> QNodes = new ArrayList<>(nodesList.get(6));
        QNode leftQNode = (QNode) QNodes.get(0);
        QNode rightQNode = (QNode) QNodes.get(1);

        rightQNode.rotate();

        PQTree PQTree = new PQTree();
        boolean rt = PQTree.TEMPLATE_Q3(mainQNode);
        assertTrue(rt);

        //for(PQNode n : mainQNode.getChildren()){
        //    System.out.print(n.labelType + " ");
        //}

        int sumOfParts = emptyLeft.size() + partialLeft.size() + fullMid.size() + partialRight.size() + emptyRight.size();
        assertTrue(mainQNode.getChildren().size() == sumOfParts);
        assertTrue(PQHelpers.checkIfConsecutive(mainQNode.getChildren()));
    }

    @Test
    public void templateQ3Test4() {
        List<List<PQNode>> nodesList = new ArrayList<>(templateQ3Tree());
        PQNode mainQNode = nodesList.get(0).get(0);
        List<PQNode> emptyLeft = new ArrayList<>(nodesList.get(1));
        List<PQNode> partialLeft = new ArrayList<>(nodesList.get(2));
        List<PQNode> fullMid = new ArrayList<>(nodesList.get(3));
        List<PQNode> partialRight = new ArrayList<>(nodesList.get(4));
        List<PQNode> emptyRight = new ArrayList<>(nodesList.get(5));

        List<PQNode> QNodes = new ArrayList<>(nodesList.get(6));
        QNode leftQNode = (QNode) QNodes.get(0);
        QNode rightQNode = (QNode) QNodes.get(1);

        leftQNode.rotate();
        rightQNode.rotate();

        PQTree PQTree = new PQTree();
        boolean rt = PQTree.TEMPLATE_Q3(mainQNode);
        assertTrue(rt);

        //for(PQNode n : mainQNode.getChildren()){
        //    System.out.print(n.labelType + " ");
        //}

        int sumOfParts = emptyLeft.size() + partialLeft.size() + fullMid.size() + partialRight.size() + emptyRight.size();
        assertTrue(mainQNode.getChildren().size() == sumOfParts);
        assertTrue(PQHelpers.checkIfConsecutive(mainQNode.getChildren()));
    }

    @Test
    public void templateL1Test(){

    }

    @Test
    public void rootTest1(){
        PQNode z = new PNode(PQNode.EMPTY);
        PQNode x = new PQNode("X");
        PQNode a = new PNode(PQNode.EMPTY);
        PQNode b = new PNode(PQNode.PARTIAL);
        PQNode c = new PNode(PQNode.FULL);
        PQNode d = new PQNode("D");
        PQNode e = new PQNode("E");
        PQNode f = new PNode(PQNode.PARTIAL);
        PQNode g = new PQNode("G");
        PQNode h = new PQNode("H");
        PQNode i = new PQNode("I");

        z.addChildren(Arrays.asList(x, a));
        x.setParent(z);
        a.setParent(z);

        a.addChildren(Arrays.asList(b, f));

        b.setParent(a);
        f.setParent(a);
        b.addChildren(Arrays.asList(c, e));
        c.setParent(b);
        e.setParent(b);
        c.addChild(d);
        d.setParent(c);

        f.setParent(a);
        f.addChildren(Arrays.asList(g, h, i));
        g.setParent(f);
        h.setParent(f);
        i.setParent(f);

        List<PQNode> S = new ArrayList<>();
        S.addAll(Arrays.asList(d, e, h));

        PQTree pqTree = new PQTree();
        PQNode _root = pqTree.root(z, S);

        assertTrue(_root == a);

    }

}

