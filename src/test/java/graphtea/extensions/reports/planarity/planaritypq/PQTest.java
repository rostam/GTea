package graphtea.extensions.reports.planarity.planaritypq;

import org.junit.Test;

import java.lang.reflect.Array;
import java.util.*;

import static graphtea.extensions.reports.planarity.planaritypq.PQHelpers.printChildren;
import static graphtea.extensions.reports.planarity.planaritypq.PQHelpers.setCircularLinks;
import static graphtea.extensions.reports.planarity.planaritypq.PQHelpers.union;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;


public class PQTest {

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
        A.parent = _root;
        B.parent = _root;
        C.parent = _root;

        // Constraint set S := {B,C}
        List<PQNode> S = new ArrayList<>();

        //Get preorder before
        List<PQNode> preorderBefore = PQHelpers.preorder(_root);

        // Test PQTree
        PQ PQTree = new PQ();
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
        A.parent = _root;
        B.parent = _root;
        C.parent = _root;
        List<PQNode> list = new ArrayList<>(Arrays.asList(A,B,C));
        setCircularLinks(list);

        // Constraint set S := {B,C}
        List<PQNode> S = Arrays.asList(B, C);

        //Get preorder before
        List<PQNode> preorderBefore = PQHelpers.preorder(_root);

        // Test PQTree
        PQ PQTree = new PQ();
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

        assertTrue(_root.pertinentChildCount == 2);
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
        A.parent = _root;
        B.parent = _root;
        C.parent = _root;
        qNode.parent = _root;
        D.parent = qNode;
        F.parent = qNode;

        setCircularLinks(Arrays.asList(qNode,A,B,C));
        setCircularLinks(Arrays.asList(D,E,F));

        qNode.setQNodeEndmostChildren(D, F);
        qNode.setParentQNodeChildren();


        // Constraint set S
        List<PQNode> S = Arrays.asList(D, E, F);

        // Test PQTree
        PQ PQTree = new PQ();
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
        A.parent = _root;
        B.parent = _root;
        C.parent = _root;
        qNode.parent = _root;
        D.parent = qNode;
        F.parent = qNode;

        setCircularLinks(Arrays.asList(qNode,A,B,C));
        setCircularLinks(Arrays.asList(D,E,F));

        qNode.setQNodeEndmostChildren(D, F);
        qNode.setParentQNodeChildren();

        // Constraint set S
        List<PQNode> S = Arrays.asList(D, E, A);

        // Test PQTree
        PQ PQTree = new PQ();
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
        _root.id = "root";
        A.id = "A";
        B.id = "B";
        C.id = "C";
        D.id = "D";
        E.id = "E";
        F.id = "F";
        G.id = "G";
        H.id = "H";

        _root.addChildren(Arrays.asList(A, B, C, qNode));
        A.parent = _root;
        B.parent = _root;
        C.parent = _root;
        qNode.parent = _root;
        D.parent = qNode;
        E.parent = qNode;
        F.parent = qNode;
        G.parent = qNode;
        H.parent = qNode;

        setCircularLinks(Arrays.asList(qNode,A,B,C));
        setCircularLinks(Arrays.asList(D,E,F,G,H));

        qNode.setQNodeEndmostChildren(D, H);
        qNode.setParentQNodeChildren();

        // Constraint set S
        List<PQNode> S = Arrays.asList(F, B, C);

        // Test PQTree
        PQ PQTree = new PQ();
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
        A.nodeType = PQNode.PSEUDO_NODE;
        B.nodeType = PQNode.PSEUDO_NODE;
        C.nodeType = PQNode.PSEUDO_NODE;

        _root.addChildren(Arrays.asList(A, B, C));
        A.parent = _root;
        B.parent = _root;
        C.parent = _root;
        List<PQNode> list = new ArrayList<>(Arrays.asList(A,B,C));
        setCircularLinks(list);

        // Constraint set S := {B,C}
        List<PQNode> S = Arrays.asList(B, C);

        PQ PQTree = new PQ();
        PQNode r = PQTree.reduce(_root, S);

        assertTrue(r != null);
    }

    @Test
    public void reduceTemplateP1Test(){
        List<PQNode> nodes = new ArrayList<>(templateP1Tree());
        PNode _root = (PNode) nodes.get(0);
        PQNode A = nodes.get(1);
        PQNode B = nodes.get(2);

        PQ PQTree = new PQ();
        List<PQNode> S = new ArrayList<>(Arrays.asList(A, B));
        PQNode rt = PQTree.reduce(_root, S);

        assertTrue(rt != null);
        assertTrue(rt == _root);
        assertTrue(_root.labelType.equals(PQNode.FULL));
        assertTrue(A.labelType.equals(PQNode.FULL));
        assertTrue(B.labelType.equals(PQNode.FULL));
        assertTrue(_root.getChildren().size() == 2);
        assertTrue(A.parent == _root);
        assertTrue(B.parent == _root);
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

        PQ PQTree = new PQ();
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
            assertTrue(n.parent == pNode);
            assertTrue(n.labelType.equals(PQNode.FULL));
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
        //extraNode.labelType.equals(PQNode.FULL);
        extraNode.id = "extraNode";
        extraNode.parent = _root;
        _root.addChild(extraNode);

        PQ PQTree = new PQ();

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
        assertTrue(parent.labelType.equals(PQNode.PARTIAL));
        assertTrue(parent.getChildren().size() == 2);
        for (PQNode notFullChild : parent.getChildren().get(0).getChildren()) {
            assertTrue(!notFullChild.labelType.equals(PQNode.FULL));
        }
        for (PQNode fullChild : parent.getChildren().get(1).getChildren()) {
            assertTrue(fullChild.labelType.equals(PQNode.FULL));
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

        PQ PQTree = new PQ();
        List<PQNode> S = new ArrayList<>(Arrays.asList(C, D, G, H));
        PQNode rt = PQTree.reduce(_root, S);

        assertTrue(A.parent == _root);
        assertTrue(B.parent == _root);
        assertTrue(qNode.parent == _root);

        assertTrue(C.parent != qNode && C.parent != _root);
        assertTrue(D.parent != qNode && D.parent != _root);

        assertTrue(E.parent == qNode);
        assertTrue(F.parent == null);
        assertTrue(G.parent == null);
        assertTrue(H.parent == null);
        assertTrue(C.parent == D.parent);
        assertTrue(C.parent.parent == qNode);
        assertTrue(qNode.getChildren().contains(C.parent));
        assertTrue(_root.getChildren().contains(qNode));
        assertTrue(C.parent.getClass() == PNode.class);
        assertTrue(C.parent.labelType.equals(PQNode.FULL));
        assertTrue(qNode.labelType.equals(PQNode.PARTIAL));

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
        extraPNode.labelType = PQNode.FULL;
        _root.parent = extraPNode;
        extraPNode.addChild(_root);

        PQNode extraSNode = new PQNode();
        extraSNode.labelType = PQNode.FULL;
        extraSNode.parent = extraPNode;
        extraPNode.addChild(extraSNode);
        // end of extra nodes

        List<PQNode> S = new ArrayList<>();
        for(int i=9; i<17; i++){
            S.add(nodes.get(i));
        }
        S.add(extraSNode);

        PQNode head = new PNode();
        head.addChild(_root);
        _root.parent = head;
        PQ PQTree = new PQ();

        PQNode rt = PQTree.reduce(_root, S);

        PQNode _r = head.getChildren().get(0);

        for(PQNode n : empties){
            assertTrue(n.parent != _r);
            assertTrue(n.getParent().parent == _r);
            assertTrue(n.getParent().getChildren().contains(n));
        }

        for(PQNode n : fulls){
            assertTrue(n.parent != _r);
            assertTrue(n.getParent().parent == _r);
            assertTrue(n.parent.getChildren().contains(n));
        }

        PQNode leftmostPNode = (empties.get(0)).parent;
        PQNode rightmostPNode = (fulls.get(0)).parent;

        assertTrue(_r.endmostChildren().contains(leftmostPNode));
        assertTrue(_r.endmostChildren().contains(rightmostPNode));

        assertTrue(_r.endmostChildren().size() == 2);

        assertTrue(_r.labelType.equals(PQNode.PARTIAL));
        assertTrue(_r.getClass() == QNode.class);

        PQNode iter = leftmostPNode;
        int countRootChildren = 1;
        while(iter.circularLink_next != leftmostPNode){
            countRootChildren++;
            iter = iter.circularLink_next;
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
        PQ PQTree = new PQ();
        PQNode rt = PQTree.reduce(_root, S);

        for(PQNode n : empties){
            assertTrue(n.parent == _root);
        }

        for(PQNode n : fulls){
            assertTrue(n.parent != _root);
            assertTrue(n.parent.parent != _root);
            assertTrue(n.parent.getChildren().contains(n));
        }

        assertTrue(_root.children.size() == empties.size() + 1);
        assertTrue(_root.getClass() == PNode.class);

        int qNodeCount = 0;
        PQNode mergingQNode = null;
        for(PQNode n : _root.children){
            if (n.getClass() == QNode.class) {
                qNodeCount++;
                mergingQNode = n;
            }
        }
        assertTrue(qNodeCount == 1);
        assertTrue(mergingQNode.labelType.equals(PQNode.PARTIAL));
        assertTrue(mergingQNode.endmostChildren().size() == 2);

        PQNode iter = mergingQNode.endmostChildren().get(0);
        PQNode leftMostNode = iter;
        int nodeIndex = 0;
        int interiorPNodeCount = 0;
        PQNode interiorPNode = null;
        while(iter.circularLink_next != leftMostNode){

            // Asserts first 4 nodes are EMPTY
            if(nodeIndex < 4) {
                assertTrue(iter.labelType.equals(PQNode.EMPTY));
            }
            // Asserts node indices between 4 and 13 and PARTIAL/FULL
            else if(4 <= nodeIndex && nodeIndex <= 12){
                // Asserts if singular interior pNode is FULL
                if (iter.getClass() == PNode.class) {
                    assertTrue(iter.labelType.equals(PQNode.FULL));
                    interiorPNode = iter;
                    interiorPNodeCount++;
                }
                // Asserts if non-PNodes are full
                else {
                    assertTrue(iter.labelType.equals(PQNode.FULL));
                }
            }
            else {
                assertTrue(iter.labelType.equals(PQNode.EMPTY));
            }

            nodeIndex++;
            iter = iter.circularLink_next;
        }

        assertTrue(interiorPNodeCount == 1);
        assertTrue(interiorPNode.getChildren().size() == 4);
        for(PQNode n : interiorPNode.getChildren()){
            assertTrue(n.labelType.equals(PQNode.FULL));
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

        _root.id = "_root";
        A.id = "A";
        B.id = "B";

        // Extra nodes so Q1 is not ROOT(T, S)
        PNode extraPNode = new PNode();
        extraPNode.addChild(_root);
        _root.parent = extraPNode;

        PQNode extraSNode = new PQNode();
        extraSNode.parent = extraPNode;
        extraPNode.getChildren().add(extraSNode);
        extraSNode.labelType = PQNode.FULL;

        extraPNode.id = "extraPNode";
        extraSNode.id = "extraSNode";
        // end of extra nodes

        List<PQNode> S = new ArrayList<>();
        S.add(A);
        S.add(B);
        S.add(extraSNode);

        PQ PQTree = new PQ();

        // All children blocked
        assertTrue(!_root.labelType.equals(PQNode.FULL));
        //PQNode rt = PQTree.reduce(_root, S);
        PQNode rt = PQTree.reduce(extraPNode, S);
        assertTrue(_root.labelType.equals(PQNode.FULL));

        // One child not blocked
        PQNode C = new PQNode();
        C.parent = _root;
        _root.labelType = PQNode.EMPTY;
        C.labelType = PQNode.EMPTY;
        C.id = "C";

        _root.clearChildren();
        _root.addChildren(Arrays.asList(A,C));
        B.parent = null;
        setCircularLinks(Arrays.asList(A,B,C));

        rt = PQTree.reduce(extraPNode, S);
        assertTrue(rt != null);
        assertTrue(!_root.labelType.equals(PQNode.FULL));
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
        extraSNode.parent = _root;
        _root.addChild(extraSNode);

        List<PQNode> S = new ArrayList<>(Arrays.asList(Cc, Cd, D, E, extraSNode));

        PQ PQTree = new PQ();
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
            if (iterNode != qNode.getChildren().get(0) && iterNode.circularLink_prev.labelType != iterNode.labelType) {
                flips++;
            }
            iterNode = iterNode.circularLink_next;
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

        _root.id="_root";
        A.id = "A";
        B.id = "B";

        _root.pertinentChildCount = 2;

        _root.addChildren(Arrays.asList(A,B));
        A.parent = _root;
        B.parent = _root;
        _root.labelType = PQNode.EMPTY;
        A.labelType = PQNode.FULL;
        B.labelType = PQNode.FULL;

        return Arrays.asList(_root, A, B);
    }

    @Test
    public void templateP1Test(){
        List<PQNode> nodes = new ArrayList<>(templateP1Tree());
        PQNode _root = nodes.get(0);
        PQNode A = nodes.get(1);
        PQNode B = nodes.get(2);

        PQ PQTree = new PQ();

        // All children blocked
        assertTrue(!_root.labelType.equals(PQNode.FULL));
        boolean rt = PQTree.TEMPLATE_P1(_root);
        assertTrue(rt);
        assertTrue(_root.labelType.equals(PQNode.FULL));

        // One child not blocked
        PQNode C = new PQNode();
        C.parent = _root;
        _root.labelType = PQNode.EMPTY;
        C.labelType = PQNode.EMPTY;

        _root.addChildren(Arrays.asList(A,B,C));
        rt = PQTree.TEMPLATE_P1(_root);
        assertTrue(!rt);
        assertTrue(!_root.labelType.equals(PQNode.FULL));
    }

    private List<PQNode> templateP2Tree(int treeNum){
        PQNode _root = new PNode();
        PQNode A = new LeafNode();
        PQNode B = new LeafNode();
        PQNode C = new LeafNode();
        PQNode D = new LeafNode();
        PQNode E = new LeafNode();

        _root.id = "root";
        A.id = "A";
        B.id = "B";
        C.id = "C";
        D.id = "D";
        E.id = "E";

        _root.pertinentChildCount = 3;

        A.parent = _root;
        B.parent = _root;
        C.parent = _root;
        D.parent = _root;
        E.parent = _root;

        A.labelType = PQNode.FULL;
        B.labelType = PQNode.FULL;
        C.labelType = PQNode.FULL;

        D.labelType = PQNode.EMPTY;
        E.labelType = PQNode.EMPTY;

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

        PQ PQTree = new PQ();
        boolean rt = PQTree.TEMPLATE_P2(_root);

        assertTrue(rt);

        assertTrue(A.parent != _root);
        assertTrue(B.parent != _root);
        assertTrue(C.parent != _root);

        assertTrue(D.parent == _root);
        assertTrue(E.parent == _root);

        assertTrue(A.parent.getClass() == PNode.class);
        assertTrue(B.parent.getClass() == PNode.class);
        assertTrue(C.parent.getClass() == PNode.class);


        assertTrue(A.parent.labelType.equals(PQNode.FULL));

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

        PQ PQTree = new PQ();
        boolean rt = PQTree.TEMPLATE_P2(_root);

        assertTrue(rt);

        assertTrue(A.parent != _root);
        assertTrue(B.parent != _root);
        assertTrue(C.parent != _root);

        assertTrue(D.parent == _root);
        assertTrue(E.parent == _root);

        assertTrue(A.parent.getClass() == PNode.class);
        assertTrue(B.parent.getClass() == PNode.class);
        assertTrue(C.parent.getClass() == PNode.class);

        assertTrue(A.parent.labelType.equals(PQNode.FULL));

    }

    private List<PQNode> templateP3Tree(int treeNum){
        PQNode _root = new PNode();
        PQNode parent = new PNode();
        PQNode A = new LeafNode();
        PQNode B = new LeafNode();
        PQNode C = new LeafNode();
        PQNode D = new LeafNode();
        PQNode E = new LeafNode();

        _root.id = "_root";
        parent.id = "parent";
        A.id = "A";
        B.id = "B";
        C.id = "C";
        D.id = "D";
        E.id = "E";

        _root.addChild(parent);
        _root.nodeType = PQNode.PNODE;
        parent.parent = _root;
        parent.nodeType = PQNode.PNODE;

        parent.pertinentChildCount = 3;

        A.parent = parent;
        B.parent = parent;
        C.parent = parent;
        D.parent = parent;
        E.parent = parent;

        A.labelType = PQNode.FULL;
        B.labelType = PQNode.FULL;
        C.labelType = PQNode.FULL;

        D.labelType = PQNode.EMPTY;
        E.labelType = PQNode.EMPTY;


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

        PQ PQTree = new PQ();
        boolean rt = PQTree.TEMPLATE_P3(initialParent);
        PQNode parent = _root.getChildren().get(0);
        assertTrue(parent.getClass() == QNode.class);
        assertTrue(parent.labelType == PQNode.PARTIAL);
        assertTrue(parent.getChildren().size() == 2);
        for (PQNode notFullChild : parent.getChildren().get(0).getChildren()) {
            assertTrue(notFullChild.labelType != PQNode.FULL);
        }
        for (PQNode fullChild : parent.getChildren().get(1).getChildren()) {
            assertTrue(fullChild.labelType == PQNode.FULL);
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

        PQ PQTree = new PQ();
        boolean rt = PQTree.TEMPLATE_P3(initialParent);
        PQNode parent = _root.getChildren().get(0);
        assertTrue(parent.getClass() == QNode.class);
        assertTrue(parent.labelType == PQNode.PARTIAL);
        assertTrue(parent.getChildren().size() == 2);
        for (PQNode notFullChild : parent.getChildren().get(0).getChildren()) {
            assertTrue(notFullChild.labelType != PQNode.FULL);
        }
        for (PQNode fullChild : parent.getChildren().get(1).getChildren()) {
            assertTrue(fullChild.labelType == PQNode.FULL);
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

        _root.id = "_root";
        A.id = "A";
        B.id = "B";
        C.id = "C";
        D.id = "D";
        qNode.id = "qNode";
        E.id = "E";
        F.id = "F";
        G.id = "G";
        H.id = "H";

        A.parent = _root;
        B.parent = _root;
        C.parent = _root;
        D.parent = _root;

        qNode.parent = _root;
        E.parent = qNode;
        F.parent = qNode;
        G.parent = qNode;
        H.parent = qNode;

        A.labelType = PQNode.EMPTY;
        B.labelType = PQNode.EMPTY;
        C.labelType = PQNode.FULL;
        D.labelType = PQNode.FULL;

        qNode.labelType = PQNode.PARTIAL;
        E.labelType = PQNode.EMPTY;
        F.labelType = PQNode.EMPTY;
        G.labelType = PQNode.FULL;
        H.labelType = PQNode.FULL;

        _root.pertinentChildCount = 3;
        qNode.pertinentChildCount = 2;

        _root.labelType = PQNode.PARTIAL;

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

        PQ PQTree = new PQ();
        boolean rt = PQTree.TEMPLATE_P4(_root);

        assertTrue(rt);
        assertTrue(A.parent == _root);
        assertTrue(B.parent == _root);
        assertTrue(qNode.parent == _root);

        assertTrue(C.parent != qNode && C.parent != _root);
        assertTrue(D.parent != qNode && D.parent != _root);

        assertTrue(E.parent == qNode);
        assertTrue(F.parent == null);
        assertTrue(G.parent == null);
        assertTrue(H.parent == null);
        assertTrue(C.parent == D.parent);
        assertTrue(C.parent.parent == qNode);
        assertTrue(qNode.getChildren().contains(C.parent));
        assertTrue(_root.getChildren().contains(qNode));
        assertTrue(C.parent.getClass() == PNode.class);
        assertTrue(C.parent.labelType.equals(PQNode.FULL));
        assertTrue(qNode.labelType.equals(PQNode.PARTIAL));

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

        PQ PQTree = new PQ();
        boolean rt = PQTree.TEMPLATE_P4(_root);

        assertFalse(rt);
    }

    private List<PQNode> templateP5Tree(){
        PQNode _root = new PNode();
        _root.id = "_root";
        List<PQNode> empties = new ArrayList<PQNode>();
        List<PQNode> emptyChildrenOfQNode = new ArrayList<PQNode>();
        List<PQNode> pertinentChildrenOfQNode = new ArrayList<PQNode>();
        List<PQNode> fulls = new ArrayList<PQNode>();
        List<PQNode> S = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            PQNode empty = new PQNode();
            empty.id = "empty" + Integer.toString(i);
            empty.parent = _root;
            empty.labelType = PQNode.EMPTY;
            empties.add(empty);
        }

        PQNode partialQNode = new QNode();
        partialQNode.labelType = PQNode.PARTIAL;
        partialQNode.id = "partialQNode";
        partialQNode.parent = _root;

        for (int i = 0; i < 4; i++) {
            PQNode emptyChildOfQNode = new PQNode();
            emptyChildOfQNode.id = "emptyChildOfQNode" + Integer.toString(i);
            emptyChildOfQNode.labelType = PQNode.EMPTY;
            emptyChildrenOfQNode.add(emptyChildOfQNode);
        }


        for (int i = 0; i < 4; i++) {
            PQNode pertinentChildOfQNode = new PQNode();
            pertinentChildOfQNode.id = "fullChildOfQNode" + Integer.toString(i);
            pertinentChildOfQNode.labelType = PQNode.FULL;
            pertinentChildrenOfQNode.add(pertinentChildOfQNode);
            S.add(pertinentChildOfQNode);
        }

        List<PQNode> combinedChildrenOfQNode = new ArrayList<PQNode>();
        combinedChildrenOfQNode.addAll(emptyChildrenOfQNode);
        combinedChildrenOfQNode.addAll(pertinentChildrenOfQNode);
        combinedChildrenOfQNode.get(0).parent = partialQNode;
        combinedChildrenOfQNode.get(combinedChildrenOfQNode.size()-1).parent = partialQNode;
        setCircularLinks(combinedChildrenOfQNode);


        for (int i = 0; i < 4; i++) {
            PQNode full = new PQNode();
            full.id = "partial" + Integer.toString(i);
            full.parent = _root;
            full.labelType = PQNode.FULL;
            fulls.add(full);
            S.add(full);
        }

        List<PQNode> combinedChildrenOfRoot = new ArrayList<PQNode>();
        combinedChildrenOfRoot.addAll(empties);
        combinedChildrenOfRoot.add(partialQNode);
        combinedChildrenOfRoot.addAll(fulls);
        setCircularLinks(combinedChildrenOfRoot);

        _root.addChildren(combinedChildrenOfRoot);

        _root.pertinentChildCount = 5;
        partialQNode.pertinentChildCount = 4;

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

        _root.parent = T;

        List<PQNode> empties = new ArrayList<>();
        for(int i=1; i<5; i++){
            empties.add(nodes.get(i));
        }
        List<PQNode> fulls = new ArrayList<>();
        for(int i=5; i<9; i++){
            fulls.add(nodes.get(i));
        }


        PQ PQTree = new PQ();
        boolean ret = PQTree.TEMPLATE_P5(_root);
        assertTrue(ret);

        for(PQNode n : empties){
            assertTrue(n.parent != _root);
            assertTrue(n.parent.getChildren().contains(n));
        }

        for(PQNode n : fulls){
            assertTrue(n.parent != _root);
            assertTrue(n.parent.getChildren().contains(n));
        }

        PQNode leftmostPNode = (empties.get(0)).parent;
        PQNode rightmostPNode = (fulls.get(0)).parent;

        PQNode iter = leftmostPNode;
        int countRootChildren = 1;
        while(iter.circularLink_next != leftmostPNode){
            countRootChildren++;
            iter = iter.circularLink_next;
        }
        assertTrue(countRootChildren == 10);

    }

    private List<List<PQNode>> templateP6Tree(){
        PQNode _root = new PNode();
        _root.id = "_root";
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
        partialQNode1.labelType = PQNode.PARTIAL;
        partialQNode1.id = "partialQNode";
        partialQNode1.parent = _root; // Added
        for (int i = 0; i < 4; i++) {
            PQNode emptyChildOfQNode = new PQNode();
            emptyChildOfQNode.id = "emptyChildOfQNode1" + Integer.toString(i);
            emptyChildOfQNode.labelType = PQNode.EMPTY;
            emptyChildrenOfQNode1.add(emptyChildOfQNode);
        }
        partialQNode1.addChild(emptyChildrenOfQNode1.get(0));

        for (int i = 0; i < 4; i++) {
            PQNode pertinentChildOfQNode = new PQNode();
            pertinentChildOfQNode.id = "fullChildOfQNode1" + Integer.toString(i);
            pertinentChildOfQNode.labelType = PQNode.FULL;
            S.add(pertinentChildOfQNode);
            pertinentChildrenOfQNode1.add(pertinentChildOfQNode);
        }
        partialQNode1.addChild(pertinentChildrenOfQNode1.get(pertinentChildrenOfQNode1.size()-1));

        List<PQNode> combinedChildrenOfQNode1 = new ArrayList<PQNode>();
        combinedChildrenOfQNode1.addAll(emptyChildrenOfQNode1);
        combinedChildrenOfQNode1.addAll(pertinentChildrenOfQNode1);
        setCircularLinks(combinedChildrenOfQNode1);
        combinedChildrenOfQNode1.get(0).parent = partialQNode1;
        combinedChildrenOfQNode1.get(combinedChildrenOfQNode1.size()-1).parent = partialQNode1;
        partialQNode1.pertinentChildCount = pertinentChildrenOfQNode1.size();
        // Finished setting up QNode 1

        // QNode 2
        PQNode partialQNode2 = new QNode();
        //partialQNode2.nodeType = PQNode.QNODE;
        partialQNode2.labelType = PQNode.PARTIAL;
        partialQNode2.id = "partialQNode";
        partialQNode2.parent = _root; // Added
        for (int i = 0; i < 4; i++) {
            PQNode emptyChildOfQNode = new PQNode();
            emptyChildOfQNode.id = "emptyChildOfQNode2" + Integer.toString(i);
            emptyChildOfQNode.labelType = PQNode.EMPTY;
            emptyChildrenOfQNode2.add(emptyChildOfQNode);
        }
        partialQNode2.addChild(emptyChildrenOfQNode2.get(0));

        for (int i = 0; i < 4; i++) {
            PQNode pertinentChildOfQNode = new PQNode();
            pertinentChildOfQNode.id = "fullChildOfQNode2" + Integer.toString(i);
            pertinentChildOfQNode.labelType = PQNode.FULL;
            pertinentChildrenOfQNode2.add(pertinentChildOfQNode);
            S.add(pertinentChildOfQNode);
        }
        partialQNode2.addChild(pertinentChildrenOfQNode2.get(pertinentChildrenOfQNode2.size()-1));

        List<PQNode> combinedChildrenOfQNode2 = new ArrayList<PQNode>();
        combinedChildrenOfQNode2.addAll(emptyChildrenOfQNode2);
        combinedChildrenOfQNode2.addAll(pertinentChildrenOfQNode2);
        setCircularLinks(combinedChildrenOfQNode2);
        combinedChildrenOfQNode2.get(0).parent = partialQNode2;
        combinedChildrenOfQNode2.get(combinedChildrenOfQNode2.size()-1).parent = partialQNode2;
        partialQNode2.pertinentChildCount = pertinentChildrenOfQNode2.size();
        // Finished setting up QNode 2


        partialQNode1.setQNodeEndmostChildren(emptyChildrenOfQNode1.get(0), pertinentChildrenOfQNode1.get(pertinentChildrenOfQNode1.size()-1));
        partialQNode1.setParentQNodeChildren();
        partialQNode2.setQNodeEndmostChildren(emptyChildrenOfQNode2.get(0), pertinentChildrenOfQNode2.get(pertinentChildrenOfQNode2.size()-1));
        partialQNode2.setParentQNodeChildren();

        // PNode (root)
        for (int i = 0; i < 4; i++) {
            PQNode empty = new PQNode();
            empty.id = "empty" + Integer.toString(i);
            empty.parent = _root;
            empty.labelType = PQNode.EMPTY;
            empties.add(empty);
        }
        for (int i = 0; i < 4; i++) {
            PQNode full = new PQNode();
            full.id = "partial" + Integer.toString(i);
            full.parent = _root;
            full.labelType = PQNode.FULL;
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
        _root.pertinentChildCount = fulls.size() + 2;
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
        PQ PQTree = new PQ();
        boolean ret = PQTree.TEMPLATE_P6(_root);
        assertTrue(ret);

        for(PQNode n : empties){
            assertTrue(n.parent == _root);
        }

        for(PQNode n : fulls){
            assertTrue(n.parent != _root);
            assertTrue(n.parent.parent != _root);
            assertTrue(n.parent.getChildren().contains(n));
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
        assertTrue(mergingQNode.labelType.equals(PQNode.PARTIAL));
        assertTrue(mergingQNode.endmostChildren().size() == 2);

        PQNode iter = mergingQNode.endmostChildren().get(0);
        PQNode leftMostNode = iter;
        int nodeIndex = 0;
        int interiorPNodeCount = 0;
        PQNode interiorPNode = null;
        while(iter.circularLink_next != leftMostNode){

            // Asserts first 4 nodes are EMPTY
            if(nodeIndex < 4) {
                assertTrue(iter.labelType.equals(PQNode.EMPTY));
            }
            // Asserts node indices between 4 and 13 and PARTIAL/FULL
            else if(4 <= nodeIndex && nodeIndex <= 12){
                // Asserts if singular interior pNode is FULL
                if (iter.getClass() == PNode.class) {
                    assertTrue(iter.labelType.equals(PQNode.FULL));
                    interiorPNode = iter;
                    interiorPNodeCount++;
                }
                // Asserts if non-PNodes are full
                else {
                    assertTrue(iter.labelType.equals(PQNode.FULL));
                }
            }
            else {
                assertTrue(iter.labelType.equals(PQNode.EMPTY));
            }

            nodeIndex++;
            iter = iter.circularLink_next;
        }

        assertTrue(interiorPNodeCount == 1);
        assertTrue(interiorPNode.getChildren().size() == 4);
        for(PQNode n : interiorPNode.getChildren()){
            assertTrue(n.labelType.equals(PQNode.FULL));
        }
        // Finished testing
    }


    private List<PQNode> templateQ1Tree(){
        PQNode T = new PNode(PQNode.EMPTY);
        PQNode _root = new QNode(PQNode.EMPTY);
        PQNode A = new LeafNode();
        PQNode B = new LeafNode();

        T.addChild(_root);
        _root.parent = T;
        _root.addChildren(Arrays.asList(A,B));
        A.parent = _root;
        B.parent = _root;
        A.labelType = PQNode.FULL;
        B.labelType = PQNode.FULL;

        _root.pertinentChildCount = 2;

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

        PQ PQTree = new PQ();

        // All children blocked
        assertTrue(!_root.labelType.equals(PQNode.FULL));
        boolean rt = PQTree.TEMPLATE_Q1(_root);
        assertTrue(rt);
        assertTrue(_root.labelType.equals(PQNode.FULL));

        // One child not blocked
        PQNode C = new PQNode();
        C.parent = _root;
        _root.labelType = PQNode.EMPTY;
        C.labelType = PQNode.EMPTY;

        _root.addChildren(Arrays.asList(A,B,C));
        rt = PQTree.TEMPLATE_P1(_root);
        assertTrue(!rt);
        assertTrue(!_root.labelType.equals(PQNode.FULL));
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

        qNode.parent = _root;
        A.parent = qNode;
        Ca.parent = C;
        Cd.parent = C;
        E.parent = qNode;

        A.id = "A";
        B.id = "B";
        C.id = "C";
        D.id = "D";
        E.id = "E";
        Ca.id = "Ca";
        Cb.id = "Cb";
        Cc.id = "Cc";
        Cd.id = "Cd";
        qNode.id = "qNode";
        _root.id = "_rootQ";

        A.labelType = PQNode.EMPTY;
        B.labelType = PQNode.EMPTY;
        C.labelType = PQNode.PARTIAL;
        Ca.labelType = PQNode.EMPTY;
        Cb.labelType = PQNode.EMPTY;
        Cc.labelType = PQNode.FULL;
        Cd.labelType = PQNode.FULL;
        D.labelType = PQNode.FULL;
        E.labelType = PQNode.FULL;

        C.pertinentChildCount = 2;
        qNode.pertinentChildCount = 1;
        _root.pertinentChildCount = 2;

        //Only A, E as official children, since this is a qnode
        if(treeNum == 1) {
            //C.children = Arrays.asList(Ca, Cd);
            C.addChildren(Arrays.asList(Ca, Cd));
            //qNode.children = Arrays.asList(A, E);
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

        PQ PQTree = new PQ();
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
            if (iterNode != qNode.getChildren().get(0) && iterNode.circularLink_prev.labelType != iterNode.labelType) {
                flips++;
            }
            iterNode = iterNode.circularLink_next;
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

        PQ PQTree = new PQ();
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
            if (iterNode != qNode.getChildren().get(0) && iterNode.circularLink_prev.labelType != iterNode.labelType) {
                flips++;
            }
            iterNode = iterNode.circularLink_next;
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

        //_root.children = Arrays.asList(qNode);
        _root.addChildren(Arrays.asList(qNode));

        qNode.parent = _root;
        A.parent = qNode;
        Ca.parent = C;
        Cd.parent = C;
        E.parent = qNode;

        A.id = "A";
        B.id = "B";
        C.id = "C";
        D.id = "D";
        E.id = "E";
        Ca.id = "Ca";
        Cb.id = "Cb";
        Cc.id = "Cc";
        Cd.id = "Cd";
        qNode.id = "qNode";
        _root.id = "_root";

        A.labelType = PQNode.EMPTY;
        B.labelType = PQNode.PARTIAL;
        C.labelType = PQNode.PARTIAL;
        Ca.labelType = PQNode.EMPTY;
        Cb.labelType = PQNode.EMPTY;
        Cc.labelType = PQNode.FULL;
        Cd.labelType = PQNode.FULL;
        D.labelType = PQNode.FULL;
        E.labelType = PQNode.FULL;

        qNode.setChildren(Arrays.asList(E, D, C, B, A));
        C.setChildren(Arrays.asList(Ca, Cb, Cc, Cd));

        PQ PQTree = new PQ();
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
            tmp.labelType = PQNode.EMPTY;
            tmp.id = "emptyLeft" + Integer.toString(i);
            tmp.parent = mainQNode;
            emptyLeft.add(tmp);
        }

        //Populating partialLeft
        for (int i = 0; i < 4; i++) {
            PQNode tmp = new PQNode();
            tmp.labelType = PQNode.EMPTY;
            if (i == 0) {
                tmp.parent = partialLeftQNode;
            }
            tmp.id = "partialLeft" + Integer.toString(i);
            partialLeft.add(tmp);
        }
        for (int i = 0; i < 4; i++) {
            PQNode tmp = new PQNode();
            tmp.labelType = PQNode.FULL;
            if (i == 3) {
                tmp.parent = partialLeftQNode;
            }
            tmp.id = "partialLeft" + Integer.toString(i);
            partialLeft.add(tmp);
        }

        //Populating fullMid
        for (int i = 0; i < 4; i++) {
            PQNode tmp = new PQNode();
            tmp.labelType = PQNode.FULL;
            tmp.id = "fullMid" + Integer.toString(i);
            tmp.parent = mainQNode;
            fullMid.add(tmp);
        }


        //Populating partialRight
        for (int i = 0; i < 4; i++) {
            PQNode tmp = new PQNode();
            tmp.labelType = PQNode.EMPTY;
            if (i == 0) {
                tmp.parent = partialRightQNode;
            }

            tmp.id = "partialRight" + Integer.toString(i);
            partialRight.add(tmp);
        }
        for (int i = 0; i < 4; i++) {
            PQNode tmp = new PQNode();
            tmp.labelType = PQNode.FULL;
            if (i == 3) {
                tmp.parent = partialRightQNode;
            }
            tmp.id = "partialRight" + Integer.toString(i);
            partialRight.add(tmp);
        }

        //Populating emptyRight
        for (int i = 0; i < 4; i++) {
            PQNode tmp = new PQNode();
            tmp.labelType = PQNode.EMPTY;
            tmp.id = "emptyRight" + Integer.toString(i);
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

        PQ PQTree = new PQ();
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

        PQ PQTree = new PQ();
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

        PQ PQTree = new PQ();
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

        PQ PQTree = new PQ();
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
        x.parent = z;
        a.parent = z;

        a.addChildren(Arrays.asList(b, f));

        b.parent = a;
        f.parent = a;
        b.addChildren(Arrays.asList(c, e));
        c.parent = b;
        e.parent = b;
        c.addChild(d);
        d.parent = c;

        f.parent = a;
        f.addChildren(Arrays.asList(g, h, i));
        g.parent = f;
        h.parent = f;
        i.parent = f;

        List<PQNode> S = new ArrayList<>();
        S.addAll(Arrays.asList(d, e, h));

        PQ pq = new PQ();
        PQNode _root = pq.root(z, S);

        assertTrue(_root == a);

    }

}

