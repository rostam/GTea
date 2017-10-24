package graphtea.extensions.reports.planarity.planaritypq;

import java.lang.Math;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
}
