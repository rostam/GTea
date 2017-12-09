package graphtea.extensions.reports.planarity.planaritypq;

import java.util.ArrayList;
import java.util.List;

public class LeafNode extends PQNode {
    public LeafNode(String id){
        this.id = id;
    }
    public LeafNode(){
        super();
    }

    public List<PQNode> getChildrenOfType(Class type) {
        return new ArrayList<PQNode>();
    }

    public List<PQNode> getChildrenOfLabel(String label) {
        return new ArrayList<PQNode>();
    }

    public List<PQNode> immediateSiblings(boolean treatAsContainer) {
        return new ArrayList<>();
    }

    public List<PQNode> fullChildren(){
        return new ArrayList<PQNode>();
    }

    public List<PQNode> getChildren(){
        return new ArrayList<>();
    }

    public List<PQNode> fullAndPartialChildren() {
        return new ArrayList<>();
    }

}
