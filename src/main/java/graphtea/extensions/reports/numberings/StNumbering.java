package graphtea.extensions.reports.numberings;

import graphtea.extensions.reports.connectivity.KConnected;
import graphtea.graph.graph.GraphModel;
import graphtea.graph.graph.Vertex;
import graphtea.library.algorithms.Algorithm;
import graphtea.plugins.reports.extension.GraphReportExtension;
import java.lang.Math;

import java.util.HashMap;

/*
 * Selects the first vertex in the graph, and labels the vertices in the graph by doing a pre-order
 * traversal. (Described in the paper "Computing an st-Numbering" by S. Even and R. E. Tarjan).
 *
 * @author Hannes Kr. Hannesson
 */
public class StNumbering extends Algorithm implements GraphReportExtension {

    private int highestId;
    private HashMap<Vertex, Integer> preOrderMapping;
    private HashMap<Vertex, Integer> L;
    private HashMap<Vertex, Integer> stMapping;
    private HashMap<Vertex, Boolean> visited;
    private HashMap<Vertex, Integer> LNumbering;
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

	public StNumbering(GraphModel g) throws NotBiconnectedException {
        KConnected kc = new KConnected();
        if (kc.kconn(g) < 2) {
            throw new NotBiconnectedException("Graph must be biconnected for StNumbering to work!");
        }
        this.graph = g;
        this.highestId = 0;
        this.preOrderMapping = new HashMap<Vertex, Integer>();
        this.stMapping = new HashMap<Vertex, Integer>();
        this.visited = new HashMap<Vertex, Boolean>();
        this.LNumbering = new HashMap<Vertex, Integer>();
    }

    public HashMap<Vertex, Integer> stNumbering() {

        return stMapping;
    }

    public HashMap<Vertex, Integer> preOrderNumbering() {
        for (Vertex v : this.graph) {
            visited.put(v, false);
        }
        for (Vertex v : this.graph) {
            preOrderNumberingHelper(v);
        }
        this.highestId = 0;
        this.visited.clear();
        return this.preOrderMapping;
    }

    public void preOrderNumberingHelper(Vertex v) {
        preOrderMapping.put(v, this.highestId++);
        for (Vertex u : this.graph.neighbors(v)) {
            if (visited.get(u) != true) {
                visited.put(u, true);
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
            for (Vertex u : this.graph.neighbors(v)) {
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
