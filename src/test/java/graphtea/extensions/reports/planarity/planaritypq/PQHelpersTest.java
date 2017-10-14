package graphtea.extensions.reports.planarity.planaritypq;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import graphtea.extensions.reports.planarity.planaritypq.PQHelpers;

import static org.junit.Assert.*;

public class PQHelpersTest {
    @Test
    public void setCircularLinks() throws Exception {
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
}
