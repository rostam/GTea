package graphtea.library.algorithms.planarity;

import graphtea.library.BaseEdge;
import graphtea.library.BaseGraph;
import graphtea.library.BaseVertex;
import graphtea.library.algorithms.Algorithm;
import graphtea.library.algorithms.AutomatedAlgorithm;
import graphtea.library.event.MessageEvent;
import graphtea.library.event.typedef.BaseGraphRequest;

public class PlanarityChecker extends Algorithm implements AutomatedAlgorithm {
    public static <VertexType extends BaseVertex, EdgeType extends BaseEdge<VertexType>>
    boolean isPlanar(BaseGraph<VertexType, EdgeType> graph) {
        return false;
    }

    @Override
    public void doAlgorithm() {

    }
}
