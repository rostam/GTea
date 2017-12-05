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

    HashMap<Vertex, Integer> stMapping;

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

        PQNode T = new PQNode(PQNode.PNODE, PQNode.EMPTY);
        T.id = "T";
        if(lowerAndHigherVerticesMap.get(0) != null) {
            for (Edge e : lowerAndHigherVerticesMap.get(0).first) {
                PQNode leafNode = new PQNode(stMapping.get(e.source) + " -> " + stMapping.get(e.target));
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

            try {
                if (S.isEmpty()) {
                    throw new IllegalNumberingException("S is empty");
                }
            }
            catch (IllegalNumberingException e) {
                return false;
            }

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
                        leafNode = new PQNode(_id);
                    }
                    Sp.add(leafNode);

                }
            }

            try {
                if (Sp.isEmpty()){
                    throw new IllegalNumberingException("Sp is empty");
                }
            }
            catch (IllegalNumberingException e) {
                return false;
            }


            PQNode root = PQTree.root(T, S);

            if (root.nodeType.equals(PQNode.QNODE) && Sp.size() > 0) {
                // replace the full children of ROOT(T, S) and their descendants by T(S', S')
                replaceFullChildrenOfRoot(root, Sp);

            } else if(Sp.size() > 0) {
                // replace ROOT(T, S) and its descendants by T(S', S')
                replaceRootAndDescendants(root, Sp);

            }

            // U := U - S union S' ->  U := (U - S) union S'
            U.removeAll(S);
            U.addAll(Sp);

            PQHelpers.printPreorderIds(T);
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

            // Finding the correct index to place nodeSp
            int index = 0;
            PQNode traversal = root.endmostChildren().get(0);
            while(!fullChildren.contains(traversal)){
                traversal = traversal.circularLink_next;
                index++;
            }

            PQNode rightMostFullNext = fullChildren.get(fullChildren.size() - 1).circularLink_next;
            PQNode leftMostFullPrev = fullChildren.get(0).circularLink_prev;

            if(index == 0){
                PQHelpers.insertNodeIntoCircularList(nodeSp, root.endmostChildren().get(1), root.endmostChildren().get(0));
            }
            else {
                PQHelpers.insertNodeIntoCircularList(nodeSp, leftMostFullPrev, rightMostFullNext);
            }

            // Because Q-Node should not keep track of all children adding at the index should eventually be phased
            // out and replaced with replacing the endMost children
            if(root.children.size() < index) {
                root.children.add(nodeSp);
            }
            else {
                root.children.add(index, nodeSp);
            }

            root.removeChildren(fullChildren);

        }
        else if(Sp.size() > 0) {
            // Otherwise, create a P node

            PQNode replacementPNode = new PQNode(PQNode.PNODE, PQNode.EMPTY);
            replacementPNode.id = "rNode-2";
            replacementPNode.parent = root;
            replacementPNode.children.addAll(Sp);
            setCircularLinks(replacementPNode.children);

            for(PQNode n : Sp){
                n.parent = replacementPNode;
            }

            boolean fullsOnLeftSide = false;
            if(root.endmostChildren().get(0).labelType.equals(PQNode.FULL)){
                fullsOnLeftSide = true;
            }

            root.removeChildren(fullChildren);

            // Set the circular links with replacementPNode
            if(fullsOnLeftSide){
                root.children.add(0, replacementPNode); // Add to start of list
            }
            else {
                // Fulls were on the right side
                root.children.add(replacementPNode); // Add to end of list
            }

            //PQHelpers.insertNodeIntoCircularList(replacementPNode, fullChildren.get(0), fullChildren.get(fullChildren.size()-1));
            PQHelpers.insertNodeIntoCircularList(replacementPNode, fullChildren.get(0).circularLink_prev,
                    fullChildren.get(fullChildren.size()-1).circularLink_next);

        }

        //root.setParentQNodeChildren();

        // Q-Nodes are directional, but this only matters if they have 3+ children.
        if(root.getChildren().size() < 3) {
            root.nodeType = PQNode.PNODE;
        }

    }

    public void replaceRootAndDescendants(PQNode root, List<PQNode> Sp){
        PQNode rParent = root.getParent();
        //boolean SpEmpty = false;
        PQNode replacementNode;
        if(Sp.size() == 1){
            // Simplify by not adding an intermediary P-Node
            replacementNode = Sp.get(0);
        }
        else {
            // Create an intermediary P-Node
            replacementNode = new PQNode(PQNode.PNODE, PQNode.EMPTY);
            replacementNode.id = "rNode";
            replacementNode.children.addAll(Sp);
            for(PQNode n : Sp){
                n.parent = replacementNode;
            }
        }

        if(rParent != null /*&& !SpEmpty*/) {
            // ROOT(T, S) is not the root of the whole tree

            replacementNode.parent = rParent;

            if(rParent.nodeType.equals(PQNode.QNODE)){
                // rParent is a Q-Node

                // Places replacementPNode into the same index in the children list as the root
                // This is important because q-nodes are directional and ordered

                rParent.replaceQNodeChild(replacementNode, root);

            }
            else {
                // rParent is a P-Node

                rParent.removeChildren(Arrays.asList(root));
                rParent.children.add(replacementNode);
            }

        }
        else {
            // ROOT(T, S) is the root of the whole tree.

            //PQHelpers.printPreorderIds(root);
            //PQHelpers.printPreorderIds(replacementNode);

            List<PQNode> removedNodes = root.fullChildren();
            root.removeChildren(removedNodes);

            root.children.add(replacementNode);

            //root.id = "rT"; // For testing purposes
        }

    }

}
