package graphtea.extensions.reports;

import graphtea.extensions.Centrality;
import graphtea.extensions.RandomTree;
import graphtea.graph.graph.Edge;
import graphtea.graph.graph.GraphModel;
import graphtea.plugins.reports.extension.GraphReportExtension;

import java.util.Vector;

/**
 * Created by rostami on 26.04.17.
 *
 */
public class Betweenness implements GraphReportExtension {
    @Override
    public Object calculate(GraphModel g) {
        int[][] links = new int[2][g.getEdgesCount()];
        int cnt = 0;
        for(Edge e : g.edges()) {
            links[0][cnt] = e.source.getId();
            links[1][cnt] = e.target.getId();
            cnt++;
        }

//        int[][] edges = g.getEdgeArray();
//        int[][] links = new int[2][edges.length];
//        System.out.println(edges.length + " " + edges[0].length);
//        for(int i=0;i<edges.length;i++) {
//            for(int j=0;j<edges[i].length;j++) {
//                System.out.println(j + " " + i);
//                links[j][i] = edges[i][j];
//            }
//        }
//
//

        RandomTree rt= new RandomTree(100);

        Centrality between = new Centrality(rt.getEdgeList());
        double [] Deg_bet = between.Betweenness_Centrality(g.numOfVertices());
        Vector<Double> v = new Vector<>();
        for(double d : Deg_bet) {
            v.add(d);
        }

//
//        int highBCVertex = v.indexOf(Collections.max(v));
//        Vertex newV = new Vertex();
//        g.addVertex(newV);
//        g.addEdge(new Edge(newV,g.getVertex(highBCVertex)));

        return v;
    }

    @Override
    public String getCategory() {
        return "Betweenness";
    }

    @Override
    public String getName() {
        return "Betweenness";
    }

    @Override
    public String getDescription() {
        return "Betweenness";
    }
}
