package graphtea.extensions.reports.planarity;

import graphtea.graph.graph.Edge;
import graphtea.graph.graph.GraphModel;
import graphtea.graph.graph.Vertex;
import java.util.ArrayList;

public class WagnerMethod {

    private boolean quitEarly;
    public WagnerMethod() {
        quitEarly = false;
    }

    public boolean isPlanar(GraphModel graph){

        if(quitEarly) return false;

        if(graph.getEdgesCount() < 9 || graph.getVerticesCount() < 5){
            return true;
        }

        if(isNotPlanar(graph)) {
            System.out.println("Is Not Planar!");
            quitEarly = true;
            return false;
        }

        for (Edge e : graph.getEdges()) {
            isPlanar( contractEdge(e, graph));
            isPlanar( removeEdge(e, graph));
        }

        for (Vertex v : graph) {
            isPlanar( removeVertex(v, graph));
            if(quitEarly) return false;
        }

        if(quitEarly) return false;

        return true;
    }

    public GraphModel removeEdge( Edge edge, GraphModel graph ){
        GraphModel g = new GraphModel();

        for (Vertex v : graph) {
            g.insertVertex(v);
        }

        g.insertEdges(graph.getEdges());
        g.removeEdge(edge);
        return g;
    }

    /** Contracts source into target */
    public GraphModel contractEdge(Edge edge, GraphModel graph){
        GraphModel g = new GraphModel();

        for (Vertex v : graph) {
            g.insertVertex(v);
        }
        g.insertEdges(graph.getEdges());

        Vertex trg = edge.target;
        Vertex srcContract = edge.source;

        ArrayList<Edge> edgesToAdd = new ArrayList<Edge>();
        g.removeEdge(edge);

        for(Vertex v : g.directNeighbors(srcContract)){
            edgesToAdd.add(new Edge(v, trg));
            if(g.getEdge(v, srcContract) != null) {
                g.removeEdge(g.getEdge(v, srcContract));
            }
            else {
                g.removeEdge(g.getEdge(srcContract, v));
            }
        }

        for (Edge e : edgesToAdd) {
            g.insertEdge(e);
        }
        g.removeVertex(srcContract);

        return g;
    }

    public GraphModel removeVertex(Vertex vertex, GraphModel graph ){
        GraphModel g = new GraphModel();

        for (Vertex v : graph) {
            g.insertVertex(v);
        }
        g.insertEdges(graph.getEdges());

        g.removeVertex(vertex);
        return g;
    }

    /** Checks if graph is isomorphic to k5 or k3,3
     *
     * Returns true if the current graph is not isomorphic to k5 or k3,3 */
    public boolean isNotPlanar( GraphModel graph ) {
        /**k5 check*/
        if (graph.getEdgesCount() == 10 && graph.getVerticesCount() == 5) {
            return true; // Definitely not planar
        }

        /**k3,3 check*/
        if (graph.getEdgesCount() == 9 && graph.getVerticesCount() == 6) {

            for (Vertex v : graph) {

                int degree = 0;
                for(Vertex vi : graph.directNeighbors(v)){
                    degree++;
                }
                if (degree != 3) return false; // May be planar

            }
            return true; // Definitely not planar
        }
        return false; // May be planar
    }


}
