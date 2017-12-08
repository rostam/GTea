package graphtea.extensions.reports.planarity.planaritypq;

import java.lang.Math;
import java.util.*;

import graphtea.extensions.reports.planarity.planaritypq.PQNode;

/**
 * This class holds various functions that are applied in the PQTree data-structure
 * in regards to traversal and updating the tree
 *
 * @author Alex Cregten
 * @author Hannes Kr. Hannesson
 * */

public class PQHelpers {
    public static void setCircularLinks(List<PQNode> nodes) {
        int modulo = nodes.size();
        for (int i = 0; i < nodes.size(); i++) {
            nodes.get(i).circularLink_prev = nodes.get(Math.floorMod(i-1, modulo));
            nodes.get(i).circularLink_next = nodes.get(Math.floorMod(i+1, modulo));
        }
    }


    /**
     * Checks whether the given nodes are consecutive, i.e. do they form
     * a linked list (not necessarily circular).
     * @return whether the nodes are consecutive or not
     */
    public static boolean checkIfConsecutive(List<PQNode> nodes) {
        if (nodes.size() == 1) {
            return true;
        }
        for (int i = 0; i < nodes.size(); i++) {
            //If first node
            if (i == 0) {
                if (!nodes.get(i).circularLink_next.equals(nodes.get(i+1))) {
                    return false;
                }
            }
            //If last node
            else if (i == nodes.size()-1) {
                if (!nodes.get(i).circularLink_prev.equals(nodes.get(i-1))) {
                    return false;
                }
            }
            //General case
            else {
                if (!nodes.get(i).circularLink_prev.equals(nodes.get(i-1))) {
                    return false;
                }
                if (!nodes.get(i).circularLink_next.equals(nodes.get(i+1))) {
                    return false;
                }
            }
        }
        return true;
    }

    public static <E> boolean subset(List<E> list1, List<E> list2) {
        return list2.containsAll(list1);
    }

    public static <E> List<E> intersection(List<E> list1, List<E> list2){
        list1.retainAll(list2);
        return list1;
    }


    public static <E> List<E> union(List<E> list1, List<E> list2) {
        HashSet<E> set = new HashSet<>();

        // set does not insert duplicates
        set.addAll(list1);
        set.addAll(list2);

        return new ArrayList<>(set);
    }

    public static void printChildren(PQNode x) {
        String nodeBuf = "";
        for (PQNode n : x.getChildren()) {
            //if (n.nodeType == PQNode.PNODE) {
            if (n.getClass() == PNode.class) {
                if (n.labelType == PQNode.EMPTY) {
                    nodeBuf += "(E)";
                }
                else if (n.labelType == PQNode.FULL) {
                    nodeBuf += "(F)";
                }
                 else if (n.labelType == PQNode.PARTIAL) {
                    nodeBuf += "(P)";
                }
                else {
                    nodeBuf += "(.)";
                }
            }
            //else if (n.nodeType == PQNode.QNODE) {
            else if (n.getClass() == QNode.class) {
                if (n.labelType == PQNode.EMPTY) {
                    nodeBuf += "[E]";
                }
                else if (n.labelType == PQNode.FULL) {
                    nodeBuf += "[F]";
                }
                else if (n.labelType == PQNode.PARTIAL) {
                    nodeBuf += "[P]";
                }
                else {
                    nodeBuf += "[.]";
                }
            }
            else {
                if (n.labelType == PQNode.EMPTY) {
                    nodeBuf += " E ";
                }
                else if (n.labelType == PQNode.FULL) {
                    nodeBuf += " F ";
                }
                else if (n.labelType == PQNode.PARTIAL) {
                    nodeBuf += " P ";
                }
                else {
                    nodeBuf += " . ";
                }
            }
        }
    }

    public static List<PQNode> frontier(PQNode x) {
        List<PQNode> frontier = new ArrayList<PQNode>();
        frontierHelper(x, frontier);
        return frontier;
    }

    public static void frontierHelper(PQNode x, List<PQNode> frontier) {
        if (x.children.size() != 0) {
            for (PQNode n : x.children) {
                frontierHelper(n, frontier);
            }
        }
        else {
            frontier.add(x);
        }
    }

    public static void gatherQNodeChildren(List<PQNode> emptyChildren, List<PQNode> fullChildren, PQNode qNode){
        if(qNode.getChildren().size() == 0){
            return;
        }

        PQNode leftMost = qNode.getChildren().get(0);
        PQNode iter = leftMost;

        do{
            if(iter.labelType.equals(PQNode.EMPTY)){
                emptyChildren.add(iter);
            }
            else if(iter.labelType.equals(PQNode.FULL)){
                fullChildren.add(iter);
            }
            iter = iter.circularLink_next;
        } while(iter != leftMost);
    }

    static void reverseCircularLinks(PQNode node){
        if(node == null)
            return;

        PQNode iter = node;
        while(iter.circularLink_next != node){
            PQNode tmp = iter.circularLink_next;
            iter.circularLink_next = iter.circularLink_prev;
            iter.circularLink_prev = tmp;
            iter = tmp;
        }
        iter.circularLink_next = iter.circularLink_prev;
        iter.circularLink_prev = node;

    }

    public static void rotateQNode(PQNode qNode){
        reverseCircularLinks(qNode.endmostChildren().get(0));
        Collections.reverse(qNode.children);
    }

    public static boolean equalTrees(PQNode a, PQNode b) {
        return preorder(a).equals(preorder(b));
    }

    public static List<PQNode> preorder(PQNode _root) {
       List<PQNode> S = new ArrayList<PQNode>();
       List<PQNode> output = new ArrayList<PQNode>();
       S.add(_root);
       output.add(_root);
       while (S.isEmpty() == false) {
           PQNode curNode = S.get(0);
           S.remove(0);

           //if(curNode.nodeType.equals(PQNode.QNODE)){
           if (curNode.getClass() == QNode.class) {
               PQNode itr = curNode.endmostChildren().get(0);

               if(itr != null){
                   S.add(itr);
                   output.add(itr);
                   itr = itr.circularLink_next;
               }
               while(itr != curNode.endmostChildren().get(0)){
                   S.add(itr);
                   output.add(itr);
                   itr = itr.circularLink_next;
               }
           }
           else {
               for (PQNode child : curNode.getChildren()) {
                   S.add(child);
                   output.add(child);
               }
           }
       }
       return output;
    }

    public static void reset(PQNode _root, boolean counts, boolean labels){
        List<PQNode> output = preorder(_root);
        if(output.size() == 0) return;

        //if(_root.nodeType.equals(PQNode.QNODE)){
        if (_root.getClass() == QNode.class) {
            PQNode itr = _root;
            while(itr.circularLink_next != _root){
                if(counts) {
                    itr.pertinentLeafCount = 0;
                    itr.pertinentChildCount = 0;
                }
                if(labels) {
                    itr.labelType = PQNode.EMPTY;
                }
                itr = itr.circularLink_next;
            }
        }
        else {
            for (PQNode n : output) {
                if(counts) {
                    n.pertinentLeafCount = 0;
                    n.pertinentChildCount = 0;
                }
                if(labels) {
                    n.labelType = PQNode.EMPTY;
                }
            }
        }
    }

    public static void printPreorderIds(PQNode _root){
        List<PQNode> output = preorder(_root);
        System.out.print("Preorder: ");
        if(output.size() == 0) return;

        //if(_root.nodeType.equals(PQNode.QNODE)){
        if (_root.getClass() == QNode.class) {
            PQNode itr = _root;
            while(itr.circularLink_next != _root){
                if (itr.id.equals("")) System.out.print("no_id, ");
                else System.out.print(itr.id + ", ");
                itr = itr.circularLink_next;
            }
        }
        else {
            for (PQNode n : output) {
                if (n.id.equals("")) System.out.print("no_id, ");
                else System.out.print(n.id + ", ");
            }
        }
        System.out.println();
    }


    public static void printListIds(List<PQNode> lis, String listName){
        System.out.print(listName + ": ");
        for(PQNode n : lis){
            if(n.id.equals("")) System.out.print("no_id, ");
            else System.out.print(n.id + ", ");
        }
        System.out.println();
    }

    public static void insertNodeIntoCircularList(PQNode insertionNode, PQNode leftNode, PQNode rightNode){
        insertionNode.circularLink_next = rightNode;
        insertionNode.circularLink_prev = leftNode;
        leftNode.circularLink_next = insertionNode;
        rightNode.circularLink_prev = insertionNode;
    }

    public static void reduceChildQNodeIntoParentQNode(PQNode partialNode, PQNode parentQNode){
        PQNode leftmostChildOfParent = parentQNode.endmostChildren().get(0);
        PQNode rightmostChildOfParent = parentQNode.endmostChildren().get(1);

        PQNode rightMostChildOfQ = partialNode.endmostChildren().get(1);
        PQNode leftMostChildOfQ = partialNode.endmostChildren().get(0);

        PQNode leftNode = partialNode.circularLink_prev, rightNode = partialNode.circularLink_next;
        PQNode partialChildTraversal = leftMostChildOfQ;
        do {
            PQNode tmp = partialChildTraversal.circularLink_next;
            PQHelpers.insertNodeIntoCircularList(partialChildTraversal, leftNode, rightNode);
            leftNode = partialChildTraversal;
            partialChildTraversal = tmp;
        } while(partialChildTraversal != leftMostChildOfQ);

        if(leftmostChildOfParent == partialNode){
            parentQNode.children.remove(leftmostChildOfParent);
            parentQNode.children.add(1, leftMostChildOfQ);
        }
        else if(rightmostChildOfParent == partialNode){
            parentQNode.children.remove(rightmostChildOfParent);
            parentQNode.children.add(rightMostChildOfQ);
        }
        parentQNode.children.remove(partialNode);

        for (PQNode n : parentQNode.endmostChildren()) {
            n.parent = parentQNode;
        }

        List<PQNode> removalNodes = new ArrayList<>();
        for (PQNode n : parentQNode.internalChildren()) {
            n.parent = null;
            removalNodes.add(n);
        }
        parentQNode.children.removeAll(removalNodes);

    }

    /** Places newNode into the same index in the children list as oldNode
     * This is important because q-nodes are directional and ordered*/
    public static void insertNodeIntoSameChildIndex(PQNode newNode, PQNode oldNode, PQNode parent){
        try {
            //if (parent.nodeType.equals(PQNode.QNODE)) {
            if (parent.getClass() == QNode.class) {
                PQNode end = parent.endmostChildren().get(1);
                PQNode itr = parent.endmostChildren().get(0);
                int index = 0;
                while (itr != oldNode && itr != end) {
                    itr = itr.circularLink_next;
                }
                parent.children.remove(oldNode);
                parent.children.add(index, newNode);

            } else throw new IllegalNodeTypeException("Parent must be a Q-Node");

        } catch (IllegalNodeTypeException e) { }

    }

    public static void insertChildrenIntoRespectiveLists(PQNode x, List<PQNode> children) {
        for (int i = 0; i < children.size(); i++) {
            PQNode cur = children.get(i);
            if (cur.labelType.equals(PQNode.EMPTY)) {
                x.emptyChildren.add(cur);
            }
            else if (cur.labelType.equals(PQNode.FULL)) {
                x.fullChildren.add(cur);
            }
            else {
                x.partialChildren.add(cur);
            }
        }
    }

    public static void insertChildIntoRespectiveLists(PQNode x, PQNode child) {
        PQNode cur = child;
        if (cur.labelType.equals(PQNode.EMPTY)) {
            x.emptyChildren.add(cur);
        }
        else if (cur.labelType.equals(PQNode.FULL)) {
            x.fullChildren.add(cur);
        }
        else if (cur.labelType.equals(PQNode.PARTIAL)){
            x.partialChildren.add(cur);
        }
        else {
            System.out.println("label not set on child!");
        }
    }

    public static void removeChildrenFromRespectiveLists(PQNode x, List<PQNode> children) {
        for (int i = 0; i < children.size(); i++) {
            PQNode cur = children.get(i);
            if (cur.labelType.equals(PQNode.EMPTY)) {
                x.emptyChildren.remove(cur);
            }
            else if (cur.labelType.equals(PQNode.FULL)) {
                x.fullChildren.remove(cur);
            }
            else if (cur.labelType.equals(PQNode.PARTIAL)) {
                x.partialChildren.remove(cur);
            }
            else {
                System.out.println("label not set on child!");
            }
        }
    }

    public static void removeChildrenFromRespectiveLists(PQNode x, PQNode child) {
        PQNode cur = child;
        if (cur.labelType.equals(PQNode.EMPTY)) {
            x.emptyChildren.remove(cur);
        }
        else if (cur.labelType.equals(PQNode.FULL)) {
            x.fullChildren.remove(cur);
        }
        else if (cur.labelType.equals(PQNode.PARTIAL)) {
            x.partialChildren.remove(cur);
        }
        else {
            System.out.println("label not set on child!");
        }
    }
    /*public static void collectChildrenByLabel(PQNode parent, List<PQNode> emptiesList, List<PQNode> fullsList){

        //if(parent.nodeType.equals(PQNode.QNODE)){
        if (parent.getClass() == QNode.class) {
            PQNode traversal = parent.endmostChildren().get(0);
            PQNode start = traversal;
            do {
                if (traversal.labelType.equals(PQNode.FULL)) {
                    fullsList.add(traversal);
                } else if (traversal.labelType.equals(PQNode.EMPTY)) {
                    emptiesList.add(traversal);
                }
                traversal = traversal.circularLink_next;
            } while(traversal != start);
        }
        else {
            for (PQNode child : parent.getChildren()) {
                if (child.labelType.equals(PQNode.FULL)) {
                    fullsList.add(child);
                } else if (child.labelType.equals(PQNode.EMPTY)) {
                    emptiesList.add(child);
                }
            }
        }
    }*/

    public static void collectChildrenByLabel(PQNode parent, List<PQNode> emptiesList, List<PQNode> fullsList){
        emptiesList = parent.emptyChildren;
        fullsList = parent.fullChildren;
    }

    public static boolean addNodesAsChildrenToQNode(List<PQNode> newNodes, PQNode parentQNode){
        //if(!parentQNode.nodeType.equals(PQNode.QNODE)) return false;
        if (parentQNode.getClass() != QNode.class) return false;

        PQNode left = parentQNode.endmostChildren().get(0);
        PQNode right = parentQNode.endmostChildren().get(1);

        //PQNode newLeft = left;
        //PQNode newRight = right;

        for(PQNode n : newNodes) {

            PQNode newNode = n;
            boolean fullLeft = false;


            if (left.labelType.equals(PQNode.FULL)) {
                fullLeft = true;
            }
            if (right.labelType.equals(PQNode.FULL)) {
                fullLeft = false;
            }

            if (fullLeft) {
                if (newNode.labelType.equals(PQNode.FULL)) {
                    parentQNode.children.add(0, newNode);
                    insertNodeIntoCircularList(newNode, left, right);

                    //newLeft.parent = null;
                    //newLeft = newNode;
                } else {
                    parentQNode.children.add(newNode);
                    insertNodeIntoCircularList(newNode, right, left);

                    //newRight.parent = null;
                    //newRight = newNode;
                }
            } else {
                if (newNode.labelType.equals(PQNode.FULL)) {
                    parentQNode.children.add(newNode);
                    insertNodeIntoCircularList(newNode, left, right);

                    //newRight.parent = null;
                    //newRight = newNode;

                } else {
                    parentQNode.children.add(0, newNode);
                    insertNodeIntoCircularList(newNode, right, left);

                    //newLeft.parent = null;
                    //newLeft = newNode;
                }
            }

            newNode.parent = parentQNode;
        }

        //newLeft.parent = parentQNode;
        //newRight.parent = parentQNode;
        //parentQNode.setQNodeEndmostChildren(newLeft, newRight);

        return true;
    }

}
