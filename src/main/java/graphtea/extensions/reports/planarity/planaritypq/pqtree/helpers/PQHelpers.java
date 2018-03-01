package graphtea.extensions.reports.planarity.planaritypq.pqtree.helpers;

import graphtea.extensions.reports.planarity.planaritypq.pqtree.pqnodes.PNode;
import graphtea.extensions.reports.planarity.planaritypq.pqtree.pqnodes.PQNode;
import graphtea.extensions.reports.planarity.planaritypq.pqtree.pqnodes.QNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

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
            nodes.get(i).setCircularLink_prev(nodes.get(Math.floorMod(i-1, modulo)));
            nodes.get(i).setCircularLink_next( nodes.get(Math.floorMod(i+1, modulo)));
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
                if (!nodes.get(i).getCircularLink_next().equals(nodes.get(i+1))) {
                    return false;
                }
            }
            //If last node
            else if (i == nodes.size()-1) {
                if (!nodes.get(i).getCircularLink_prev().equals(nodes.get(i-1))) {
                    return false;
                }
            }
            //General case
            else {
                if (!nodes.get(i).getCircularLink_prev().equals(nodes.get(i-1))) {
                    return false;
                }
                if (!nodes.get(i).getCircularLink_next().equals(nodes.get(i+1))) {
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
            if (n.getClass() == PNode.class) {
                if (n.getLabel() == PQNode.EMPTY) {
                    nodeBuf += "(E)";
                }
                else if (n.getLabel() == PQNode.FULL) {
                    nodeBuf += "(F)";
                }
                 else if (n.getLabel() == PQNode.PARTIAL) {
                    nodeBuf += "(P)";
                }
                else {
                    nodeBuf += "(.)";
                }
            }
            else if (n.getClass() == QNode.class) {
                if (n.getLabel() == PQNode.EMPTY) {
                    nodeBuf += "[E]";
                }
                else if (n.getLabel() == PQNode.FULL) {
                    nodeBuf += "[F]";
                }
                else if (n.getLabel() == PQNode.PARTIAL) {
                    nodeBuf += "[P]";
                }
                else {
                    nodeBuf += "[.]";
                }
            }
            else {
                if (n.getLabel() == PQNode.EMPTY) {
                    nodeBuf += " E ";
                }
                else if (n.getLabel() == PQNode.FULL) {
                    nodeBuf += " F ";
                }
                else if (n.getLabel() == PQNode.PARTIAL) {
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
        if (x.getChildren().size() != 0) {
            for (PQNode n : x.getChildren()) {
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
            if(iter.getLabel().equals(PQNode.EMPTY)){
                emptyChildren.add(iter);
            }
            else if(iter.getLabel().equals(PQNode.FULL)){
                fullChildren.add(iter);
            }
            iter = iter.getCircularLink_next();
        } while(iter != leftMost);
    }

    public static void reverseCircularLinks(PQNode node){
        if(node == null)
            return;

        PQNode iter = node;
        while(iter.getCircularLink_next() != node){
            PQNode tmp = iter.getCircularLink_next();
            iter.setCircularLink_next( iter.getCircularLink_prev());
            iter.setCircularLink_prev(tmp);
            iter = tmp;
        }
        iter.setCircularLink_next( iter.getCircularLink_prev());
        iter.setCircularLink_prev(node);

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

           if (curNode.getClass() == QNode.class) {
               PQNode itr = curNode.endmostChildren().get(0);

               if(itr != null){
                   S.add(itr);
                   output.add(itr);
                   itr = itr.getCircularLink_next();
               }
               while(itr != curNode.endmostChildren().get(0)){
                   S.add(itr);
                   output.add(itr);
                   itr = itr.getCircularLink_next();
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

        if (_root.getClass() == QNode.class) {
            PQNode itr = _root;
            while(itr.getCircularLink_next() != _root){
                if(counts) {
                    itr.setPertinentLeafCount(0);
                    itr.setPertinentChildCount(0);
                }
                if(labels) {
                    itr.setLabel(PQNode.EMPTY);
                }
                itr = itr.getCircularLink_next();
            }
        }
        else {
            for (PQNode n : output) {
                if(counts) {
                    n.setPertinentLeafCount(0);
                    n.setPertinentChildCount(0);
                }
                if(labels) {
                    n.setLabel(PQNode.EMPTY);
                }
            }
        }
    }

    public static void printPreorderIds(PQNode _root){
        List<PQNode> output = preorder(_root);
        System.out.print("Preorder: ");
        if(output.size() == 0) return;

        if (_root.getClass() == QNode.class) {
            PQNode itr = _root;
            while(itr.getCircularLink_next() != _root){
                if (itr.getId().equals("")) System.out.print("no_id, ");
                else System.out.print(itr.getId() + ", ");
                itr = itr.getCircularLink_next();
            }
        }
        else {
            for (PQNode n : output) {
                if (n.getId().equals("")) System.out.print("no_id, ");
                else System.out.print(n.getId() + ", ");
            }
        }
        System.out.println();
    }


    public static void printListIds(List<PQNode> lis, String listName){
        System.out.print(listName + ": ");
        for(PQNode n : lis){
            if(n.getId().equals("")) System.out.print("no_id, ");
            else System.out.print(n.getId() + ", ");
        }
        System.out.println();
    }

    public static void insertNodeIntoCircularList(PQNode insertionNode, PQNode leftNode, PQNode rightNode){
        insertionNode.setCircularLink_next( rightNode);
        insertionNode.setCircularLink_prev(leftNode);
        leftNode.setCircularLink_next( insertionNode);
        rightNode.setCircularLink_prev(insertionNode);
    }

    public static void reduceChildQNodeIntoParentQNode(QNode partialNode, QNode parentQNode){
        PQNode leftmostChildOfParent = parentQNode.endmostChildren().get(0);
        PQNode rightmostChildOfParent = parentQNode.endmostChildren().get(1);

        PQNode rightMostChildOfQ = partialNode.endmostChildren().get(1);
        PQNode leftMostChildOfQ = partialNode.endmostChildren().get(0);

        PQNode leftNode = partialNode.getCircularLink_prev(), rightNode = partialNode.getCircularLink_next();
        PQNode partialChildTraversal = leftMostChildOfQ;
        do {
            PQNode tmp = partialChildTraversal.getCircularLink_next();
            PQHelpers.insertNodeIntoCircularList(partialChildTraversal, leftNode, rightNode);
            leftNode = partialChildTraversal;
            partialChildTraversal = tmp;
        } while(partialChildTraversal != leftMostChildOfQ);

        if(leftmostChildOfParent == partialNode){
            parentQNode.setQNodeEndmostChildren(leftMostChildOfQ, null);
            leftMostChildOfQ.setCircularLink_prev(leftmostChildOfParent.getCircularLink_prev());
            rightMostChildOfQ.setCircularLink_next( leftmostChildOfParent);
            leftmostChildOfParent.setCircularLink_prev(rightmostChildOfParent);
        }
        else if(rightmostChildOfParent == partialNode){
            parentQNode.setQNodeEndmostChildren(null, rightMostChildOfQ);
            rightMostChildOfQ.setCircularLink_next( rightmostChildOfParent.getCircularLink_next());
            leftMostChildOfQ.setCircularLink_prev(rightmostChildOfParent);
            rightmostChildOfParent.setCircularLink_next( leftMostChildOfQ);
        }
        parentQNode.removeChildren(Arrays.asList(partialNode));
        parentQNode.setParentQNodeChildren();

    }

    public static void collectChildrenByLabel(PQNode parent, List<PQNode> emptiesList, List<PQNode> fullsList){

        if (parent.getClass() == QNode.class) {
            PQNode traversal = parent.endmostChildren().get(0);
            PQNode start = traversal;
            do {
                if (traversal.getLabel().equals(PQNode.FULL)) {
                    fullsList.add(traversal);
                } else if (traversal.getLabel().equals(PQNode.EMPTY)) {
                    emptiesList.add(traversal);
                }
                traversal = traversal.getCircularLink_next();
            } while(traversal != start);
        }
        else {
            for (PQNode child : parent.getChildren()) {
                if (child.getLabel().equals(PQNode.FULL)) {
                    fullsList.add(child);
                } else if (child.getLabel().equals(PQNode.EMPTY)) {
                    emptiesList.add(child);
                }
            }
        }
    }

    public static boolean addNodesAsChildrenToQNode(List<PQNode> newNodes, QNode parentQNode){
        if (parentQNode.getClass() != QNode.class) return false;

        PQNode left = parentQNode.endmostChildren().get(0);
        PQNode right = parentQNode.endmostChildren().get(1);

        for(PQNode n : newNodes) {

            PQNode newNode = n;
            boolean fullLeft = false;


            if (left.getLabel().equals(PQNode.FULL)) {
                fullLeft = true;
            }
            if (right.getLabel().equals(PQNode.FULL)) {
                fullLeft = false;
            }

            insertNodeIntoCircularList(newNode, right, left);
            if (fullLeft) {
                if (newNode.getLabel().equals(PQNode.FULL)) {
                    parentQNode.addChild(newNode, true);
                } else {
                    parentQNode.addChild(newNode, false);
                }
            } else {
                if (newNode.getLabel().equals(PQNode.FULL)) {
                    parentQNode.addChild(newNode, false);
                } else {
                    parentQNode.addChild(newNode, true);
                }
            }
        }

        return true;
    }

    public static void replaceParent(PQNode replacementNode, PQNode originalNode){
        PQNode parent = originalNode.getParent();
        parent.replaceChild(replacementNode, originalNode);
    }

    public static boolean rotateIfNeeded(QNode qNode){
        if(qNode.getCircularLink_next().getLabel().equals(PQNode.FULL) && qNode.getCircularLink_prev().getLabel().equals(PQNode.EMPTY)){
            if(qNode.leftmostChild.getLabel().equals(PQNode.FULL)){
                qNode.rotate();
            }
            return true;
        }
        else if(qNode.getCircularLink_prev().getLabel().equals(PQNode.FULL) && qNode.getCircularLink_next().getLabel().equals(PQNode.EMPTY)){
            if(qNode.rightmostChild.getLabel().equals(PQNode.FULL)){
                qNode.rotate();
            }
            return true;
        }
        else {
            return false;
        }
    }


}
