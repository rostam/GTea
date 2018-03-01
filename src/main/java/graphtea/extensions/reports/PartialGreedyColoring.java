// GraphTea Project: http://github.com/graphtheorysoftware/GraphTea
// Copyright (C) 2012 Graph Theory Software Foundation: http://GraphTheorySoftware.com
// Copyright (C) 2008 Mathematical Science Department of Sharif University of Technology
// Distributed under the terms of the GNU General Public License (GPL): http://www.gnu.org/licenses/

package graphtea.extensions.reports;

import graphtea.extensions.reports.coloring.ColumnIntersectionGraph;
import graphtea.graph.graph.GraphModel;
import graphtea.graph.graph.Vertex;
import graphtea.plugins.reports.extension.GraphReportExtension;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.Vector;

/**
 * @author Azin Azadi
 */
public class PartialGreedyColoring implements GraphReportExtension {

    public String getName() {
        return "Partial Greedy Coloring";
    }

    public String getDescription() {
        return "The coloring of graph computed by greedy algorithm partially.";
    }

    public Object calculate(GraphModel g) {
        GraphModel sparsifiedG = ColumnIntersectionGraph.sparsify(g, 100);
        GreedyColoring gc = new GreedyColoring();
        JSONObject jsonObject = (JSONObject) gc.calculate(sparsifiedG);
        try {
            JSONArray jsonArray = (JSONArray) jsonObject.get("colors");
            for (int i = 0; i < jsonArray.length(); i++)
                sparsifiedG.getVertex(i).setColor((Integer) jsonArray.get(i));
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        for(Edge e : g.getEdges()) {
//            if(!sparsifiedG.isEdge(sparsifiedG.getVertex(e.source.getId()),
//                    sparsifiedG.getVertex(e.target.getId()))) {
//                if(sparsifiedG.getVertex(e.source.getId()).getColor() ==
//                        sparsifiedG.getVertex(e.target.getId()).getColor()) {
//                    Vertex v =  sparsifiedG.getVertex(e.source.getId());
//                    Vector<Vertex> ns = g.directNeighbors(g.getVertex(v.getId()));
//                    Vector<Integer> cs = new Vector<>();
//                    cs.add(sparsifiedG.getVertex(e.target.getId()).getColor());
//                    for(Vertex n : ns) cs.add(n.getColor());
//                    for(int i=0;i < 100;i++) {
//                        if(!cs.contains(i)) {
//                            sparsifiedG.getVertex(e.source.getId()).setColor(i);
//                            break;
//                        }
//                    }
//                }
//            }
//        }

        for (Vertex v : sparsifiedG) {
            Vector<Integer> ns = g.directNeighborsInts(g.getVertex(v.getId()));
            for (int n : ns) {
                if (sparsifiedG.getVertex(n).getColor() == v.getColor()) {
                    Vector<Integer> colors = new Vector<>();
                    for (int nn : ns) {
                        colors.add(sparsifiedG.getVertex(nn).getColor());
                    }
                    for (int i = 0; i < 100; i++)
                        if (!colors.contains(i)) {
                            v.setColor(i);
                            break;
                        }
                    break;
                }
            }
        }

        int max = 0;
        for (Vertex v : sparsifiedG.vertices()) {
            if (v.getColor() > max) max = v.getColor();
        }

        JSONObject ret = new JSONObject();
        try {
            ret.put("num_of_colors", max + 1);
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < sparsifiedG.numOfVertices(); i++)
                jsonArray.put(sparsifiedG.getVertex(i).getColor());
            ret.put("colors", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ret;
    }

    @Override
    public String getCategory() {
        return "Coloring";
    }
}