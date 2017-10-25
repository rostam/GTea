package graphtea.extensions.reports.planarity.planaritypq;

import java.util.*;
import java.util.stream.Collectors;

import static graphtea.extensions.reports.planarity.planaritypq.PQHelpers.*;

/**
 * This class contains subroutines used to construct a PQ-Tree as described
 * in the 1976 paper "Testing for Consecutive Ones Property, Interval Graphs,
 * and Graph Planarity Using PQ-Tree Algorithms".
 *
 * We highly recommend reading over the paper before reading this codebase.
 *
 * @author Alex Cregten
 * @author Hannes Kr. Hannesson
 */

public class PQ {

    /**
     * This subroutine determines which nodes of the PQ-Tree should be pruned
     * with respect to the constraint sequence S.
     *
     * @param _root root of the tree/subtree
     * @param S a set of nodes, describes a constraint sequence
     * @return PQNodes which do not violate S (nor the reverse of S) and all previously applied constraint sequences.
     */
    public PQNode bubble(PQNode _root, List<PQNode> S){
        Queue<PQNode> queue = new LinkedList<>(S);
        int blockCount = 0;
        int blockedNodes = 0;
        int offTheTop = 0;

        while(queue.size() + blockCount + offTheTop > 1){
            //System.out.println("|Queue| = " + queue.size());
            if(queue.size() == 0){
                _root = null;
                return _root;
            }

            PQNode x = queue.remove();
            x.blocked = true;
            //System.out.println("Processing: " + x.id);

            List<PQNode> BS = new ArrayList<>();
            List<PQNode> US = new ArrayList<>();
            for(PQNode u : x.immediateSiblings()){
                if(u == null){
                    continue;
                }
                if(u.blocked){
                    BS.add(u);
                }
                else {
                    US.add(u);
                }
            }
            //System.out.println("|BS| = " + BS.size());
            //System.out.println("|US| = " + US.size());
            if(US.size() > 0){
                PQNode y = US.get(0);
                x.parent = y.parent;
                x.blocked = false;
            }
            else if(x.immediateSiblings().size() < 2){
                x.blocked = false;
            }

            int listSize = 0;
            if(!x.blocked){
                PQNode y = x.parent;
                if(BS.size() > 0){
                    Set<PQNode> list = x.maximalConsecutiveSetOfSiblingsAdjacent(true);

                    listSize = list.size();
                    for(PQNode z : list){
                        z.blocked = false;
                        y.pertinentChildCount++;
                    }
                }
                if(y == null){
                    offTheTop = 1;
                }
                else {
                    y.pertinentChildCount++;
                    if(!y.queued){
                        queue.add(y);
                        y.queued = true;
                    }
                }
                blockCount = blockCount - BS.size();
                blockedNodes = blockedNodes - listSize;
            }
            else {
                blockCount = blockCount + 1 - BS.size();
                blockedNodes = blockedNodes + 1;
            }

        }

        return _root;
    }

    public PQNode reduce(PQNode T, List<PQNode> S){
        Queue<PQNode> queue = new LinkedList<>(S);
        for(PQNode x : S){
            x.pertinentLeafCount = 1;
        }
        while(queue.size() > 0){
            PQNode x = queue.remove();
            if(x.pertinentLeafCount < S.size()){
                // X is not ROOT(T, S)

                PQNode y = x.parent;
                y.pertinentLeafCount = y.pertinentLeafCount + x.pertinentLeafCount;
                y.pertinentChildCount = y.pertinentChildCount - 1;
                if(y.pertinentChildCount == 0){
                    queue.add(y);
                }
                //if(TEMPLATE_L1(x)) break;
                if(TEMPLATE_P1(x)) break;
                //if(TEMPLATE_P3(x)) break;
                //if(TEMPLATE_P5(x)) break;
                //if(TEMPLATE_Q1(x)) break;
                //if(TEMPLATE_Q2(x)) break;

                // If all templates fail, return null tree
                return null;
            }
            else {
                // X is ROOT(T, S)

                //if(TEMPLATE_L1(x)) break;
                if(TEMPLATE_P1(x)) break;
                //if(TEMPLATE_P2(x)) break;
                //if(TEMPLATE_P4(x)) break;
                //if(TEMPLATE_P6(x)) break;
                //if(TEMPLATE_Q1(x)) break;
                //if(TEMPLATE_Q2(x)) break;
                //if(TEMPLATE_Q3(x)) break;

                // If all templates fail, return null tree
                return null;
            }

        }

        return T;
    }

    public void replace(PQNode T, PQNode TPrime){

    }

    public PQNode root(PQNode T, List<PQNode> S){

        return null;
    }

    /**
    * TEMPLATES
    * */

    public boolean TEMPLATE_L1(PQNode x){ return false; }

    public boolean GENERALIZED_TEMPLATE_1(PQNode x){
        if(!x.labelType.equals(PQNode.FULL)){
            for(PQNode n : x.children){
                if(!n.labelType.equals(PQNode.FULL)){
                    return false;
                }
            }
            x.labelType = PQNode.FULL;
            return true;
        }
        return false;
    }

    /**
     * Tries to match the tree with root x to template P1, if successful,
     * we apply it.
     *
     * TEMPLATE:
     * Case #1:           |Case #2:
     *        (E)         |        (E)
     *    |        |      |    |        |
     *                    |
     *    E  ....  E      |    F  ....  F
     *                    |
     * REPLACEMENT:       |
     *        (E)         |        (F)
     *    |        |      |    |        |
     *                    |
     *    E  ....  E      |    F  ....  F
     *                    |
     * @param x the node which represents the root of the tree/subtree
     * @return whether or not x matches the template
     */
    public boolean TEMPLATE_P1(PQNode x){
       if (x.nodeType == PQNode.PNODE) {
           return GENERALIZED_TEMPLATE_1(x);
       }
       return false;
    }

    public boolean TEMPLATE_P2(PQNode x) {

        //Matching Phase

        //If not root
        if (x.parent != null) {
            return false;
        }

        List<PQNode> emptyChildren = new ArrayList<PQNode>();
        List<PQNode> fullChildren = new ArrayList<PQNode>();

        for (PQNode child : x.getChildren()) {
            if (child.labelType == PQNode.FULL) {
                fullChildren.add(child);
            }
            else if (child.labelType == PQNode.EMPTY) {
                emptyChildren.add(child);
            }
        }

        //If there are no full nodes
        if (fullChildren.size() == 0) {
            return false;
        }
        //If there are no empty nodes
        if (emptyChildren.size() == 0) {
            return false;
        }
        //If there were other nodes than full or empty
        if ( fullChildren.size() + emptyChildren.size() != x.children.size()) {
            return false;
        }


        //Replacement phase

        PQNode fullParent = new PQNode();
        fullParent.nodeType = PQNode.PNODE;
        fullParent.labelType = PQNode.FULL;
        fullParent.parent = x;

        //Adding the full children to a new P node
        fullParent.children = fullChildren;

        //Pointing the children to the new P node
        for (PQNode child : fullChildren) {
            child.parent = fullParent;
        }

        //Setting the links again, otherwise the endmost children would point to the previous siblings (the empty ones)
        setCircularLinks(fullChildren);

        return true;
    }

    /**
     * This case is very similiar to TEMPLATE_P2.
     * The matching is nearly identical, the only different being that x cannot be a root.
     */
    public boolean TEMPLATE_P3(PQNode x){

        //Matching Phase

        //If root
        if (x.parent == null) {
            return false;
        }

        List<PQNode> emptyChildren = new ArrayList<PQNode>();
        List<PQNode> fullChildren = new ArrayList<PQNode>();

        for (PQNode child : x.getChildren()) {
            if (child.labelType == PQNode.FULL) {
                fullChildren.add(child);
            }
            else if (child.labelType == PQNode.EMPTY) {
                emptyChildren.add(child);
            }
        }

        //If there are no full nodes
        if (fullChildren.size() == 0) {
            return false;
        }
        //If there are no empty nodes
        if (emptyChildren.size() == 0) {
            return false;
        }
        //If there were other nodes than full or empty
        if ( fullChildren.size() + emptyChildren.size() != x.children.size()) {
            return false;
        }

        //Replacement phase

        x.labelType = PQNode.PARTIAL;
        x.nodeType = PQNode.QNODE;
        x.children = new ArrayList<PQNode>();

        PQNode emptyPNode = new PQNode();
        PQNode fullPNode = new PQNode();

        x.children.add(emptyPNode);
        x.children.add(fullPNode);

        emptyPNode.nodeType = PQNode.PNODE;
        fullPNode.nodeType = PQNode.PNODE;
        emptyPNode.labelType = PQNode.EMPTY;
        fullPNode.labelType = PQNode.FULL;

        emptyPNode.parent = x;
        fullPNode.parent = x;

        //Adding the children to the appropriate P node
        emptyPNode.children = emptyChildren;
        fullPNode.children = fullChildren;


        //Pointing the children to the appropriate P node
        for (PQNode child : emptyChildren) {
            child.parent = emptyPNode;
        }
        for (PQNode child : fullChildren) {
            child.parent = fullPNode;
        }

        //Setting the links again, otherwise the endmost children would point to the previous siblings (the empty ones)
        setCircularLinks(emptyChildren);
        setCircularLinks(fullChildren);

        return true;
    }

    public boolean TEMPLATE_P4(PQNode x){

        //Matching phase

        //Check if node is not root
        if (x.parent != null) {
            return false;
        }

        List<PQNode> emptyChildren = x.getChildrenOfLabel(PQNode.EMPTY);
        List<PQNode> fullChildren = x.getChildrenOfLabel(PQNode.FULL);
        List<PQNode> partialChildren = x.getChildrenOfLabel(PQNode.PARTIAL);


        //If there are no full nodes
        if (fullChildren.size() == 0) {
            return false;
        }
        //If there are no empty nodes
        if (emptyChildren.size() == 0) {
            return false;
        }
        //If there is not exactly 1 partial child
        if (partialChildren.size() != 1) {
            return false;
        }

        //If there were other nodes than full, empty or partial
        if ( fullChildren.size() + emptyChildren.size() + partialChildren.size() != x.children.size()) {
            return false;
        }

        //If partial node is not a Q node
        if (partialChildren.get(0).nodeType != PQNode.QNODE) {
            return false;
        }

        //Replacement phase

        PQNode pNodeParent = new PQNode();
        pNodeParent.nodeType = PQNode.PNODE;
        pNodeParent.labelType = PQNode.FULL;
        pNodeParent.children = fullChildren;
        pNodeParent.parent = partialChildren.get(0);

        partialChildren.get(0).children.add(pNodeParent);

        for (PQNode n : fullChildren) {
            n.parent = pNodeParent;
        }


        //Setting the circular links, otherwise the endmost children would still point to their former neighbours
        setCircularLinks(partialChildren.get(0).children);
        setCircularLinks(pNodeParent.children);
        setCircularLinks(x.children);

        return true;
    }

    public boolean TEMPLATE_P5(PQNode x){

        // Check if x is _root of whole tree
        /*if(x.parent == null){
            return false;
        }*/

        if(!x.nodeType.equals(PQNode.PNODE)){
            return false;
        }

        if(x.children.size() < 1){
            return false;
        }

        PQNode qNode = null;
        int qNodeChildCount = 0;
        List<PQNode> emptyChildList = new ArrayList<>();
        List<PQNode> fullChildList = new ArrayList<>();
        for(PQNode n : x.children){
            if(n.nodeType.equals(PQNode.QNODE)){
                qNodeChildCount++;
                qNode = n;
            }
            else if(n.labelType.equals(PQNode.EMPTY)){
                emptyChildList.add(n);
            }
            else if(n.labelType.equals(PQNode.FULL)){
                fullChildList.add(n);
            }

        }

        if(emptyChildList.size() == 0 || fullChildList.size() == 0) {
            return false;
        }

        if(qNodeChildCount > 1 || qNode == null){
            return false;
        }

        if(!qNode.labelType.equals(PQNode.PARTIAL)){
            return false;
        }

        PQNode leftmostChild = qNode.endmostChildren().get(0);
        PQNode rightmostChild = qNode.endmostChildren().get(1);

        PQNode newLeftmostPNode = new PQNode();
        PQNode newRightmostPNode = new PQNode();

        newLeftmostPNode.labelType = PQNode.EMPTY;
        newLeftmostPNode.nodeType = PQNode.PNODE;
        newRightmostPNode.labelType = PQNode.FULL;
        newRightmostPNode.nodeType = PQNode.PNODE;

        leftmostChild.circularLink_prev = newLeftmostPNode;
        rightmostChild.circularLink_next = newRightmostPNode;

        newLeftmostPNode.parent = qNode;
        newLeftmostPNode.circularLink_next = leftmostChild;
        newLeftmostPNode.circularLink_prev = newRightmostPNode;
        leftmostChild.circularLink_prev = newLeftmostPNode;

        newRightmostPNode.parent = qNode;
        newRightmostPNode.circularLink_prev = rightmostChild;
        newRightmostPNode.circularLink_next = newLeftmostPNode;
        rightmostChild.circularLink_next = newRightmostPNode;

        leftmostChild.parent = null;
        rightmostChild.parent = null;

        if(emptyChildList.size() > 0) {
            //emptyChildList.get(0).circularLink_prev = emptyChildList.get(emptyChildList.size() - 1);
            //emptyChildList.get(emptyChildList.size() - 1).circularLink_next = emptyChildList.get(0);
            for (PQNode e : emptyChildList) {
                e.parent = newLeftmostPNode;
                newLeftmostPNode.children.add(e);
                x.children.remove(e);
            }
            qNode.children.remove(0);
            qNode.children.add(0, newLeftmostPNode);
        }

        if(fullChildList.size() > 0) {
            //fullChildList.get(0).circularLink_prev = fullChildList.get(fullChildList.size() - 1);
            //fullChildList.get(fullChildList.size() - 1).circularLink_next = fullChildList.get(0);
            for (PQNode f : fullChildList) {
                f.parent = newRightmostPNode;
                newRightmostPNode.children.add(f);
                x.children.remove(f);
            }
            qNode.children.remove(qNode.children.size()-1);
            qNode.children.add(qNode.children.size()-1, newRightmostPNode);
        }

        newLeftmostPNode.parent = x;
        newRightmostPNode.parent = x;
        x.labelType = qNode.labelType;
        x.nodeType = qNode.nodeType;
        x.children = qNode.children;

        qNode = null;

        return true;
    }

    public boolean TEMPLATE_P6(PQNode x){

        return false;
    }

    /**
     * Tries to match the tree with root x to template Q1, if successful,
     * we apply it.
     *
     * TEMPLATE:
     * Case #1:           |Case #2:
     *        [E]         |        [E]
     *    |        |      |    |        |
     *                    |
     *    E  ....  E      |    F  ....  F
     *                    |
     * REPLACEMENT:       |
     *        [E]         |        [F]
     *    |        |      |    |        |
     *                    |
     *    E  ....  E      |    F  ....  F
     *                    |
     * @param x the node which represents the root of the tree/subtree
     * @return whether or not x matches the template
     */
    public boolean TEMPLATE_Q1(PQNode x){
       if (x.nodeType == PQNode.QNODE) {
           return GENERALIZED_TEMPLATE_1(x);
       }
       return false;
    }

    public boolean TEMPLATE_Q2(PQNode x){

        //Matching

        //If x is not a qNode
        if (x.nodeType != PQNode.QNODE) {
            return false;
        }

        //If empty children are not consecutive
        if (!checkIfConsecutive(x.getChildrenOfLabel(PQNode.EMPTY))) {
            return false;
        }

        //If full children are not consecutive
        if (!checkIfConsecutive(x.getChildrenOfLabel(PQNode.FULL))) {
            return false;
        }

        //Check if x is not singly partial
        if (x.getChildrenOfLabel(PQNode.PARTIAL).size() != 1) {
            return false;
        }

        //Check if partial node is not a qnode
        if (x.getChildrenOfLabel(PQNode.PARTIAL).get(0).nodeType != PQNode.QNODE) {
            return false;
        }

        //Check if the partial node's full children are not consecutive
        if (!PQHelpers.checkIfConsecutive(x.getChildrenOfLabel(PQNode.PARTIAL).get(0).getChildrenOfLabel(PQNode.FULL))) {
            return false;
        }

        //Check if the partial node's empty children are not consecutive
        if (!PQHelpers.checkIfConsecutive(x.getChildrenOfLabel(PQNode.PARTIAL).get(0).getChildrenOfLabel(PQNode.EMPTY))) {
            return false;
        }




        //Replacement

        PQNode partialNode = x.getChildrenOfLabel(PQNode.PARTIAL).get(0);


        x.labelType = PQNode.PARTIAL;

        //Move all children of the partial node to the root
        List<PQNode> replacementChildren = new ArrayList<PQNode>();
        replacementChildren.addAll(x.getChildrenOfLabel(PQNode.EMPTY));
        replacementChildren.addAll(partialNode.getChildrenOfLabel(PQNode.EMPTY));
        replacementChildren.addAll(partialNode.getChildrenOfLabel(PQNode.FULL));
        replacementChildren.addAll(x.getChildrenOfLabel(PQNode.FULL));

        //Delete partial child
        partialNode = null;

        //Reset circular links
        setCircularLinks(replacementChildren);

        x.children = replacementChildren;

        //Set parent links
        for (PQNode n : x.endmostChildren()) {
            n.parent = x;
        }

        for (PQNode n : x.internalChildren()) {
            n.parent = null;
        }

        return true;
    }

    public boolean TEMPLATE_Q3(PQNode x){

        //Matching phase

        //Check if not qnode
        if (x.nodeType != PQNode.QNODE) {
            return false;
        }

        //Check if x is not doubly partial
        if (x.getChildrenOfLabel(PQNode.PARTIAL).size() != 2) {
            return false;
        }

        //Check if partial nodes are not qnodes
        if (x.getChildrenOfLabel(PQNode.PARTIAL).get(0).nodeType != PQNode.QNODE) {
            return false;
        }
        if (x.getChildrenOfLabel(PQNode.PARTIAL).get(1).nodeType != PQNode.QNODE) {
            return false;
        }

        //Check if the partial node's full children are not consecutive
        if (!PQHelpers.checkIfConsecutive(x.getChildrenOfLabel(PQNode.PARTIAL).get(0).getChildrenOfLabel(PQNode.FULL))) {
            return false;
        }
        if (!PQHelpers.checkIfConsecutive(x.getChildrenOfLabel(PQNode.PARTIAL).get(1).getChildrenOfLabel(PQNode.FULL))) {
            return false;
        }

        //Check if the partial node's empty children are not consecutive
        if (!PQHelpers.checkIfConsecutive(x.getChildrenOfLabel(PQNode.PARTIAL).get(0).getChildrenOfLabel(PQNode.EMPTY))) {
            return false;
        }
        if (!PQHelpers.checkIfConsecutive(x.getChildrenOfLabel(PQNode.PARTIAL).get(1).getChildrenOfLabel(PQNode.EMPTY))) {
            return false;
        }

        List<PQNode> leftEmpties = new ArrayList<PQNode>();
        List<PQNode> rightEmpties = new ArrayList<PQNode>();

        int flips = 0;
        int cntr = 0;
        for (PQNode n : x.getChildren()) {
            cntr++;
            if (flips == 0) {
                if (n.labelType == PQNode.PARTIAL) {
                    flips++;
                }
                else if (n.labelType != PQNode.EMPTY) {
                    return false;
                }
                //If empty
                else {
                   leftEmpties.add(n);
                }
            }
            else if (flips == 1) {
                if (n.labelType == PQNode.FULL) {
                    flips++;
                }
                else if (n.labelType != PQNode.PARTIAL) {
                    return false;
                }
            }
            else if (flips == 2) {
                if (n.labelType == PQNode.PARTIAL) {
                    flips++;
                }
                else if (n.labelType != PQNode.FULL) {
                    return false;
                }
            }
            else if (flips == 3) {
                if (n.labelType == PQNode.EMPTY) {
                    flips++;
                    rightEmpties.add(n);
                }
                else if (n.labelType != PQNode.PARTIAL) {
                    return false;
                }
            }
            else {
                if (n.labelType != PQNode.EMPTY) {
                    return false;
                }
                //If empty
                else {
                    rightEmpties.add(n);
                }
            }
        }

        //Replacement phase

        x.labelType = PQNode.PARTIAL;
        List<PQNode> partials = x.getChildrenOfLabel(PQNode.PARTIAL);

        List<PQNode> replacementChildren = new ArrayList<PQNode>();
        replacementChildren.addAll(leftEmpties);
        replacementChildren.addAll(partials.get(0).getChildren());
        replacementChildren.addAll(x.getChildrenOfLabel(PQNode.FULL));
        replacementChildren.addAll(partials.get(1).getChildren());
        replacementChildren.addAll(rightEmpties);

        leftEmpties.get(0).parent = x;
        rightEmpties.get(rightEmpties.size()-1).parent = x;
        x.children = replacementChildren;
        setCircularLinks(replacementChildren);

        return true;
    }

}
















