package graphtea.extensions.reports;


import graphtea.extensions.edgeCrossing;
import graphtea.graph.graph.Edge;
import graphtea.graph.graph.GPoint;
import graphtea.graph.graph.GraphModel;
import graphtea.plugins.reports.extension.GraphReportExtension;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by rostami on 26.04.17.
 *
 */
public class EdgeCrossing implements GraphReportExtension {
    @Override
    public Object calculate(GraphModel g) {
        int[][] edge = new int[2][g.getEdgesCount()];
        int cnt = 0;
        for(Edge e : g.edges()) {
            edge[0][cnt] = e.source.getId();
            edge[1][cnt] = e.target.getId();
            cnt++;
        }
        double[][] pos = new double[2][g.numOfVertices()];
        for(int i=0;i< g.numOfVertices();i++) {
            GPoint p = g.getVertex(i).getLocation();
            pos[0][i] = p.x;pos[1][i]=p.y;
        }
        edgeCrossing cr = new edgeCrossing(pos, edge);
        ArrayList<int[]> edgeCr = cr.getCrossedEdges();
        String crosses = "";
        for(int[] e : edgeCr) {
            crosses+=e[0] + "-" + e[1] + "," + e[2] + "-" + e[3] +"<br/>";
        }

        return "Number of crossing:" + cr.number_of_edge_crossing()+"<br/><br/>"+
                "Edge crossings:" + crosses;
    }

    @Override
    public String getCategory() {
        return "Edge crossings";
    }

    @Override
    public String getName() {
        return "Edge crossings";
    }

    @Override
    public String getDescription() {
        return "Edge crossings";
    }
}
