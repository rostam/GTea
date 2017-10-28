package graphtea.extensions.reports.planarity.planaritypq;

import graphtea.extensions.generators.KmnGenerator;
import graphtea.extensions.reports.planarity.WagnerMethod;
import graphtea.graph.graph.GraphModel;
import org.junit.Test;

import static org.junit.Assert.*;

public class PQMethodTest {
    @Test
    public void k33Test(){
        KmnGenerator gen = new KmnGenerator();
        GraphModel gm = gen.generateGraph();
        PQMethod pc = new PQMethod();
        assertFalse(pc.isPlanar(gm));
    }
}