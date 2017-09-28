package graphtea.library.algorithms.planarity;

import graphtea.library.BaseVertex;
import graphtea.library.BaseEdge;
import graphtea.library.BaseGraph;
import graphtea.library.algorithms.Algorithm;
import graphtea.library.algorithms.AutomatedAlgorithm;
import graphtea.library.event.GraphRequest;
import java.util.ArrayList;

/**
 * Tests whether a graph is planar.
 *
 * The intention of this class is to use it as a base in order to compare and help
 * test a much more efficient planarity checking algorithm.
 * */
public class PlanarityCheckerSlow <VertexType extends BaseVertex, EdgeType extends BaseEdge<VertexType>> extends Algorithm implements AutomatedAlgorithm {

    public boolean isPlanar( CustomGraph graph ){

        if(isNotPlanar(graph)) return false;

        for (CustomEdge e : graph.edgesList) {
            isPlanar( removeEdge(e, graph) );
            isPlanar( contractEdge(e, graph) );

            // TODO: I think subdivideEdge(...) causes an infinite loop because it adds a vertex and edge
            isPlanar( subdivideEdge(e, graph) );
        }

        for (CustomVertex v : graph.verticesList) {
            isPlanar( removeVertex(v, graph) );
        }

        return true;
    }

    public CustomGraph removeEdge( CustomEdge edge, CustomGraph graph ){
        CustomGraph g = new CustomGraph(new ArrayList<>(graph.edgesList), new ArrayList<>(graph.verticesList));
        g.edgesList.remove(edge);
        return g;
    }

    /** Contracts source into target */
    public CustomGraph contractEdge(CustomEdge edge, CustomGraph graph){
        CustomGraph g = new CustomGraph(new ArrayList<>(graph.edgesList), new ArrayList<>(graph.verticesList));

        CustomVertex trg = edge.target;
        CustomVertex srcContract = edge.source;

        g.edgesList.remove(edge);
        for ( CustomEdge e : g.edgesList ) {
            if( e.source == srcContract ){
                e.source = trg;
            }
            else if( e.target == srcContract ){
                e.target = trg;
            }
        }
        g.verticesList.remove(srcContract);

        return g;
    }

    public CustomGraph subdivideEdge(CustomEdge edge, CustomGraph graph ){
        CustomGraph g = new CustomGraph(new ArrayList<>(graph.edgesList), new ArrayList<>(graph.verticesList));

        CustomVertex trg = edge.target;
        CustomVertex src = edge.source;

        g.edgesList.remove(edge);
        CustomVertex newVertex = new CustomVertex(g.getNewVertexId());
        g.edgesList.add(new CustomEdge(src, newVertex));
        g.edgesList.add(new CustomEdge(newVertex, trg));
        g.verticesList.add(newVertex);

        return g;
    }

    public CustomGraph removeVertex(CustomVertex vertex, CustomGraph graph ){
        CustomGraph g = new CustomGraph(new ArrayList<>(graph.edgesList), new ArrayList<>(graph.verticesList));
        g.verticesList.remove(vertex);
        return g;
    }

    /** Checks if graph is isomorphic to k5 or k3,3
     *
     * Returns true if the current graph is not isomorphic to k5 or k3,3 */
    public boolean isNotPlanar( CustomGraph graph ) {
        /**k5 check*/
        if (graph.edgesList.size() == 10 && graph.verticesList.size() == 5) {
            return true; // Definitely not planar
        }

        /**k3,3 check*/
        if (graph.edgesList.size() == 9 && graph.verticesList.size() == 6) {

            for (CustomVertex v : graph.verticesList) {

                int degree = 0;
                for (CustomEdge e : graph.edgesList) {
                    if (e.source == v || e.target == v) {
                        degree++;
                    }
                }
                if (degree != 3) return false; // May be planar
            }
            return true; // Definitely not planar
        }
        return false; // May be planar
    }


    @Override
    public void doAlgorithm() {
        GraphRequest<VertexType, EdgeType> gr = new GraphRequest<>();
        dispatchEvent(gr);
        BaseGraph<VertexType, EdgeType> graph = gr.getGraph();

        ArrayList<CustomVertex> vertices = new ArrayList<>(graph.getVerticesCount());
        for (VertexType v : graph) {
            vertices.add(new CustomVertex(v.getId()));
        }

        ArrayList<CustomEdge> edges = new ArrayList<>(graph.getEdgesCount());
        for (EdgeType e : graph.edges())
            edges.add(new CustomEdge(e.source, e.target));


        // Check planarity
        System.out.println("Checking planarity ... ");
        boolean p = isPlanar(new CustomGraph(edges, vertices));
    }

    public PlanarityCheckerSlow() { }

    ////

}






