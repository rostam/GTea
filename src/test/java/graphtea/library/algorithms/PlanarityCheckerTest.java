package graphtea.library.algorithms;

import graphtea.extensions.actions.ComplementGraph;
import graphtea.extensions.generators.CompleteGraphGenerator;
import graphtea.graph.graph.GraphModel;
import org.junit.Test;
import static org.junit.Assert.*;

public class PlanarityCheckerTest {
    @Test
    public void K5test() {
        CompleteGraphGenerator cgg = new CompleteGraphGenerator();
        GraphModel gm = cgg.generateCompleteGraph(6);
        assertFalse(false);
    }
}
