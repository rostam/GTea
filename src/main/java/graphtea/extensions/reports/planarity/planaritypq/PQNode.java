package graphtea.extensions.reports.planarity.planaritypq;

import java.util.*;

import graphtea.extensions.reports.planarity.planaritypq.IllegalNodeTypeException;
import sun.awt.image.ImageWatched;

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

    public List<PQNode> getChildrenOfType(Class type) {
        List<PQNode> cList = new ArrayList<PQNode>();

        if(this.getClass() == QNode.class){
            // QNode
            if(this.children.size() > 0){

                PQNode start = this.children.get(0);
                PQNode iter = start.circularLink_next;
                //if(start.nodeType.equals(type)) {
                if(start.getClass() == type){
                    cList.add(start);
                }
                while(iter != start){
                    //if(iter.nodeType.equals(type)) {
                    if(iter.getClass() == type){
                        cList.add(iter);
                    }
                    iter = iter.circularLink_next;
                }
            }

        }
        else {
            // PNode
            for (PQNode v : this.children) {
                //if (v.nodeType.equals(type)) {
                if(v.getClass() == type){
                    cList.add(v);
                }
            }
        }
        return cList;
    }

    public List<PQNode> getChildrenOfLabel(String label) {
        List<PQNode> cList = new ArrayList<PQNode>();

        //if(nodeType.equals(QNODE)){
        if(this.getClass() == QNode.class){
            // QNode
            if(this.children.size() > 0){

                PQNode start = this.children.get(0);
                PQNode iter = start;
                do {
                    if(iter.labelType.equals(label)) {
                        cList.add(iter);
                    }
                    iter = iter.circularLink_next;
                } while(iter != start);
            }
        }
        else {
            // PNode
            for (PQNode v : this.children) {
                if (v.labelType.equals(label)) {
                    cList.add(v);
                }
            }
        }
        return cList;
    }

    /*
     * Note: only for q-nodes
     */
    public List<PQNode> endmostChildren(){
        try {
            //if (!this.nodeType.equals(QNODE)) {
            if(this.getClass() != QNode.class){

                throw new IllegalNodeTypeException("endmostChildren() is only valid for Q-Nodes");
            }
            //return this.children;
            if(this.children.size() == 1){
                return Arrays.asList(this.children.get(0));
            }
            else if(this.children.size() >= 2){
                return Arrays.asList(this.children.get(0), this.children.get(this.children.size()-1));
            }
            else {
                return Arrays.asList();
            }
        }
        catch (IllegalNodeTypeException e) {

        }
        return new ArrayList<PQNode>();
    }

    public List<PQNode> internalChildren() {
        try {
            //if (!this.nodeType.equals(QNODE)) {
            if(this.getClass() != QNode.class){

                throw new IllegalNodeTypeException("internalChildren() is only valid for Q-Nodes");
            }
            //return this.children;
            List<PQNode> internals = new ArrayList<PQNode>();
            //Get nodes of index 1 ... (n-1)
            List<PQNode> allChildren = this.getChildren();
            for (int i = 1; i < allChildren.size()-1; i++) {
                internals.add(allChildren.get(i));
            }
            return internals;
        }
        catch (IllegalNodeTypeException e) {

        }
        return new ArrayList<PQNode>();
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

    // x is replaced by this
    public void replaceInCircularLink(PQNode x){
        this.circularLink_next.circularLink_prev = x;
        this.circularLink_prev.circularLink_next = x;
        x.circularLink_prev = this.circularLink_prev;
        x.circularLink_next = this.circularLink_next;
        this.circularLink_next = null;
        this.circularLink_prev = null;
    }

    public void replaceInImmediateSiblings(PQNode x, PQNode y){
        this.circularLink_prev = x;
        this.circularLink_next = y;
        // x is replaced by y
    }

    public void setQNodeEndmostChildren(PQNode leftMost, PQNode rightMost){
        try {
            if (this.getClass() != QNode.class) {
                throw new IllegalNodeTypeException("endmostChildren() is only valid for Q-Nodes");
            }
            this.children = new ArrayList<>();
            if(leftMost != null) {
                if(this.children.contains(leftMost))
                    this.children.remove(leftMost);
                this.children.add(leftMost);
                leftMost.parent = this;
            }
            if(rightMost != null) {
                if(this.children.contains(rightMost))
                    this.children.remove(rightMost);
                this.children.add(rightMost);
                rightMost.parent = this;
            }
        }
        catch (IllegalNodeTypeException e) { }
    }

    public List<PQNode> fullChildren(){
        List<PQNode> full = new ArrayList<PQNode>();

        if(this.getClass() == QNode.class){

            PQNode traversal = this.endmostChildren().get(0);
            PQNode start = this.endmostChildren().get(0);

            do {
                if(traversal.labelType.equals(PQNode.FULL))
                    full.add(traversal);
                traversal = traversal.circularLink_next;
            } while(traversal != start);
        }
        else {
            for (PQNode c : children) {
                if (c.labelType.equals(FULL)) {
                    full.add(c);
                }
            }
        }
        return full;
    }

    public List<PQNode> fullAndPartialChildren(){
        List<PQNode> full = new ArrayList<PQNode>();

        if(this.getClass() == QNode.class){

            PQNode traversal = this.endmostChildren().get(0);
            PQNode start = this.endmostChildren().get(0);

            do {
                if(traversal.labelType.equals(PQNode.FULL))
                    full.add(traversal);
                else if (traversal.labelType.equals(PQNode.PARTIAL))
                    full.add(traversal);
                traversal = traversal.circularLink_next;
            } while(traversal != start);
        }
        else {
            for (PQNode c : children) {
                if (c.labelType.equals(FULL)) {
                    full.add(c);
                }
            }
        }
        return full;
    }

    public void setImmediateSiblings(List<PQNode> list){

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

    public List<PQNode> getChildren(){
        if(this.getClass() != QNode.class){
            return children;
        }

        // Qnode
        List<PQNode> list = new ArrayList<>();
        if(this.children.size() > 0) {
            PQNode start = this.children.get(0);
            PQNode iter = start.circularLink_next;
            list.add(start);
            while(iter != start){
                list.add(iter);
                iter = iter.circularLink_next;
            }

        }
        return list;
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

    public void removeChildren(List<PQNode> removalNodes){
        if(this.getClass() == QNode.class){

            if(this.endmostChildren().size() == 0) return;

            List<PQNode> survivors = new ArrayList<>();

            PQNode traversal = this.endmostChildren().get(0);
            PQNode start = traversal;
            boolean updateStart = false;
            do {
                if(updateStart) {
                    start = traversal;
                    updateStart = false;
                }

                if(removalNodes.contains(traversal)){
                    this.children.remove(traversal);
                    traversal.circularLink_next.circularLink_prev = traversal.circularLink_prev;
                    traversal.circularLink_prev.circularLink_next = traversal.circularLink_next;

                    if(traversal == start){
                        updateStart = true;
                    }

                }
                else {
                    survivors.add(traversal);
                }
                traversal = traversal.circularLink_next;
            } while(traversal != start);

            if(survivors.size() >= 2){
                setQNodeEndmostChildren(survivors.get(0), survivors.get(survivors.size()-1));
            }
            else if(survivors.size() == 1){
                setQNodeEndmostChildren(survivors.get(0), null);
            }
            else {
                setQNodeEndmostChildren(null, null);
            }

        }
        else {
            this.children.removeAll(removalNodes);
        }
    }

    public void replaceQNodeChild(PQNode newChild, PQNode oldChild){
        try {
            if (this.getClass() == QNode.class) {
                // Current node is a QNode
                int index = 0;
                for (PQNode n : this.endmostChildren()) {
                    if (n == oldChild) {
                        if (index == 0) {
                            // place newChild at front, move oldChild to internals
                            this.children.add(newChild);
                        } else {
                            // place newChild at end, move oldChild to internals
                            this.children.add(this.children.size() - 1, newChild);
                        }

                    }
                    index++;
                }
                PQHelpers.insertNodeIntoCircularList(newChild, oldChild.circularLink_prev, oldChild.circularLink_next);
                this.children.remove(oldChild);
            }
            else {
                throw new IllegalNodeTypeException("Current node must be a Q-Node.");
            }
        } catch (IllegalNodeTypeException e) { }
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


}
