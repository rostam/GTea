package graphtea.extensions.reports.planarity;

import graphtea.graph.graph.GraphModel;
import graphtea.library.BaseVertex;
import graphtea.library.BaseEdge;
import graphtea.library.BaseGraph;
import graphtea.graph.graph.Edge;
import graphtea.graph.graph.Vertex;
import graphtea.library.algorithms.Algorithm;
import graphtea.library.algorithms.AutomatedAlgorithm;
import graphtea.library.algorithms.planarity.CustomEdge;
import graphtea.library.algorithms.planarity.CustomGraph;
import graphtea.library.algorithms.planarity.CustomVertex;
import graphtea.library.event.GraphRequest;
import graphtea.plugins.reports.extension.GraphReportExtension;
import java.util.ArrayList;

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
		WagnerMethod wm = new WagnerMethod();
        return wm.isPlanar(g);
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






