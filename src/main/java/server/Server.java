package server;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.StaticHttpHandler;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;

/**
 * Basic class, used for starting and stopping the server.
 */
public class Server {

  /**
   * URI that specifies where the server is run.
   */
  private static final URI BASE_URI = getBaseURI();
  /**
   * Default port
   */
  private static final int PORT = 2342;
//  private static final int PORT = 8008;
  /**
   * Path to demo application
   */
  private static final String APPLICATION_PATH = "server/dashboard/index.html";

  /**
   * Creates the base URI.
   * @return Base URI
   */
  private static URI getBaseURI() {
    return UriBuilder.fromUri("http://0.0.0.0/").port(PORT).build();
  }

  /**
   * Starts the server and adds the request handlers.
   *
   * @return the running server
   * @throws IOException if server creation fails
   */
  private static HttpServer startServer() throws IOException {
    System.out.println("Starting grizzly...");
    ResourceConfig rc = new PackagesResourceConfig("server");
    rc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);
    HttpServer server = GrizzlyServerFactory.createHttpServer(BASE_URI, rc);
    String path =  Server.class.getResource("/web").getPath();
//    path = path.substring(path.indexOf(":")+1,path.lastIndexOf("target/")+6) + "/classes/web";
    HttpHandler staticHandler = new StaticHttpHandler(path);
    server.getServerConfiguration().addHttpHandler( staticHandler, "/server" );

    return server;
  }

  /**
   * Main method. Run this to start the server.
   *
   * @param args command line parameters
   * @throws IOException if server creation fails
   */
  public static void main(String[] args) throws IOException {
    HttpServer httpServer = startServer();
    System.out.printf("server started at %s%s%n" +
      "Press any key to stop it.%n", getBaseURI(), APPLICATION_PATH);
    System.in.read();
    httpServer.stop();
  }
}
