package server;

import graphtea.extensions.Centrality;
import graphtea.extensions.G6Format;
import graphtea.extensions.RandomTree;
import graphtea.extensions.io.LatexWriter;
import graphtea.extensions.io.SaveGraph;
import graphtea.graph.graph.*;
import graphtea.platform.extension.Extension;
import graphtea.plugins.graphgenerator.core.extension.GraphGeneratorExtension;
import graphtea.plugins.main.extension.GraphActionExtension;
import graphtea.plugins.main.saveload.core.GraphIOException;
import graphtea.plugins.reports.extension.GraphReportExtension;
import graphtea.plugins.reports.ui.ReportsUI;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.print.attribute.standard.Media;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;

/**
 * Handles REST requests to the server.
 */
@Path("")
public class RequestHandler {

    public GraphModel generateGraph(String[] props, String graph) {
        GraphGeneratorExtension gge = Helpers.getInstanceOfExtension(graph);
        String[] propsNameSplitted = props[0].split(",");
        String[] propsValueSplitted = props[1].split(",");
        PropsTypeValueFill.fill(gge, propsNameSplitted, propsValueSplitted);
        return gge.generateGraph();
    }

    /**
     * Switches the type of graph to directed or undirected,
     * @param info string containing the vertex ID and the session ID string name
     * @return Response containing the graph as a JSON, in cytoscape conform format,
     *  or noContent if type shouldn't be changed
     * @throws JSONException if JSON creation fails
     * */
    @GET
    @Path("/selectType/{info}")
    @Produces("application/json;charset=utf-8")
    public Response selectType(@PathParam("info") String info) {
        String[] infos = info.split("--");
        String type = infos[0];
        String sessionID = infos[1];
        handleSession(sessionID);
        try {
            if ((type.equals("directed")) != Helpers.sessionToGraph.get(sessionID).isDirected()) {
                if (type.equals("directed")) {
                    Helpers.sessionToGraph.get(sessionID).setDirected(true);
                } else if (type.equals("undirected")) {
                    Helpers.sessionToGraph.get(sessionID).setDirected(false);
                }

            } else {
                // Only change if types differ
                return Response.noContent().header("Access-Control-Allow-Origin", "*").build();

            }
            String json = CytoJSONBuilder.getJSON(Helpers.sessionToGraph.get(sessionID));
            return Response.ok(json).header("Access-Control-Allow-Origin", "*").build();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Response.ok("{}").header("Access-Control-Allow-Origin", "*").build();
    }

    /**
     *  Condenses all parallel edges into a singular edge,
     *  ids contains all of the edges that have a parallel edge.
     *  Those edges are then deleted and the remaining edges become undirected.
     *
     * @param info string containing the vertex ID and the session ID string name
     * @return Response containing the graph as a JSON, in cytoscape conform format.
     * @throws JSONException if JSON creation fails
     */
    @GET
    @Path("/condenseParallelEdges/{info}")
    public Response condenseParallelEdges(@PathParam("info") String info){
        String[] infos = info.split("--");
        String[] ids = infos[0].split("~~");
        String sessionID = infos[1];
        handleSession(sessionID);

        try {
            for(String id : ids) {
                String[] sId = id.split(",");
                int _source = Integer.parseInt(sId[0]);
                int _target = Integer.parseInt(sId[1]);

                Vertex vertex = Helpers.sessionToGraph.get(sessionID).getVertex(_source);
                Vertex opposingVertex = Helpers.sessionToGraph.get(sessionID).getVertex(_target);

                Edge parallelEdge = Helpers.sessionToGraph.get(sessionID).getEdge(vertex, opposingVertex);
                Helpers.sessionToGraph.get(sessionID).removeEdge(parallelEdge);
            }

            String json = CytoJSONBuilder.getJSON(Helpers.sessionToGraph.get(sessionID));
            return Response.ok(json).header("Access-Control-Allow-Origin", "*").build();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Response.ok("{}").header("Access-Control-Allow-Origin", "*").build();


    }

    /**
     * Deletes a particular edge by the source vertex ID and target vertex ID
     *
     * @param info string containing the source vertex ID, target vertex ID and the session ID string name
     * @return Response containing the graph as a JSON, in cytoscape conform format.
     * @throws JSONException if JSON creation fails
     */
    @GET
    @Path("/removeEdge/{info}")
    public Response removeEdge(@PathParam("info") String info){
        String[] infos = info.split("--");
        int sourceID = Integer.parseInt(infos[0]);
        int targetID = Integer.parseInt(infos[1]);
        String edgeType = infos[2];
        String sessionID = infos[3];
        handleSession(sessionID);

        Vertex _source = Helpers.sessionToGraph.get(sessionID).getVertex(sourceID);
        Vertex _target = Helpers.sessionToGraph.get(sessionID).getVertex(targetID);

        try {
            Edge _edge = Helpers.sessionToGraph.get(sessionID).getEdge(_source, _target);
            Helpers.sessionToGraph.get(sessionID).removeEdge(_edge);
            String json = CytoJSONBuilder.getJSON(Helpers.sessionToGraph.get(sessionID));
            return Response.ok(json).header("Access-Control-Allow-Origin", "*").build();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Response.ok("{}").header("Access-Control-Allow-Origin", "*").build();

    }

    /**
     * Adds a vertex into the graph
     *
     * @param info string containing the ID of the new vertex, position on the x-axis, y-axis and the session ID string name
     * @return Response containing the graph as a JSON, in cytoscape conform format.
     * @throws JSONException if JSON creation fails
     */
    @GET
    @Path("/addVertex/{info}")
    @Produces("application/json;charset=utf-8")
    public Response addVertex(@PathParam("info") String info) {
        String[] infos = info.split("--");
        String vertexId = infos[0];
        Double xPos = Double.parseDouble(infos[1]);
        Double yPos = Double.parseDouble(infos[2]);
        String type = infos[3];
        String sessionID = infos[4];

        if(handleSession(sessionID)) {
            // New session was created
            if(type.equals("directed")){
                Helpers.sessionToGraph.get(sessionID).setDirected(true);
            } else if (type.equals("undirected")){
                Helpers.sessionToGraph.get(sessionID).setDirected(false);
            }
        }

        Vertex vertex = new Vertex();
        vertex.setLabel(vertexId);
        vertex.setLocation(new GPoint(xPos, yPos));
        try {
            Helpers.sessionToGraph.get(sessionID).insertVertex(vertex);
            String json = CytoJSONBuilder.getJSON(Helpers.sessionToGraph.get(sessionID));
            return Response.ok(json).header("Access-Control-Allow-Origin", "*").build();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Response.ok("{}").header("Access-Control-Allow-Origin", "*").build();
    }

    /**
     * Deletes a particular vertex (and subsequently all of its edges) by ID
     *
     * @param info string containing the vertex ID and the session ID string name
     * @return Response containing the graph as a JSON, in cytoscape conform format.
     * @throws JSONException if JSON creation fails
     */
    @GET
    @Path("/remove/{info}")
    public Response deleteVertex(@PathParam("info") String info) {
        String[] infos = info.split("--");
        String vertexId = infos[0];
        String sessionID = infos[1];
        handleSession(sessionID);
        try {
            Vertex v = Helpers.sessionToGraph.get(sessionID).getVertex(Integer.parseInt(vertexId));
            Helpers.sessionToGraph.get(sessionID).deleteVertex(v);
            Iterable<Vertex> vi = Helpers.sessionToGraph.get(sessionID).vertices();
            String json = CytoJSONBuilder.getJSON(Helpers.sessionToGraph.get(sessionID));
            return Response.ok(json).header("Access-Control-Allow-Origin", "*").build();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Response.ok("{}").header("Access-Control-Allow-Origin", "*").build();

    }

    /**
     * Deletes all edges and vertices of the current graph
     *
     * @param info string containing the session ID string name
     * @return Response containing the graph as a JSON, in cytoscape conform format.
     * @throws JSONException if JSON creation fails
     */
    @GET
    @Path("/clear/{info}")
    @Produces("application/json;charset=utf-8")
    public Response clear(@PathParam("info") String info) {
        String[] infos = info.split("--");
        String sessionID = infos[0];
        try {
            Helpers.sessionToGraph.get(sessionID).clear();
            String json = CytoJSONBuilder.getJSON(Helpers.sessionToGraph.get(sessionID));
            return Response.ok(json).header("Access-Control-Allow-Origin", "*").build();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Response.ok("{}").header("Access-Control-Allow-Origin", "*").build();
    }

    @GET
    @Path("/tea/{info}")
    public Response saveTea(@PathParam("info") String info) {
        String[] infos = info.split("--");
        String name = infos[0];String sessionID = infos[1];
        GraphModel g = Helpers.sessionToGraph.get(sessionID);
        try {
            new SaveGraph().write(new File(name+".tea"),g);
        } catch (GraphIOException e) {
            e.printStackTrace();
        }
        StreamingOutput fileStream = new StreamingOutput() {
            @Override
            public void write(java.io.OutputStream output) throws IOException, WebApplicationException {
                try {
                    java.nio.file.Path path = Paths.get(name+".tea");
                    byte[] data = Files.readAllBytes(path);
                    output.write(data);
                    output.flush();
                } catch (Exception e) {
                    throw e;
                }
            }
        };
        return Response
                .ok(fileStream, MediaType.APPLICATION_OCTET_STREAM)
                .header("content-disposition", "attachment; filename = " + name +".tea")
                .build();
    }

    @GET
    @Path("/tex/{info}")
    public Response saveTex(@PathParam("info") String info) {
        String[] infos = info.split("--");
        String name = infos[0];String sessionID = infos[1];
        GraphModel g = Helpers.sessionToGraph.get(sessionID);
        try {
            new LatexWriter().write(new File(name+".tex"),g);
        } catch (GraphIOException e) {
            e.printStackTrace();
        }
        StreamingOutput fileStream = new StreamingOutput() {
            @Override
            public void write(java.io.OutputStream output) throws IOException, WebApplicationException {
                java.nio.file.Path path = Paths.get(name+".tex");
                byte[] data = Files.readAllBytes(path);
                output.write(data);
                output.flush();
            }
        };
        return Response
                .ok(fileStream, MediaType.APPLICATION_OCTET_STREAM)
                .header("content-disposition", "attachment; filename = " + name +".tex")
                .build();
    }


    /**
     * Adds a single edge between two given vertices
     *
     * @param info string containing the ID of the source vertex, target vertex and the session ID string name
     * @return Response containing the graph as a JSON, in cytoscape conform format.
     * @throws JSONException if JSON creation fails
     */
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
            Vertex source = Helpers.sessionToGraph.get(sessionID).getVertex(Integer.parseInt(sourceID));
            Vertex target = Helpers.sessionToGraph.get(sessionID).getVertex(Integer.parseInt(targetID));
            Edge edge = new Edge(source, target);
            Helpers.sessionToGraph.get(sessionID).insertEdge(edge);
            String json = CytoJSONBuilder.getJSON(Helpers.sessionToGraph.get(sessionID));
            return Response.ok(json).header("Access-Control-Allow-Origin", "*").build();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Response.ok("{}").header("Access-Control-Allow-Origin", "*").build();
    }

    @GET
    @Path("/action/{info}")
    @Produces("application/json;charset=utf-8")
    public Response action(@PathParam("info") String info) {
        String[] infos = info.split("--");
        String graph = infos[0];
        String action = infos[1];
        String[] props = infos[2].replaceAll(" ", "").split(":");
//        String[] reportProps = infos[3].replaceAll(" ", "").split(":");
        String sessionID = infos[3];
        handleSession(sessionID);

        try {
            if (Helpers.sessionToGraph.get(sessionID).getVerticesCount() == 0)
                Helpers.sessionToGraph.put(sessionID, generateGraph(props, graph));

            GraphActionExtension gre = Helpers.getInstanceOfExtension(graph);
            String[] propsNameSplitted = props[0].split(",");
            String[] propsValueSplitted = props[1].split(",");
            for(Field f : gre.getClass().getFields())
                System.out.println(f.getType());
//            PropsTypeValueFill.fill(gre, propsNameSplitted, propsValueSplitted);
//            Object o = gre.calculate(sessionToGraph.get(sessionID));
//            if (o instanceof JSONObject) {
//                return Response.ok(o.toString()).header("Access-Control-Allow-Origin", "*").build();
//            }
            JSONObject jsonObject = new JSONObject();
//            if (o instanceof RenderTable) {
//                jsonObject.put("titles", ((RenderTable) o).getTitles().toString());
//            }
//            jsonObject.put("results", o.toString());
            return Response.ok(jsonObject.toString()).header("Access-Control-Allow-Origin", "*").build();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Response.ok("{}").header("Access-Control-Allow-Origin", "*").build();
    }

    @GET
    @Path("/loadGraph/{info}")
    @Produces("application/json;charset=utf-8")
    public Response edgeList(@PathParam("info") String info) {
        System.out.println(info);
        String[] data = info.split("--");
        String loadType = data[0];
        String graph = data[1];
        String type = data[2];
        String sessionID = data[3];
        handleSession(sessionID);
        GraphModel g = new GraphModel();
        g = MatrixHandler.getGraphFromMatrix(loadType, graph, g);
        Helpers.sessionToGraph.put(sessionID, g);
        if(type.equals("directed")){
            Helpers.sessionToGraph.get(sessionID).setDirected(true);
        } else if (type.equals("undirected")){
            Helpers.sessionToGraph.get(sessionID).setDirected(false);
        }
        try {
            String json = CytoJSONBuilder.getJSON(Helpers.sessionToGraph.get(sessionID));
            return Response.ok(json).header("Access-Control-Allow-Origin", "*").build();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Response.ok("").header("Access-Control-Allow-Origin", "*").build();
    }


    @GET
    @Path("/save/{info}")
    @Produces("application/json;charset=utf-8")
    public Response save(@PathParam("info") String info) {
        String[] infos = info.split("--");
        String sessionID = infos[1];
        handleSession(sessionID);
        String output = infos[0];
        String result = "";
        GraphModel g = Helpers.sessionToGraph.get(sessionID);
        if(g.getVerticesCount() != 0) {
            if (output.equals("g6")) {
                result=G6Format.graphToG6(g);
            }
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("results",result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Response.ok(jsonObject.toString()).header("Access-Control-Allow-Origin", "*").build();
    }


    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response extensionHandler(JSONExtensionHandler customer){
        System.out.println(customer);
        String sessionID = customer.getuuid();
        String[] props = {customer.getpropsKeys(), customer.getpropsVals()};
        String type = customer.getDirected();
        handleSession(sessionID);
        switch (customer.gettype()) {
            case "gen":
                Helpers.sessionToGraph.put(sessionID, generateGraph(props,customer.getgraph()));
                if(type.equals("directed")){
                    Helpers.sessionToGraph.get(sessionID).setDirected(true);
                } else if (type.equals("undirected")){
                    Helpers.sessionToGraph.get(sessionID).setDirected(false);
                }
                try {
                    String json = CytoJSONBuilder.getJSON(Helpers.sessionToGraph.get(sessionID));
                    return Response.ok(json).header("Access-Control-Allow-Origin", "*").build();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case "report":
                if(Helpers.sessionToGraph.containsKey(sessionID)) {
                    GraphReportExtension gre = Helpers.getInstanceOfExtension(customer.getname());
                    String[] propsNameSplitted = props[0].split(",");
                    String[] propsValueSplitted = props[1].split(",");
                    PropsTypeValueFill.fill(gre, propsNameSplitted, propsValueSplitted);
                    Object o = gre.calculate(Helpers.sessionToGraph.get(sessionID));
                    if (o instanceof JSONObject) {
                        return Response.ok(o.toString()).header("Access-Control-Allow-Origin", "*").build();
                    }
                    JSONObject jsonObject = new JSONObject();
                    try {
                        if (o instanceof RenderTable) {
                            jsonObject.put("titles", ((RenderTable) o).getTitles().toString());
                        }
                        jsonObject.put("results", o.toString());
                    } catch (Exception e) {}
                    return Response.ok(jsonObject.toString()).header("Access-Control-Allow-Origin", "*").build();
                }
                break;
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
        try {
            jsonObject.put("graphs", Helpers.getExtensions("graphtea.extensions.generators", GraphGeneratorExtension.class));
            jsonObject.put("reports",Helpers.getExtensions("graphtea.extensions.reports", GraphReportExtension.class));
            jsonObject.put("actions",Helpers.getExtensions("graphtea.extensions.actions", GraphActionExtension.class));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Response.ok(jsonObject.toString()).header("Access-Control-Allow-Origin", "*").build();
    }

    @GET
    @Path("/mats")
    @Produces("application/json;charset=utf-8")
    public Response getMats() {
        JSONArray jsonArray = new JSONArray();
        File matsFolder = new File(Server.getPathOfMats());
        String[] mats = matsFolder.list((current,name) -> !(new File(current,name).isDirectory()));
        Arrays.sort(mats);
        for(String m : mats) jsonArray.put(m);
        return Response.ok(jsonArray.toString()).header("Access-Control-Allow-Origin","*").build();
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

    private boolean handleSession(String sessionID) {
        if(!Helpers.sessionToGraph.containsKey(sessionID)){
            Helpers.sessionToGraph.put(sessionID, new GraphModel());
            return true; // Session created
        }
        return false; // Session exists
    }
}
