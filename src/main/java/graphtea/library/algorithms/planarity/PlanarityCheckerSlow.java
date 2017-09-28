package graphtea.library.algorithms.planarity;

import graphtea.graph.graph.Edge;
import graphtea.library.BaseVertex;
import graphtea.library.BaseEdge;
import graphtea.library.BaseGraph;
import graphtea.library.algorithms.Algorithm;
import graphtea.library.algorithms.AutomatedAlgorithm;
import graphtea.library.event.GraphRequest;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Tests whether a graph is planar.
 *
 * The intention of this class is to use it as a base in order to compare and help
 * test a much more efficient planarity checking algorithm.
 * */
public class PlanarityCheckerSlow <VertexType extends BaseVertex, EdgeType extends BaseEdge<VertexType>> extends Algorithm implements AutomatedAlgorithm {

    boolean quitEarly = false;

    public boolean isPlanar( CustomGraph graph ){

        if(quitEarly) return false;

        if(graph.getEdgeList().size() < 9 || graph.getVerticesList().size() < 5){
            return true;
        }

        if(isNotPlanar(graph)) {
            System.out.println("Is Not Planar!");
            quitEarly = true;
            return false;
        }

        for (CustomEdge e : graph.getEdgeList()) {
            isPlanar( removeEdge(e, graph) );
            isPlanar( contractEdge(e, graph) );

            // TODO: I think subdivideEdge(...) causes an infinite loop because it adds a vertex and edge
            // isPlanar( subdivideEdge(e, graph) );
        }

        for (CustomVertex v : graph.getVerticesList()) {
            isPlanar( removeVertex(v, graph) );
        }

        if(quitEarly) return false;

        return true;
    }

    public CustomGraph removeEdge( CustomEdge edge, CustomGraph graph ){
        CustomGraph g = new CustomGraph(new ArrayList<>(graph.getEdgeList()), new ArrayList<>(graph.getVerticesList()));
        g.getEdgeList().remove(edge);
        return g;
    }

    /** Contracts source into target */
    public CustomGraph contractEdge(CustomEdge edge, CustomGraph graph){
        CustomGraph g = new CustomGraph(new ArrayList<>(graph.getEdgeList()), new ArrayList<>(graph.getVerticesList()));

        CustomVertex trg = edge.target;
        CustomVertex srcContract = edge.source;

        g.getEdgeList().remove(edge);
        for ( CustomEdge e : g.getEdgeList() ) {
            if( e.source == srcContract ){
                e.source = trg;
            }
            else if( e.target == srcContract ){
                e.target = trg;
            }
        }
        g.getVerticesList().remove(srcContract);

        return g;
    }

    public CustomGraph subdivideEdge(CustomEdge edge, CustomGraph graph ){
        CustomGraph g = new CustomGraph(new ArrayList<>(graph.getEdgeList()), new ArrayList<>(graph.getVerticesList()));

        CustomVertex trg = edge.target;
        CustomVertex src = edge.source;

        g.getEdgeList().remove(edge);
        CustomVertex newVertex = new CustomVertex(g.getNewVertexId());
        g.getEdgeList().add(new CustomEdge(src, newVertex));
        g.getEdgeList().add(new CustomEdge(newVertex, trg));
        g.getVerticesList().add(newVertex);

        return g;
    }

    public CustomGraph removeVertex(CustomVertex vertex, CustomGraph graph ){
        CustomGraph g = new CustomGraph(new ArrayList<>(graph.getEdgeList()), new ArrayList<>(graph.getVerticesList()));

        for(Iterator<CustomEdge> it = g.getEdgeList().iterator(); it.hasNext(); ){
            CustomEdge e = it.next();
            if(e.getSource() == vertex || e.getTarget() == vertex){
                it.remove();
            }
        }

        g.getVerticesList().remove(vertex);
        return g;
    }

    /** Checks if graph is isomorphic to k5 or k3,3
     *
     * Returns true if the current graph is not isomorphic to k5 or k3,3 */
    public boolean isNotPlanar( CustomGraph graph ) {
        /**k5 check*/
        if (graph.getEdgeList().size() == 10 && graph.getVerticesList().size() == 5) {
            return true; // Definitely not planar
        }

        /**k3,3 check*/
        if (graph.getEdgeList().size() == 9 && graph.getVerticesList().size() == 6) {

            for (CustomVertex v : graph.getVerticesList()) {

                int degree = 0;
                for (CustomEdge e : graph.getEdgeList()) {
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

}






