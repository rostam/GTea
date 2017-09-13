package server;

import graphtea.extensions.Centrality;
import graphtea.extensions.RandomTree;
import graphtea.graph.graph.*;
import graphtea.plugins.graphgenerator.core.extension.GraphGeneratorExtension;
import graphtea.plugins.reports.extension.GraphReportExtension;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.reflections.Reflections;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Handles REST requests to the server.
 */
@Path("")
public class RequestHandler {
    private static HashMap<String,Class> extensionNameToClass = new HashMap<>();
    //private static GraphModel currentGraph = new GraphModel();
    private static HashMap<String, GraphModel> sessionToGraph = new HashMap<>();


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
    @Path("/selectType/{info}")
    @Produces("application/json;charset=utf-8")
    public Response selectType(@PathParam("info") String info) {
        String[] infos = info.split("--");
        String type = infos[0];
        String sessionID = infos[1];
        handleSession(sessionID);
        try {
            if ((type.equals("directed")) != sessionToGraph.get(sessionID).isDirected()) {

                if (type.equals("directed")) {
                    sessionToGraph.get(sessionID).setDirected(true);
                } else if (type.equals("undirected")) {
                    sessionToGraph.get(sessionID).setDirected(false);
                }

            } else {
                //Only change if they differ
                System.out.println("type is not changed ... ");
                //String json = CytoJSONBuilder.getJSON(sessionToGraph.get(sessionID));
                return Response.noContent().header("Access-Control-Allow-Origin", "*").build();

            }
            String json = CytoJSONBuilder.getJSON(sessionToGraph.get(sessionID));
            return Response.ok(json).header("Access-Control-Allow-Origin", "*").build();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Response.ok("{}").header("Access-Control-Allow-Origin", "*").build();
    }


    @GET
    @Path("/condenseParallelEdges/{info}")
    public Response condenseParallelEdges(@PathParam("info") String info){
        System.out.println("condenseParallelEdges");
        String[] infos = info.split("--");
        String[] ids = infos[0].split("~~");
        String sessionID = infos[1];
        handleSession(sessionID);

        try {

            for(String id : ids) {
                int _source = Character.getNumericValue(id.charAt(0));
                int _target = Character.getNumericValue(id.charAt(2));

                Vertex vertex = sessionToGraph.get(sessionID).getVertex(_source);
                Vertex opposingVertex = sessionToGraph.get(sessionID).getVertex(_target);

                Edge parallelEdge = sessionToGraph.get(sessionID).getEdge(vertex, opposingVertex);
                System.out.println("Removing: " + parallelEdge);
                sessionToGraph.get(sessionID).removeEdge(parallelEdge);

            }

            String json = CytoJSONBuilder.getJSON(sessionToGraph.get(sessionID));
            return Response.ok(json).header("Access-Control-Allow-Origin", "*").build();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Response.ok("{}").header("Access-Control-Allow-Origin", "*").build();


    }

    @GET
    @Path("/removeEdge/{info}")
    public Response removeEdge(@PathParam("info") String info){
        System.out.println("removeEdge called");

        String[] infos = info.split("--");
        int sourceID = Integer.parseInt(infos[0]);
        int targetID = Integer.parseInt(infos[1]);
        String edgeType = infos[2];
        String sessionID = infos[3];
        handleSession(sessionID);

        Vertex _source = sessionToGraph.get(sessionID).getVertex(sourceID);
        Vertex _target = sessionToGraph.get(sessionID).getVertex(targetID);

        try {
            Edge _edge = sessionToGraph.get(sessionID).getEdge(_source, _target);
            sessionToGraph.get(sessionID).removeEdge(_edge);
            String json = CytoJSONBuilder.getJSON(sessionToGraph.get(sessionID));
            return Response.ok(json).header("Access-Control-Allow-Origin", "*").build();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Response.ok("{}").header("Access-Control-Allow-Origin", "*").build();

    }

    @GET
    @Path("/moveVertex/{info}")
    public Response moveVertex(@PathParam("info") String info){
        String[] infos = info.split("--");
        Double xPos = Double.parseDouble(infos[0]);
        Double yPos = Double.parseDouble(infos[1]);
        int vertexID = Integer.parseInt(infos[2]);
        String sessionID = infos[3];
        handleSession(sessionID);

        sessionToGraph.get(sessionID).getVertex(vertexID).setLocation(new GPoint(xPos, yPos));

        return Response.ok("{}").header("Access-Control-Allow-Origin", "*").build();
    }

    @GET
    @Path("/addVertex/{info}")
    @Produces("application/json;charset=utf-8")
    public Response addVertex(@PathParam("info") String info) {
        String[] infos = info.split("--");
        String vertexId = infos[0];
        Double xPos = Double.parseDouble(infos[1]);
        Double yPos = Double.parseDouble(infos[2]);
        String sessionID = infos[3];
        handleSession(sessionID);

        Vertex vertex = new Vertex();
        vertex.setLabel(vertexId);
        vertex.setLocation(new GPoint(xPos, yPos));
        try {
            sessionToGraph.get(sessionID).insertVertex(vertex);
            String json = CytoJSONBuilder.getJSON(sessionToGraph.get(sessionID));
            System.out.println("adding vertex");
            return Response.ok(json).header("Access-Control-Allow-Origin", "*").build();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Response.ok("{}").header("Access-Control-Allow-Origin", "*").build();
    }

    @GET
    @Path("/remove/{info}")
    public Response deleteVertex(@PathParam("info") String info){
        String[] infos = info.split("--");
        String vertexId = infos[0];
        String sessionID = infos[1];
        handleSession(sessionID);
        try {
            Vertex v = sessionToGraph.get(sessionID).getVertex(Integer.parseInt(vertexId));
            sessionToGraph.get(sessionID).deleteVertex(v);

            Iterable<Vertex> vi = sessionToGraph.get(sessionID).vertices();
            for (Vertex i : vi) {
                System.out.println(i);
            }

            String json = CytoJSONBuilder.getJSON(sessionToGraph.get(sessionID));
            return Response.ok(json).header("Access-Control-Allow-Origin", "*").build();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Response.ok("{}").header("Access-Control-Allow-Origin", "*").build();

    }

    @GET
    @Path("/clear/{info}")
    @Produces("application/json;charset=utf-8")
    public Response clear(@PathParam("info") String info) {
        String[] infos = info.split("--");
        String sessionID = infos[0];
        System.out.println("removing: " + sessionID);
        try {
            sessionToGraph.get(sessionID).clear();
            String json = CytoJSONBuilder.getJSON(sessionToGraph.get(sessionID));
            return Response.ok(json).header("Access-Control-Allow-Origin", "*").build();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Response.ok("{}").header("Access-Control-Allow-Origin", "*").build();
    }

    @GET
    @Path("/addEdge/{info}")
    @Produces("application/json;charset=utf-8")
    public Response addEdge(@PathParam("info") String info) {
        String[] infos = info.split("--");
        String sourceID = infos[0];
        String targetID = infos[1];

        String sessionID = infos[2];
        handleSession(sessionID);

        try {
            Vertex source = sessionToGraph.get(sessionID).getVertex(Integer.parseInt(sourceID));
            Vertex target = sessionToGraph.get(sessionID).getVertex(Integer.parseInt(targetID));
            Edge edge = new Edge(source, target);
            sessionToGraph.get(sessionID).insertEdge(edge);
            String json = CytoJSONBuilder.getJSON(sessionToGraph.get(sessionID));
            System.out.println("adding edge");
            return Response.ok(json).header("Access-Control-Allow-Origin", "*").build();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Response.ok("{}").header("Access-Control-Allow-Origin", "*").build();
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

        String sessionID = infos[4];
        handleSession(sessionID);

        for(String s : reportProps) {
            System.out.println(s);
        }

        try {
            if(sessionToGraph.get(sessionID).getVerticesCount() == 0)
                sessionToGraph.put(sessionID, generateGraph(props, graph));
                        //= generateGraph(props,graph);
            if(!report.contains("No ")) {
                GraphReportExtension gre = ((GraphReportExtension) extensionNameToClass.get(report).newInstance());
                Object o = gre.calculate(sessionToGraph.get(sessionID));
                JSONObject jsonObject = new JSONObject();
                if(o instanceof RenderTable) {
                    jsonObject.put("titles",((RenderTable)o).getTitles().toString());
                }
                jsonObject.put("results",o.toString());
                System.out.println(jsonObject);
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
    @Path("/draw/{info}")
    @Produces("application/json;charset=utf-8")
    public Response draw(@PathParam("info") String info) {
        String[] infos = info.split("--");
        String graph = infos[0];
        String report = infos[1];
        String[] props = infos[2].replaceAll(" ","").split(":");

        String type = infos[3];
        String sessionID = infos[4];
        handleSession(sessionID);

        try {
            sessionToGraph.put(sessionID, generateGraph(props, graph));

            if(type.equals("directed")){
                System.out.println("Directed!!");
                sessionToGraph.get(sessionID).setDirected(true);
            } else if (type.equals("undirected")){
                System.out.println("Undirected!!");
                sessionToGraph.get(sessionID).setDirected(false);
            }

            String json = CytoJSONBuilder.getJSON(sessionToGraph.get(sessionID));
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

    private void handleSession(String sessionID) {
        System.out.println(sessionID);
        if(!sessionToGraph.containsKey(sessionID)){
            sessionToGraph.put(sessionID, new GraphModel());
        }
    }

}