package bot;

/**
 * Created by Aluno on 16/09/2016.
 */
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.FindIterable;
import org.bson.Document;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.ascending;
import static java.util.Arrays.asList;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class Model {
    MongoClientURI uri;
    MongoClient mongoClient;
    MongoDatabase db;
    DB db2;
    MongoCollection<Document> artistsCollection;

    Model(){
        uri  = new MongoClientURI("mongodb://arteiro:abacate@ds019846.mlab.com:19846/arteiro");
        mongoClient = new MongoClient(uri);
        db = mongoClient.getDatabase("arteiro");
        artistsCollection = db.getCollection("artistas");
    }

    protected ArrayList<Artist> searchArtistName(String artistName){
        ArrayList< Artist > artistas = new ArrayList< Artist >();
        FindIterable<Document> iterable = artistsCollection
                .find(new BasicDBObject("$text", new BasicDBObject("$search", artistName))).limit(5);

        iterable.forEach(new Block<Document>() {
            @Override
            public void apply(final Document document) {
                artistas.add(new Artist(
                                            document.getString("name"),
                                            document.getString("country"),
                                            document.getString("work"),
                                            document.getString("profile")
                                            )
                );
            }
        });

        return artistas;
    }

    // TODO: Is there a way to make it faster? Gets all 7000 artists only to return one.
    protected Artist showRandomArtist(){
//        Random rand = new Random();
//        int randomNum = rand.nextInt(7001);
//        final Artist[] artist = new Artist[1];

        DBCollection artistsCollection2 = db2.getCollection("artistas");
        DBObject artistDocument = artistsCollection2.findOne();
        Artist artist = new Artist((String) artistDocument.get("name"), (String) artistDocument.get("country"),
                (String) artistDocument.get("work"), (String) artistDocument.get("profile"));
//
//        FindIterable<Document> documents = artistsCollection.find();
//
//        documents.forEach(new Block<Document>() {
//            int counter = 1;
//            @Override
//            public void apply(final Document document) {
//                if(counter == randomNum){
//                    artist[0] = new Artist(document.getString("name"),
//                            document.getString("country"),
//                            document.getString("work"),
//                            document.getString("profile"));
//                }
//                ++counter;
//            }
//        });

//        return artist[0];
        return artist;
    }
}
