package graphtea.extensions.reports.planarity.planaritypq.pqtree.helpers;

import graphtea.extensions.reports.planarity.planaritypq.pqtree.helpers.PQHelpers;
import graphtea.extensions.reports.planarity.planaritypq.pqtree.pqnodes.LeafNode;
import graphtea.extensions.reports.planarity.planaritypq.pqtree.pqnodes.PNode;
import graphtea.extensions.reports.planarity.planaritypq.pqtree.pqnodes.PQNode;
import graphtea.extensions.reports.planarity.planaritypq.pqtree.pqnodes.QNode;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static graphtea.extensions.reports.planarity.planaritypq.pqtree.helpers.PQHelpers.reverseCircularLinks;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

public class PQTreeHelpersTest {

    @Test
    public void setCircularLinksTest() throws Exception {
        PQNode c1 = new LeafNode();
        PQNode c2 = new QNode();
        PQNode c3 = new PNode();
        List<PQNode> children = new ArrayList<PQNode>();
        children.add(c1);
        children.add(c2);
        children.add(c3);
        PQHelpers.setCircularLinks(children);
        assertTrue(c1.getCircularLink_prev() == c3);
        assertTrue(c1.getCircularLink_next() == c2);
        assertTrue(c2.getCircularLink_prev() == c1);
        assertTrue(c2.getCircularLink_next() == c3);
        assertTrue(c3.getCircularLink_prev() == c2);
        assertTrue(c3.getCircularLink_next() == c1);
    }

    @Test
    public void checkIfConsecutiveTestPositive() throws Exception {
        List<PQNode> nodes = new LinkedList<PQNode>();
        for (int i = 0; i < 10; i++) {
            PQNode n = new PQNode();
            nodes.add(n);
        }
        PQHelpers.setCircularLinks(nodes);
        assertTrue(PQHelpers.checkIfConsecutive(nodes));
    }

    @Test
    public void checkIfConsecutiveTestNegative() throws Exception {
        List<PQNode> nodes = new LinkedList<PQNode>();
        for (int i = 0; i < 10; i++) {
            PQNode n = new PQNode();
            nodes.add(n);
        }
        PQHelpers.setCircularLinks(nodes);
        nodes.get(3).setCircularLink_prev(nodes.get(5));
        assertFalse(PQHelpers.checkIfConsecutive(nodes));
    }

    @Test
    public void checkIfConsecutiveTestOneElement() throws Exception {
        List<PQNode> nodes = new LinkedList<PQNode>();
        PQNode n = new PQNode();
        nodes.add(n);
        PQHelpers.setCircularLinks(nodes);
        assertTrue(PQHelpers.checkIfConsecutive(nodes));
    }

    @Test
    public void checkIfConsecutiveTestEmpty() throws Exception {
        List<PQNode> nodes = new LinkedList<PQNode>();
        PQHelpers.setCircularLinks(nodes);
        assertTrue(PQHelpers.checkIfConsecutive(nodes));
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

        List<PQNode> tester = PQHelpers.union(list1, list2);

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

        List<PQNode> tester = PQHelpers.intersection(list1, list2);

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

        assertTrue(PQHelpers.subset(list1, list2));
        assertTrue(!PQHelpers.subset(list2, list1));

    }

    @Test
    public void frontierTestPath() {
        PQNode _root = new PNode("_root");
        PQNode child1 = new PNode("child1");
        PQNode child2 = new PNode("child2");
        PQNode child3 = new PNode("child3");

        _root.addChild(child1);
        child1.addChild(child2);
        child2.addChild(child3);
        //_root.children.add(child1);
        //child1.children.add(child2);
        //child2.children.add(child3);

        List<PQNode> front = PQHelpers.frontier(_root);
        assertTrue(PQHelpers.frontier(_root).equals(Arrays.asList(child3)));
    }

    @Test
    public void frontierTestSimpleBinaryTree() {
        PQNode _root = new PNode("_root");
        PQNode childL = new PNode("childL");
        PQNode childR = new PNode("childR");
        PQNode childLL = new PNode("childLL");
        PQNode childLR= new PNode("childLR");
        PQNode childLLL = new PNode("childLLL");
        PQNode childLLR = new PNode("childLLR");

        //_root.children.add(childL);
        //_root.children.add(childR);
        _root.addChild(childL);
        _root.addChild(childR);

        //childL.children.add(childLL);
        //childL.children.add(childLR);
        childL.addChild(childLL);
        childL.addChild(childLR);

        //childLL.children.add(childLLL);
        //childLL.children.add(childLLR);
        childLL.addChild(childLLL);
        childLL.addChild(childLLR);

        List<PQNode> front = PQHelpers.frontier(_root);
        assertTrue(PQHelpers.frontier(_root).equals(Arrays.asList(childLLL, childLLR, childLR, childR)));

    }


    @Test
    public void reverseCircularLinksTest() {
        List<PQNode> list = new ArrayList<>();
        int listSize = 10;
        for(int i=0; i<listSize; i++){
            PQNode n = new PQNode();
            n.setId(""+i);
            list.add(n);
        }
        PQNode leftMost = list.get(0);
        PQNode rightMost = list.get(list.size()-1);

        leftMost.setCircularLink_prev(rightMost);
        leftMost.setCircularLink_next(list.get(1));

        rightMost.setCircularLink_next(leftMost);
        rightMost.setCircularLink_prev(list.get( list.size()-2 ));

        for(int i=1; i<listSize-1; i++){
            list.get(i).setCircularLink_next(list.get(i+1));
            list.get(i).setCircularLink_prev(list.get(i-1));
        }

        for(PQNode n : list){
            assertNotNull(n.getCircularLink_next());
            assertNotNull(n.getCircularLink_prev());
        }

        PQNode nodeSecond = list.get(1);
        PQNode nodeSecondLast = list.get(list.size() - 2);

        assertTrue(leftMost.getCircularLink_next() == nodeSecond);
        assertTrue(leftMost.getCircularLink_prev() == rightMost);

        assertTrue(rightMost.getCircularLink_next() == leftMost);
        assertTrue(rightMost.getCircularLink_prev() == nodeSecondLast);

        List<PQNode> saveList = new ArrayList<>(list);

        // Function we are testing
        reverseCircularLinks(list.get(0));

        assertTrue(leftMost.getCircularLink_next() == rightMost);
        assertTrue(leftMost.getCircularLink_prev() == nodeSecond);

        assertTrue(rightMost.getCircularLink_next() == nodeSecondLast);
        assertTrue(rightMost.getCircularLink_prev() == leftMost);

        PQNode iter = leftMost;

        while(iter.getCircularLink_next() != leftMost){
            assertNotNull(iter);
            iter = iter.getCircularLink_next();
        }
    }

    @Test
    public void rotateQNodeTest(){
        QNode qNode = new QNode();

        PQNode leftMost = new LeafNode();
        PQNode interior = new LeafNode();
        PQNode rightMost = new LeafNode();

        leftMost.setId("A");
        interior.setId("B");
        rightMost.setId("C");

        leftMost.setParent(qNode);
        rightMost.setParent(qNode);

        List<PQNode> children = new ArrayList<>();
        children.add(leftMost);
        children.add(interior);
        children.add(rightMost);

        PQHelpers.setCircularLinks(children);
        qNode.setQNodeEndmostChildren(children.get(0), children.get(children.size()-1));
        qNode.setParentQNodeChildren();

        qNode.rotate();

        assertTrue(qNode.endmostChildren().get(0) == rightMost);
        assertTrue(qNode.endmostChildren().get(1) == leftMost);

    }

    @Test
    public void equalTreesTestIdentical1() {
        PQNode root = new PNode();

        for (int i = 0; i < 10; i++) {
            PQNode child = new LeafNode();
            child.setParent(root);
            //root.children.add(child);
            root.addChild(child);
        }

        assertTrue(PQHelpers.equalTrees(root, root));
    }

    @Test
    public void equalTreesTestDifferent1() {
        PQNode rootA = new PNode();
        PQNode rootB = new PNode();

        for (int i = 0; i < 10; i++) {
            PQNode child = new LeafNode();
            child.setParent(rootA);
            //rootA.children.add(child);
            rootA.addChild(child);
        }
        for (int i = 0; i < 10; i++) {
            PQNode child = new LeafNode();
            child.setParent(rootB);
            //rootB.children.add(child);
            rootB.addChild(child);
        }

        assertFalse(PQHelpers.equalTrees(rootA, rootB));
    }

    @Test
    public void equalTreesTestDifferent2() {
        PQNode root = new PNode();

        for (int i = 0; i < 10; i++) {
            PQNode child = new LeafNode();
            child.setParent(root);
            //root.children.add(child);
            root.addChild(child);
        }

        PQNode rootCopy = new PNode();
        for (int i = 0; i < 10; i++) {
            //rootCopy.children.add(root.children.get(i));
            rootCopy.addChild(root.getChildren().get(i));
        }
        //rootCopy.children.add(new PQNode());
        rootCopy.addChild(new LeafNode());
        assertFalse(PQHelpers.equalTrees(root, rootCopy));
    }

    @Test
    public void gatherQNodeChildrenTest(){
        PQNode root = new QNode();
        PQNode a = new PQNode();
        PQNode b = new PQNode();
        PQNode c = new PQNode();
        PQNode d = new PQNode();
        root.addChildren(Arrays.asList(a, b, c, d));
        PQHelpers.setCircularLinks(Arrays.asList(a, b, c, d));
        root.setQNodeEndmostChildren(a, d);
        root.setParentQNodeChildren();
        for (PQNode n : root.getChildren()) {
            n.setLabel(PQNode.EMPTY);
        }
        List<PQNode> empties = new ArrayList<>();
        List<PQNode> fulls = new ArrayList<>();

        PQHelpers.gatherQNodeChildren(empties, fulls, root);

        assertTrue(empties.size() == 4);
    }

    @Test
    public void insertNodeIntoCircularList(){
        PQNode left = new PQNode("left");
        PQNode middle = new PQNode("middle");
        PQNode right = new PQNode("right");
        PQHelpers.setCircularLinks(Arrays.asList(left, middle, right));

        PQNode newLeft = new PQNode("newNode");
        PQHelpers.insertNodeIntoCircularList(newLeft, right, left);
        assertTrue(newLeft.getCircularLink_next() == left);
        assertTrue(newLeft.getCircularLink_prev() == right);
        assertTrue(right.getCircularLink_next() == newLeft );
        assertTrue(left.getCircularLink_prev() == newLeft );



    }


}

