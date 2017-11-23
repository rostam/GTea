package graphtea.extensions.reports.planarity.planaritypq;

import java.lang.Math;
import java.util.*;

import graphtea.extensions.reports.planarity.planaritypq.PQNode;

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
            if (n.nodeType == PQNode.PNODE) {
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
            else if (n.nodeType == PQNode.QNODE) {
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
        System.out.println(nodeBuf);
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
        PQNode leftMost = qNode.getChildren().get(0);
        PQNode iter = leftMost;
        while(iter.circularLink_next != leftMost){
            if(iter.labelType.equals(PQNode.EMPTY)){
                emptyChildren.add(iter);
            }
            else if(iter.labelType.equals(PQNode.FULL)){
                fullChildren.add(iter);
            }
            iter = iter.circularLink_next;
        }
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

           if(curNode.nodeType.equals(PQNode.QNODE)){
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

    public static void printPreorderIds(PQNode _root){
        List<PQNode> output = preorder(_root);
        System.out.print("Preorder: ");
        if(output.size() == 0) return;

        if(_root.nodeType.equals(PQNode.QNODE)){
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
        leftNode.circularLink_prev.circularLink_next = insertionNode;
        rightNode.circularLink_next.circularLink_prev = insertionNode;
        insertionNode.circularLink_prev = leftNode.circularLink_prev;
        insertionNode.circularLink_next = rightNode.circularLink_next;
    }

    /** Places newNode into the same index in the children list as oldNode
     * This is important because q-nodes are directional and ordered*/
    public static void insertNodeIntoSameChildIndex(PQNode newNode, PQNode oldNode, PQNode parent){
        try {
            if (parent.labelType.equals(PQNode.QNODE)) {
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


}
