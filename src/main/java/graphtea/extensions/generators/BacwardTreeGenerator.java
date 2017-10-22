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
import graphtea.plugins.graphgenerator.core.PositionGenerators;
import graphtea.plugins.graphgenerator.core.SimpleGeneratorInterface;
import graphtea.plugins.graphgenerator.core.extension.GraphGeneratorExtension;

import java.awt.*;

/**
 * @author azin azadi, Hoshmand Hasannia
 */
//@CommandAttitude(name = "generate_tree" , abbreviation = "_g_t"
//        ,description = "generate a tree with depth and degree")
public class BacwardTreeGenerator implements GraphGeneratorExtension, Parametrizable, SimpleGeneratorInterface {
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
        return "Complete Tree with the backward positioning";
    }

    public String getDescription() {
        return "Generates a complete tree  with the backward positioning";
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

    public Point[] getVertexPositionsBackward() {
        Point[] ret = new Point[n];
        ret[0] = new Point(20000 / 2, 20000 / 2);
        int last = 1;
        int ww = 20000;
        int hh = 20000;
        int rad0 = Math.min(ww, hh) / 2;
        for (int i = 0; i < n - Math.pow(degree, depth); i++) {
            int h = (int) ((Math.log((i + 1) * (degree - 1))) / Math.log(degree));
            int rad = (i == 0 ? rad0 : (int) (rad0 / Math.pow(2.5, h)));
            Point _p[] = PositionGenerators.circle(rad, ret[i].x, ret[i].y, degree);
            System.arraycopy(_p, 0, ret, last, degree);
            last += degree;
        }
        return ret;
    }

    public Point[] getVertexPositions() {
            return getVertexPositionsBackward();
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
        BacwardTreeGenerator.depth = depth;
        BacwardTreeGenerator.degree = degree;
        return GraphGenerator.getGraph(false, new BacwardTreeGenerator());
    }

    @Override
    public String getCategory() {
        return "Trees";
    }
}
