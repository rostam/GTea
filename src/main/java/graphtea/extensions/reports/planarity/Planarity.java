package graphtea.extensions.reports.planarity;

import graphtea.extensions.reports.numberings.NotBiconnectedException;
import graphtea.extensions.reports.planarity.planaritypq.PQMethod;
import graphtea.graph.graph.GraphModel;
import graphtea.library.algorithms.Algorithm;
import graphtea.plugins.reports.extension.GraphReportExtension;

/**
 * Tests whether a graph is planar.
 *
 * The intention of this class is to use it as a base in order to compare and help
 * test a much more efficient planarity checking algorithm.
 * */
public class Planarity extends Algorithm implements GraphReportExtension {

	GraphModel g;

	public String getName() {
		return "Is Planar";
	}

	public String getDescription() {
		return "Is the graph Planar?";
	}

	public Object calculate(GraphModel g) {
		//WagnerMethod wm = new WagnerMethod();
        //return wm.isPlanar(g);
		PQMethod pq = new PQMethod();
		try {
			return pq.isPlanar(g);
		} catch (NotBiconnectedException e) {
			return null;
		}
        //return doAlgorithm();
	}

	@Override
	public String getCategory() {
		// TODO Auto-generated method stub
		return "General";
	}

    public boolean doAlgorithm() {
        //GraphRequest<Vertex, Edge> gr = new GraphRequest<>();
        //dispatchEvent(gr);
        //BaseGraph<Vertex, Edge> graph = gr.getGraph();

        // Check planarity
        System.out.println("Checking planarity ... ");
        WagnerMethod wm = new WagnerMethod();
        boolean p = wm.isPlanar(g);
        return p;
    }

}






