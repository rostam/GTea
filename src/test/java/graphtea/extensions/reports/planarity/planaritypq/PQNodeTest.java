package graphtea.extensions.reports.planarity.planaritypq;

import static graphtea.extensions.reports.planarity.planaritypq.PQHelpers.setCircularLinks;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import org.junit.Test;
import graphtea.extensions.reports.planarity.planaritypq.PQNode;

import java.util.*;

import static org.junit.Assert.*;


public class PQNodeTest {
    @Test
    public void nodeType() throws Exception {

    }

    @Test
    public void partialChildren() throws Exception {
    }

    @Test
    public void setPartialChildren() throws Exception {
    }

    @Test
    public void endmostChildrenTest() throws Exception {
        PQNode _root = new PQNode();
        _root.nodeType = PQNode.QNODE;

        PQNode first = new PQNode("A");
        PQNode second = new PQNode("B");
        PQNode third = new PQNode("C");
        PQNode fourth = new PQNode("D");

        //Setting parent links
        first.parent = _root;
        fourth.parent = _root;

        _root.children = Arrays.asList(first, second, third, fourth);
        setCircularLinks(Arrays.asList(first, second, third, fourth));

        List<PQNode> expected = Arrays.asList(first, fourth);
        assertTrue(expected.equals(_root.endmostChildren()));
    }

    @Test
    public void internalChildrenTest() throws Exception {
        PQNode _root = new PQNode();
        _root.nodeType = PQNode.QNODE;

        PQNode first = new PQNode();
        PQNode second = new PQNode();
        PQNode third = new PQNode();
        PQNode fourth = new PQNode();

        first.id = "first";
        second.id = "second";
        third.id = "third";
        fourth.id = "fourth";

        //Setting parent links
        first.parent = _root;
        fourth.parent = _root;

        _root.children = Arrays.asList(first, second, third, fourth);
        setCircularLinks(Arrays.asList(first, second, third, fourth));

        List<PQNode> expected = Arrays.asList(second, third);
        assertTrue(expected.equals(_root.internalChildren()));
    }

    @Test
    public void emptySibling() throws Exception {
        PQNode mid = new PQNode();
        PQNode prev = new PQNode();
        PQNode next = new PQNode();
        next.labelType = PQNode.EMPTY;

        prev.circularLink_prev = next;
        prev.circularLink_next = mid;

        mid.circularLink_next = next;
        mid.circularLink_prev = prev;

        next.circularLink_prev = mid;
        next.circularLink_next = prev;

        assertTrue(prev.emptySibling().equals(next));
        assertTrue(mid.emptySibling().equals(next));
        assertTrue(next.emptySibling() == null);
    }

    @Test
    public void removeFromCircularLink() throws Exception {
        PQNode mid = new PQNode();
        PQNode prev = new PQNode();
        PQNode next = new PQNode();

        prev.circularLink_prev = next;
        prev.circularLink_next = mid;

        mid.circularLink_next = next;
        mid.circularLink_prev = prev;

        next.circularLink_next = prev;
        next.circularLink_prev = mid;

        mid.removeFromCircularLink();
        assertTrue(prev.circularLink_prev == next);
        assertTrue(prev.circularLink_next == next);
        assertTrue(next.circularLink_prev == prev);
        assertTrue(next.circularLink_next == prev);

    }

    @Test
    public void immediateSiblings() throws Exception {

    }

    @Test
    public void replaceInCircularLink() throws Exception {
        PQNode mid = new PQNode();
        PQNode prev = new PQNode();
        PQNode next = new PQNode();
        PQNode x = new PQNode();

        prev.circularLink_prev = next;
        prev.circularLink_next = mid;

        mid.circularLink_next = next;
        mid.circularLink_prev = prev;

        next.circularLink_next = prev;
        next.circularLink_prev = mid;

        mid.replaceInCircularLink(x);
        assertTrue(prev.circularLink_prev == next);
        assertTrue(prev.circularLink_next == x);
        assertTrue(next.circularLink_prev == x);
        assertTrue(next.circularLink_next == prev);

    }

    @Test
    public void replaceInImmediateSiblings() throws Exception {
    }

    @Test
    public void setEndmostChildren() throws Exception {
    }

    @Test
    public void fullChildren() throws Exception {
        PQNode root = new PQNode();
        PQNode c1 = new PQNode();
        PQNode c2 = new PQNode();
        PQNode c3 = new PQNode();
        PQNode c4 = new PQNode();
        c1.labelType = PQNode.FULL;
        c3.labelType = PQNode.FULL;
        List<PQNode> fullChildren = new ArrayList<PQNode>();
        fullChildren.add(c1);
        fullChildren.add(c3);
        root.children = fullChildren;
        Set<PQNode> functionCallReturn = new HashSet<PQNode>(root.fullChildren());
        Set<PQNode> fullChildrenSet = new HashSet<PQNode>(fullChildren);
        assertTrue(functionCallReturn.equals(fullChildrenSet));

    }

    @Test
    public void setImmediateSiblings() throws Exception {
    }

    @Test
    public void getImmediateSiblingOfNodeType() throws Exception {
    }

    @Test
    public void endmostChild() throws Exception {
    }

    @Test
    public void maximalConsectutiveSetOfSiblingsAdjacent() throws Exception {
        /* Testing: 011011101
        *              ^
        *              x
        */
        PQNode root = new PQNode();
        PQNode c1 = new PQNode();
        PQNode c2 = new PQNode();
        PQNode c3 = new PQNode();
        PQNode c4 = new PQNode();
        PQNode c5 = new PQNode();
        PQNode c6 = new PQNode();
        PQNode c7 = new PQNode();
        PQNode c8 = new PQNode();
        PQNode c9 = new PQNode();

        c1.blocked = false;
        c2.blocked = true;
        c3.blocked = true;
        c4.blocked = false;
        c5.blocked = true;
        c6.blocked = true;
        c7.blocked = true;
        c8.blocked = false;
        c9.blocked = true;

        c1.circularLink_next = c2;
        c1.circularLink_prev = c9;
        c2.circularLink_next = c3;
        c2.circularLink_prev = c1;
        c3.circularLink_next = c4;
        c3.circularLink_prev = c2;
        c4.circularLink_next = c5;
        c4.circularLink_prev = c4;
        c5.circularLink_next = c6;
        c5.circularLink_prev = c5;
        c6.circularLink_next = c7;
        c6.circularLink_prev = c5;
        c7.circularLink_next = c8;
        c7.circularLink_prev = c6;
        c8.circularLink_next = c9;
        c8.circularLink_prev = c7;
        c9.circularLink_next = c1;
        c9.circularLink_prev = c8;


        List<PQNode> children = new ArrayList<PQNode>();
        children.add(c1);
        children.add(c2);
        children.add(c3);
        children.add(c4);
        children.add(c5);
        children.add(c6);
        children.add(c7);
        children.add(c8);
        children.add(c9);

        root.children = children;

        Set<PQNode> results = c4.maximalConsecutiveSetOfSiblingsAdjacent(true);
        assertTrue(results.contains(c5) && results.contains(c6) && results.contains(c7));
    }

    @Test
    public void setQNodeEndmostChildrenTest(){
        PQNode qNode = new PQNode();
        qNode.nodeType = PQNode.QNODE;

        PQNode A = new PQNode();
        PQNode B = new PQNode();
        PQNode C = new PQNode();
        PQNode D = new PQNode();

        List<PQNode> children = Arrays.asList(A, B, C, D);
        setCircularLinks(children);

        qNode.children = children;

        qNode.setQNodeEndmostChildren(D, A);

        assertTrue(qNode.endmostChildren().get(0) == D);
        assertTrue(qNode.endmostChildren().get(1) == A);


    }

    @Test
    public void removeChildrenLeftMost(){
        PQNode qNode = new PQNode();
        qNode.nodeType = PQNode.QNODE;
        PQNode A = new PQNode("A");
        PQNode B = new PQNode("B");
        PQNode C = new PQNode("C");
        PQNode D = new PQNode("D");
        List<PQNode> children = Arrays.asList(A, B, C, D);
        setCircularLinks(children);
        qNode.setQNodeEndmostChildren(A, D);

        List<PQNode> removeThese = new ArrayList<>(Arrays.asList(A));
        qNode.removeChildren(removeThese);

        //assertTrue(A.circularLink_next == B);
        assertTrue(B.circularLink_next == C);
        assertTrue(C.circularLink_next == D);
        assertTrue(D.circularLink_next == B);

        //assertTrue(A.circularLink_prev == D);
        assertTrue(D.circularLink_prev == C);
        assertTrue(C.circularLink_prev == B);
        assertTrue(B.circularLink_prev == D);

        assertTrue(qNode.endmostChildren().get(0) ==  B);
        assertTrue(qNode.endmostChildren().get(1) ==  D);
    }

    @Test
    public void removeInteriorChildren(){
        PQNode qNode = new PQNode();
        qNode.nodeType = PQNode.QNODE;
        PQNode A = new PQNode("A");
        PQNode B = new PQNode("B");
        PQNode C = new PQNode("C");
        PQNode D = new PQNode("D");
        List<PQNode> children = Arrays.asList(A, B, C, D);
        setCircularLinks(children);
        qNode.setQNodeEndmostChildren(A, D);

        List<PQNode> removeThese = new ArrayList<>(Arrays.asList(B, C));
        qNode.removeChildren(removeThese);

        assertTrue(A.circularLink_next == D);
        assertTrue(D.circularLink_next == A);

        assertTrue(A.circularLink_prev == D);
        assertTrue(D.circularLink_prev == A);

        assertTrue(qNode.endmostChildren().get(0) ==  A);
        assertTrue(qNode.endmostChildren().get(1) ==  D);
    }

    @Test
    public void removeChildrenRightMost(){
        PQNode qNode = new PQNode();
        qNode.nodeType = PQNode.QNODE;
        PQNode A = new PQNode("A");
        PQNode B = new PQNode("B");
        PQNode C = new PQNode("C");
        PQNode D = new PQNode("D");
        List<PQNode> children = Arrays.asList(A, B, C, D);
        setCircularLinks(children);
        qNode.setQNodeEndmostChildren(A, D);

        List<PQNode> removeThese = new ArrayList<>(Arrays.asList(D));
        qNode.removeChildren(removeThese);

        assertTrue(A.circularLink_next == B);
        assertTrue(B.circularLink_next == C);
        assertTrue(C.circularLink_next == A);

        assertTrue(A.circularLink_prev == C);
        assertTrue(C.circularLink_prev == B);
        assertTrue(B.circularLink_prev == A);

        assertTrue(qNode.endmostChildren().get(0) ==  A);
        assertTrue(qNode.endmostChildren().get(1) ==  C);
    }

    @Test
    public void removeChildrenLeftAndRightMost(){
        PQNode qNode = new PQNode();
        qNode.nodeType = PQNode.QNODE;
        PQNode A = new PQNode("A");
        PQNode B = new PQNode("B");
        PQNode C = new PQNode("C");
        PQNode D = new PQNode("D");
        List<PQNode> children = Arrays.asList(A, B, C, D);
        setCircularLinks(children);
        qNode.setQNodeEndmostChildren(A, D);

        List<PQNode> removeThese = new ArrayList<>(Arrays.asList(A, D));
        qNode.removeChildren(removeThese);

        assertTrue(B.circularLink_next == C);
        assertTrue(C.circularLink_next == B);

        assertTrue(C.circularLink_prev == B);
        assertTrue(B.circularLink_prev == C);

        assertTrue(qNode.endmostChildren().get(0) ==  B);
        assertTrue(qNode.endmostChildren().get(1) ==  C);
    }

    @Test
    public void removeLeftAndRightAndInterior(){
        PQNode qNode = new PQNode();
        qNode.nodeType = PQNode.QNODE;
        PQNode A = new PQNode("A");
        PQNode B = new PQNode("B");
        PQNode C = new PQNode("C");
        PQNode D = new PQNode("D");
        List<PQNode> children = Arrays.asList(A, B, C, D);
        setCircularLinks(children);
        qNode.setQNodeEndmostChildren(A, D);

        List<PQNode> removeThese = new ArrayList<>(Arrays.asList(A, B, D));
        qNode.removeChildren(removeThese);

        assertTrue(C.circularLink_next == C);
        assertTrue(C.circularLink_prev == C);

        assertTrue(qNode.endmostChildren().get(0) ==  C);
        assertTrue(qNode.endmostChildren().size() == 1);
    }

    @Test
    public void removeAllChildren(){
        PQNode qNode = new PQNode();
        qNode.nodeType = PQNode.QNODE;
        PQNode A = new PQNode("A");
        PQNode B = new PQNode("B");
        PQNode C = new PQNode("C");
        PQNode D = new PQNode("D");
        List<PQNode> children = Arrays.asList(A, B, C, D);
        setCircularLinks(children);
        qNode.setQNodeEndmostChildren(A, D);

        List<PQNode> removeThese = new ArrayList<>(children);
        qNode.removeChildren(removeThese);

        assertTrue(qNode.endmostChildren().size() == 0);
    }

}
