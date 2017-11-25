package graphtea.extensions.reports.planarity.planaritypq;

import graphtea.graph.graph.Edge;
import graphtea.graph.graph.GraphModel;

import java.util.*;

import static graphtea.extensions.reports.planarity.planaritypq.PQHelpers.*;

public class PQMethod {

    public PQMethod() {
    }

    public boolean isPlanar(GraphModel graph){

        Iterable<Edge> source = graph.getEdges();
        HashSet<PQNode> U = new HashSet<>();
        //List<PQNode> U = new ArrayList<>();

        PQNode T = new PQNode(PQNode.PNODE, PQNode.EMPTY);
        T.id = "T";
        for(Edge e : source){

            System.out.println(e.source.getId() + " -> " + e.target.getId());

            int targetId = e.target.getId();
            int sourceId = e.source.getId();
            if (Math.min(targetId, sourceId) == 0) {
                PQNode leafNode = new PQNode(e.source.getId() + " -> " + e.target.getId());
                leafNode.parent = T;
                U.add(leafNode);
            }
        }

        System.out.println("----------A-----------");
        PQ PQTree = new PQ();

        // Same as T(U, U) because all U are set to full and T (a p-node) can reach all of U in any order
        T.children.addAll(U);
        PQHelpers.setCircularLinks(T.children);

        for(int j=1; j<graph.getVertexArray().length-1; j++){
            System.out.println("ITERATION: " + j);

            // The set of edges whose higher numbered vertex is j
            // These are the edges we are to reduce
             ArrayList<PQNode> S = new ArrayList<>();
             for(Edge e : source){
                 int targetId = e.target.getId();
                 int sourceId = e.source.getId();
                 if (Math.max(targetId, sourceId) == j) {
                     String _id = e.source.getId() + " -> " + e.target.getId();
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

                    System.out.println(S.get(S.size()-1).id);
                }
            }

            System.out.println("----------B-----------");
            PQHelpers.printPreorderIds(T);
            PQHelpers.printListIds(S, "S");

            System.out.println("Running Bubble");
            if(S.size() > 0)
                T = PQTree.bubble(T, S);

            System.out.println("Running Reduce");
            if(S.size() > 0)
                T = PQTree.reduce(T, S);

            if (T == null) {
                return false;
            }

            PQHelpers.printPreorderIds(T);

            // The set of edges whose lower numbered vertex is j
            // These edges are added to Sp
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
                            break;
                        }
                    }
                    // If leaf node does not exist yet, make it exist
                    if(leafNode == null){
                        leafNode = new PQNode(_id);
                    }
                    Sp.add(leafNode);

                    System.out.println(Sp.get(Sp.size()-1).id);
                }
            }
            System.out.println("----------C-----------");
            PQHelpers.printListIds(Sp, "Sp");
            PQNode root = PQTree.root(T, S);

            if(root == null && Sp.size() > 0){
                // If S is empty

                try {
                    if (T.nodeType.equals(PQNode.PNODE)) { // T should always be a P-Node
                        T.children.addAll(Sp);
                        setCircularLinks(T.children);
                        for(PQNode n : Sp){
                            n.parent = T;
                        }
                    }
                    else throw new IllegalNodeTypeException("T is not a P-Node");
                }
                catch (IllegalNodeTypeException e) { }


            }
            // if ROOT(T, S) is a Q-Node
            else if (root.nodeType.equals(PQNode.QNODE) && Sp.size() > 0) {
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
                        PQHelpers.insertNodeIntoCircularList(nodeSp, leftMostFull, rightMostFull);
                    }
                    else {
                        // Left link points to fulls
                        PQHelpers.insertNodeIntoCircularList(nodeSp, rightMostFull, leftMostFull);
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

                    PQHelpers.insertNodeIntoCircularList(replacementPNode, fullChildren.get(0), fullChildren.get(fullChildren.size()-1));

                }
                else {
                    // Otherwise, SP.Size() == 0.

                    // ignored intentionally
                }

                // Q-Nodes are directional, but this only matters if they have 3+ children.
                if(root.getChildren().size() < 3) {
                    root.nodeType = PQNode.PNODE;
                }

            } else if(Sp.size() > 0) {
                // replace ROOT(T, S) and its descendants by T(S', S')
                System.out.println("Inside: Subtree root is a P-node");

                PQNode rParent = root.getParent();

                /*if(rParent == T){
                    System.out.println("rParent is T!");
                    T.children = Sp;
                    setCircularLinks(T.children);
                    for(PQNode n : Sp){
                        n.parent = T;
                    }
                }
                else {*/

                PQNode replacementNode;
                if(Sp.size() == 1/* && rParent != null*/){
                    replacementNode = Sp.get(0);
                }
                else {
                    replacementNode = new PQNode(PQNode.PNODE, PQNode.EMPTY);
                    replacementNode.id = "rNode";
                    replacementNode.children.addAll(Sp);
                    setCircularLinks(replacementNode.children);
                    for(PQNode n : Sp){
                        n.parent = replacementNode;
                    }
                }

                if(rParent != null) {

                    replacementNode.parent = rParent;

                    // Circular links are set for Q-Node and P-Node children for bubbling up phase
                    PQHelpers.insertNodeIntoCircularList(replacementNode, root, root);

                //public static void insertInto(PQNode newNode, PQNode oldNode, PQNode parent){

                    if(rParent.nodeType.equals(PQNode.QNODE)){
                        // Parent is a Q-Node

                        // Places replacementPNode into the same index in the children list as the root
                        // This is important because q-nodes are directional and ordered
                        PQHelpers.insertNodeIntoSameChildIndex(replacementNode, root, rParent);

                    }
                    else {
                        // Parent is a P-Node

                        rParent.children.remove(root);
                        rParent.children.add(replacementNode);
                        setCircularLinks(rParent.children);
                    }

                }
                else {
                    // root is the root of the whole tree, not a subtree.

                    /*if(replacementNode.equals(PQNode.QNODE) || replacementNode.equals(PQNode.PNODE)) {
                        PQHelpers.union(T.children, replacementNode.children);
                    }
                    else {
                        PQHelpers.union(T.children, Arrays.asList(replacementNode));
                    }*/

                    PQHelpers.printPreorderIds(T);
                    PQHelpers.printPreorderIds(replacementNode);

                    //T.children.removeAll()
                    List<PQNode> removedNodes = new ArrayList<>();
                    for(PQNode n : T.children){
                        if(n.labelType.equals(PQNode.FULL) || n.labelType.equals(PQNode.PARTIAL)){
                            //T.children.remove(n);
                            removedNodes.add(n);
                        }
                    }
                    T.children.removeAll(removedNodes);

                    if(replacementNode.children.size() == 1){
                        T.children.add(replacementNode.children.get(0));
                    }
                    else {
                        T.children.add(replacementNode);
                    }
                    setCircularLinks(T.children);

                    //root = replacementNode;
                    //T = root;
                    T.id = "rT";
                }


            }


            PQHelpers.printListIds(S, "S");
            PQHelpers.printListIds(Sp, "Sp");
            PQHelpers.printListIds(new ArrayList<>(U), "U before");

            // U := U - S union S' ->  U := (U - S) union S'
            U.removeAll(S);
            U.addAll(Sp);
            //U.addAll(0, Sp);

            PQHelpers.printListIds(new ArrayList<>(U), "U after");
            PQHelpers.printPreorderIds(T);

            System.out.println("COMPLETED ITERATION: " + j);
        }

        return true;
    }

}
