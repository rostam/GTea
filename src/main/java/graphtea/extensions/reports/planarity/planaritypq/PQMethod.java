package graphtea.extensions.reports.planarity.planaritypq;

import graphtea.graph.graph.Edge;
import graphtea.graph.graph.GraphModel;
import graphtea.graph.graph.Vertex;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static graphtea.extensions.reports.planarity.planaritypq.PQHelpers.setCircularLinks;
import static graphtea.extensions.reports.planarity.planaritypq.PQHelpers.union;

public class PQMethod {
    private GraphModel graph;

    public PQMethod() {
    }

    public boolean isPlanar(GraphModel graph){

        Iterable<Edge> source = graph.getEdges();
        ArrayList<PQNode> U = new ArrayList<>();
        PQNode T = new PQNode(PQNode.PNODE, PQNode.EMPTY);
        T.id = "T";
        for(Edge e : source){
            int targetId = e.target.getId();
            int sourceId = e.source.getId();
            if (Math.min(targetId, sourceId) == 0) {
                PQNode leafNode = new PQNode(e.source.getId() + " -> " + e.target.getId());
                // Labelled full because it is in the initial universal set T(U, U)
                //leafNode.labelType = PQNode.FULL;
                leafNode.parent = T;
                U.add(leafNode);
                //U.add(new PQNode(e.source.getLabel() + " -> " + e.target.getLabel()));
                System.out.println(U.get(U.size()-1).id);
            }
        }

        System.out.println("----------A-----------");
        //PQNode T = new PQTree(U, U);
        PQ PQTree = new PQ();

        // Same as T(U, U) because all U are set to full and T (a p-node) can reach all of U in any order
        T.children.addAll(U);
        PQHelpers.setCircularLinks(T.children);

        // This set is to contain all the leaves that are to be eventually queried.
        HashSet<PQNode> searchLeavesSet = new HashSet<>();
        searchLeavesSet.addAll(U);

        for(int j=1; j<graph.getVertexArray().length-1; j++){
            System.out.println("ITERATION: " + j);

            /** The set of edges whose higher numbered vertex is j
             *  These are the edges we are to reduce */
             ArrayList<PQNode> S = new ArrayList<>();
             for(Edge e : source){
                //Vertex higherNumberedVertex;
                 int targetId = e.target.getId();
                 int sourceId = e.source.getId();
                if (Math.max(targetId, sourceId) == j) {

                    String _id = e.source.getId() + " -> " + e.target.getId();
                    PQNode leafNode = null;

                    // Find leaf in the searchLeavesSet
                    //for(PQNode pq : U){
                    for(PQNode pq : searchLeavesSet){
                        if(pq.id.equals(_id)){
                            leafNode = pq;
                            leafNode.labelType = PQNode.FULL; // All descendants in S
                            S.add(leafNode);
                        }
                        else {
                            if(!pq.labelType.equals(PQNode.FULL)) // leaves will never be PARTIAL
                                pq.labelType = PQNode.EMPTY; // No descendants in S
                        }
                    }

                    // Leaf node should always exist
                    if(leafNode == null){
                        System.out.println("ERROR: leafNode wasn't found.");
                        return false;
                        //leafNode = new PQNode(_id);
                        //leafNode.labelType = PQNode.FULL;
                        //leafNode.parent = T; // This may be wrong, check here first if something fails
                    }

                    // here we would lose the object because it has no association with T.
                    //S.add(new PQNode(e.source.getId() + " -> " + e.target.getId()));

                    System.out.println(S.get(S.size()-1).id);
                }
            }
            System.out.println("----------B-----------");
            PQHelpers.printPreorderIds(T);
            //System.out.print("S: ");
            //for(PQNode s : S) System.out.print(s.id + ", ");
            PQHelpers.printListIds(S, "S");

            System.out.println("Running Bubble");
            T = PQTree.bubble(T, S);

            PQHelpers.printPreorderIds(T);

            System.out.println("Running Reduce");
            T = PQTree.reduce(T, S);

            if (T == null) {
                return false;
            }

            PQHelpers.printPreorderIds(T);

            /** The set of edges whose lower numbered vertex is j */
            ArrayList<PQNode> Sp = new ArrayList<>();
            for(Edge e : source){
                //Vertex higherNumberedVertex;
                int targetId = e.target.getId();
                int sourceId = e.source.getId();
                if (Math.min(targetId, sourceId) == j) {

                    String _id = e.source.getId() + " -> " + e.target.getId();
                    PQNode leafNode = null;
                    // Check if leaf node already exists in the universal set
                    for(PQNode pq : U){
                        if(pq.id.equals(_id)){
                            leafNode = pq;
                        }
                    }
                    // If leaf node does not exist yet, make it exist
                    if(leafNode == null){
                        leafNode = new PQNode(_id);
                    }
                    Sp.add(leafNode);


                    //Sp.add(new PQNode(e.source.getLabel() + " -> " + e.target.getLabel()));
                    System.out.println(Sp.get(Sp.size()-1).id);
                }
            }
            System.out.println("----------C-----------");
            PQHelpers.printListIds(Sp, "Sp");
            PQNode root = PQTree.root(T, S);

            // if ROOT(T, S) is a Q-Node
            if (root.nodeType.equals(PQNode.QNODE)) {
                // replace the full children of ROOT(T, S) and their descendants by T(S', S')
                System.out.println("Inside: Subtree root is a Q-node");

                List<PQNode> fullChildren = root.fullChildren();

                if(fullChildren.size() == 0) {
                    break;
                }

                if(Sp.size() == 1){
                    // No need for a P-Node, so we simplify and leave it out
                    PQNode nodeSp = Sp.get(0);
                    nodeSp.parent = root;

                    if(fullChildren.size() > 0) {

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
                        root.children.add(index, nodeSp);

                        root.children.removeAll(fullChildren);

                        // Replacing fulls with nodeSp in circular link
                        PQNode rightMostFull = fullChildren.get(fullChildren.size() - 1);
                        PQNode leftMostFull = fullChildren.get(0);

                        if(leftMostFull.circularLink_prev.labelType.equals(PQNode.EMPTY)){
                            // Right link points to fulls
                            leftMostFull.circularLink_prev.circularLink_next = nodeSp;
                            rightMostFull.circularLink_next.circularLink_prev = nodeSp;
                            nodeSp.circularLink_prev = leftMostFull.circularLink_prev;
                            nodeSp.circularLink_next = rightMostFull.circularLink_next;
                        }
                        else {
                            // Left link points to fulls
                            rightMostFull.circularLink_prev.circularLink_next = nodeSp;
                            leftMostFull.circularLink_next.circularLink_prev = nodeSp;
                            nodeSp.circularLink_prev = rightMostFull.circularLink_prev;
                            nodeSp.circularLink_next = leftMostFull.circularLink_next;
                        }


                    }
                    else {
                        PQNode rightMost = root.children.get(0);
                        nodeSp.circularLink_next = rightMost.circularLink_next;
                        rightMost.circularLink_next.circularLink_prev = nodeSp;
                        rightMost.circularLink_next = nodeSp;
                        nodeSp.circularLink_prev = rightMost;
                        root.children.add(nodeSp);
                    }

                }
                else {
                    // Otherwise, create a P node

                    PQNode replacementPNode = new PQNode(PQNode.PNODE, PQNode.EMPTY);
                    replacementPNode.id = "rNode-2";
                    replacementPNode.parent = root;
                    replacementPNode.children.addAll(Sp);
                    setCircularLinks(replacementPNode.children);

                    for(PQNode n : Sp){
                        n.parent = replacementPNode;
                    }

                    PQNode rParent = root.getParent(); // Q-Nodes are never root, so a parent always exists

                    root.children.removeAll(fullChildren);
                    root.children.add(replacementPNode);

                    // Set the circular links with replacementPNode
                    PQNode start = fullChildren.get(fullChildren.size()-1).circularLink_next;
                    PQNode end = fullChildren.get(0).circularLink_prev;
                    replacementPNode.circularLink_next = start;
                    replacementPNode.circularLink_prev = end;
                    start.circularLink_prev = replacementPNode;
                    end.circularLink_next = replacementPNode;


                }

                //if(root.getChildren().size() == 1) {
                //    System.out.println("Removing redundant internal node: NOT IMPLEMENTED YET");
                //}
                if(root.getChildren().size() < 3) {
                    root.nodeType = PQNode.PNODE;
                }

            } else {
                // replace ROOT(T, S) and its descendants by T(S', S')
                System.out.println("Inside: Subtree root is a P-node");

                PQNode replacementPNode = new PQNode(PQNode.PNODE, PQNode.EMPTY);
                replacementPNode.id = "rNode";
                replacementPNode.children.addAll(Sp);
                setCircularLinks(replacementPNode.children);
                for(PQNode n : Sp){
                    n.parent = replacementPNode;
                }

                PQNode rParent = root.getParent();
                if(rParent != null) {

                    replacementPNode.parent = rParent;

                    // Circular links are set for Q-Node and P-Node children for bubbling up phase
                    replacementPNode.circularLink_next = root.circularLink_next;
                    replacementPNode.circularLink_prev = root.circularLink_prev;
                    root.circularLink_prev.circularLink_next = replacementPNode;
                    root.circularLink_next.circularLink_prev = replacementPNode;

                    if(rParent.nodeType.equals(PQNode.QNODE)){
                        // Parent is a Q-Node

                        // Places replacementPNode into the same index in the children list as the root
                        // This is important because q-nodes are directional and ordered
                        PQNode end = rParent.endmostChildren().get(1);
                        PQNode itr = rParent.endmostChildren().get(0);
                        int index = 0;
                        while(itr != root && itr != end){
                            itr = itr.circularLink_next;
                        }
                        rParent.children.remove(root);
                        rParent.children.add(index, replacementPNode);
                    }
                    else {
                        // Parent is a P-Node

                        rParent.children.remove(root);
                        rParent.children.add(replacementPNode);
                    }

                }
                else {
                    // root is the root of the whole tree, not a subtree.
                    root = replacementPNode;
                    T = root;
                }


            }

            // U := U - (S union S')
            PQHelpers.printListIds(S, "S");
            PQHelpers.printListIds(Sp, "Sp");
            PQHelpers.printListIds(U, "U before");
            U.removeAll(PQHelpers.union(S, Sp));
            PQHelpers.printListIds(U, "U after");
            PQHelpers.printPreorderIds(T);

            searchLeavesSet.addAll(Sp);

            System.out.println("COMPLETED ITERATION: " + j);
        }

        return true;
    }

}
