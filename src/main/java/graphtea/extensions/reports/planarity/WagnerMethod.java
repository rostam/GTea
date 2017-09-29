package graphtea.extensions.reports.planarity;

import graphtea.graph.graph.Edge;
import graphtea.library.BaseVertex;
import graphtea.library.BaseEdge;
import graphtea.library.BaseGraph;
import graphtea.library.algorithms.Algorithm;
import graphtea.library.algorithms.AutomatedAlgorithm;
import graphtea.library.algorithms.planarity.CustomEdge;
import graphtea.library.algorithms.planarity.CustomGraph;
import graphtea.library.algorithms.planarity.CustomVertex;
import graphtea.library.event.GraphRequest;
import graphtea.graph.graph.GraphModel;
import graphtea.graph.graph.Edge;
import graphtea.graph.graph.Vertex;
import java.util.ArrayList;
import java.util.Iterator;

public class WagnerMethod {

    private boolean quitEarly;
    public WagnerMethod() {
        quitEarly = false;
    }

    public boolean isPlanar(GraphModel graph){

        if(quitEarly) return false;

        //if(graph.getEdgesCount() < 9 || graph.getVerticesCount() < 5){
        //    return true;
        //}

        if(isNotPlanar(graph)) {
            System.out.println("Is Not Planar!");
            quitEarly = true;
            return false;
        }

        for (Edge e : graph.getEdges()) {
            isPlanar( removeEdge(e, graph));
            isPlanar( contractEdge(e, graph));

            // TODO: I think subdivideEdge(...) causes an infinite loop because it adds a vertex and edge
            // isPlanar( subdivideEdge(e, graph) );
        }

        for (Vertex v : graph) {
            isPlanar( removeVertex(v, graph));
        }

        if(quitEarly) return false;

        return true;
    }

    public GraphModel removeEdge( Edge edge, GraphModel graph ){
        GraphModel g = new GraphModel();
        for (Vertex v : graph) {
            g.insertVertex(v);
        }
        for (Edge e : graph.edges()) {
            g.insertEdge(e);
        }
        g.removeEdge(edge);
        return g;
    }

    /** Contracts source into target */
    public GraphModel contractEdge(Edge edge, GraphModel graph){
        GraphModel g = new GraphModel();
        for (Vertex v : graph) {
            g.insertVertex(v);
        }
        for (Edge e : graph.edges()) {
            g.insertEdge(e);
        }

        Vertex trg = edge.target;
        Vertex srcContract = edge.source;

        ArrayList<Edge> edgesToDelete = new ArrayList<Edge>();
        ArrayList<Edge> edgesToAdd = new ArrayList<Edge>();
        g.removeEdge(edge);
        for ( Edge e : g.edges() ) {
            if (e.source == srcContract) {
                //Set edge source as trg
                Edge eNew = new Edge(trg, e.target);
                edgesToDelete.add(e);
                edgesToAdd.add(eNew);
            } else if (e.target == srcContract) {
                Edge eNew = new Edge(e.source, trg);
                edgesToDelete.add(e);
                edgesToAdd.add(eNew);
            }
        }
        for ( Edge e : edgesToDelete) {
            g.removeEdge(e);
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
        for (Edge e : graph.edges()) {
            g.insertEdge(e);
        }

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
                for (Edge e : graph.edges()) {
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
}
