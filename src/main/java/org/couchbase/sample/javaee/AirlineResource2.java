package org.couchbase.sample.javaee;

import com.couchbase.client.java.document.RawJsonDocument;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.dsl.Expression.i;
import static com.couchbase.client.java.query.dsl.Expression.x;
import javax.inject.Inject;

/**
 * @author Arun Gupta
 */
@Path("airline")
public class AirlineResource2 {

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
        N1qlQuery query = N1qlQuery.simple("SELECT * from `travel-sample` WHERE id = " + id);
        System.out.println(query.statement().toString());        
        N1qlQueryResult result = database.getBucket().query(query);
        if (result.finalSuccess() && !result.allRows().isEmpty()) {
            return result.allRows().get(0).toString();
        }
        return null;
    }
}
