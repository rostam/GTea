package server;

import graphtea.extensions.Centrality;
import graphtea.extensions.G6Format;
import graphtea.extensions.RandomTree;
import graphtea.graph.graph.Edge;
import graphtea.graph.graph.GraphModel;
import graphtea.graph.graph.RenderTable;
import graphtea.graph.graph.Vertex;
import graphtea.plugins.graphgenerator.core.extension.GraphGeneratorExtension;
import graphtea.plugins.reports.extension.GraphReportExtension;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.reflections.Reflections;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Handles REST requests to the server.
 */
@Path("")
public class RequestHandler {
    private static HashMap<String,Class> extensionNameToClass = new HashMap<>();
    private static GraphModel currentGraph = new GraphModel();

    public GraphModel generateGraph(String[] props, String graph) {
        try {
            GraphGeneratorExtension gge =
                    ((GraphGeneratorExtension) extensionNameToClass.get(graph).newInstance());
            String[] propsNameSplitted = props[0].split(",");
            String[] propsValueSplitted = props[1].split(",");
            for(int i=0;i < propsNameSplitted.length;i++) {
                try {
                    if(propsValueSplitted.equals("true") || propsValueSplitted.equals("false")) {
                        gge.getClass().getField(propsNameSplitted[i])
                                .set(gge, Boolean.parseBoolean(propsValueSplitted[i]));
                    } else {
                        gge.getClass().getField(propsNameSplitted[i])
                                .set(gge, Integer.parseInt(propsValueSplitted[i]));
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            return gge.generateGraph();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return new GraphModel();
    }

    @GET
    @Path("/report/{info}")
    @Produces("application/json;charset=utf-8")
    public Response report(@PathParam("info") String info) {
        String[] infos = info.split("--");
        String graph = infos[0];
        String report = infos[1];
        String[] props = infos[2].replaceAll(" ","").split(":");
        String[] reportProps = infos[3].replaceAll(" ","").split(":");
        try {
            if(!report.contains("No ")) {
                GraphReportExtension gre = ((GraphReportExtension) extensionNameToClass.get(report).newInstance());
                Object o = gre.calculate(currentGraph);
                if(o instanceof JSONObject) {
                    return Response.ok(o.toString()).header("Access-Control-Allow-Origin", "*").build();
                }
                JSONObject jsonObject = new JSONObject();
                if(o instanceof RenderTable) {
                    jsonObject.put("titles",((RenderTable)o).getTitles().toString());
                }
                jsonObject.put("results",o.toString());
                return Response.ok(jsonObject.toString()).header("Access-Control-Allow-Origin", "*").build();
            } else {
                return Response.ok("").header("Access-Control-Allow-Origin", "*").build();
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return Response.ok("{}").header("Access-Control-Allow-Origin", "*").build();
    }

    @GET
    @Path("/g6/{info}")
    @Produces("application/json;charset=utf-8")
    public Response g6(@PathParam("info") String info) {
        GraphModel g = G6Format.stringToGraphModel(info);
        try {
            String json = CytoJSONBuilder.getJSON(g);
            return Response.ok(json).header("Access-Control-Allow-Origin", "*").build();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Response.ok("").header("Access-Control-Allow-Origin", "*").build();
    }

    @GET
    @Path("/el/{info}")
    @Produces("application/json;charset=utf-8")
    public Response edgeList(@PathParam("info") String info) {
        currentGraph = getGraphFromEdgeList(info);
        try {
            String json = CytoJSONBuilder.getJSON(currentGraph);
            return Response.ok(json).header("Access-Control-Allow-Origin", "*").build();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Response.ok("").header("Access-Control-Allow-Origin", "*").build();
    }

    @GET
    @Path("/adj/{info}")
    @Produces("application/json;charset=utf-8")
    public Response adjMat(@PathParam("info") String info) {
        currentGraph = new GraphModel();
        String[] rows = info.split("-");
        for(int i=0;i<rows.length;i++) currentGraph.addVertex(new Vertex());
        for(int i=0;i<rows.length;i++) {
            String tmp[] = rows[i].split(",");
            for(int j=0;j<tmp.length;j++) {
                if(tmp[j].equals("1")) {
                    currentGraph.addEdge(new Edge(currentGraph.getVertex(i),currentGraph.getVertex(j)));
                }
            }
        }
        try {
            String json = CytoJSONBuilder.getJSON(currentGraph);
            return Response.ok(json).header("Access-Control-Allow-Origin", "*").build();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Response.ok("").header("Access-Control-Allow-Origin", "*").build();
    }

    private GraphModel getGraphFromEdgeList(String info) {
        String[] rows = info.split("-");
        Vector<String> vs = new Vector<>();
        for(String row : rows) {
            String tmp[] = row.split(",");
            String v1 = tmp[0].trim();
            String v2 = tmp[1].trim();
            if(!vs.contains(v1)) vs.add(v1);
            if(!vs.contains(v2)) vs.add(v2);
        }
        HashMap<String,Vertex> labelVertex = new HashMap<>();
        currentGraph = new GraphModel();
        for(String v : vs) {
            Vertex vertex = new Vertex();
            vertex.setLabel(v);
            labelVertex.put(v,vertex);
            currentGraph.addVertex(vertex);
        }
        for(String row : rows) {
            String tmp[] = row.split(",");
            String v1 = tmp[0].trim();
            String v2 = tmp[1].trim();
            Edge e = new Edge(labelVertex.get(v1),labelVertex.get(v2));
            currentGraph.addEdge(e);
        }
        return currentGraph;
    }

    @GET
    @Path("/draw/{info}")
    @Produces("application/json;charset=utf-8")
    public Response draw(@PathParam("info") String info) {
        String[] infos = info.split("--");
        String graph = infos[0];
        String report = infos[1];
        String[] props = infos[2].replaceAll(" ","").split(":");
        try {
            currentGraph = generateGraph(props,graph);
            String json = CytoJSONBuilder.getJSON(currentGraph);
            return Response.ok(json).header("Access-Control-Allow-Origin", "*").build();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return Response.ok("{}").header("Access-Control-Allow-Origin", "*").build();
    }

    /**
     * Creates a list of all available databases from the file structure under the /data/ folder.
     *
     * @return List of folders (datbases) under the /data/ folder.
     */
    @GET
    @Path("/graphs")
    @Produces("application/json;charset=utf-8")
    public Response getGraphs() {
        JSONObject jsonObject = new JSONObject();
        Reflections reflectionsGenerators = new Reflections("graphtea.extensions.generators");
        Set<Class<? extends GraphGeneratorExtension>> subTypes = reflectionsGenerators.getSubTypesOf(GraphGeneratorExtension.class);
        Vector<String> graphs = new Vector<>();
        JSONArray jsonArray = new JSONArray();
        for(Class<? extends GraphGeneratorExtension> c : subTypes) {
            JSONObject jo = new JSONObject();
            String classSimpleName = c.getSimpleName();
            try {
                jo.put("name",classSimpleName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            graphs.add(classSimpleName);
            extensionNameToClass.put(classSimpleName,c);
            Field[] fs = c.getFields();
            JSONArray properties = new JSONArray();
            for(Field f : fs)
                properties.put(f.getName()+":"+f.getType().getSimpleName());
            try {
                jo.put("properties",properties);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(jo);
        }
        try {
            jsonObject.put("graphs",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Reflections reflectionsReports = new Reflections("graphtea.extensions.reports");
        Set<Class<? extends GraphReportExtension>> subTypesReport = reflectionsReports.getSubTypesOf(GraphReportExtension.class);
        Vector<String> reports = new Vector<>();
        JSONArray jsonArray2 = new JSONArray();
        for(Class<? extends GraphReportExtension> c : subTypesReport) {
            JSONObject jo = new JSONObject();
            String classSimpleName = c.getSimpleName();
            try {
                jo.put("name",classSimpleName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Field[] fs = c.getFields();
            JSONArray properties = new JSONArray();
            for(Field f : fs)
                if(f.getAnnotations().length!=0)
                    properties.put(f.getName()+":"+f.getType().getSimpleName());
            try {
                jo.put("properties",properties);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray2.put(jo);
            reports.add(classSimpleName);
            extensionNameToClass.put(classSimpleName,c);
        }
//        Collections.sort(reports);
//        for (String s : reports) {
//            jsonArray2.put(s);
//        }
        try {
            jsonObject.put("reports",jsonArray2);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return Response.ok(jsonObject.toString()).header("Access-Control-Allow-Origin", "*").build();
    }

    /**
     * Get the complete graph in cytoscape-conform form.
     *
     * @param databaseName name of the database
     * @return Response containing the graph as a JSON, in cytoscape conform format.
     * @throws JSONException if JSON creation fails
     * @throws IOException   if reading fails
     */
    @POST
    @Path("/graph/{databaseName}")
    @Produces("application/json;charset=utf-8")
    public Response getGraph(@PathParam("databaseName") String databaseName) throws Exception {
        String[] splitted = databaseName.split(",");
        String dbname = splitted[0].replace("-", "/");//databaseName.substring(0,databaseName.indexOf(","));
        String sampling = splitted[1];//databaseName.substring(databaseName.indexOf(",") + 1, databaseName.lastIndexOf(","));
        float samplingThreshold = Float.parseFloat(splitted[2]);
        String layout = splitted[3];
        int maxIter = Integer.parseInt(splitted[4]);
        String[] clusterSize = splitted[5].split("-");
        int min = Integer.parseInt(clusterSize[0]);
        int max = Integer.parseInt(clusterSize[1]);
        String clusterId = splitted[6];
//
//        String path = RequestHandler.class.getResource("/data/" + dbname).getPath();
//        LogicalGraph graph = readLogicalGraph(path);
//
//        Vector<Pair<String, String>> hm = new Vector<>();
//        graph = samplings.preprocess(graph, sampling, samplingThreshold, hm, min, max, clusterId, ENV);
//
//        List<GraphHead> ghead = new ArrayList<>();
//        List<Vertex> lv = new ArrayList<>();
//        List<Edge> le = new ArrayList<>();
//
//        graph.getGraphHead().output(new LocalCollectionOutputFormat<>(ghead));
//        graph.getVertices().output(new LocalCollectionOutputFormat<>(lv));
//        graph.getEdges().output(new LocalCollectionOutputFormat<>(le));
//        ENV.execute();
//
//        NeighborhoodGraph ng = new NeighborhoodGraph(lv, le);
//        //lv = ng.filterDisconnectedVertices();
//        hm.add(new Pair<>("Vertex count after sampling", lv.size() + ""));
//        hm.add(new Pair<>("Edge count after sampling", le.size() + ""));
//        if (layout.equals("FR layout on server")) ng.forceDirected2(lv, le, maxIter);
//        else if (layout.equals("Compound layout on server")) ng.forceDirectedCluster(lv, le, maxIter);
//        String json = CytoJSONBuilder.getJSON(ghead.get(0), lv, le, hm);
        RandomTree rt = new RandomTree(maxIter);
        int[][] edgeList = rt.getEdgeList();
//        SyncBurst graph = new SyncBurst(edgeList, maxIter, false, 4, false);
//        double [][] pos = graph.Cir_Force_Free();
        Centrality p = new Centrality(edgeList);  // for degree of centrality  Note: not necessary for drawing
        double [] res = p.Betweenness_Centrality(maxIter);
        Vector<Double> v = new Vector<>();
        for(double d : res) {
            v.add(d);
        }
        int highBCVertex = v.indexOf(Collections.max(v));
        int[][] newEdgeList = new int[2][edgeList[0].length+1];
        for(int i=0;i<edgeList[0].length;i++) {
            newEdgeList[0][i] = edgeList[0][i];
            newEdgeList[1][i] = edgeList[1][i];
        }
        newEdgeList[0][edgeList[0].length] = maxIter;
        newEdgeList[1][edgeList[0].length] = highBCVertex;

//        double [][] newPos = new double[2][pos[0].length+1];
//        for(int i=0;i<pos[0].length;i++) {
//            newPos[0][i] = pos[0][i];
//            newPos[1][i] = pos[1][i];
//        }
//        newPos[0][pos[0].length] = pos[0][highBCVertex] + 1;
//        newPos[1][pos[0].length] = pos[1][highBCVertex] + 1;

        String json = "";//CytoJSONBuilder.getJSON(newEdgeList,newPos);
        return Response.ok(json).header("Access-Control-Allow-Origin", "*").build();
    }

}