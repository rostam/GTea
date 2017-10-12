package graphtea.extensions.reports.planarity.planaritypq;

import graphtea.graph.graph.Edge;
import graphtea.graph.graph.GraphModel;
import graphtea.graph.graph.Vertex;

import java.util.ArrayList;
import java.util.List;

public class Planar extends PQ {
    private GraphModel graph;

    public Planar(GraphModel graph){
        this.graph = graph;
    }

    public boolean doAlgorithm(){
        //graph.setDirected(false);

        Iterable<Edge> source = graph.getEdges();
        ArrayList<PQNode> U = new ArrayList<>();
        for(Edge e : source){
            // TODO: U.add(new PQEdge(e, (PQNode) e.source, (PQNode) e.target));
        }
        PQTree T = new PQTree(U, U);

        for(int j=2; j<graph.getVertexArray().length-1; j++){
            Vertex curVertex = graph.getVertex(j);

            // S := the set of edges whose higher-numbered vertex is j
            List<PQNode> S = new ArrayList<>();
            for(Vertex v : graph.directNeighbors(curVertex)){
                // TODO: S.add((PQNode) graph.getEdge(curVertex, v));
            }

            T = bubble(T, S);
            T = reduce(T, S);

            if(T == null){
                return false;
            }

            // S' := the set of edges whose lower-numbered vertex is j
            List<PQNode> SPrime = new ArrayList<>();
            for(Vertex v : graph.directNeighbors(curVertex)){
                // TODO: S.add((PQEdge) graph.getEdge(v, curVertex));
            }

            PQNode root = root(T,S);
            if(root.nodeType.equals(PQNode.QNODE)){
                // Replace the full children of root(T,S) and their descendants by T(S',S')
                replace(root, new PQTree(SPrime, SPrime));
            } else {
                // Replace root(T,S) and its descendants by T(S',S')
                replace(root, new PQTree(SPrime, SPrime));
            }

            // U = U - S union S'
            U.removeAll(union(S, SPrime));
        }

        return true;
    }

}
