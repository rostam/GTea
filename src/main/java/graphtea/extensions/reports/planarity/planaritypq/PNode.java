package graphtea.extensions.reports.planarity.planaritypq;

import java.util.ArrayList;
import java.util.List;

public class PNode extends PQNode {
    public PNode(String labelType){
        this.labelType = labelType;
    }

    public PNode(){
        super();
    }

    public List<PQNode> getChildrenOfType(Class type) {
        List<PQNode> cList = new ArrayList<PQNode>();

        for (PQNode v : this.children) {
            //if (v.nodeType.equals(type)) {
            if(v.getClass() == type){
                cList.add(v);
            }
        }
        return cList;
    }

    public List<PQNode> getChildrenOfLabel(String label) {
        List<PQNode> cList = new ArrayList<PQNode>();

        for (PQNode v : this.children) {
            if (v.labelType.equals(label)) {
                cList.add(v);
            }
        }

        return cList;
    }

    public List<PQNode> immediateSiblings(boolean treatAsContainer) {
        return new ArrayList<>();
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

    public List<PQNode> fullAndPartialChildren(){
        List<PQNode> full = new ArrayList<PQNode>();
        for (PQNode c : children) {
            if (c.labelType.equals(FULL)) {
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
        this.emptyChildren.removeAll(removalNodes);
        this.fullChildren.removeAll(removalNodes);
        this.partialChildren.removeAll(removalNodes);
        this.pNodeChildren.removeAll(removalNodes);
        this.qNodeChildren.removeAll(removalNodes);
    }


}
