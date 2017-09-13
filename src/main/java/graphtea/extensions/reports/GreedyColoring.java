// GraphTea Project: http://github.com/graphtheorysoftware/GraphTea
// Copyright (C) 2012 Graph Theory Software Foundation: http://GraphTheorySoftware.com
// Copyright (C) 2008 Mathematical Science Department of Sharif University of Technology
// Distributed under the terms of the GNU General Public License (GPL): http://www.gnu.org/licenses/

package graphtea.extensions.reports;

import graphtea.graph.graph.GraphModel;
import graphtea.graph.graph.RenderTable;
import graphtea.graph.graph.Vertex;
import graphtea.plugins.reports.extension.GraphReportExtension;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.Iterator;
import java.util.Vector;

/**
 * @author Azin Azadi
 */
public class GreedyColoring implements GraphReportExtension {

    public String getName() {
        return "Greedy Coloring";
    }

    public String getDescription() {
        return "The coloring of graph computed by greedy algorithm";
    }

    public Object calculate(GraphModel g) {
        int V = g.numOfVertices();
        int result[] = new int[V];
        result[0]  = 0;

        for (int u = 1; u < V; u++)
            result[u] = -1;

        boolean available[] = new boolean[V];
        for (int cr = 0; cr < V; cr++)
            available[cr] = false;

        for (int u = 1; u < V; u++) {

            Iterator<Vertex> it = g.directNeighbors(g.getVertex(u)).iterator() ;
            while (it.hasNext())
            {
                int i = it.next().getId();
                if (result[i] != -1)
                    available[result[i]] = true;
            }

            int cr;
            for (cr = 0; cr < V; cr++)
                if (available[cr] == false)
                    break;

            result[u] = cr;

            it = g.directNeighbors(g.getVertex(u)).iterator() ;
            while (it.hasNext())
            {
                int i = it.next().getId();
                if (result[i] != -1)
                    available[result[i]] = false;
            }
        }

        int max = 0;
        for(int i : result) {
            if(max < i) max = i;
        }
        max++;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("num_of_colors",max);
            JSONArray jsonArray = new JSONArray();
            for(int i =0; i < result.length;i++) jsonArray.put(result[i]);
            jsonObject.put("colors",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    @Override
    public String getCategory() {
        return "Coloring";
    }
}