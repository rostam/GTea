package graphtea.extensions.reports.planarity.planaritypq;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import graphtea.extensions.reports.planarity.planaritypq.PQHelpers;

import static graphtea.extensions.reports.planarity.planaritypq.PQHelpers.reverseCircularLinks;
import static graphtea.extensions.reports.planarity.planaritypq.PQHelpers.rotateQNode;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

public class PQHelpersTest {
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
        assertTrue(c1.circularLink_prev == c3);
        assertTrue(c1.circularLink_next == c2);
        assertTrue(c2.circularLink_prev == c1);
        assertTrue(c2.circularLink_next == c3);
        assertTrue(c3.circularLink_prev == c2);
        assertTrue(c3.circularLink_next == c1);
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
        nodes.get(3).circularLink_prev = nodes.get(5);
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
        PQNode _root = new PQNode("_root");
        PQNode child1 = new PQNode("child1");
        PQNode child2 = new PQNode("child2");
        PQNode child3 = new PQNode("child3");

        _root.children.add(child1);
        child1.children.add(child2);
        child2.children.add(child3);

        List<PQNode> front = PQHelpers.frontier(_root);
        assertTrue(PQHelpers.frontier(_root).equals(Arrays.asList(child3)));
    }

    @Test
    public void frontierTestSimpleBinaryTree() {
        PQNode _root = new PQNode("_root");
        PQNode childL = new PQNode("childL");
        PQNode childR = new PQNode("childR");
        PQNode childLL = new PQNode("childLL");
        PQNode childLR= new PQNode("childLR");
        PQNode childLLL = new PQNode("childLLL");
        PQNode childLLR = new PQNode("childLLR");

        _root.children.add(childL);
        _root.children.add(childR);

        childL.children.add(childLL);
        childL.children.add(childLR);

        childLL.children.add(childLLL);
        childLL.children.add(childLLR);

        List<PQNode> front = PQHelpers.frontier(_root);
        assertTrue(PQHelpers.frontier(_root).equals(Arrays.asList(childLLL, childLLR, childLR, childR)));

    }


    @Test
    public void reverseCircularLinksTest() {
        List<PQNode> list = new ArrayList<>();
        int listSize = 10;
        for(int i=0; i<listSize; i++){
            PQNode n = new PQNode();
            n.id = ""+i;
            list.add(n);
        }
        PQNode leftMost = list.get(0);
        PQNode rightMost = list.get(list.size()-1);

        leftMost.circularLink_prev = rightMost;
        leftMost.circularLink_next = list.get(1);

        rightMost.circularLink_next = leftMost;
        rightMost.circularLink_prev = list.get( list.size()-2 );

        for(int i=1; i<listSize-1; i++){
            list.get(i).circularLink_next = list.get(i+1);
            list.get(i).circularLink_prev = list.get(i-1);
        }

        for(PQNode n : list){
            assertNotNull(n.circularLink_next);
            assertNotNull(n.circularLink_prev);
        }

        PQNode nodeSecond = list.get(1);
        PQNode nodeSecondLast = list.get(list.size() - 2);

        assertTrue(leftMost.circularLink_next == nodeSecond);
        assertTrue(leftMost.circularLink_prev == rightMost);

        assertTrue(rightMost.circularLink_next == leftMost);
        assertTrue(rightMost.circularLink_prev == nodeSecondLast);

        List<PQNode> saveList = new ArrayList<>(list);

        /** Function we are testing */
        reverseCircularLinks(list.get(0));

        assertTrue(leftMost.circularLink_next == rightMost);
        assertTrue(leftMost.circularLink_prev == nodeSecond);

        assertTrue(rightMost.circularLink_next == nodeSecondLast);
        assertTrue(rightMost.circularLink_prev == leftMost);

        PQNode iter = leftMost;

        while(iter.circularLink_next != leftMost){
            assertNotNull(iter);
            iter = iter.circularLink_next;
        }
    }

    @Test
    public void rotateQNodeTest(){
        PQNode qNode = new QNode();

        PQNode leftMost = new LeafNode();
        PQNode interior = new LeafNode();
        PQNode rightMost = new LeafNode();

        leftMost.id = "A";
        interior.id = "B";
        interior.id = "C";

        leftMost.parent = qNode;
        rightMost.parent = qNode;

        leftMost.circularLink_prev = rightMost;
        leftMost.circularLink_next = interior;

        interior.circularLink_prev = leftMost;
        interior.circularLink_next = rightMost;

        rightMost.circularLink_prev = interior;
        rightMost.circularLink_next = leftMost;

        List<PQNode> children = new ArrayList<>();
        children.add(leftMost);
        children.add(interior);
        children.add(rightMost);

        qNode.children = children;

        rotateQNode(qNode);

        assertTrue(qNode.endmostChildren().get(0) == rightMost);
        assertTrue(qNode.endmostChildren().get(1) == leftMost);

    }

    @Test
    public void equalTreesTestIdentical1() {
        PQNode root = new PQNode();

        for (int i = 0; i < 10; i++) {
            PQNode child = new PQNode();
            child.parent = root;
            root.children.add(child);
        }

        assertTrue(PQHelpers.equalTrees(root, root));
    }

    @Test
    public void equalTreesTestDifferent1() {
        PQNode rootA = new PQNode();
        PQNode rootB = new PQNode();

        for (int i = 0; i < 10; i++) {
            PQNode child = new PQNode();
            child.parent = rootA;
            rootA.children.add(child);
        }
        for (int i = 0; i < 10; i++) {
            PQNode child = new PQNode();
            child.parent = rootB;
            rootB.children.add(child);
        }

        assertFalse(PQHelpers.equalTrees(rootA, rootB));
    }

    @Test
    public void equalTreesTestDifferent2() {
        PQNode root = new PQNode();

        for (int i = 0; i < 10; i++) {
            PQNode child = new PQNode();
            child.parent = root;
            root.children.add(child);
        }

        PQNode rootCopy = new PQNode();
        for (int i = 0; i < 10; i++) {
            rootCopy.children.add(root.children.get(i));
        }
        rootCopy.children.add(new PQNode());
        assertFalse(PQHelpers.equalTrees(root, rootCopy));
    }

    @Test
    public void gatherQNodeChildrenTest(){
        PQNode root = new PQNode();
        PQNode a = new PQNode();
        PQNode b = new PQNode();
        PQNode c = new PQNode();
        PQNode d = new PQNode();
        root.children.addAll(Arrays.asList(a, b, c, d));
        PQHelpers.setCircularLinks(root.children);
        root.nodeType = PQNode.QNODE;
        for (PQNode n : root.children) {
            n.labelType = PQNode.EMPTY;
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
        assertTrue(newLeft.circularLink_next == left);
        assertTrue(newLeft.circularLink_prev == right);
        assertTrue(right.circularLink_next == newLeft );
        assertTrue(left.circularLink_prev == newLeft );



    }

}

