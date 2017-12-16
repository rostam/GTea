package graphtea.extensions.reports.planarity.planaritypq.pqtree.pqnodes;

import java.util.*;

import graphtea.extensions.reports.planarity.planaritypq.pqtree.exceptions.IllegalNodeTypeException;
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
    private String id = "";
    public static String PARTIAL = "partial";
    public static String FULL = "full";
    public static String EMPTY = "empty";

    private String labelType = ""; // PARTIAL, FULL, or EMPTY
    private PQNode circularLink_next;
    private PQNode circularLink_prev;

    private int pertinentChildCount;
    private int pertinentLeafCount;

    public boolean blocked;

    private PQNode parent;
    private PQNode leftMostSibling = null;
    private PQNode rightMostSibling = null;

    public PQNode(){
        blocked = false;
        setPertinentChildCount(0);
        setPertinentLeafCount(0);
        setCircularLink_next(null);
        setCircularLink_prev(null);
        setParent(null);
    }

    public PQNode(String id){
        blocked = false;
        setPertinentChildCount(0);
        setPertinentLeafCount(0);
        setCircularLink_next(null);
        setCircularLink_prev(null);
        setParent(null);
        this.setId(id);
    }

    public boolean isPertinent() {
        return (this.labelType == PARTIAL || this.labelType == FULL);
    }

    public boolean nodeType(Class checkType){
        return this.getClass() == checkType;
    }

    public PQNode emptySibling(){
        PQNode currNode = this.getCircularLink_next();
        while (true) {
            if (currNode.labelType == EMPTY && currNode != this) {
                return currNode;
            }
            else if (currNode == this) {
                return null;
            }
            currNode = currNode.getCircularLink_next();
        }
    }

    public void removeFromCircularLink(){
        this.getCircularLink_next().setCircularLink_prev(this.getCircularLink_prev());
        this.getCircularLink_prev().setCircularLink_next(this.getCircularLink_next());
        this.setCircularLink_next(null);
        this.setCircularLink_prev(null);
    }

    public PQNode getImmediateSiblingOfNodeType(Class nodeType){
        if(this.getCircularLink_next().getClass() == nodeType){
            return this.getCircularLink_next();
        }
        else if(this.getCircularLink_prev().getClass() == nodeType){
            return this.getCircularLink_prev();
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
        if(this.getCircularLink_prev() != null) {
            left = this.getCircularLink_prev().siblingsAdjacent(blocked, true);
        }
        if(this.getCircularLink_next() != null) {
            right = this.getCircularLink_next().siblingsAdjacent(blocked, false);
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
            if(goLeft && this.getCircularLink_prev() != null){
                try {
                    Set<PQNode> list = new HashSet<PQNode>(this.getCircularLink_prev().siblingsAdjacent(blocked, goLeft));
                    list.add(this);
                    return list;
                }
                catch (NullPointerException e){
                    return Collections.singleton(this);
                }
            }
            else if(this.getCircularLink_next() != null){
                try {
                    Set<PQNode> list = new HashSet<>(this.getCircularLink_next().siblingsAdjacent(blocked, goLeft));
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


    protected void setEndmostSiblings(PQNode left, PQNode right){
        if(left == null)
            leftMostSibling = null;
        else leftMostSibling = left;

        if(right == null)
            rightMostSibling = null;
        else rightMostSibling = right;
    }


    public List<PQNode> immediateSiblings(boolean treatAsContainer){
        List<PQNode> adjacents = new ArrayList<PQNode>();

        if(this.getParent() == null) return adjacents;

        if(this.getParent() != null && this.getParent().getClass() == PNode.class){
            return adjacents;
        }

        // If only 2 in list
        if(this.getCircularLink_prev() == this.getCircularLink_next() && this.getCircularLink_prev() != null){
            adjacents.add(this.getCircularLink_next());
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
                adjacents.add(this.getCircularLink_next());
                return adjacents;
            }
            // If current is rightmost
            if (containerNodes.get(1) == this) {
                adjacents.add(this.getCircularLink_prev());
                return adjacents;
            }
        }

        // Otherwise, current is interior
        if(this.getCircularLink_prev() != null)
            adjacents.add(this.getCircularLink_prev());
        if(this.getCircularLink_next() != null)
            adjacents.add(this.getCircularLink_next());
        return adjacents;
    }

    public void replaceInImmediateSiblings(PQNode x, PQNode y){
        this.setCircularLink_prev(x);
        this.setCircularLink_next(y);
        // x is replaced by y
    }

    public void replaceInCircularLink(PQNode x){
        this.getCircularLink_next().setCircularLink_prev(x);
        this.getCircularLink_prev().setCircularLink_next(x);
        x.setCircularLink_prev(this.getCircularLink_prev());
        x.setCircularLink_next(this.getCircularLink_next());
        this.setCircularLink_next(null);
        this.setCircularLink_prev(null);
    }

    /** Below are functions that are overridden in each subclass */

    public void setParent(PQNode parent){
        this.parent = parent;
    }

    public void setParentQNodeChildren(){
        try {
            throw new IllegalNodeTypeException("Current node must be a Q-Node.");
        } catch (IllegalNodeTypeException e) {
            System.err.println("endmostChildren(): " + e);
        }
    }

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

    public void addChildren(List<PQNode> children){
        try {
            throw new NotImplementedException();
        } catch (NotImplementedException e) {
            System.err.println("addChildren(): " + e);
        }
    }

    public void addChild(PQNode child){
        try {
            throw new NotImplementedException();
        } catch (NotImplementedException e) {
            System.err.println("addChild(PQNode): " + e);
        }
    }

    public void addChild(PQNode child, boolean left){
        try {
            throw new NotImplementedException();
        } catch (NotImplementedException e) {
            System.err.println("addChild(PQNode, boolean): " + e);
        }
    }

    public void removeChildren(List<PQNode> removalNodes) {
        try {
            throw new NotImplementedException();
        } catch (NotImplementedException e) {
            System.err.println("removeChildren(): " + e);
        }
    }

    public void removeChild(PQNode child){
        try {
            throw new NotImplementedException();
        } catch (NotImplementedException e) {
            System.err.println("removeChild(): " + e);
        }
    }

    public void replaceChild(PQNode newChild, PQNode oldChild){
        try {
            throw new NotImplementedException();
        } catch (NotImplementedException e) {
            System.err.println("replaceChild(): " + e);
        }
    }

    public void clearChildren(){
        try {
            throw new NotImplementedException();
        } catch (NotImplementedException e) {
            System.err.println("clearChildren(): " + e);
        }
    }

    public void setChildren(List<PQNode> children){
        try {
            throw new IllegalNodeTypeException("Current node must be a P-Node.");
        } catch (IllegalNodeTypeException e) {
            System.err.println("setChildren(): " + e);
        }
    }

    /** The simple getters and setters */

    public PQNode getCircularLink_next() {
        return circularLink_next;
    }

    public void setCircularLink_next(PQNode circularLink_next) {
        this.circularLink_next = circularLink_next;
    }

    public PQNode getCircularLink_prev() {
        return circularLink_prev;
    }

    public void setCircularLink_prev(PQNode circularLink_prev) {
        this.circularLink_prev = circularLink_prev;
    }

    public int getPertinentChildCount() {
        return pertinentChildCount;
    }

    public void setPertinentChildCount(int pertinentChildCount) {
        this.pertinentChildCount = pertinentChildCount;
    }

    public int getPertinentLeafCount() {
        return pertinentLeafCount;
    }

    public void setPertinentLeafCount(int pertinentLeafCount) {
        this.pertinentLeafCount = pertinentLeafCount;
    }

    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    public void setLabel(String label){
        this.labelType = label;
    }

    public String getLabel(){
        return labelType;
    }
}
