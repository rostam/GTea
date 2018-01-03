package graphtea.extensions.reports.planarity.planaritypq.pqtree.pqnodes;

import java.util.ArrayList;
import java.util.List;

/**
 * This class holds the required functions and variables for a Leaf-Node which are used
 * in the PQTree data-structure.
 *
 * @author Alex Cregten
 * @author Hannes Kr. Hannesson
 * */

public class LeafNode extends PQNode {
    public LeafNode(String id){
        this.setId(id);
    }
    public LeafNode(){
        super();
    }

    public void setParent(PQNode parent){
        super.setParent(parent);
    }

    public List<PQNode> getChildrenOfType(Class type) {
        return new ArrayList<PQNode>();
    }

    public List<PQNode> getChildrenOfLabel(String label) {
        return new ArrayList<PQNode>();
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
