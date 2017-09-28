package graphtea.library.algorithms.planarity;

import graphtea.library.BaseEdge;
import graphtea.library.BaseVertex;
import graphtea.library.algorithms.Algorithm;

public class CustomEdge  <VertexType extends BaseVertex, EdgeType extends BaseEdge<VertexType>> extends Algorithm {
    public CustomVertex source;
    public CustomVertex target;
    boolean marked;
    public CustomEdge(CustomVertex source, CustomVertex target){
        this.source = source;
        this.target = target;
        marked = false;
    }
    public CustomEdge(VertexType source, VertexType target){
        this.source = new CustomVertex(source.getId());
        this.target = new CustomVertex(target.getId());
        marked = false;
    }
    public void mark(){
        marked = true;
    }
    public boolean isMarked(){
        return marked;
    }
    public CustomVertex getSource(){
        return source;
    }
    public CustomVertex getTarget(){
        return target;
    }


}