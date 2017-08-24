package server;

import graphtea.plugins.graphgenerator.core.extension.GraphGeneratorExtension;
import org.reflections.Reflections;

import java.util.Set;

public class Test {
    public static void main(String[] args) {
        Reflections reflections = new Reflections("graphtea");
        Set<Class<? extends GraphGeneratorExtension>> subTypes = reflections.getSubTypesOf(GraphGeneratorExtension.class);
        for(Class<? extends GraphGeneratorExtension> c : subTypes) {
            System.out.println(c.getSimpleName());
        }
    }
}
