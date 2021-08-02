package server;

import Jama.Matrix;
import graphtea.extensions.G6Format;
import graphtea.extensions.io.LoadMtx;
import graphtea.extensions.io.LoadSpecialGML;
import graphtea.extensions.reports.coloring.ColumnIntersectionGraph;
import graphtea.graph.graph.Edge;
import graphtea.graph.graph.GraphModel;
import graphtea.graph.graph.Vertex;
import graphtea.plugins.main.saveload.core.GraphIOException;

import java.io.File;
import java.util.HashMap;
import java.util.Vector;

public class MatrixHandler {

    public static GraphModel getGraphFromMatrix(String loadType, String graph, GraphModel g) {
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
                    g = new LoadMtx().read(mat);
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
                    g1 = new LoadMtx().read(mat);
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
        return g;
    }

    public static GraphModel getGraphFromEdgeList(String info) {
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
}
