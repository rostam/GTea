package graphtea.extensions.reports.planarity.planaritypq;

import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QNode extends PQNode {
    public QNode(String labelType){
        this.labelType = labelType;
    }

    public QNode(){
        super();
    }

    public List<PQNode> getChildrenOfType(Class type) {
        List<PQNode> cList = new ArrayList<PQNode>();

        if(this.children.size() > 0){
            PQNode start = this.children.get(0);
            PQNode iter = start.circularLink_next;
            if(start.getClass() == type){
                cList.add(start);
            }
            while(iter != start){
                if(iter.getClass() == type){
                    cList.add(iter);
                }
                iter = iter.circularLink_next;
            }
        }
        return cList;
    }

    public List<PQNode> getChildrenOfLabel(String label) {
        List<PQNode> cList = new ArrayList<PQNode>();

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

    public List<PQNode> fullChildren(){
        List<PQNode> full = new ArrayList<PQNode>();
        PQNode traversal = this.endmostChildren().get(0);
        PQNode start = this.endmostChildren().get(0);

        do {
            if(traversal.labelType.equals(PQNode.FULL))
                full.add(traversal);
            traversal = traversal.circularLink_next;
        } while(traversal != start);
        return full;
    }

    public List<PQNode> fullAndPartialChildren(){
        List<PQNode> full = new ArrayList<PQNode>();
        PQNode traversal = this.endmostChildren().get(0);
        PQNode start = this.endmostChildren().get(0);

        do {
            if(traversal.labelType.equals(PQNode.FULL))
                full.add(traversal);
            else if (traversal.labelType.equals(PQNode.PARTIAL))
                full.add(traversal);
            traversal = traversal.circularLink_next;
        } while(traversal != start);
        return full;
    }

    public List<PQNode> getChildren(){
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

    public void setQNodeEndmostChildren(PQNode leftMost, PQNode rightMost){
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

    public void replaceQNodeChild(PQNode newChild, PQNode oldChild){
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
        this.setQNodeEndmostChildren(this.endmostChildren().get(0), this.endmostChildren().get(1));
    }

}
