package org.couchbase.sample.javaee;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.JsonLongDocument;
import com.couchbase.client.java.document.JsonStringDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.query.N1qlQueryRow;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import static com.couchbase.client.java.query.Select.select;
import com.couchbase.client.java.query.SimpleN1qlQuery;
import static com.couchbase.client.java.query.dsl.Expression.i;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;

/**
 * @author Arun Gupta
 */
@Path("airline")
public class AirlineResource {

    @Inject Database database;
    
    @GET
    public String getAll() {
        N1qlQuery query = N1qlQuery
                .simple(select("*")
                .from(i(database.getBucket().name()))
                .limit(10));
//        N1qlQuery query = N1qlQuery.simple("SELECT * FROM `travel-sample` LIMIT 10");
        System.out.println(query.statement().toString());
        N1qlQueryResult result = database.getBucket().query(query);
        System.err.println(result.errors());
        System.out.println(result.toString());
        return result.allRows().toString();
    }
    
    @GET
    @Path("{id}")
    public String getAirline(@PathParam("id") String id) {
//        N1qlQuery query = N1qlQuery
//                .simple(select("*")
//                .from(i(database.getBucket().name()))
//                .where(x("id").eq(id)));
//        N1qlQuery query = N1qlQuery.simple("SELECT * from `travel-sample` WHERE id = " + id);
        
        // SELECT RAW(`travel-sample`) from `travel-sample` USE KEYS ["airline_10"];
        // SELECT RAW(`travel-sample`) from `travel-sample` USE KEYS ["airline_137", "airline_10"];
        
        // SELECT META(`travel-sample`).id,* FROM `travel-sample` LIMIT 10;
        
        N1qlQuery query = N1qlQuery.simple("SELECT RAW(*) from `travel-sample` USE KEYS [\"airline_" + id + "\"]");
        System.out.println(query.statement().toString());        
        N1qlQueryResult result = database.getBucket().query(query);
        if (result.finalSuccess() && !result.allRows().isEmpty()) {
            return result.allRows().get(0).toString();
        }

//        RawJsonDocument d = database.getBucket().get("airline_" + id, RawJsonDocument.class);
//        if (null != d) {
//            return d.content();
//        }
        
        
        return null;
    }
    
    @POST
//    @Consumes("application/json")
    public void addAirline(String data) {
        // {"country":"France","iata":"A5","callsign":"AIRLINAIR","name":"Airlinair","icao":"RLA","type":"airline"}
//        N1qlQuery query = N1qlQuery
//                .simple(select("*")
//                .from(i(database.getBucket().name()))
//                .where(x("id").eq(id)));
        
//        database.getBucket().counter("airline", l);
        JsonObject json = JsonObject.fromJson(data);
        JsonLongDocument id = database.getBucket().counter("airline_sequence", 1);
        json.put("id", id);
        json.put("type", "airline");
        JsonDocument document = JsonDocument.create("airline_" + id, json);
        database.getBucket().insert(document);
    }
//    
//    @PUT
//    public void updateAirline(String data) {
//        if (database.getBucket().exists(data)) {
//            JsonStringDocument document = JsonStringDocument.create(data);
//            database.getBucket().upsert(document);
//        }
//    }
//    
//    @DELETE
//    @Path("${id}")
//    public void delete(@PathParam("id")String id) {
//        // Query for all airlines for name
//        // Use LIKE?
//        SimpleN1qlQuery query = N1qlQuery.simple("SELECT * from `travel-sample` WHERE id = " + name);
//        N1qlQueryResult result = database.getBucket().query(query);
//        for (N1qlQueryRow row : result.allRows()) {
//            row
//        }
//        if (database.getBucket().exists(data)) {
//            JsonStringDocument document = JsonStringDocument.create(data);
//            database.getBucket().remove(document);
//        }
//    }
}
