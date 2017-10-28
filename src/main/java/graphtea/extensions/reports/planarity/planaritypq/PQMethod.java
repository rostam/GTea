package graphtea.extensions.reports.planarity.planaritypq;

import graphtea.graph.graph.Edge;
import graphtea.graph.graph.GraphModel;
import graphtea.graph.graph.Vertex;

import java.util.ArrayList;
import java.util.List;

import static graphtea.extensions.reports.planarity.planaritypq.PQHelpers.union;

public class PQMethod {
    private GraphModel graph;

    public PQMethod() {
    }

    public boolean isPlanar(GraphModel graph){
        //graph.setDirected(false);

        Iterable<Edge> source = graph.getEdges();
        ArrayList<PQNode> U = new ArrayList<>();
        for(Edge e : source){
            Vertex lowerNumberedVertex;
            int targetId = Integer.parseInt(e.target.getLabel());
            int sourceId = Integer.parseInt(e.source.getLabel());
            if (Math.min(targetId, sourceId) == 1) {
                U.add(new PQNode(e.source.getLabel() + " -> " + e.target.getLabel()));
                System.out.println(U.get(U.size()-1).id);
            }
        }
        //PQNode T = new PQTree(U, U);
        PQ PQTree = new PQ();
        PQNode T = new PQNode();
        for(int j=2; j<graph.getVertexArray().length-1; j++){
             ArrayList<PQNode> S = new ArrayList<>();
             for(Edge e : source){
                Vertex higherNumberedVertex;
                int targetId = Integer.parseInt(e.target.getLabel());
                int sourceId = Integer.parseInt(e.source.getLabel());
                if (Math.max(targetId, sourceId) == j) {
                    S.add(new PQNode(e.source.getLabel() + " -> " + e.target.getLabel()));
                    System.out.println(S.get(S.size()-1).id);
                }
            }
            T = PQTree.bubble(T, S);
            T = PQTree.reduce(T, S);
            if (T == null) {
                return false;
            }

            ArrayList<PQNode> Sp = new ArrayList<>();
            for(Edge e : source){
                Vertex higherNumberedVertex;
                int targetId = Integer.parseInt(e.target.getLabel());
                int sourceId = Integer.parseInt(e.source.getLabel());
                if (Math.min(targetId, sourceId) == j) {
                    Sp.add(new PQNode(e.source.getLabel() + " -> " + e.target.getLabel()));
                    System.out.println(Sp.get(Sp.size()-1).id);
                }
            }
            if (T.nodeType.equals(PQNode.QNODE)) {
                
            }

        }

        return true;
    }

}
