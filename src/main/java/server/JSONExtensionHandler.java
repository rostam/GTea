package server;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class JSONExtensionHandler {
    private final long id;
    private final String type;
    private final String name;
    private final String graph;
    private final String uuid;
    private final String propsKeys;
    private final String propsVals;
    private final String directed;
    private final String status;
    private final ArrayList<String> steps;
    private static final AtomicLong counter = new AtomicLong(100);

    private JSONExtensionHandler(JSONExtensionHandlerBuilder builder){
        this.id = builder.id;
        this.type = builder.type;
        this.name = builder.name;
        this.graph = builder.graph;
        this.uuid = builder.uuid;
        this.propsKeys = builder.propsKeys;
        this.propsVals = builder.propsVals;
        this.directed = builder.directed;
        this.status = builder.status;
        this.steps = builder.steps;
    }

    public JSONExtensionHandler(){
        JSONExtensionHandler cust = new JSONExtensionHandler.JSONExtensionHandlerBuilder().id().build();
        this.id = cust.getId();
        this.type = cust.gettype();
        this.name = cust.getname();
        this.graph = cust.getgraph();
        this.uuid = cust.getuuid();
        this.propsKeys = cust.getpropsKeys();
        this.propsVals = cust.getpropsVals();
        this.directed = cust.getDirected();
        this.status = cust.getStatus();
        this.steps = cust.getSteps();
    }

    public JSONExtensionHandler(long id, String type, String name,
                                String graph, String uuid, String propsKeys, String propsVals,
                                String directed, String status, ArrayList<String> steps) {
        JSONExtensionHandler cust = new JSONExtensionHandler.JSONExtensionHandlerBuilder().id()
                .type(type)
                .name(name)
                .graph(graph)
                .uuid(uuid)
                .propsKeys(propsKeys)
                .propsVals(propsVals)
                .directed(directed)
                .status(status)
                .steps(steps)
                .build();
        this.id = cust.getId();
        this.type = cust.gettype();
        this.name = cust.getname();
        this.graph = cust.getgraph();
        this.uuid = cust.getuuid();
        this.propsKeys = cust.getpropsKeys();
        this.propsVals = cust.getpropsVals();
        this.directed = cust.getDirected();
        this.status = status;
        this.steps = steps;
    }

    public long getId(){
        return this.id;
    }

    public String gettype() {
        return this.type;
    }

    public String getname() {
        return this.name;
    }

    public String getgraph(){
        return this.graph;
    }

    public String getuuid() {
        return this.uuid;
    }

    public String getpropsKeys() {
        return this.propsKeys;
    }

    public String getpropsVals(){
        return this.propsVals;
    }

    public String getDirected() {
        return this.directed;
    }

    public String getStatus() {return this.status;}

    public ArrayList<String> getSteps() {return this.steps;}

    @Override
    public String toString(){
        return "ID: " + id
                + " Type: " + type
                + " Name: " + name + "\n"
                + " graph: " + graph + "\n"
                + " uuid: " + uuid + "\n"
                + " propsKeys: " + propsKeys + "\n"
                + " propsVals: " + propsVals + "\n"
                + " Directed:" + directed + "\n"
                + " status:" + status + "\n"
                + " steps:" + steps;
    }

    public static class JSONExtensionHandlerBuilder{
        private long id;
        private String type = "";
        private String name = "";
        private String graph = "";
        private String uuid = "";
        private String propsKeys = "";
        private String propsVals = "";
        private String directed = "";
        private String status = "";
        private ArrayList<String> steps = new ArrayList<>();

        public JSONExtensionHandlerBuilder id(){
            this.id = JSONExtensionHandler.counter.getAndIncrement();
            return this;
        }

        public JSONExtensionHandlerBuilder id(long id){
            this.id = id;
            return this;
        }

        public JSONExtensionHandlerBuilder type(String type){
            this.type = type;
            return this;
        }

        public JSONExtensionHandlerBuilder name(String name){
            this.name = name;
            return this;
        }

        public JSONExtensionHandlerBuilder graph(String graph){
            this.graph = graph;
            return this;
        }

        public JSONExtensionHandlerBuilder uuid(String uuid){
            this.uuid = uuid;
            return this;
        }

        public JSONExtensionHandlerBuilder propsKeys(String propsKeys){
            this.propsKeys = propsKeys;
            return this;
        }

        public JSONExtensionHandlerBuilder propsVals(String propsVals){
            this.propsVals = propsVals;
            return this;
        }

        public JSONExtensionHandlerBuilder directed(String directed) {
            this.directed = directed;
            return this;
        }

        public JSONExtensionHandlerBuilder status(String status) {
            this.status = status;
            return this;
        }

        public JSONExtensionHandlerBuilder steps(ArrayList<String> steps) {
            this.steps = steps;
            return this;
        }

        public JSONExtensionHandler build(){
            return new JSONExtensionHandler(this);
        }

    }
}
