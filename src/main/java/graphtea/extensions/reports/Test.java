package graphtea.extensions.reports;

import graphtea.extensions.G6Format;
import graphtea.graph.graph.Edge;
import graphtea.graph.graph.GraphModel;

public class Test {
    public static void main(String[] args) {
        GraphModel g = G6Format.stringToGraphModel("CF");
        for(Edge e : g.edges()) {
            System.out.println(e.source + " " + e.target);
        }
    }
}
