package graphtea.extensions.reports.planarity.planaritypq;

import com.sun.org.apache.xerces.internal.impl.dv.dtd.NOTATIONDatatypeValidator;
import graphtea.extensions.algs4.Graph;
import graphtea.extensions.reports.numberings.NotBiconnectedException;
import graphtea.extensions.reports.numberings.StNumbering;
import graphtea.graph.graph.Edge;
import graphtea.graph.graph.GraphModel;
import graphtea.graph.graph.Vertex;

import java.util.*;

import static graphtea.extensions.reports.planarity.planaritypq.PQHelpers.*;

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

    public boolean isPlanar(GraphModel graph) throws NotBiconnectedException {

        if(!configureSTMapping(graph))
            throw new NotBiconnectedException("Graph must be biconnected for StNumbering to work!");

        Iterable<Edge> source = graph.getEdges();
        HashSet<PQNode> U = new HashSet<>();

        PQNode T = new PQNode(PQNode.PNODE, PQNode.EMPTY);
        T.id = "T";
        for(Edge e : source){

            //System.out.print("(" + e.source.getId() + " -> " + e.target.getId() + "), ");
            //int targetId = e.target.getId();
            //int sourceId = e.source.getId();
            int targetId = stMapping.get(e.target);
            int sourceId = stMapping.get(e.source);
            System.out.print("(" + sourceId + " -> " + targetId + "), ");

            if (Math.min(targetId, sourceId) == 0) {
                //PQNode leafNode = new PQNode(e.source.getId() + " -> " + e.target.getId());
                PQNode leafNode = new PQNode(sourceId + " -> " + targetId);
                leafNode.parent = T;
                U.add(leafNode);
            }
        }

        System.out.println("\n----------A-----------");
        PQ PQTree = new PQ();

        // Same as T(U, U) because all U are set to full and T (a p-node) can reach all of U in any order
        T.children.addAll(U);
        //PQHelpers.setCircularLinks(T.children);

        for(int j=1; j<graph.getVertexArray().length-1; j++){
            System.out.println("ITERATION: " + j);

            PQHelpers.reset(T, false, true);

            // The set of edges whose higher numbered vertex is j
            // These are the edges we are to reduce
             ArrayList<PQNode> S = new ArrayList<>();
             for(Edge e : source){
                 //int targetId = e.target.getId();
                 //int sourceId = e.source.getId();
                 int targetId = stMapping.get(e.target);
                 int sourceId = stMapping.get(e.source);

                 if (Math.max(targetId, sourceId) == j) {
                     //String _id = e.source.getId() + " -> " + e.target.getId();
                     String _id = sourceId + " -> " + targetId;

                     PQNode leafNode = null;

                     //System.out.println("Max: " + _id);

                     // Find leaf in the universal set
                     for(PQNode pq : U){
                         //System.out.println("U:" + pq.id);
                         if(pq.id.equals(_id)){
                             leafNode = pq;
                             leafNode.labelType = PQNode.FULL; // All descendants in S
                             // System.out.println("Add to S: " + pq.id);
                             //S.add(leafNode);
                             S.add(0, leafNode);
                        }
                        else {
                             if(!pq.labelType.equals(PQNode.FULL)) // leaves will never be PARTIAL
                                 pq.labelType = PQNode.EMPTY; // No descendants in S
                        }
                    }

                    // The Leaf node should always exist
                     try {
                         if (leafNode == null) {
                             throw new NodeNotFoundException("Leaf Node was not found");
                         }
                     } catch (NodeNotFoundException exc) { }

                    //System.out.println(S.get(S.size()-1).id);
                }
            }

            try {
                if (S.isEmpty()) {
                    throw new IllegalNumberingException("S is empty");
                }
            }
            catch (IllegalNumberingException e) {
                //System.exit(2);
                return false;
            }

            System.out.println("----------B-----------");
            PQHelpers.printPreorderIds(T);
            PQHelpers.printListIds(S, "S");

            System.out.println("Running Bubble");
            //if(S.size() > 0) {
            PQHelpers.reset(T, true, false);
            T = PQTree.bubble(T, S);
            //}

            System.out.println("Running Reduce");
            //if(S.size() > 0)
            T = PQTree.reduce(T, S);

            if (T == null) {
                return false;
            }

            PQHelpers.printPreorderIds(T);

            // The set of edges whose lower numbered vertex is j
            // These edges are added to Sp
            ArrayList<PQNode> Sp = new ArrayList<>();
            for(Edge e : source){
                //int targetId = e.target.getId();
                //int sourceId = e.source.getId();
                int targetId = stMapping.get(e.target);
                int sourceId = stMapping.get(e.source);
                if (Math.min(targetId, sourceId) == j) {

                    //String _id = e.source.getId() + " -> " + e.target.getId();
                    String _id = sourceId + " -> " + targetId;

                    PQNode leafNode = null;
                    // Check if leaf node already exists in the universal set
                    for(PQNode pq : U){
                        if(pq.id.equals(_id)){
                            leafNode = pq;
                            break;
                        }
                    }
                    // If leaf node does not exist yet, make it exist
                    if(leafNode == null){
                        leafNode = new PQNode(_id);
                    }
                    Sp.add(leafNode);

                    //System.out.println(Sp.get(Sp.size()-1).id);
                }
            }
            System.out.println("----------C-----------");
            PQHelpers.printListIds(Sp, "Sp");

            try {
                if (Sp.isEmpty()){
                    throw new IllegalNumberingException("Sp is empty");
                }
            }
            catch (IllegalNumberingException e) {
                //System.exit(2);
                return false;
            }


            PQNode root = PQTree.root(T, S);

            /*if(root == null && Sp.size() > 0){
                // If S is empty

                try {
                    if (T.nodeType.equals(PQNode.PNODE)) { // T should always be a P-Node

                        if(Sp.size() == 1){
                            T.children.add(Sp.get(0));
                            Sp.get(0).parent = T;
                        }
                        else {
                            PQNode intermediaryPNode = new PQNode(PQNode.PNODE, PQNode.EMPTY);
                            T.children.add(intermediaryPNode);
                            intermediaryPNode.parent = T;
                            for (PQNode n : Sp) {
                                n.parent = intermediaryPNode;
                            }
                        }

                    }
                    else throw new IllegalNodeTypeException("T is not a P-Node");
                }
                catch (IllegalNodeTypeException e) { }


            }
            // if ROOT(T, S) is a Q-Node
            else */
            if (root.nodeType.equals(PQNode.QNODE) && Sp.size() > 0) {
                // replace the full children of ROOT(T, S) and their descendants by T(S', S')
                System.out.println("Inside: Subtree root is a Q-node");

                replaceFullChildrenOfRoot(root, Sp);

            } else if(Sp.size() > 0) {
                // replace ROOT(T, S) and its descendants by T(S', S')
                System.out.println("Inside: Subtree root is a P-node");

                replaceRootAndDescendants(root, Sp);

            } /*else {
                // if the set Sp is empty

                List<PQNode> removalNodes = root.fullChildren();
                root.removeChildren(removalNodes);

                if(root.getChildren().size() == 1) {
                    PQNode child = root.getChildren().get(0);
                    PQNode rParent = root.getParent();

                    if(rParent.nodeType.equals(PQNode.QNODE)){
                        // rParent is Q-Node

                        PQHelpers.insertNodeIntoSameChildIndex(child, root, rParent);
                        rParent.removeChildren(Arrays.asList(root));

                    }
                    else {
                        // rParent is P-Node

                        rParent.removeChildren(Arrays.asList(root));
                        rParent.children.add(child);
                        child.parent = rParent;
                    }
                }

            }*/

            PQHelpers.printListIds(S, "S");
            PQHelpers.printListIds(Sp, "Sp");
            PQHelpers.printListIds(new ArrayList<>(U), "U before");

            // U := U - S union S' ->  U := (U - S) union S'
            U.removeAll(S);
            U.addAll(Sp);
            //U.addAll(0, Sp);

            PQHelpers.printListIds(new ArrayList<>(U), "U after");
            PQHelpers.printPreorderIds(T);
            //System.out.println("COMPLETED ITERATION: " + j);
        }
        System.out.println("DONE!");
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
            PQNode leftMost = fullChildren.get(0);
            PQNode traversal = root.children.get(0);
            int index = 0;
            while (traversal != leftMost) {
                if(traversal.labelType.equals(PQNode.FULL)){
                    break;
                }
                index++;
                traversal = traversal.circularLink_next;
            }

            PQNode rightMostFullNext = fullChildren.get(fullChildren.size() - 1).circularLink_next;
            PQNode leftMostFullPrev = fullChildren.get(0).circularLink_prev;

            root.removeChildren(fullChildren);

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
        /*else {
            // Otherwise, SP.Size() == 0.

            // ignored intentionally
            System.out.println("QNode: SP ZERO");
            root.removeChildren(fullChildren);
        }*/

        // Q-Nodes are directional, but this only matters if they have 3+ children.
        if(root.getChildren().size() < 3) { //todo: should this be here?
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
        else /*if(Sp.size() > 1)*/ {
            // Create an intermediary P-Node
            replacementNode = new PQNode(PQNode.PNODE, PQNode.EMPTY);
            replacementNode.id = "rNode";
            replacementNode.children.addAll(Sp);
            //setCircularLinks(replacementNode.children);
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
        else /*if(!SpEmpty)*/{
            // ROOT(T, S) is the root of the whole tree.

            PQHelpers.printPreorderIds(root);
            PQHelpers.printPreorderIds(replacementNode);

            List<PQNode> removedNodes = root.fullChildren();
            root.removeChildren(removedNodes);

            root.children.add(replacementNode);

            root.id = "rT"; // For testing purposes
        }

    }

}
