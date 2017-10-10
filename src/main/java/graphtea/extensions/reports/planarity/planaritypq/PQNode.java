package graphtea.extensions.reports.planarity.planaritypq;

import graphtea.graph.graph.Edge;
import graphtea.graph.graph.Vertex;

import java.util.ArrayList;
import java.util.List;

public class PQNode extends Vertex {
    static int PNODE = 0;
    static int QNODE = 1;
    static int ELEMENT = 2;
    boolean blocked;
    boolean marked;
    PQNode parent;
    List<Object> siblings = new ArrayList<Object>();

    public PQNode(){
        blocked = false;
        marked = false;
    }

    public int type(){
        return -1;
    }

}
