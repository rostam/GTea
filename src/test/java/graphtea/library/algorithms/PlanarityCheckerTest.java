package graphtea.library.algorithms;

import graphtea.extensions.actions.ComplementGraph;
import graphtea.extensions.generators.BananaTreeGenerator;
import graphtea.extensions.generators.CircleGenerator;
import graphtea.extensions.generators.CompleteGraphGenerator;
import graphtea.extensions.generators.GearGenerator;
import graphtea.graph.graph.GraphModel;
import graphtea.library.algorithms.planarity.PlanarityChecker;
import graphtea.library.algorithms.util.BipartiteChecker;
import org.junit.Test;
import static org.junit.Assert.*;

public class PlanarityCheckerTest {
    @Test
    public void CompleteGraphTest() {
        CompleteGraphGenerator gen = new CompleteGraphGenerator();
        for (int i = 1; i < 20; i++) {
            GraphModel gm = gen.generateCompleteGraph(i);
            if (i < 5) {
                assertTrue(PlanarityChecker.isPlanar(gm));
            }
            else {
                assertFalse(PlanarityChecker.isPlanar(gm));
            }
        }
    }
    @Test
    public void CircleGraphTest() {
        CircleGenerator gen = new CircleGenerator();
        for (int i = 1; i < 20; i++) {
            GraphModel gm = gen.generateCircle(i);
            assertTrue(PlanarityChecker.isPlanar(gm));
        }
    }

    @Test
    public void BananaTreeGraphTest() {
        GearGenerator gen = new GearGenerator();
        for (int i = 1; i < 9; i++) {
            GraphModel gm = gen.generateGear(i);
            assertTrue(PlanarityChecker.isPlanar(gm));
        }
    }
}
