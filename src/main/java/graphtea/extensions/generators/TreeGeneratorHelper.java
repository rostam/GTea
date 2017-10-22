package graphtea.extensions.generators;

import graphtea.graph.graph.Edge;
import graphtea.graph.graph.Vertex;

public class TreeGeneratorHelper {

    public static Vertex[] getVertices(int degree, int depth) {
        int n = (int) ((Math.pow(degree, depth + 1) - 1) / (degree - 1));
        Vertex[] ret = new Vertex[n];
        for (int i = 0; i < n; i++)
            ret[i] = new Vertex();
        return ret;
    }

    public static Edge[] getEdges(int n, int degree, int depth, Vertex[] v) {
        n = (int) ((Math.pow(degree, depth + 1) - 1) / (degree - 1));
        Edge[] ret = new Edge[n - 1];
        for (int i = 0; i < n - 1; i++) {
            ret[i] = new Edge(v[i + 1], v[i / degree]);
        }
        return ret;
    }
}
