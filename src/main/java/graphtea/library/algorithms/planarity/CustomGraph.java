package graphtea.library.algorithms.planarity;


import java.util.ArrayList;

public class CustomGraph {
    int increasingVertexId;
    final ArrayList<CustomEdge> edgesList;
    final ArrayList<CustomVertex> verticesList;

    public CustomGraph(ArrayList<CustomEdge> x, ArrayList<CustomVertex> y) {
        this.edgesList = x;
        this.verticesList = y;
        increasingVertexId = verticesList.size();
    }

    public int getNewVertexId() {
        return increasingVertexId++;
    }

    public ArrayList<CustomEdge> getEdgeList(){
        return edgesList;
    }

    public ArrayList<CustomVertex> getVerticesList(){
        return verticesList;
    }

}
