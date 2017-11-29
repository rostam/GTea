package graphtea.extensions.reports.numberings;

import graphtea.library.algorithms.Algorithm;
import graphtea.plugins.reports.extension.GraphReportExtension;

import graphtea.graph.graph.GraphModel;
import graphtea.graph.graph.Vertex;
import graphtea.platform.lang.CommandAttitude;
import graphtea.plugins.reports.extension.GraphReportExtension;

import java.util.HashMap;

/*
 * Selects the first vertex in the graph, and labels the vertices in the graph by doing a pre-order
 * traversal. (Described in the paper "Computing an st-Numbering" by S. Even and R. E. Tarjan).
 *
 * @author Hannes Kr. Hannesson
 */

public class PreOrderNumbering extends Algorithm implements GraphReportExtension {

    int highestId;
    HashMap<Vertex, Integer> mapping;
    HashMap<Vertex, Boolean> visited;

    public String getName() {
		return "Preorder numbering";
	}

	public String getDescription() {
		return "Retreives an numbering of the vertices, by doing a DFS on the first vertex.";
	}

	public Object calculate(GraphModel g) {
        return preOrderNumbering(g, g.getVertex(0));
	}

	public HashMap<Vertex, Integer> preOrderNumbering(GraphModel g, Vertex v) {
        this.highestId = 0;
        this.mapping = new HashMap<Vertex, Integer>();
        this.visited = new HashMap<Vertex, Boolean>();
        for (Vertex u : g.vertices()) {
            this.mapping.put(u, -1);
            this.visited.put(u, false);
        }
        preOrderNumberingHelper(g, v);
        return this.mapping;
    }

    public void preOrderNumberingHelper(GraphModel g, Vertex v) {

        mapping.put(v, this.highestId++);
        for (Vertex u : g.neighbors(v)) {
            if (visited.get(u) != true) {
                visited.put(u, true);
                preOrderNumberingHelper(g, u);
            }
        }
    }

	@Override
	public String getCategory() {
		// TODO Auto-generated method stub
		return "General";
	}

}
