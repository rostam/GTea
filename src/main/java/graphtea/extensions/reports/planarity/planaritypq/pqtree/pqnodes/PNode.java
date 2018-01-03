package graphtea.extensions.reports.planarity.planaritypq.pqtree.pqnodes;

import java.util.ArrayList;
import java.util.List;

/**
 * This class holds the required functions and variables for a P-Node which are used
 * in the PQTree data-structure.
 *
 * @author Alex Cregten
 * @author Hannes Kr. Hannesson
 * */

public class PNode extends PQNode {
    List<PQNode> children = new ArrayList<>();

    public PNode(String labelType){
        this.setLabel(labelType);
    }

    public PNode(){
        super();
    }

    public void setParent(PQNode parent){
        super.setParent(parent);
    }

    public List<PQNode> getChildrenOfType(Class type) {
        List<PQNode> cList = new ArrayList<PQNode>();

        for (PQNode v : this.children) {
            if(v.getClass() == type){
                cList.add(v);
            }
        }
        return cList;
    }

    public List<PQNode> getChildrenOfLabel(String label) {
        List<PQNode> cList = new ArrayList<PQNode>();

        for (PQNode v : this.children) {
            if (v.getLabel().equals(label)) {
                cList.add(v);
            }
        }

        return cList;
    }

    public List<PQNode> fullChildren(){
        List<PQNode> full = new ArrayList<PQNode>();
        for (PQNode c : children) {
            if (c.getLabel().equals(FULL)) {
                full.add(c);
            }
        }
        return full;
    }

    public List<PQNode> fullAndPartialChildren(){
        List<PQNode> full = new ArrayList<PQNode>();
        for (PQNode c : children) {
            if (c.getLabel().equals(FULL)) {
                full.add(c);
            }
        }
        return full;
    }

    public List<PQNode> getChildren(){
        return children;
    }

    public void removeChildren(List<PQNode> removalNodes){
        this.children.removeAll(removalNodes);
    }

    public void removeChild(PQNode child){
        this.children.remove(child);
    }

    public void addChildren(List<PQNode> children){
        for (PQNode n : children){
            this.children.add(n);
            n.setParent(this);
        }
    }

    public void addChild(PQNode child){
        this.children.add(child);
        child.setParent(this);
    }

    public void replaceChild(PQNode newChild, PQNode oldChild){
        this.removeChild(oldChild);
        this.addChild(newChild);
        newChild.setParent(this);
    }

    public void clearChildren(){
        this.children.clear();
    }

}
