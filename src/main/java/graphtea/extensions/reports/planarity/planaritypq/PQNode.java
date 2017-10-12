package graphtea.extensions.reports.planarity.planaritypq;

import java.util.ArrayList;
import java.util.List;

public class PQNode {
    static String PNODE = "p-node";
    static String QNODE = "q-node";
    static String PSEUDO_NODE = "pseudonode";
    String nodeType = ""; // update the type

    static String PARTIAL = "partial";
    static String FULL = "full";
    static String EMPTY = "EMPTY";
    String labelType = "";

    PQNode circularLink_next;
    PQNode circularLink_prev;

    int pertinentChildCount;

    int pertinentLeafCount;

    boolean queued;

    boolean blocked;

    boolean marked;

    int childCount;

    PQNode parent;
    List<PQNode> children = new ArrayList<>();

    public PQNode(){
        blocked = false;
        marked = false;
        pertinentChildCount = 0;
        pertinentLeafCount = 0;
        queued = false;
        circularLink_next = null;
        circularLink_prev = null;
        childCount = 0;
    }

    public boolean nodeType(String checkType){
        return nodeType.equals(checkType);
    }

    public List<PQNode> partialChildren(){
        return null;
    }

    public void setPartialChildren(List<PQNode> list){

    }

    public List<PQNode> endmostChildren(){
        return null;
    }

    public PQNode emptySibling(){
        PQNode = this.circularLink_next;
            if (v.labelType.equals(EMPTY)) {
                return v;
            }
        }
        return null;
    }

    public void removeFromCircularLink(){
        this.circularLink_next.circularLink_prev = this.circularLink_prev;
        this.circularLink_prev.circularLink_next = this.circularLink_next;
        this.circularLink_next = null;
        this.circularLink_prev = null;
    }

    public List<PQNode> immediateSiblings(){
        List<PQNode> adjacents = new ArrayList<PQNode>();
        adjacents.add(this.circularLink_prev);
        adjacents.add(this.circularLink_next);
        return adjacents;
    }

    public void replaceInCircularLink(PQNode x, PQNode y){
        // x is replaced by y
    }

    public void replaceInImmediateSiblings(PQNode x, PQNode y){
        this.circularLink_prev = x;
        this.circularLink_next = y;
        // x is replaced by y
    }

    public void setEndmostChildren(List<PQNode> list){

    }

    public List<PQNode> fullChildren(){
        List<PQNode> full = new ArrayList<PQNode>();
        for (PQNode c : children) {
            if (c.labelType.equals(FULL)) {
                full.add(c);
            }
        }
        return full;
    }

    public void setImmediateSiblings(List<PQNode> list){

    }

    public PQNode getImmediateSiblingOfNodeType(String nodeType){
        return null;
    }

    public PQNode endmostChild(String nodeType){
        return null;
    }


}
