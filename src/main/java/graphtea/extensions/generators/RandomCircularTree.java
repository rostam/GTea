// GraphTea Project: http://github.com/graphtheorysoftware/GraphTea
// Copyright (C) 2012 Graph Theory Software Foundation: http://GraphTheorySoftware.com
// Copyright (C) 2008 Mathematical Science Department of Sharif University of Technology
// Distributed under the terms of the GNU General Public License (GPL): http://www.gnu.org/licenses/
package graphtea.extensions.generators;

import graphtea.extensions.RandomTree;
import graphtea.graph.graph.Edge;
import graphtea.graph.graph.GraphModel;
import graphtea.graph.graph.Vertex;
import graphtea.platform.lang.ArrayX;
import graphtea.platform.parameter.Parameter;
import graphtea.platform.parameter.Parametrizable;
import graphtea.plugins.graphgenerator.GraphGenerator;
import graphtea.plugins.graphgenerator.core.PositionGenerators;
import graphtea.plugins.graphgenerator.core.SimpleGeneratorInterface;
import graphtea.plugins.graphgenerator.core.extension.GraphGeneratorExtension;

import java.awt.*;

/**
 * @author M. Ali Rostami
 */
public class RandomCircularTree implements GraphGeneratorExtension, Parametrizable, SimpleGeneratorInterface {
    @Parameter(name = "n")
    public static Integer n = 3;
    GraphModel g;
    RandomTree rt;
    int[][] edgeList;

    public void setWorkingGraph(GraphModel g) {
        this.g = g;
    }


    public String getName() {
        return "Circular Tree Generator";
    }

    public String getDescription() {
        return "Generates a complete tree";
    }

    Vertex[] v;

    public Vertex[] getVertices() {
        rt = new RandomTree(n);
        Vertex[] ret = new Vertex[n];
        for (int i = 0; i < n; i++)
            ret[i] = new Vertex();
        v = ret;
        return ret;
    }

    public Edge[] getEdges() {
        Edge[] ret = new Edge[edgeList[0].length];
        for (int i = 0; i < edgeList[0].length; i++) {
            ret[i] = new Edge(v[edgeList[0][i]], v[edgeList[1][i]]);
        }
        return ret;
    }

    public Point[] getVertexPositions() {
        edgeList = rt.getEdgeList();
//        SyncBurst graph = new SyncBurst(edgeList, n, false, 4, false);
//        double [][] pos = graph.Cir_Force_Free();
        Point[] ps = new Point[n];
//        System.out.printf("");
//        System.out.printf("");
        for(int i=0;i<n;i++) {
            //ps[i] = new Point((int)pos[0][i],(int)pos[1][i]);
            ps[i] = new Point(0,0);
        }
        return ps;
    }

    public GraphModel generateGraph() {
        return GraphGenerator.getGraph(false, this);
    }

    @Override
    public String getCategory() {
        return "Trees";
    }

    @Override
    public String checkParameters() {
        return null;
    }
}
