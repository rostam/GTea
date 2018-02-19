package server;

import graphtea.platform.extension.Extension;
import graphtea.plugins.reports.extension.GraphReportExtension;

import java.lang.reflect.Field;

public class PropsTypeValueFill {
    public static void fill(Extension gre, String[] propsNameSplitted, String[] propsValueSplitted) {
        for (int i = 0; i < propsNameSplitted.length; i++)
            if (!propsNameSplitted[i].equals("undefined"))
                PropsTypeValueFill.fill(gre, propsNameSplitted[i], propsValueSplitted[i]);
    }

    public static void fill(Extension gre, String name, String val) {
        Field f = null;
        try {
            f = gre.getClass().getField(name);
            String nameOfType = f.getType().getSimpleName();
            switch (nameOfType) {
                case "double":
                case "Double":
                    f.set(gre, Double.parseDouble(val));
                    break;
                case "int":
                case "Integer":
                    f.set(gre, Integer.parseInt(val));
                    break;
                case "bool":
                case "Boolean":
                    f.set(gre, Boolean.parseBoolean(val));
                    break;
                case "String":
                    f.set(gre, val);
                    break;
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
