/*
 * This file is part of Gradoop.
 *
 * Gradoop is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Gradoop is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Gradoop.  If not, see <http://www.gnu.org/licenses/>.
 */

package server;

import graphtea.graph.graph.Edge;
import graphtea.graph.graph.GPoint;
import graphtea.graph.graph.GraphModel;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import java.util.List;
import java.util.Vector;

/**
 * Converts a logical graph or a read JSON into a cytoscape-conform JSON.
 */

public class CytoJSONBuilder {
  /**
   * Key for vertex, edge and graph id.
   */
  private static final String IDENTIFIER = "id";
  /**
   * Key for the type of the returned JSON, either graph or collection.
   */
  private static final String TYPE = "type";
  /**
   * Key for meta Json object.
   */
  private static final String META = "meta";
  /**
   * Key for data Json object.
   */
  private static final String DATA = "data";
  /**
   * Key for vertex, edge and graph label.
   */
  private static final String LABEL = "label";
  /**
   * Key for graph identifiers at vertices and edges.
   */
  private static final String GRAPHS = "graphs";
  /**
   * Key for properties of graphs, vertices and edges.
   */
  private static final String PROPERTIES = "properties";
  /**
   * Key for vertex identifiers at graphs.
   */
  private static final String VERTICES = "nodes";
  /**
   * Key for edge identifiers at graphs.
   */
  private static final String EDGES = "edges";
  /**
   * Key for edge source vertex id.
   */
  private static final String EDGE_SOURCE = "source";
  /**
   * Key for edge target vertex id.
   */
  private static final String EDGE_TARGET = "target";
  /**
   * Extra Data
   */
  private static final String EXTRA = "extra";

  /**
   * Takes a JSON containing a logical graph and converts it into a cytoscape-conform JSON.
   *
   * @param graph    graph JSON object
   * @param vertices vertices JSON array
   * @param edges    edges JSON array
   * @return cytoscape-conform JSON
   * @throws JSONException if JSON creation fails
   */
  static String getJSON(JSONObject graph, JSONArray vertices, JSONArray edges) throws
    JSONException {

    JSONObject returnedJSON = new JSONObject();

    returnedJSON.put(TYPE, "graph");


    JSONObject graphObject = new JSONObject();
    graphObject.put(IDENTIFIER, graph.getString("id"));
    graphObject.put(LABEL, graph.getJSONObject("meta").getString("label"));

    graphObject.put(PROPERTIES, graph.getJSONObject("data"));

    JSONArray graphArray = new JSONArray();
    graphArray.put(graphObject);

    returnedJSON.put(GRAPHS, graphArray);

    JSONArray vertexArray = new JSONArray();
    for (int i = 0; i < vertices.length(); i++) {
      JSONObject vertex = vertices.getJSONObject(i);

      JSONObject vertexData = new JSONObject();
      vertexData.put(IDENTIFIER, vertex.getString("id"));
      vertexData.put(LABEL, vertex.getJSONObject("meta").getString("label"));

      vertexData.put(PROPERTIES, vertex.getJSONObject("data"));

      JSONObject vertexObject = new JSONObject();
      vertexObject.put(DATA, vertexData);

      vertexArray.put(vertexObject);
    }
    returnedJSON.put(VERTICES, vertexArray);

    JSONArray edgeArray = new JSONArray();
    for (int i = 0; i < edges.length(); i++) {
      JSONObject edge = edges.getJSONObject(i);

      JSONObject edgeData = new JSONObject();
      edgeData.put(EDGE_SOURCE, edge.getString("source"));
      edgeData.put(EDGE_TARGET, edge.getString("target"));
      edgeData.put(IDENTIFIER, edge.getString("id"));
      edgeData.put(LABEL, edge.getJSONObject("meta").getString("label"));

      edgeData.put(PROPERTIES, edge.getJSONObject("data"));

      JSONObject edgeObject = new JSONObject();
      edgeObject.put(DATA, edgeData);

      edgeArray.put(edgeObject);
    }
    returnedJSON.put(EDGES, edgeArray);
    return returnedJSON.toString();
  }

  /**
   * Takes a logical graph and converts it into a cytoscape-conform JSON.
   *
   * @return a cytoscape-conform JSON
   * @throws JSONException if the creation of the JSON fails
   */
  static String getJSON(int[][] edgeList, double[][] positions) throws
          JSONException {
    int n = positions[0].length;
    JSONObject returnedJSON = new JSONObject();
    returnedJSON.put(TYPE, "graph");

    JSONArray graphArray = new JSONArray();
    JSONObject graphObject = new JSONObject();
    JSONObject graphProperties = new JSONObject();
    graphObject.put(IDENTIFIER, "root");
    graphObject.put(LABEL, "Tree");
    graphObject.put(PROPERTIES, graphProperties);
    graphArray.put(graphObject);
    returnedJSON.put(GRAPHS, graphArray);

    JSONArray vertexArray = new JSONArray();
    for(int i=0;i<n;i++) {
      JSONObject vertexObject = new JSONObject();
      JSONObject vertexData = new JSONObject();

      vertexData.put(IDENTIFIER,i);
      vertexData.put(LABEL,i+"");
      JSONObject pos = new JSONObject();
      JSONObject vertexProperties = new JSONObject();
      pos.put("x",positions[0][i]);
      pos.put("y",positions[1][i]);
      vertexData.put(PROPERTIES, vertexProperties);
      vertexObject.put(DATA, vertexData);
      vertexObject.put("position",pos);
      vertexArray.put(vertexObject);
    }
    returnedJSON.put(VERTICES, vertexArray);

    JSONArray edgeArray = new JSONArray();
    for (int i=0;i<edgeList[0].length;i++) {
      JSONObject edgeObject = new JSONObject();
      JSONObject edgeData = new JSONObject();
      edgeData.put(EDGE_SOURCE, edgeList[0][i]);
      edgeData.put(EDGE_TARGET, edgeList[1][i]);
      edgeData.put(IDENTIFIER, edgeList[0][i]+","+edgeList[1][i]);
      JSONObject edgeProperties = new JSONObject();
      edgeData.put(PROPERTIES, edgeProperties);
      edgeObject.put(DATA, edgeData);
      edgeArray.put(edgeObject);
    }
    returnedJSON.put(EDGES, edgeArray);

    return returnedJSON.toString();
  }

  static String getJSON(GraphModel g) throws
          JSONException {
    JSONObject returnedJSON = new JSONObject();
    returnedJSON.put(TYPE, "graph");

    JSONArray graphArray = new JSONArray();
    JSONObject graphObject = new JSONObject();
    JSONObject graphProperties = new JSONObject();
    graphObject.put(IDENTIFIER, "root");
    graphObject.put(LABEL, "Tree");
    graphObject.put(PROPERTIES, graphProperties);
    graphArray.put(graphObject);
    returnedJSON.put(GRAPHS, graphArray);

    JSONArray vertexArray = new JSONArray();
    for(int i=0;i<g.numOfVertices();i++) {
      JSONObject vertexObject = new JSONObject();
      JSONObject vertexData = new JSONObject();

      vertexData.put(IDENTIFIER,i);
      vertexData.put(LABEL,i+"");
      JSONObject pos = new JSONObject();
      JSONObject vertexProperties = new JSONObject();
      GPoint points =g.getVertex(i).getLocation();
      pos.put("x", points.getX());
      pos.put("y",points.getY());
      vertexData.put(PROPERTIES, vertexProperties);
      vertexObject.put(DATA, vertexData);
      vertexObject.put("position",pos);
      vertexArray.put(vertexObject);
    }
    returnedJSON.put(VERTICES, vertexArray);

    JSONArray edgeArray = new JSONArray();
    for(Edge e : g.getEdges()) {
      JSONObject edgeObject = new JSONObject();
      JSONObject edgeData = new JSONObject();
      edgeData.put(EDGE_SOURCE, e.source.getId());
      edgeData.put(EDGE_TARGET, e.target.getId());
      edgeData.put(IDENTIFIER, e.source.getId()+","+e.target.getId());
      JSONObject edgeProperties = new JSONObject();
      edgeData.put(PROPERTIES, edgeProperties);
      edgeObject.put(DATA, edgeData);
      edgeArray.put(edgeObject);
    }
    returnedJSON.put(EDGES, edgeArray);
    returnedJSON.put(EDGES, edgeArray);
    return returnedJSON.toString();
  }
}
