package org.couchbase.sample.javaee;

import com.couchbase.client.java.document.RawJsonDocument;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.dsl.Expression.i;
import javax.inject.Inject;

/**
 * @author Arun Gupta
 */
@Path("airline")
public class AirlineResource {

    @Inject Database database;
    
    @GET
    public String getAll() {
        N1qlQuery query = N1qlQuery
                .simple(select("id")
                .from(i(database.getBucket().name()))
                .limit(10));
//        N1qlQuery query = N1qlQuery.simple("SELECT * FROM `travel-sample`");
//        N1qlQuery query = N1qlQuery.simple("SELECT * FROM `travel-sample` LIMIT 10");
//        System.out.println(query.statement().toString());
        N1qlQueryResult result = database.getBucket().query(query);
        System.out.println(result.toString());
        return result.allRows().toString();
    }

    @GET
    @Path("{id}")
    public String getAirline(@PathParam("id") String id) {
        return database.getBucket().get(id, RawJsonDocument.class).content();
    }
}
