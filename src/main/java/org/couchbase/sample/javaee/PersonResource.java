package org.couchbase.sample.javaee;

/**
 * @author arungupta
 */
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.json.Json;
import javax.json.JsonReader;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * @author Arun Gupta
 */
@Path("persons")
public class PersonResource {

    CouchbaseCluster cluster;
    Bucket bucket;

    Set<String> set = new HashSet<>();

    @PostConstruct
    private void initDB() {
        // Connect to the Cluster
        cluster = CouchbaseCluster.create("http://192.168.99.100:8091");

        bucket = cluster.openBucket();
    }

    @PreDestroy
    private void stopDB() {
        // Disconnect from the cluster
        cluster.disconnect();
    }

    @GET
    @Path("{person}")
    public String getPerson(@PathParam("param") String name) {
        // Read it back out
        JsonDocument walter = bucket.get(name);
        return walter.toString();
    }

    @POST
    public void addPerson(String person) throws IOException {
        JsonReader reader = Json.createReader(new StringReader(person));
        for (String key : reader.readObject().keySet()) {
            JsonDocument.create(key, JsonObject.fromJson(reader.readObject().getString(key)));
        }
    }

    @DELETE
    public void deletePerson(String key) {
        bucket.remove(key);
    }
}
