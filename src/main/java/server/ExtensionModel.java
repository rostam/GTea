package server;

import java.util.concurrent.atomic.AtomicLong;

public class ExtensionModel {
    //sessionID
    private final long id;
    //type = report ? generate ? action
    private final String type;
    //name = 
    private final String name;
    //graph type
    private final String graph;
    //props
    private final String props;
    private final String state;
    private final String birthday;
    private static final AtomicLong counter = new AtomicLong(100);

    private ExtensionModel(ExtensionModelBuilder builder){
        this.id = builder.id;
        this.type = builder.type;
        this.name = builder.name;
        this.graph = builder.graph;
        this.props = builder.props;
        this.state = builder.state;
        this.birthday = builder.birthday;
    }

    public ExtensionModel(){
        ExtensionModel cust = new ExtensionModel.ExtensionModelBuilder().id().build();
        this.id = cust.getId();
        this.type = cust.gettype();
        this.name = cust.getname();
        this.graph = cust.getgraph();
        this.props = cust.getprops();
        this.state = cust.getState();
        this.birthday = cust.getBirthday();
    }

    public ExtensionModel(long id, String type, String name,
                    String graph, String props, String state, String birthday){
        ExtensionModel cust = new ExtensionModel.ExtensionModelBuilder().id()
                .type(type)
                .name(name)
                .graph(graph)
                .props(props)
                .state(state)
                .birthday(birthday)
                .build();
        this.id = cust.getId();
        this.type = cust.gettype();
        this.name = cust.getname();
        this.graph = cust.getgraph();
        this.props = cust.getprops();
        this.state = cust.getState();
        this.birthday = cust.getBirthday();
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

    public String getprops() {
        return this.props;
    }

    public String getState() {
        return this.state;
    }

    public String getBirthday(){
        return this.birthday;
    }

    @Override
    public String toString(){
        return "ID: " + id
                + " First: " + type
                + " Last: " + name + "\n"
                + "graph: " + graph + "\n"
                + "props: " + props
                + " State: " + state
                + " Birthday " + birthday;
    }

    public static class ExtensionModelBuilder{
        private long id;
        private String type = "";
        private String name = "";
        private String graph = "";
        private String props = "";
        private String state = "";
        private String birthday = "";

        public ExtensionModelBuilder id(){
            this.id = ExtensionModel.counter.getAndIncrement();
            return this;
        }

        public ExtensionModelBuilder type(String type){
            this.type = type;
            return this;
        }

        public ExtensionModelBuilder name(String name){
            this.name = name;
            return this;
        }

        public ExtensionModelBuilder graph(String graph){
            this.graph = graph;
            return this;
        }

        public ExtensionModelBuilder props(String props){
            this.props = props;
            return this;
        }

        public ExtensionModelBuilder state(String state){
            this.state = state;
            return this;
        }

        public ExtensionModelBuilder birthday(String birthday){
            this.birthday = birthday;
            return this;
        }

        public ExtensionModel build(){
            return new ExtensionModel(this);
        }

    }
}
