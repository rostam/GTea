package graphtea.extensions.reports.planarity.planaritypq;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import graphtea.extensions.reports.planarity.planaritypq.PQHelpers;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

public class PQHelpersTest {
    @Test
    public void setCircularLinksTest() throws Exception {
        PQNode c1 = new PQNode();
        PQNode c2 = new PQNode();
        PQNode c3 = new PQNode();
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

}
