package graphtea.extensions.reports.planarity.planaritypq.pqtree.pqnodes;

import graphtea.extensions.reports.planarity.planaritypq.pqtree.helpers.PQHelpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class holds the required functions and variables for a Q-Node which are used
 * in the PQTree data-structure.
 *
 * @author Alex Cregten
 * @author Hannes Kr. Hannesson
 * */

public class QNode extends PQNode {
    public PQNode leftmostChild = null;
    public PQNode rightmostChild = null;


    public QNode(String labelType){
        this.setLabel(labelType);
    }

    public QNode(){
        super();
    }

    public List<PQNode> getChildrenOfType(Class type) {
        List<PQNode> cList = new ArrayList<PQNode>();

        PQNode start = this.leftmostChild;
        PQNode iter = start.getCircularLink_next();
        if(start.getClass() == type){
            cList.add(start);
        }
        while(iter != start){
            if(iter.getClass() == type){
                cList.add(iter);
            }
            iter = iter.getCircularLink_next();
        }

        return cList;
    }

    public List<PQNode> getChildrenOfLabel(String label) {
        List<PQNode> cList = new ArrayList<PQNode>();

        PQNode start = this.leftmostChild;
        PQNode iter = start;
        do {
            if(iter.getLabel().equals(label)) {
                cList.add(iter);
            }
            iter = iter.getCircularLink_next();
        } while(iter != start);


        return cList;
    }

    public List<PQNode> internalChildren() {
        List<PQNode> internals = new ArrayList<PQNode>();
        //Get nodes of index 1 ... (n-1)
        List<PQNode> allChildren = this.getChildren();
        for (int i = 1; i < allChildren.size()-1; i++) {
            internals.add(allChildren.get(i));
        }
        return internals;
    }

    public List<PQNode> endmostChildren(){
        return Arrays.asList(this.leftmostChild, this.rightmostChild);
    }

    public List<PQNode> fullChildren(){
        List<PQNode> full = new ArrayList<PQNode>();
        PQNode traversal = this.endmostChildren().get(0);
        PQNode start = this.endmostChildren().get(0);

        do {
            if(traversal.getLabel().equals(PQNode.FULL))
                full.add(traversal);
            traversal = traversal.getCircularLink_next();
        } while(traversal != start);
        return full;
    }

    public List<PQNode> fullAndPartialChildren(){
        List<PQNode> full = new ArrayList<PQNode>();
        PQNode traversal = this.endmostChildren().get(0);
        PQNode start = this.endmostChildren().get(0);

        do {
            if(traversal.getLabel().equals(PQNode.FULL))
                full.add(traversal);
            else if (traversal.getLabel().equals(PQNode.PARTIAL))
                full.add(traversal);
            traversal = traversal.getCircularLink_next();
        } while(traversal != start);
        return full;
    }

    public List<PQNode> getChildren(){
        List<PQNode> list = new ArrayList<>();

        PQNode start = this.leftmostChild;
        PQNode iter = start.getCircularLink_next();
        list.add(start);
        while(iter != start){
            list.add(iter);
            iter = iter.getCircularLink_next();
        }
        return list;
    }

    public void removeChildren(List<PQNode> removalNodes){

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
                traversal.getCircularLink_next().setCircularLink_prev(traversal.getCircularLink_prev());
                traversal.getCircularLink_prev().setCircularLink_next(traversal.getCircularLink_next());

                if(traversal == start){
                    updateStart = true;
                }

            }
            else {
                survivors.add(traversal);
            }
            traversal = traversal.getCircularLink_next();
        } while(traversal != start);

        if(survivors.size() >= 2){
            setQNodeEndmostChildren(survivors.get(0), survivors.get(survivors.size()-1));
            //setParentQNodeChildren();
        }
        else if(survivors.size() == 1){
            setQNodeEndmostChildren(survivors.get(0), survivors.get(0));
            //setParentQNodeChildren();
        }
        else {
            //setQNodeEndmostChildren(null, null);
            setEmptyEndmostChildren();
            //setParentQNodeChildren();
        }

    }

    private void setEmptyEndmostChildren(){
        this.leftmostChild = null;
        this.rightmostChild = null;
    }

    public void setQNodeEndmostChildren(PQNode leftMost, PQNode rightMost){
        if(leftMost != null) {
            this.leftmostChild = leftMost;
            this.leftmostChild.setParent(this);
        }
        if(rightMost != null) {
            this.rightmostChild = rightMost;
            this.rightmostChild.setParent(this);
        }
    }

    public void replaceChild(PQNode newChild, PQNode oldChild){
        if(oldChild == this.leftmostChild){
            this.setQNodeEndmostChildren(newChild, null);
        }
        else if (oldChild == this.rightmostChild){
            this.setQNodeEndmostChildren(null, newChild);
        }
        PQHelpers.insertNodeIntoCircularList(newChild, oldChild.getCircularLink_prev(), oldChild.getCircularLink_next());
    }

    public void setParentQNodeChildren(){
        for (PQNode n : this.endmostChildren()) {
            n.setParent(this);
            n.setEndmostSiblings(this.leftmostChild, this.rightmostChild);
        }

        // Traverse internals
        PQNode itr = this.leftmostChild.getCircularLink_next();
        while(itr != this.rightmostChild){
            itr.setParent(null);
            itr.setEndmostSiblings(this.leftmostChild, this.rightmostChild);
            itr = itr.getCircularLink_next();
        }
    }

    public void rotate(){
        PQHelpers.reverseCircularLinks(this.endmostChildren().get(0));
        this.setQNodeEndmostChildren(this.rightmostChild, this.leftmostChild);
        this.setParentQNodeChildren();
    }

    public void setChildren(List<PQNode> children){
        PQHelpers.setCircularLinks(children);
        this.setQNodeEndmostChildren(children.get(0), children.get(children.size()-1));
        this.setParentQNodeChildren();
    }

    public void addChild(PQNode child, boolean left){
        if(left){
            // Add left side
            PQHelpers.insertNodeIntoCircularList(child, this.rightmostChild, this.leftmostChild);
            this.setQNodeEndmostChildren(child, null);

        }
        else if(!left){
            // Add right side
            PQHelpers.insertNodeIntoCircularList(child, this.rightmostChild, this.leftmostChild);
            this.setQNodeEndmostChildren(null, child);
        }
        setParentQNodeChildren();
    }


}
