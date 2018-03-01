package server;

import graphtea.graph.graph.GraphModel;
import graphtea.platform.extension.Extension;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

public class Helpers {
    public static HashMap<String,Class> extensionNameToClass = new HashMap<>();
    public static HashMap<String, GraphModel> sessionToGraph = new HashMap<>();

    public static <T>  T getInstanceOfExtension(String name) {
        try {
            return (T)extensionNameToClass.get(name).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static JSONArray getExtensions(String extensionPackage, Class extensionClass) {
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
                    } catch (IllegalAccessException | InstantiationException e) {
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
}
