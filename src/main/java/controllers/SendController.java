package controllers;

import com.mongodb.*;
import models.Send;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.FindIterable;
import org.bson.Document;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.ascending;
import static java.util.Arrays.asList;

/**
 * Created by Samuel on 10/09/2016.
 */
public class SendController extends BaseController {
    String adminId = "153878723"; 
	// Lucas' chat id 136505761
	////chatid samuel = 153878723 
    Send sendModel = new Send();
    MongoClientURI uri  = new MongoClientURI("mongodb://arteiro:abacate@ds019846.mlab.com:19846/arteiro");
    MongoClient mongoClient = new MongoClient(uri);
    MongoDatabase db = mongoClient.getDatabase("arteiro");

    protected void test(String text){

        FindIterable<Document> iterable = db.getCollection("artistas").find(new BasicDBObject("$text", new BasicDBObject("$search", text)));

        iterable.forEach(new Block<Document>() {
            @Override
            public void apply(final Document document) {
                sendModel.sendMessage(document.toString(), adminId);
            }
        });

    }
}
