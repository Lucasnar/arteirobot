package bot;

import static spark.Spark.*;

import java.io.IOException;
import java.util.*;

public class MainServer {
    Model model;
    Bot bot;
    String adminId = "-164346147";
    MainServer(){
        model = new Model("mongodb://arteiro:abacate@ds019846.mlab.com:19846/arteiro");
        bot = new Bot();

    }
    protected void testMessage(String text) {
        bot.sendMessage(text, adminId);
    }

    protected void testSearch(String artistName){
        ArrayList< Artist > artistas;
        artistas = model.searchArtistName(artistName);
        for (int i = 0; i < artistas.size(); i++) {
            try {
                bot.sendPhoto(adminId, artistas.get(i).getArte() ,  artistas.get(i).mountArtist());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    protected void read(byte[] bodyRequest){
        try {
            bot.setUpdate(new String(bodyRequest, "UTF-8"));
            if(bot.getTypeChatCommon() == true) {
                ArrayList<Artist> artistas;
                artistas = model.searchArtistName(bot.getMessage());
                for (int i = 0; i < artistas.size(); i++) {
                    try {
                        bot.sendPhoto(bot.getChatId(), artistas.get(i).getArte(), artistas.get(i).mountArtist());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }else{
                bot.sendMessage("Hello, you using inline!", bot.getChatId());

            }

        } catch(Exception e){
            // Admin notification
            bot.sendMessage(e.getMessage() + "\n" + e.getStackTrace(),"-145562622");
        }
    }
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

        //Data is sent by telegram API on this route
        post("/readMessages", (req, res) -> {
            controller.read(req.bodyAsBytes());
            return "Success";
        });

    }
}