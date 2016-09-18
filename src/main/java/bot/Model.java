package bot;

/**
 * Created by Aluno on 16/09/2016.
 */
import com.mongodb.*;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.FindIterable;
import org.bson.Document;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.ascending;
import static java.util.Arrays.asList;
import java.util.ArrayList;

public class Model {
    MongoClientURI uri;
    MongoClient mongoClient;
    MongoDatabase db;
    Model(String url){
        uri  = new MongoClientURI(url);
        mongoClient = new MongoClient(uri);
        db = mongoClient.getDatabase("arteiro");
    }
    protected ArrayList<Artist> searchArtistName(String artistName){
        ArrayList< Artist > artistas = new ArrayList< Artist >();
        BasicDBObject query = new BasicDBObject();
        query.put("name", "samuel");
        FindIterable<Document> iterable = db.getCollection("artistas").find(new BasicDBObject("$text", new BasicDBObject("$search", artistName))).limit(5);
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
}
