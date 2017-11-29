package graphtea.extensions.reports.numberings;

import graphtea.extensions.reports.connectivity.KConnected;
import graphtea.graph.graph.GraphModel;
import graphtea.graph.graph.Vertex;
import graphtea.library.algorithms.Algorithm;
import graphtea.plugins.reports.extension.GraphReportExtension;

import java.util.HashMap;

/*
 * Selects the first vertex in the graph, and labels the vertices in the graph by doing a pre-order
 * traversal. (Described in the paper "Computing an st-Numbering" by S. Even and R. E. Tarjan).
 *
 * @author Hannes Kr. Hannesson
 */
public class StNumbering extends Algorithm implements GraphReportExtension {

    int highestId;
    HashMap<Vertex, Integer> preOrderMapping;
    HashMap<Vertex, Integer> L;
    HashMap<Vertex, Integer> stMapping;
    HashMap<Vertex, Boolean> visited;

    public String getName() {
		return "st-numbering numbering";
	}

	public String getDescription() {
		return "Retreives an st-numbering of the vertices.";
	}

	public Object calculate(GraphModel g) {
        return -1;
	}

    public HashMap<Vertex, Integer> stNumbering(GraphModel g, Vertex v) throws NotBiconnectedException {

        KConnected kc = new KConnected();
        if(kc.kconn(g) < 2) {
            throw new NotBiconnectedException("Graph must be biconnected for StNumbering to work!");
        }
        return stMapping;
    }

    public HashMap<Vertex, Integer> preOrderNumbering(GraphModel g) {
        this.highestId = 0;
        this.preOrderMapping = new HashMap<Vertex, Integer>();
        this.visited = new HashMap<Vertex, Boolean>();
        this.L = new HashMap<Vertex, Integer>();

        for (Vertex v : g.vertices()) {
            this.preOrderMapping.put(v, -1);
            this.visited.put(v, false);
            this.L.put(v, Integer.MAX_VALUE);
        }
        for (Vertex v : g) {
            preOrderNumberingHelper(g, v);
        }
        return this.preOrderMapping;
    }

    public void preOrderNumberingHelper(GraphModel g, Vertex v) {

        preOrderMapping.put(v, this.highestId++);
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
