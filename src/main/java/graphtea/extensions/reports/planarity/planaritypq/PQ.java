package graphtea.extensions.reports.planarity.planaritypq;

import graphtea.graph.graph.Edge;
import graphtea.graph.graph.GraphModel;
import graphtea.graph.graph.Vertex;

import java.util.*;

public class PQ {
    private int blockCount;
    private int blockedNodes;
    private int offTheTop;

    public boolean planar(GraphModel graph){
        graph.setDirected(false); // do I need this?

        Iterable<Edge> source = graph.getEdges();
        ArrayList<PQEdge> U = new ArrayList<>();
        for(Edge e : source){

            U.add(new PQEdge(e, (PQNode) e.source, (PQNode) e.target));
        }
        PQTree T = new PQTree(U, U);

        for(int j=2; j<graph.getVertexArray().length-1; j++){
            Vertex curVertex = graph.getVertex(j);

            // S := the set of edges whose higher-numbered vertex is j
            List<PQEdge> S = new ArrayList<>();
            for(Vertex v : graph.directNeighbors(curVertex)){
                S.add((PQEdge) graph.getEdge(curVertex, v));
            }

            T = bubble(T, S);
            T = reduce(T, S);

            if(T == null){
                return false;
            }

            // S' := the set of edges whose lower-numbered vertex is j
            List<PQEdge> SPrime = new ArrayList<>();
            for(Vertex v : graph.directNeighbors(curVertex)){
                S.add((PQEdge) graph.getEdge(v, curVertex));
            }

            PQNode root = root(T,S);
            if(root.type() == PQNode.QNODE){
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


    public PQTree bubble(PQTree T, List<PQEdge> S){
        Queue<PQEdge> queue = new LinkedList<PQEdge>(S);
        blockCount = 0;
        blockedNodes = 0;
        offTheTop = 0;

        while(queue.size() + blockCount + offTheTop > 1){
            if(queue.size() == 0){
                T = null;
                return T;
            }

            PQNode x = queue.remove().parent;
            x.blocked = true;

            /** Should we have PQEdge/PQNode or just PQNode.. ?? Decide!*/
            List<PQNode> BS = new ArrayList<>();
            List<PQNode> US = new ArrayList<>();
            for(PQNode n : T.immediateSiblings(x)){
                if(n.blocked) {
                    BS.add(n);
                }
                else {
                    US.add(n);
                }
            }

            if(US.size() > 0){
                x.parent = US.get(0).parent;
                x.blocked = false;
            }
            else if(T.immediateSiblings(x).size() < 2){
                x.blocked = false;
            }

            if(!x.blocked){
                PQNode y = x.parent;
                if(BS.size() > 0){
                    // TODO: LIST := the maximal consecutive set of blocked siblings adjacent to x


                }
            }

        }

        return null;
    }

    public PQTree reduce(PQTree T, List<PQEdge> S){

        return null;
    }

    public void replace(PQNode T, PQTree TPrime){

    }

    public PQNode root(PQTree T, List<PQEdge> S){

        return null;
    }

    public static List<PQEdge> union(List<PQEdge> list1, List<PQEdge> list2) {
        HashSet<PQEdge> set = new HashSet<>();

        set.addAll(list1);
        set.addAll(list2);

        return new ArrayList<>(set);
    }

}
