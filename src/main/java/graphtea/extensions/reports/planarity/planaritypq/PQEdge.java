package graphtea.extensions.reports.planarity.planaritypq;

import graphtea.graph.graph.Edge;

public class PQEdge extends Edge{
    public PQNode parent;
    public PQNode sibling;
    //public boolean blocked;

    public PQEdge(Edge edge, PQNode source, PQNode target) {
        super(edge, source, target);
        parent = source;
        sibling = target;
        //blocked = false;
    }



}
