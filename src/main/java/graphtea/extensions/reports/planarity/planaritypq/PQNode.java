package graphtea.extensions.reports.planarity.planaritypq;

import java.util.*;

import graphtea.extensions.reports.planarity.planaritypq.IllegalNodeTypeException;
import sun.awt.image.ImageWatched;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * This class holds the required functions and variables for a PQNode which are used
 * in the PQTree data-structure.
 *
 * There are three types of nodes that can be modelled: P-Nodes, Q-Nodes, and leaf nodes.
 *
 * @author Alex Cregten
 * @author Hannes Kr. Hannesson
 * */

public class PQNode {
    String id = "";
    static String PNODE = "p-node";
    static String QNODE = "q-node";
    static String PSEUDO_NODE = "pseudonode";
    String nodeType = ""; // PNODE, QNODE, or PSEUDO_NODE

    static String PARTIAL = "partial";
    static String FULL = "full";
    static String EMPTY = "empty";
    String labelType = ""; // PARTIAL, FULL, or EMPTY

    PQNode circularLink_next;
    PQNode circularLink_prev;

    int pertinentChildCount;

    int pertinentLeafCount;

    boolean queued;

    boolean blocked;

    int childCount;

    PQNode parent;
    PQNode leftMostSibling = null;
    PQNode rightMostSibling = null;
    List<PQNode> children = new ArrayList<>();
    List<PQNode> fullChildren = new ArrayList<>();
    List<PQNode> partialChildren = new ArrayList<>();
    List<PQNode> emptyChildren = new ArrayList<>();
    List<PQNode> pNodeChildren = new ArrayList<>();
    List<PQNode> qNodeChildren = new ArrayList<>();

    public PQNode(){
        blocked = false;
        //marked = false;
        pertinentChildCount = 0;
        pertinentLeafCount = 0;
        queued = false;
        circularLink_next = null;
        circularLink_prev = null;
        childCount = 0;
        parent  = null;
    }

    public PQNode(String nodeType, String labelType){
        blocked = false;
        //marked = false;
        pertinentChildCount = 0;
        pertinentLeafCount = 0;
        queued = false;
        circularLink_next = null;
        circularLink_prev = null;
        childCount = 0;
        parent  = null;

        this.labelType = labelType;
        this.nodeType = nodeType;
    }

    public PQNode(String id){
        blocked = false;
        //marked = false;
        pertinentChildCount = 0;
        pertinentLeafCount = 0;
        queued = false;
        circularLink_next = null;
        circularLink_prev = null;
        childCount = 0;
        parent  = null;
        this.id = id;
    }
    public boolean isPertinent() {
        return (this.labelType == PARTIAL || this.labelType == FULL);
    }

    public boolean nodeType(Class checkType){
        return nodeType.getClass() == checkType;
    }

    public PQNode emptySibling(){
        PQNode currNode = this.circularLink_next;
        while (true) {
            if (currNode.labelType == EMPTY && currNode != this) {
                return currNode;
            }
            else if (currNode == this) {
                return null;
            }
            currNode = currNode.circularLink_next;
        }
    }

    public void removeFromCircularLink(){
        this.circularLink_next.circularLink_prev = this.circularLink_prev;
        this.circularLink_prev.circularLink_next = this.circularLink_next;
        this.circularLink_next = null;
        this.circularLink_prev = null;
    }

    public PQNode getImmediateSiblingOfNodeType(Class nodeType){
        //if(this.circularLink_next.nodeType.equals(nodeType)){
        if(this.circularLink_next.getClass() == nodeType){
            return this.circularLink_next;
        }
        //else if(this.circularLink_prev.nodeType.equals(nodeType)){
        else if(this.circularLink_prev.getClass() == nodeType){
            return this.circularLink_prev;
        }
        return null;
    }

    /**
     *
     * Returns:
     * 11110[1111]
     *     ^
     * [11111111]00
     *      ^ (any of the 1's)
     * 00[111111111]
     *       ^ (any of the 1's)
     * 1100[111]000011
     *       ^ ()any of the three 1's)
     * 00001110000
     *  ^  (any of the four 0's)
     * 1110[11111]
     *    ^ (Max{left, right})
     * etc
     * */
    public Set<PQNode> maximalConsecutiveSetOfSiblingsAdjacent(boolean blocked){
        Set<PQNode> left = null;
        Set<PQNode> right = null;
        if(this.circularLink_prev != null) {
            left = this.circularLink_prev.siblingsAdjacent(blocked, true);
        }
        if(this.circularLink_next != null) {
            right = this.circularLink_next.siblingsAdjacent(blocked, false);
        }

        if(left == null && right != null){
            if(this.blocked == blocked) {
                right.add(this);
                return right;
            }
            return right;
        }
        else if(right == null && left != null){
            if(this.blocked == blocked){
                left.add(this);
                return left;
            }
            return left;
        }
        else if(left == null){
            return null;
        }

        if(this.blocked == blocked){
            left.add(this);
            right.add(this);
        }

        if(this.blocked == blocked) {
            left.addAll(right);
            return left;
        }
        else if(left.size() > right.size()){
            return left;
        }
        else {
            return right;
        }
    }

    private Set<PQNode> siblingsAdjacent(boolean blocked, boolean goLeft){

        if(this.blocked == blocked) {
            if(goLeft && this.circularLink_prev != null){
                try {
                    Set<PQNode> list = new HashSet<PQNode>(this.circularLink_prev.siblingsAdjacent(blocked, goLeft));
                    list.add(this);
                    return list;
                }
                catch (NullPointerException e){
                    return Collections.singleton(this);
                }
            }
            else if(this.circularLink_next != null){
                try {
                    Set<PQNode> list = new HashSet<>(this.circularLink_next.siblingsAdjacent(blocked, goLeft));
                    list.add(this);
                    return list;
                }
                catch (NullPointerException e){
                    return Collections.singleton(this);
                }
            }
            else {
                return Collections.singleton(this);
            }
        }
        else {
            return null;
        }
    }

    public PQNode getParent(){
        if(this.parent == null){
            if(leftMostSibling != null)
                return leftMostSibling.parent;
            if(rightMostSibling != null)
                return rightMostSibling.parent;
        }
        else {
            return parent;
        }
        return null;
    }


    public void setEndmostSiblings(PQNode left, PQNode right){
        if(left == null)
            leftMostSibling = null;
        else leftMostSibling = left;

        if(right == null)
            rightMostSibling = null;
        else rightMostSibling = right;
    }

    public void setParentQNodeChildren(){
        for (PQNode n : this.endmostChildren()) {
            n.parent = this;
            n.setEndmostSiblings(this.endmostChildren().get(0), this.endmostChildren().get(1));
        }

        List<PQNode> internals = this.internalChildren();
        List<PQNode> removalNodes = new ArrayList<>();

        for (PQNode n : internals) {
            n.parent = null;
            n.setEndmostSiblings(this.endmostChildren().get(0), this.endmostChildren().get(1));
            if(!endmostChildren().contains(n)) removalNodes.add(n);
        }

        this.children.removeAll(removalNodes);
    }

    public List<PQNode> immediateSiblings(boolean treatAsContainer){
        List<PQNode> adjacents = new ArrayList<PQNode>();

        if(this.parent == null) return adjacents;

        if(this.parent != null && this.parent.getClass() == PNode.class){
            return adjacents;
        }

        // If only 2 in list
        if(this.circularLink_prev == this.circularLink_next && this.circularLink_prev != null){
            adjacents.add(this.circularLink_next);
            return adjacents;
        }

        if(treatAsContainer) {
            PQNode parent = this.getParent();
            if(parent == null){
                return adjacents;
            }

            // If current if leftmost
            List<PQNode> containerNodes = parent.endmostChildren();
            if (containerNodes.get(0) == this) {
                adjacents.add(this.circularLink_next);
                return adjacents;
            }
            // If current is rightmost
            if (containerNodes.get(1) == this) {
                adjacents.add(this.circularLink_prev);
                return adjacents;
            }
        }

        // Otherwise, current is interior
        if(this.circularLink_prev != null)
            adjacents.add(this.circularLink_prev);
        if(this.circularLink_next != null)
            adjacents.add(this.circularLink_next);
        return adjacents;
    }

    public void replaceInImmediateSiblings(PQNode x, PQNode y){
        this.circularLink_prev = x;
        this.circularLink_next = y;
        // x is replaced by y
    }

    public void replaceInCircularLink(PQNode x){
        this.circularLink_next.circularLink_prev = x;
        this.circularLink_prev.circularLink_next = x;
        x.circularLink_prev = this.circularLink_prev;
        x.circularLink_next = this.circularLink_next;
        this.circularLink_next = null;
        this.circularLink_prev = null;
    }

    /** Below are functions that are overridden in each subclass */

    public List<PQNode> getChildrenOfType(Class type) {
        try {
            throw new NotImplementedException();
        } catch (NotImplementedException e) {
            System.err.println("getChildrenOfType(): " + e);
            return null;
        }
    }

    public List<PQNode> getChildrenOfLabel(String label) {
        try {
            throw new NotImplementedException();
        } catch (NotImplementedException e) {
            System.err.println("getChildrenOfLabel(): " + e);
            return null;
        }
    }

    public List<PQNode> endmostChildren(){
        try {
            throw new IllegalNodeTypeException("Current node must be a Q-Node.");
        } catch (IllegalNodeTypeException e) {
            System.err.println("endmostChildren(): " + e);
            return null;
        }
    }

    public List<PQNode> internalChildren(){
        try {
            throw new IllegalNodeTypeException("Current node must be a Q-Node.");
        } catch (IllegalNodeTypeException e) {
            System.err.println("internalChildren(): " + e);
            return null;
        }
    }

    public void setQNodeEndmostChildren(PQNode leftMost, PQNode rightMost){
        try {
            throw new IllegalNodeTypeException("Current node must be a Q-Node.");
        } catch (IllegalNodeTypeException e) {
            System.err.println("setQNodeEndmostChildren(): " + e);
        }
    }

    public List<PQNode> fullChildren() {
        try {
            throw new NotImplementedException();
        } catch (NotImplementedException e) {
            System.err.println("fullChildren(): " + e);
            return null;
        }
    }

    public List<PQNode> fullAndPartialChildren() {
        try {
            throw new NotImplementedException();
        } catch (NotImplementedException e) {
            System.err.println("fullAndPartialChildren(): " + e);
            return null;
        }
    }

    public List<PQNode> getChildren() {
        try {
            throw new NotImplementedException();
        } catch (NotImplementedException e) {
            System.err.println("getChildren(): " + e);
            return null;
        }
    }

    public void setImmediateSiblings(List<PQNode> list){

    }

    public void removeChildren(List<PQNode> removalNodes) { }

    public void replaceQNodeChild(PQNode newChild, PQNode oldChild) {
        try {
            throw new IllegalNodeTypeException("Current node must be a Q-Node.");
        } catch (IllegalNodeTypeException e) { }
    }

}
