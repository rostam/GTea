// GraphTea Project: http://github.com/graphtheorysoftware/GraphTea
// Copyright (C) 2012 Graph Theory Software Foundation: http://GraphTheorySoftware.com
// Copyright (C) 2008 Mathematical Science Department of Sharif University of Technology
// Distributed under the terms of the GNU General Public License (GPL): http://www.gnu.org/licenses/

package graphtea.extensions.reports.basicreports;

import Jama.Matrix;
import graphtea.graph.graph.Edge;
import graphtea.graph.graph.GraphModel;
import graphtea.platform.lang.CommandAttitude;
import graphtea.plugins.reports.extension.GraphReportExtension;

import java.util.ArrayList;

/**
 * @author Mohammad Ali Rostami
 */

@CommandAttitude(name = "eig_values", abbreviation = "_evs")
public class RhoAdjacencyMatrix implements GraphReportExtension {
    public Object calculate(GraphModel g) {
        ArrayList<String> res = new ArrayList<>();
        res.add("Adjacency Matrix");
        Matrix A = g.getWeightedAdjacencyMatrix();
        Matrix m = new Matrix(g.getVerticesCount(),g.getVerticesCount());
        for(int i=0;i < g.getVerticesCount();i++) m.set(i,i,2);
        for(Edge e : g.getEdges()) {
            
        }
       return res;
    }



    public String getName() {
        return "Spectrum of Adjacency";
    }

    public String getDescription() {
        return "Adjacency Matrix";
    }

    @Override
    public String getCategory() {
        return "Spectral";
    }
}
