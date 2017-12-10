package graphtea.extensions.reports.planarity.planaritypq;

import graphtea.extensions.reports.numberings.NotBiconnectedException;
import graphtea.extensions.reports.numberings.StNumbering;
import graphtea.graph.graph.Edge;
import graphtea.graph.graph.GraphModel;
import graphtea.graph.graph.Vertex;
import graphtea.library.util.Pair;

import java.lang.reflect.Array;
import java.util.*;

import static graphtea.extensions.reports.planarity.planaritypq.PQHelpers.*;

/**
 * This class takes a biconnected graph, applies an st-numbering, and then runs the edges
 * through a PQTree algorithm to determine whether the graph is planar. It is described
 * in the 1976 paper "Testing for Consecutive Ones Property, Interval Graphs,
 * and Graph Planarity Using PQ-Tree Algorithms" by Kellogg S. Booth and George S. Lueker.
 *
 * We highly recommend reading over the paper before reading this codebase.
 *
 * @author Alex Cregten
 * @author Hannes Kr. Hannesson
 * */
public class PQMethod {

    private HashMap<Vertex, Integer> stMapping;

    public PQMethod() {
    }
    private boolean configureSTMapping(GraphModel graph) {
        try {
            StNumbering st = new StNumbering(graph);
            stMapping = st.stNumbering();
        } catch (NotBiconnectedException e) {
            System.err.println(e);
            return false;
        }
        return true;
    }

    private HashMap<Integer, Pair<List<Edge>, List<Edge>>> precomputeLowerAndHigherVertices(GraphModel graph){

        HashMap <Integer, Pair<List<Edge>, List<Edge>>> precomputedMap = new HashMap<>();
        Iterable<Edge> source = graph.getEdges();

        for(Edge e : source){

            if(precomputedMap.get(e.source) == null) {
                int lower;
                int higher;
                if (stMapping.get(e.target) < stMapping.get(e.source)) {
                    lower = stMapping.get(e.target);
                    higher = stMapping.get(e.source);
                }
                else {
                    lower = stMapping.get(e.source);
                    higher = stMapping.get(e.target);
                }

                if(precomputedMap.get(lower) == null) {
                    precomputedMap.put(lower, new Pair<>(new ArrayList<>(), new ArrayList<>()));
                }
                if(precomputedMap.get(higher) == null) {
                    precomputedMap.put(higher, new Pair<>(new ArrayList<>(), new ArrayList<>()));
                }

                precomputedMap.get(lower).first.add(e);
                precomputedMap.get(higher).second.add(e);
            }

        }

        return precomputedMap;
    }

    public boolean isPlanar(GraphModel graph) throws NotBiconnectedException {

        if(!configureSTMapping(graph))
            throw new NotBiconnectedException("Graph must be biconnected for StNumbering to work!");

        HashMap<Integer, Pair<List<Edge>, List<Edge>>> lowerAndHigherVerticesMap = precomputeLowerAndHigherVertices(graph);

        Iterable<Edge> source = graph.getEdges();
        HashSet<PQNode> U = new HashSet<>();

        PNode T = new PNode(PQNode.EMPTY);
        T.id = "T";
        if(lowerAndHigherVerticesMap.get(0) != null) {
            for (Edge e : lowerAndHigherVerticesMap.get(0).first) {
                PQNode leafNode = new LeafNode(stMapping.get(e.source) + " -> " + stMapping.get(e.target));
                leafNode.parent = T;
                U.add(leafNode);
            }
        }

        PQ PQTree = new PQ();

        // Same as T(U, U) because all U are set to full and T (a p-node) can reach all of U in any order
        T.children.addAll(U);

        for(int j=1; j<graph.getVertexArray().length-1; j++){
            PQHelpers.reset(T, true, true);

            // The set of edges whose higher numbered vertex is j
            // These are the edges we are to reduce
             ArrayList<PQNode> S = new ArrayList<>();
            for (Edge e : lowerAndHigherVerticesMap.get(j).second) {
                int targetId = stMapping.get(e.target);
                int sourceId = stMapping.get(e.source);
                String _id = sourceId + " -> " + targetId;
                PQNode leafNode = null;
                for(PQNode pq : U){
                    if(pq.id.equals(_id)){
                        leafNode = pq;
                        leafNode.labelType = PQNode.FULL; // All descendants in S
                        S.add(0, leafNode);
                    }
                }
                // The Leaf node should always exist
                try {
                    if (leafNode == null) {
                        throw new NodeNotFoundException("Leaf Node was not found");
                    }
                } catch (NodeNotFoundException exc) { }

            }

            //PQHelpers.printListIds(S, "S");

            T = PQTree.bubble(T, S);
            T = PQTree.reduce(T, S);

            if (T == null) {
                return false;
            }

            // The set of edges whose lower numbered vertex is j
            // These edges are added to Sp
            ArrayList<PQNode> Sp = new ArrayList<>();
            for (Edge e : lowerAndHigherVerticesMap.get(j).first) {
                int targetId = stMapping.get(e.target);
                int sourceId = stMapping.get(e.source);
                if (Math.min(targetId, sourceId) == j) {
                    String _id = sourceId + " -> " + targetId;

                    PQNode leafNode = null;
                    // Check if leaf node already exists in the universal set
                    for (PQNode pq : U) {
                        if (pq.id.equals(_id)) {
                            leafNode = pq;
                            break;
                        }
                    }
                    // If leaf node does not exist yet, make it exist
                    if (leafNode == null) {
                        leafNode = new LeafNode(_id);
                    }
                    Sp.add(leafNode);

                }
            }

            //PQHelpers.printListIds(Sp, "S'");
            PQNode root = PQTree.root(T, S);

            if (root.getClass() == QNode.class) {
                // replace the full children of ROOT(T, S) and their descendants by T(S', S')
                replaceFullChildrenOfRoot(root, Sp);

            } else {
                // replace ROOT(T, S) and its descendants by T(S', S')
                replaceRootAndDescendants(root, Sp);

            }

            // U := U - S union S' ->  U := (U - S) union S'
            U.removeAll(S);
            U.addAll(Sp);

            //PQHelpers.printPreorderIds(T);
        }
        //System.out.println("DONE!");
        return true;
    }

    public void replaceFullChildrenOfRoot(PQNode root, List<PQNode> Sp){
        List<PQNode> fullChildren = root.fullChildren();

        if(fullChildren.size() == 0) {
            return;
        }

        if(Sp.size() == 1){
            // No need for a P-Node, so we simplify and leave it out
            PQNode nodeSp = Sp.get(0);
            nodeSp.parent = root;

            // Replace arbitrary full node - they will get removed anyway
            root.replaceChild(nodeSp, fullChildren.get(0));
            root.removeChildren(fullChildren);

        }
        else {
            // Otherwise, create a P node

            PQNode replacementPNode = new PNode(PQNode.EMPTY);
            replacementPNode.id = "rNode-2";
            replacementPNode.parent = root;
            replacementPNode.addChildren(Sp);

            root.replaceChild(replacementPNode, fullChildren.get(0));
            root.removeChildren(fullChildren);

        }
        root.setParentQNodeChildren();

        // Q-Nodes are directional, but this only matters if they have 3+ children.
        if(root.getChildren().size() < 3) {
            PQNode replacementPNode = new PNode(PQNode.EMPTY);
            PQHelpers.replaceParent(replacementPNode, root);
            replacementPNode.addChildren(root.getChildren());
            root = replacementPNode;
        }

    }

    public void replaceRootAndDescendants(PQNode root, List<PQNode> Sp){
        PQNode rParent = root.getParent();
        PQNode replacementNode;
        if(Sp.size() == 1){
            // Simplify by not adding an intermediary P-Node
            replacementNode = Sp.get(0);
        }
        else {
            // Create an intermediary P-Node
            replacementNode = new PNode(PQNode.EMPTY);
            replacementNode.id = "rNode";
            replacementNode.addChildren(Sp);
        }

        if(rParent != null) {
            // ROOT(T, S) is not the root of the whole tree

            replacementNode.parent = rParent;

            if (rParent.getClass() == QNode.class) {
                // rParent is a Q-Node
                rParent.replaceChild(replacementNode, root);

            }
            else {
                // rParent is a P-Node
                rParent.replaceChild(replacementNode, root);
            }

        }
        else {
            // ROOT(T, S) is the root of the whole tree.

            //PQHelpers.printPreorderIds(root);
            //PQHelpers.printPreorderIds(replacementNode);

            List<PQNode> removedNodes = root.fullChildren();
            root.removeChildren(removedNodes);

            //root.children.add(replacementNode);
            root.addChild(replacementNode);

        }

    }

}
