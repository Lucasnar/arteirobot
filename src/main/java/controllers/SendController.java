package controllers;

import models.Send;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.Block;
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
    Send sendModel = new Send();
    MongoClient mongoClient = new MongoClient("mongodb://arteiro:abacate@ds019846.mlab.com:19846/arteiro",19846);
    MongoDatabase db = mongoClient.getDatabase("arteiro");

    protected void test(String text){
        FindIterable<Document> iterable = db.getCollection("artistas").find(new Document("name", "Samuel"));

        iterable.forEach(new Block<Document>() {
            @Override
            public void apply(final Document document) {
                sendModel.sendMessage(document.getString("name"),adminId);
            }
        });
    }
}
