package controllers;

import static spark.Spark.*;

import java.util.*;

public class MainServer  extends BaseController{
    public static void main(String[] args) {

		// Get port config of heroku on environment variable
        ProcessBuilder process = new ProcessBuilder();
        SendController send = new SendController();
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

        get("/testMessage/:name", (req, res) -> {
            send.test(req.params(":name"));
            return "The message was sent for Admin Group";
        });



    }
}
