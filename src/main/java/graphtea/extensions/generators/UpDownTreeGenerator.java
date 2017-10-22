// GraphTea Project: http://github.com/graphtheorysoftware/GraphTea
// Copyright (C) 2012 Graph Theory Software Foundation: http://GraphTheorySoftware.com
// Copyright (C) 2008 Mathematical Science Department of Sharif University of Technology
// Distributed under the terms of the GNU General Public License (GPL): http://www.gnu.org/licenses/
package graphtea.extensions.generators;

import graphtea.graph.graph.Edge;
import graphtea.graph.graph.GraphModel;
import graphtea.graph.graph.Vertex;
import graphtea.platform.parameter.Parameter;
import graphtea.platform.parameter.Parametrizable;
import graphtea.plugins.graphgenerator.GraphGenerator;
import graphtea.plugins.graphgenerator.core.SimpleGeneratorInterface;
import graphtea.plugins.graphgenerator.core.extension.GraphGeneratorExtension;

import java.awt.*;

/**
 * @author azin azadi, Hoshmand Hasannia
 */
//@CommandAttitude(name = "generate_tree" , abbreviation = "_g_t"
//        ,description = "generate a tree with depth and degree")
public class UpDownTreeGenerator implements GraphGeneratorExtension, Parametrizable, SimpleGeneratorInterface {
    @Parameter(name = "Height")
    public static Integer depth = 3;
    @Parameter(name = "Degree")
    public static Integer degree = 3;

    int n;
    GraphModel g;

    public void setWorkingGraph(GraphModel g) {
        this.g = g;
    }

/*    public NotifiableAttributeSet getParameters() {
        PortableNotifiableAttributeSetImpl a=new PortableNotifiableAttributeSetImpl();
        a.put ("Degree",3);
        a.put ("Height",3);
        a.put("Positioning Method",new ArrayX<String>("Circular", "Backward" , "UpDown"));
        return a;
    }

    public void setParameters(NotifiableAttributeSet parameters) {
        d = Integer.parseInt(""+parameters.getAttributes().get("Degree"));
        h = Integer.parseInt(""+parameters.getAttributes().get("Height"));
        positioning = ((ArrayX)parameters.getAttributes().get("Positioning Method")).getValue().toString();
        n = (int) ((Math.pow(d,h+1)-1)/(d-1));
    }*/

    public String getName() {
        return "Complete Tree with the up-down positioning";
    }

    public String getDescription() {
        return "Generates a complete tree the up-down positioning";
    }

    Vertex[] v;

    public Vertex[] getVertices() {
        v = TreeGeneratorHelper.getVertices(degree,depth);
        n = v.length;
        return v;
    }

    public Edge[] getEdges() {
        return TreeGeneratorHelper.getEdges(n,degree,depth,v);
    }

    public Point[] getVertexPositions() {
        return getVertexPositionUpdown();
    }

    private Point[] getVertexPositionUpdown() {
        Point[] ret = new Point[n];
        double vwidth = 200;
        double vheight = 200;
        double yratio = vheight / depth;
        for (int i = depth; i >= 0; i--) {
            int vertexnInRow = (int) Math.pow(degree, i);
            double xratio = vwidth / (vertexnInRow + 1);
            int firstInRow = 0;
            for (int j = 0; j <= i - 1; j++) {
                firstInRow += Math.pow(degree, j);
            }
            firstInRow++;

            for (int j = 1; j <= vertexnInRow; j++) {
                double x = j * xratio;
                ret[firstInRow + j - 2] = new Point((int) x, (int) yratio * i);
            }
        }
        return ret;
    }

    public String checkParameters() {
    	if(depth<0 || degree<0)return" Both depth & degree must be positive!";
    	else
    		return null;
    }

    public GraphModel generateGraph() {
        return GraphGenerator.getGraph(false, this);
    }

    /**
     * generates a Complete Tree with given parameters
     */
    public static GraphModel generateTree(int depth, int degree) {
        UpDownTreeGenerator.depth = depth;
        UpDownTreeGenerator.degree = degree;
        return GraphGenerator.getGraph(false, new UpDownTreeGenerator());
    }

    @Override
    public String getCategory() {
        return "Trees";
    }
}
