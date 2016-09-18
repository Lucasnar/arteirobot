package bot;

import static spark.Spark.*;

import java.util.*;

public class MainServer {
    public static void main(String[] args) {

        // Get port config of heroku on environment variable
        ProcessBuilder process = new ProcessBuilder();
        Controller controller = new Controller();
        int myPort;
        if (process.environment().get("PORT") != null) {
            myPort = Integer.parseInt(process.environment().get("PORT"));
        } else {
            myPort = 8080;
        }
        port(myPort);

        //Delivery static file
        staticFileLocation("/static");

        get("/", (req, res) -> {
            return "www.telegram.me/arteirobot";
        });

        get("/testMessage", (req, res) -> {
            controller.testMessage("Hello, Barney <3");

            return "The message was sent for Admin Group.";
        });

        get("/testSearch/:name", (req, res) -> {
            controller.testSearch(req.params(":name"));
            return "The message was sent for Admin Group. GET: " + req.params(":name");
        });
    }
}