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
        int numOfVertices = g.numOfVertices();
        int mapToColors[] = new int[numOfVertices];
        boolean available[] = new boolean[numOfVertices];
        mapToColors[0] = 0;

        for (int u = 1; u < numOfVertices; u++)
            mapToColors[u] = -1;

        for (int cr = 0; cr < numOfVertices; cr++)
            available[cr] = false;

        for (int u = 1; u < numOfVertices; u++) {

            Iterator<Vertex> it = g.directNeighbors(g.getVertex(u)).iterator();
            while (it.hasNext()) {
                int i = it.next().getId();
                if (mapToColors[i] != -1)
                    available[mapToColors[i]] = true;
            }

            for (int color = 0; color < numOfVertices; color++)
                if (available[color] == false) {
                    mapToColors[u] = color;
                    break;
                }


            it = g.directNeighbors(g.getVertex(u)).iterator();
            while (it.hasNext()) {
                int i = it.next().getId();
                if (mapToColors[i] != -1)
                    available[mapToColors[i]] = false;
            }
        }

        int max = 0;
        for (int i : mapToColors) {
            if (max < i) max = i;
        }
        max++;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("num_of_colors", max);
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < mapToColors.length; i++) jsonArray.put(mapToColors[i]);
            jsonObject.put("colors", jsonArray);
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