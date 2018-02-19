package server;

import Jama.Matrix;
import graphtea.extensions.Centrality;
import graphtea.extensions.G6Format;
import graphtea.extensions.RandomTree;
import graphtea.extensions.io.LatexWriter;
import graphtea.extensions.io.LoadMtx;
import graphtea.extensions.io.LoadSpecialGML;
import graphtea.extensions.io.SaveGraph;
import graphtea.extensions.reports.coloring.ColumnIntersectionGraph;
import graphtea.graph.graph.*;
import graphtea.plugins.graphgenerator.core.extension.GraphGeneratorExtension;
import graphtea.plugins.main.extension.GraphActionExtension;
import graphtea.plugins.main.saveload.core.GraphIOException;
import graphtea.plugins.reports.extension.GraphReportExtension;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.reflections.Reflections;
import graphtea.platform.extension.Extension;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Handles REST requests to the server.
 */
@Path("")
public class RequestHandler {
    private static HashMap<String,Class> extensionNameToClass = new HashMap<>();
    private static HashMap<String, GraphModel> sessionToGraph = new HashMap<>();

    public GraphModel generateGraph(String[] props, String graph) {
        try {
            GraphGeneratorExtension gge =
                    ((GraphGeneratorExtension) extensionNameToClass.get(graph).newInstance());
            String[] propsNameSplitted = props[0].split(",");
            String[] propsValueSplitted = props[1].split(",");
            PropsTypeValueFill.fill(gge,propsNameSplitted,propsValueSplitted);
            return gge.generateGraph();
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return new GraphModel();
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
            if ((type.equals("directed")) != sessionToGraph.get(sessionID).isDirected()) {
                if (type.equals("directed")) {
                    sessionToGraph.get(sessionID).setDirected(true);
                } else if (type.equals("undirected")) {
                    sessionToGraph.get(sessionID).setDirected(false);
                }

            } else {
                // Only change if types differ
                return Response.noContent().header("Access-Control-Allow-Origin", "*").build();

            }
            String json = CytoJSONBuilder.getJSON(sessionToGraph.get(sessionID));
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

                Vertex vertex = sessionToGraph.get(sessionID).getVertex(_source);
                Vertex opposingVertex = sessionToGraph.get(sessionID).getVertex(_target);

                Edge parallelEdge = sessionToGraph.get(sessionID).getEdge(vertex, opposingVertex);
                sessionToGraph.get(sessionID).removeEdge(parallelEdge);
            }

            String json = CytoJSONBuilder.getJSON(sessionToGraph.get(sessionID));
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
                sessionToGraph.get(sessionID).setDirected(true);
            } else if (type.equals("undirected")){
                sessionToGraph.get(sessionID).setDirected(false);
            }
        }

        Vertex vertex = new Vertex();
        vertex.setLabel(vertexId);
        vertex.setLocation(new GPoint(xPos, yPos));
        try {
            sessionToGraph.get(sessionID).insertVertex(vertex);
            String json = CytoJSONBuilder.getJSON(sessionToGraph.get(sessionID));
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
            Vertex v = sessionToGraph.get(sessionID).getVertex(Integer.parseInt(vertexId));
            sessionToGraph.get(sessionID).deleteVertex(v);

            Iterable<Vertex> vi = sessionToGraph.get(sessionID).vertices();

            String json = CytoJSONBuilder.getJSON(sessionToGraph.get(sessionID));
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
            sessionToGraph.get(sessionID).clear();
            String json = CytoJSONBuilder.getJSON(sessionToGraph.get(sessionID));
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
        GraphModel g = sessionToGraph.get(sessionID);
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
        GraphModel g = sessionToGraph.get(sessionID);
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
            Vertex source = sessionToGraph.get(sessionID).getVertex(Integer.parseInt(sourceID));
            Vertex target = sessionToGraph.get(sessionID).getVertex(Integer.parseInt(targetID));
            Edge edge = new Edge(source, target);
            sessionToGraph.get(sessionID).insertEdge(edge);
            String json = CytoJSONBuilder.getJSON(sessionToGraph.get(sessionID));
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

        try {
            if(sessionToGraph.get(sessionID).getVerticesCount() == 0)
                sessionToGraph.put(sessionID, generateGraph(props, graph));

            if(!report.contains("No ")) {
                GraphReportExtension gre = ((GraphReportExtension) extensionNameToClass.get(report).newInstance());
                String[] propsNameSplitted = reportProps[0].split(",");
                String[] propsValueSplitted = reportProps[1].split(",");
                PropsTypeValueFill.fill(gre,propsNameSplitted,propsValueSplitted);
                Object o = gre.calculate(sessionToGraph.get(sessionID));
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
        } catch (Exception e) {
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
            if (sessionToGraph.get(sessionID).getVerticesCount() == 0)
                sessionToGraph.put(sessionID, generateGraph(props, graph));

            System.out.println(graph);
            System.out.println(extensionNameToClass.get(graph));
            GraphActionExtension gre = ((GraphActionExtension) extensionNameToClass.get(graph).newInstance());
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
        switch (loadType) {
            case "el":
                g = getGraphFromEdgeList(graph);
                break;
            case "g6":
                graph = graph.replaceAll("qqq", "?");
                g = G6Format.stringToGraphModel(graph);
                break;
            case "adjadj":
                if(graph.contains(".mtx")) {
                    File mat = new File(Server.getPathOfMats()+graph);
                    try {
                        g = new LoadMtx().read(mat);
                    } catch (GraphIOException e) {
                        e.printStackTrace();
                    }
                } else {
                    String[] rows = graph.split("-");
                    for (String row : rows) g.addVertex(new Vertex());
                    for (int i = 0; i < rows.length; i++) {
                        String tmp[] = rows[i].split(",");
                        for (int j = 0; j < tmp.length; j++) {
                            if (tmp[j].equals("1")) {
                                g.addEdge(new Edge(g.getVertex(i), g.getVertex(j)));
                            }
                        }
                    }
                }
                break;
            case "adjcig":
                if(graph.contains(".mtx")) {
                    File mat = new File(Server.getPathOfMats()+graph);
                    GraphModel g1 = null;
                    try {
                        g1 = new LoadMtx().read(mat);
                    } catch (GraphIOException e) {
                        e.printStackTrace();
                    }
                    g = ColumnIntersectionGraph.from(g1);
                } else {
                    String[] rows2 = graph.split("-");
                    Matrix m = new Matrix(rows2.length, rows2.length);
                    for (int i = 0; i < rows2.length; i++) {
                        String tmp[] = rows2[i].split(",");
                        for (int j = 0; j < tmp.length; j++) {
                            m.set(j, i, Double.parseDouble(tmp[j]));
                        }
                    }
                    g = ColumnIntersectionGraph.from(m);
                }
                break;
            case "adjrcig":
                String[] rows3 = graph.split("-");
                Matrix m3 = new Matrix(rows3.length, rows3.length);
                for (int i = 0; i < rows3.length; i++) {
                    String tmp[] = rows3[i].split(",");
                    for (int j = 0; j < tmp.length; j++) {
                        m3.set(j, i, Double.parseDouble(tmp[j]));
                    }
                }
                for (String row : rows3) g.addVertex(new Vertex());
                for (int i = 0; i < m3.getColumnDimension(); i++) {
                    for (int j = 0; j < m3.getColumnDimension(); j++) {
                        for (int k = 0; k < m3.getRowDimension(); k++) {
                            if (m3.get(i, k) != 0 && m3.get(j, k) != 0) {
                                if (m3.get(i, k) == 2 || m3.get(j, k) == 2) {
                                    g.addEdge(new Edge(g.getVertex(i), g.getVertex(j)));
                                    break;
                                }
                            }
                        }
                    }
                }
                break;
            case "adjspecial":
                try {
                    g = new LoadSpecialGML().read(new File("/home/rostam/kara/GD2018/got-graph.graphml"));
                } catch (GraphIOException e) {
                    e.printStackTrace();
                }
                break;
        }
        sessionToGraph.put(sessionID, g);
        if(type.equals("directed")){
            sessionToGraph.get(sessionID).setDirected(true);
        } else if (type.equals("undirected")){
            sessionToGraph.get(sessionID).setDirected(false);
        }
        try {
            String json = CytoJSONBuilder.getJSON(sessionToGraph.get(sessionID));
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
        GraphModel currentGraph = new GraphModel();
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
    @Path("/save/{info}")
    @Produces("application/json;charset=utf-8")
    public Response save(@PathParam("info") String info) {
        String[] infos = info.split("--");
        String sessionID = infos[1];
        handleSession(sessionID);
        String output = infos[0];
        String result = "";
        GraphModel g = sessionToGraph.get(sessionID);
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
                sessionToGraph.get(sessionID).setDirected(true);
            } else if (type.equals("undirected")){
                sessionToGraph.get(sessionID).setDirected(false);
            }
            String json = CytoJSONBuilder.getJSON(sessionToGraph.get(sessionID));
            return Response.ok(json).header("Access-Control-Allow-Origin", "*").build();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return Response.ok("{}").header("Access-Control-Allow-Origin", "*").build();
    }



    private JSONArray getExtensions(String extensionPackage, Class extensionClass) {
        Reflections reflectionsReports = new Reflections(extensionPackage);
        Set<Class> subTypesReport = reflectionsReports.getSubTypesOf(extensionClass);
        Vector<String> reports = new Vector<>();
        JSONArray jsonArray2 = new JSONArray();
        for(Class<? extends Extension> c : subTypesReport) {
            JSONObject jo = new JSONObject();
            String classSimpleName = c.getSimpleName();
            try {
                jo.put("name",classSimpleName);
                jo.put("desc",c.newInstance().getDescription());
            } catch (JSONException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
            Field[] fs = c.getFields();
            JSONArray properties = new JSONArray();
            for(Field f : fs)
                if(f.getAnnotations().length!=0) {
                    try {
                        properties.put(f.getName()+":"+f.getType().getSimpleName()+":"+f.get(c.newInstance()));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    }
                }
            try {
                jo.put("properties",properties);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray2.put(jo);
            reports.add(classSimpleName);
            extensionNameToClass.put(classSimpleName,c);
        }
        return jsonArray2;
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
            jsonObject.put("graphs", getExtensions("graphtea.extensions.generators", GraphGeneratorExtension.class));
            jsonObject.put("reports",getExtensions("graphtea.extensions.reports", GraphReportExtension.class));
            jsonObject.put("actions",getExtensions("graphtea.extensions.actions", GraphActionExtension.class));
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
        System.out.println(matsFolder.getAbsolutePath());
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
        if(!sessionToGraph.containsKey(sessionID)){
            sessionToGraph.put(sessionID, new GraphModel());
            return true; // Session created
        }
        return false; // Session exists
    }
}
