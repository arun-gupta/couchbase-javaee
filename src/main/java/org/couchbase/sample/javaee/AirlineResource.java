package org.couchbase.sample.javaee;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.Query;
import com.couchbase.client.java.query.QueryResult;
import com.couchbase.client.java.query.SimpleQuery;
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
@Path("airline")
public class AirlineResource {

    CouchbaseCluster cluster;
    Bucket bucket;

    Set<String> set = new HashSet<>();

    @PostConstruct
    private void initDB() {
        System.out.println("initDB");
        // Connect to the Cluster
        cluster = CouchbaseCluster.create("http://192.168.99.100:8091");

        bucket = cluster.openBucket("travel-sample");
    }

    @PreDestroy
    private void stopDB() {
        // Disconnect from the cluster
        cluster.disconnect();
    }

    @GET
    public String getAll() {
//        JsonDocument result = bucket.query(select("*").from("travel-sample").limit(10));
        SimpleQuery query = Query.simple("SELECT * FROM travel-sample LIMIT 10");
        System.out.println(query.toString());
        QueryResult result = bucket.query(query);
        System.out.println(result.toString());
        return result.allRows().toString();
    }

    @GET
    @Path("{id}")
    public String getAirline(@PathParam("id") String id) {
        JsonDocument airline = bucket.get(id);
        return airline.toString();
    }

//    @POST
//    public void addPerson(String person) throws IOException {
//        JsonReader reader = Json.createReader(new StringReader(person));
//        for (String key : reader.readObject().keySet()) {
//            JsonDocument.create(key, JsonObject.fromJson(reader.readObject().getString(key)));
//        }
//    }

    @DELETE
    public void deletePerson(String key) {
        bucket.remove(key);
    }
}
