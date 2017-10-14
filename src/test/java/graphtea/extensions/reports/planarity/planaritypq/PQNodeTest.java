package graphtea.extensions.reports.planarity.planaritypq;

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
    public void endmostChildren() throws Exception {
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

}
