package graphtea.extensions.reports.planarity.planaritypq.pqtree.pqnodes;

import org.junit.Test;

import java.util.*;

import static graphtea.extensions.reports.planarity.planaritypq.pqtree.helpers.PQHelpers.setCircularLinks;
import static junit.framework.TestCase.assertTrue;


public class PQTreeNodeTest {
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
        PQNode _root = new QNode();

        PQNode first = new LeafNode("A");
        PQNode second = new LeafNode("B");
        PQNode third = new LeafNode("C");
        PQNode fourth = new LeafNode("D");

        //Setting parent links
        first.setParent(_root);
        fourth.setParent(_root);

        //_root.children = Arrays.asList(first, second, third, fourth);
        _root.setQNodeEndmostChildren(first, fourth);
        setCircularLinks(Arrays.asList(first, second, third, fourth));

        List<PQNode> expected = Arrays.asList(first, fourth);
        assertTrue(expected.equals(_root.endmostChildren()));
    }

    @Test
    public void internalChildrenTest() throws Exception {
        PQNode _root = new QNode();

        PQNode first = new LeafNode();
        PQNode second = new LeafNode();
        PQNode third = new LeafNode();
        PQNode fourth = new LeafNode();

        first.setId("first");
        second.setId("second");
        third.setId("third");
        fourth.setId("fourth");

        //Setting parent links
        first.setParent(_root);
        fourth.setParent(_root);

        //_root.children = Arrays.asList(first, second, third, fourth);
        setCircularLinks(Arrays.asList(first, second, third, fourth));
        _root.setQNodeEndmostChildren(first, fourth);
        _root.setParentQNodeChildren();

        List<PQNode> expected = Arrays.asList(second, third);
        assertTrue(expected.equals(_root.internalChildren()));
    }

    @Test
    public void emptySibling() throws Exception {
        PQNode mid = new LeafNode();
        PQNode prev = new LeafNode();
        PQNode next = new LeafNode();
        next.setLabel(PQNode.EMPTY);

        prev.setCircularLink_prev(next);
        prev.setCircularLink_next( mid);

        mid.setCircularLink_next( next);
        mid.setCircularLink_prev(prev);

        next.setCircularLink_prev(mid);
        next.setCircularLink_next( prev);

        assertTrue(prev.emptySibling().equals(next));
        assertTrue(mid.emptySibling().equals(next));
        assertTrue(next.emptySibling() == null);
    }

    @Test
    public void removeFromCircularLink() throws Exception {
        PQNode mid = new LeafNode();
        PQNode prev = new LeafNode();
        PQNode next = new LeafNode();

        prev.setCircularLink_prev(next);
        prev.setCircularLink_next( mid);

        mid.setCircularLink_next( next);
        mid.setCircularLink_prev(prev);

        next.setCircularLink_next( prev);
        next.setCircularLink_prev(mid);

        mid.removeFromCircularLink();
        assertTrue(prev.getCircularLink_prev() == next);
        assertTrue(prev.getCircularLink_next() == next);
        assertTrue(next.getCircularLink_prev() == prev);
        assertTrue(next.getCircularLink_next() == prev);

    }

    @Test
    public void replaceInCircularLink() throws Exception {
        PQNode mid = new LeafNode();
        PQNode prev = new LeafNode();
        PQNode next = new LeafNode();
        PQNode x = new LeafNode();

        prev.setCircularLink_prev(next);
        prev.setCircularLink_next( mid);

        mid.setCircularLink_next( next);
        mid.setCircularLink_prev(prev);

        next.setCircularLink_next( prev);
        next.setCircularLink_prev(mid);

        mid.replaceInCircularLink(x);
        assertTrue(prev.getCircularLink_prev() == next);
        assertTrue(prev.getCircularLink_next() == x);
        assertTrue(next.getCircularLink_prev() == x);
        assertTrue(next.getCircularLink_next() == prev);

    }

    @Test
    public void replaceInImmediateSiblings() throws Exception {
    }

    @Test
    public void setEndmostChildren() throws Exception {
    }

    @Test
    public void fullChildren() throws Exception {
        PQNode root = new PNode();
        PQNode c1 = new LeafNode();
        PQNode c2 = new LeafNode();
        PQNode c3 = new LeafNode();
        PQNode c4 = new LeafNode();
        c1.setLabel(PQNode.FULL);
        c3.setLabel(PQNode.FULL);
        List<PQNode> fullChildren = new ArrayList<PQNode>();
        fullChildren.add(c1);
        fullChildren.add(c3);

        root.addChildren(fullChildren);

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
        PQNode root = new PNode();
        PQNode c1 = new LeafNode();
        PQNode c2 = new LeafNode();
        PQNode c3 = new LeafNode();
        PQNode c4 = new LeafNode();
        PQNode c5 = new LeafNode();
        PQNode c6 = new LeafNode();
        PQNode c7 = new LeafNode();
        PQNode c8 = new LeafNode();
        PQNode c9 = new LeafNode();

        c1.blocked = false;
        c2.blocked = true;
        c3.blocked = true;
        c4.blocked = false;
        c5.blocked = true;
        c6.blocked = true;
        c7.blocked = true;
        c8.blocked = false;
        c9.blocked = true;

        c1.setCircularLink_next( c2);
        c1.setCircularLink_prev(c9);
        c2.setCircularLink_next( c3);
        c2.setCircularLink_prev(c1);
        c3.setCircularLink_next( c4);
        c3.setCircularLink_prev(c2);
        c4.setCircularLink_next( c5);
        c4.setCircularLink_prev(c4);
        c5.setCircularLink_next( c6);
        c5.setCircularLink_prev(c5);
        c6.setCircularLink_next( c7);
        c6.setCircularLink_prev(c5);
        c7.setCircularLink_next( c8);
        c7.setCircularLink_prev(c6);
        c8.setCircularLink_next( c9);
        c8.setCircularLink_prev(c7);
        c9.setCircularLink_next( c1);
        c9.setCircularLink_prev(c8);


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

        //root.children = children;
        root.setQNodeEndmostChildren(children.get(0), children.get(0));
        root.setParentQNodeChildren();

        Set<PQNode> results = c4.maximalConsecutiveSetOfSiblingsAdjacent(true);
        assertTrue(results.contains(c5) && results.contains(c6) && results.contains(c7));
    }

    @Test
    public void setQNodeEndmostChildrenTest(){
        PQNode qNode = new QNode();

        PQNode A = new LeafNode();
        PQNode B = new LeafNode();
        PQNode C = new LeafNode();
        PQNode D = new LeafNode();

        List<PQNode> children = Arrays.asList(A, B, C, D);
        setCircularLinks(children);

        //qNode.children = children;
        qNode.setQNodeEndmostChildren(children.get(0), children.get(1));
        qNode.setParentQNodeChildren();

        qNode.setQNodeEndmostChildren(D, A);

        assertTrue(qNode.endmostChildren().get(0) == D);
        assertTrue(qNode.endmostChildren().get(1) == A);


    }

    @Test
    public void removeChildrenLeftMost(){
        PQNode qNode = new QNode();
        PQNode A = new LeafNode("A");
        PQNode B = new LeafNode("B");
        PQNode C = new LeafNode("C");
        PQNode D = new LeafNode("D");
        List<PQNode> children = Arrays.asList(A, B, C, D);
        setCircularLinks(children);
        qNode.setQNodeEndmostChildren(A, D);

        List<PQNode> removeThese = new ArrayList<>(Arrays.asList(A));
        qNode.removeChildren(removeThese);

        //assertTrue(A.getCircularLink_next() == B);
        assertTrue(B.getCircularLink_next() == C);
        assertTrue(C.getCircularLink_next() == D);
        assertTrue(D.getCircularLink_next() == B);

        //assertTrue(A.getCircularLink_prev() == D);
        assertTrue(D.getCircularLink_prev() == C);
        assertTrue(C.getCircularLink_prev() == B);
        assertTrue(B.getCircularLink_prev() == D);

        assertTrue(qNode.endmostChildren().get(0) ==  B);
        assertTrue(qNode.endmostChildren().get(1) ==  D);
    }

    @Test
    public void removeInteriorChildren(){
        PQNode qNode = new QNode();
        PQNode A = new LeafNode("A");
        PQNode B = new LeafNode("B");
        PQNode C = new LeafNode("C");
        PQNode D = new LeafNode("D");
        List<PQNode> children = Arrays.asList(A, B, C, D);
        setCircularLinks(children);
        qNode.setQNodeEndmostChildren(A, D);

        List<PQNode> removeThese = new ArrayList<>(Arrays.asList(B, C));
        qNode.removeChildren(removeThese);

        assertTrue(A.getCircularLink_next() == D);
        assertTrue(D.getCircularLink_next() == A);

        assertTrue(A.getCircularLink_prev() == D);
        assertTrue(D.getCircularLink_prev() == A);

        assertTrue(qNode.endmostChildren().get(0) ==  A);
        assertTrue(qNode.endmostChildren().get(1) ==  D);
    }

    @Test
    public void removeChildrenRightMost(){
        PQNode qNode = new QNode();
        PQNode A = new LeafNode("A");
        PQNode B = new LeafNode("B");
        PQNode C = new LeafNode("C");
        PQNode D = new LeafNode("D");
        List<PQNode> children = Arrays.asList(A, B, C, D);
        setCircularLinks(children);
        qNode.setQNodeEndmostChildren(A, D);

        List<PQNode> removeThese = new ArrayList<>(Arrays.asList(D));
        qNode.removeChildren(removeThese);

        assertTrue(A.getCircularLink_next() == B);
        assertTrue(B.getCircularLink_next() == C);
        assertTrue(C.getCircularLink_next() == A);

        assertTrue(A.getCircularLink_prev() == C);
        assertTrue(C.getCircularLink_prev() == B);
        assertTrue(B.getCircularLink_prev() == A);

        assertTrue(qNode.endmostChildren().get(0) ==  A);
        assertTrue(qNode.endmostChildren().get(1) ==  C);
    }

    @Test
    public void removeChildrenLeftAndRightMost(){
        PQNode qNode = new QNode();
        PQNode A = new LeafNode("A");
        PQNode B = new LeafNode("B");
        PQNode C = new LeafNode("C");
        PQNode D = new LeafNode("D");
        List<PQNode> children = Arrays.asList(A, B, C, D);
        setCircularLinks(children);
        qNode.setQNodeEndmostChildren(A, D);

        List<PQNode> removeThese = new ArrayList<>(Arrays.asList(A, D));
        qNode.removeChildren(removeThese);

        assertTrue(B.getCircularLink_next() == C);
        assertTrue(C.getCircularLink_next() == B);

        assertTrue(C.getCircularLink_prev() == B);
        assertTrue(B.getCircularLink_prev() == C);

        assertTrue(qNode.endmostChildren().get(0) ==  B);
        assertTrue(qNode.endmostChildren().get(1) ==  C);
    }

    @Test
    public void removeLeftAndRightAndInterior(){
        PQNode qNode = new QNode();
        PQNode A = new LeafNode("A");
        PQNode B = new LeafNode("B");
        PQNode C = new LeafNode("C");
        PQNode D = new LeafNode("D");

        List<PQNode> children = Arrays.asList(A, B, C, D);

        setCircularLinks(children);
        qNode.setQNodeEndmostChildren(A, D);
        qNode.setParentQNodeChildren();

        List<PQNode> removeThese = new ArrayList<>(Arrays.asList(A, B, D));
        qNode.removeChildren(removeThese);

        assertTrue(C.getCircularLink_next() == C);
        assertTrue(C.getCircularLink_prev() == C);

        assertTrue(qNode.endmostChildren().get(0) ==  C);
        assertTrue(qNode.endmostChildren().get(1) ==  C);
        assertTrue(qNode.endmostChildren().size() == 2);
    }

    @Test
    public void removeAllChildren(){
        PQNode qNode = new QNode();
        PQNode A = new LeafNode("A");
        PQNode B = new LeafNode("B");
        PQNode C = new LeafNode("C");
        PQNode D = new LeafNode("D");
        List<PQNode> children = Arrays.asList(A, B, C, D);

        setCircularLinks(children);
        qNode.setQNodeEndmostChildren(A, D);
        qNode.setParentQNodeChildren();

        List<PQNode> removeThese = new ArrayList<>(children);
        qNode.removeChildren(removeThese);

        assertTrue(qNode.endmostChildren().get(0) == null);
        assertTrue(qNode.endmostChildren().get(1) == null);
    }


    @Test
    public void immediateSiblings() throws Exception {

    }



}
