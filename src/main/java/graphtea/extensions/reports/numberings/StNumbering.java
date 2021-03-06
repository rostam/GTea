package graphtea.extensions.reports.numberings;

import graphtea.graph.graph.Edge;
import graphtea.graph.graph.GraphModel;
import graphtea.graph.graph.Vertex;
import graphtea.library.algorithms.Algorithm;
import graphtea.plugins.reports.extension.GraphReportExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

/**
 * This class takes a graph and applies an st-numbering to its vertices.
 * An st-numbering is a biconnected graph numbering such that vertex 1 and n
 * are adjacent and, for any vertex numbere 1 < j < n, there exist vertices
 * numbered i and k such tht i < j < k and both i and k are adjacent to j.
 *
 * @author Alex Cregten
 * @author Hannes Kr. Hannesson
 * */
public class StNumbering extends Algorithm implements GraphReportExtension {

    private Stack<Vertex> stack;
    private int highestId;
    private HashMap<Vertex, Integer> preOrderMapping;
    private HashMap<Vertex, Integer> stMapping;
    private HashMap<Vertex, Boolean> visited;
    private HashMap<Vertex, Integer> LNumbering;
    private HashMap<Vertex, Boolean> newVertex;
    private HashMap<Edge, Boolean> newEdge;
    private List<Edge> treeEdges;
    private HashMap<Vertex, List<Vertex>> neighborMap;

    private GraphModel graph;

    public String getName() {
		return "st-numbering numbering";
	}

	public String getDescription() {
		return "Retreives an st-numbering of the vertices.";
	}

	public Object calculate(GraphModel g) {
        return -1;
	}

	public boolean biConnected(GraphModel g) {
        for (Vertex v : g) {
            if (neighborMap.get(v) != null && neighborMap.get(v).size() < 2) {
                return false;
            }
        }
        return true;
    }

	public StNumbering(GraphModel g) throws NotBiconnectedException {

        this.graph = g;
        this.highestId = 0;
        this.preOrderMapping = new HashMap<Vertex, Integer>();
        this.stMapping = new HashMap<Vertex, Integer>();
        this.visited = new HashMap<Vertex, Boolean>();
        this.LNumbering = new HashMap<Vertex, Integer>();
        this.newVertex = new HashMap<Vertex, Boolean>();
        this.newEdge = new HashMap<Edge, Boolean>();
        this.stack = new Stack<Vertex>();
        this.treeEdges = new ArrayList<>();
        this.neighborMap = new HashMap<Vertex, List<Vertex>>();

        graph.setDirected(false);
        //Neighbor precompute must be run before biConnected
        neighborPrecompute();
        if (!biConnected(g) && g.getVertexArray().length > 2) {
            throw new NotBiconnectedException("Graph must be biconnected for StNumbering to work!");
        }
    }

    public StNumbering() {

    }

    private void addEdgeMapping(Vertex src, Vertex trg){
        if(this.neighborMap.get(src) == null){
            List<Vertex> vlist = new ArrayList<>();
            vlist.add(trg);
            this.neighborMap.put(src, vlist);
        }
        else {
            this.neighborMap.get(src).add(trg);
        }
    }

    public void neighborPrecompute(){

        for(Edge e : this.graph.getEdges()){
            addEdgeMapping(e.source, e.target);
            addEdgeMapping(e.target, e.source);
        }
    }

    public HashMap<Vertex, Integer> stNumbering() {

        if(this.graph.getVerticesCount() <= 2) {
            for(Vertex v : this.graph.getVertexArray()){
                stMapping.put(v, v.getId());
            }
            return stMapping;
        }




        this.preOrderNumbering();

        Vertex arbitraryVertex = null;
        for (Vertex v : this.graph) {
            if(arbitraryVertex == null) {
                arbitraryVertex = v;
                newVertex.put(v, false);
            }
            else newVertex.put(v, true);
        }

        Vertex t = arbitraryVertex;
        Vertex s = this.neighborMap.get(t).get(0);
        newVertex.put(s, false);
        for (Edge e : this.graph.getEdges()){
            //if(e.source == t && e.target == s){
            if(e == graph.getEdge(s, t)){
                newEdge.put(e, false);
            }
            else newEdge.put(e, true);
        }

        this.computeL(t);

        stack.push(t);
        stack.push(s);

        Stack<Vertex> path;
        int i = 0;
        while(!stack.empty()) {
            Vertex v = stack.pop();

            // Find path with PATHFINDER
            path = pathfinder(v);

            if (!path.isEmpty()) {
                path.pop();
                while(!path.isEmpty())
                    stack.push(path.pop());
            } else {
                stMapping.put(v, i++);
            }

        }

        return stMapping;
    }

    public Stack<Vertex> pathfinder(Vertex v){
        Stack<Vertex> path = new Stack<>();

        // (a)
        // If there is a new cycle edge {u,w} with w *-> v
        for(Vertex w : this.neighborMap.get(v)) {
            Edge currentEdge = this.graph.getEdge(v, w);
            if(currentEdge == null) continue;
            if(newEdge.get(currentEdge) && !treeEdges.contains(currentEdge) &&
                    preOrderMapping.get(w) < preOrderMapping.get(v)){

                newEdge.put(currentEdge, false);
                path.add(v);
                path.add(w);
                return path;
            }
        }

        // (b)
        // Else if there is a new tree edge v -> w
        for(Vertex w : this.neighborMap.get(v)){
            Edge currentEdge = this.graph.getEdge(v, w);
            if(currentEdge == null) continue;

            if(newEdge.get(currentEdge) && treeEdges.contains(currentEdge) &&
                    preOrderMapping.get(w) > preOrderMapping.get(v)){

                newEdge.put(currentEdge, false);
                path.add(v);
                path.add(w);

                // while w is new
                Vertex currentVertex = w;
                boolean foundNextEdge = true;
                while(newVertex.get(currentVertex) && foundNextEdge){
                    // find a (new) edge {w,x} with x = L(w) or L(x) = L(w)
                    foundNextEdge = false;
                    for(Vertex x : this.neighborMap.get(currentVertex)){
                        Edge nextEdge = this.graph.getEdge(currentVertex, x);
                        if(nextEdge == null) continue;

                        if (newEdge.get(nextEdge) &&
                                (preOrderMapping.get(x) == LNumbering.get(currentVertex) ||
                                        LNumbering.get(x) == LNumbering.get(currentVertex))) {

                            newVertex.put(currentVertex, false);
                            newEdge.put(nextEdge, false);
                            path.add(x);
                            currentVertex = x;
                            foundNextEdge = true;
                            break;
                        }

                    }
                }
                return path;
            }
        }

        // (c)
        // Else if there is a new edge {v, w} with v *-> w
        for (Vertex w : this.neighborMap.get(v)) {
            Edge currentEdge = this.graph.getEdge(v, w);
            if(currentEdge == null) continue;
            if(newEdge.get(currentEdge) && !treeEdges.contains(currentEdge) &&
                    preOrderMapping.get(w) > preOrderMapping.get(v)){

                newEdge.put(currentEdge, false);
                path.add(v);
                path.add(w);

                Vertex currentVertex = w;
                boolean foundNextEdge = true;
                while (newVertex.get(currentVertex) && foundNextEdge){
                    foundNextEdge = false;
                    for (Vertex x : this.neighborMap.get(currentVertex)){
                        Edge nextEdge = this.graph.getEdge(currentVertex, x);
                        if(nextEdge == null) continue;
                        if (newEdge.get(nextEdge) && treeEdges.contains(nextEdge)) {
                            newVertex.put(currentVertex, false);
                            newEdge.put(nextEdge, false);
                            path.add(x);
                            currentVertex = x;
                            foundNextEdge = true;
                            break;
                        }
                    }
                }
                return path;
            }

        }

        // (d)
        // Else return empty path
        return path;
    }

    public HashMap<Vertex, Integer> preOrderNumbering() {
        for (Vertex v : this.graph) {
            visited.put(v, false);
        }
        for (Vertex v : this.graph) {
            visited.put(v, true);
            preOrderNumberingHelper(v);
        }
        this.highestId = 0;
        this.visited.clear();
        return this.preOrderMapping;
    }

    public void preOrderNumberingHelper(Vertex v) {

        if(preOrderMapping.get(v) == null){
            preOrderMapping.put(v, this.highestId++);
        }

        for (Vertex u : this.graph.neighbors(v)) {
            if (visited.get(u) != true) {
                visited.put(u, true);
                treeEdges.add(this.graph.getEdge(v, u));
                preOrderNumberingHelper(u);
            }
        }
    }


    public int computeL(Vertex v) {
        for (Vertex u : this.graph) {
            this.LNumbering.put(u, this.preOrderMapping.get(u));
            this.visited.put(u, false);
        }
        for (Vertex u : this.graph) {
            computeLHelper(u);
        }
        return this.LNumbering.get(v);
    }

    public int computeLHelper(Vertex v) {
        if (visited.get(v) == false) {
            this.visited.put(v, true);
            for (Vertex u : this.neighborMap.get(v)) {
                if (this.visited.get(u) == true) {
                    this.LNumbering.put(v, Math.min(this.LNumbering.get(v), preOrderMapping.get(u)));
                }
                else {
                    this.LNumbering.put(v, Math.min(this.LNumbering.get(v), computeLHelper(u)));
                }
            }
        }
        return LNumbering.get(v);
    }


	@Override
	public String getCategory() {
		// TODO Auto-generated method stub
		return "General";
	}
}
