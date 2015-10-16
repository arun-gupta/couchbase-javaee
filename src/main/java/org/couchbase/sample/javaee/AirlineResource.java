package org.couchbase.sample.javaee;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.RawJsonDocument;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.dsl.Expression.i;

/**
 * @author Arun Gupta
 */
@Path("airline")
public class AirlineResource {

    CouchbaseCluster cluster;
    Bucket bucket;
    
    @PostConstruct
    private void initDB() {
        cluster = CouchbaseCluster.create("192.168.99.100");
        bucket = cluster.openBucket("travel-sample");;
    }

    @PreDestroy
    private void stopDB() {
        cluster.disconnect();
    }

    @GET
    public String getAll() {

        N1qlQuery query = N1qlQuery.simple(select("id").from(i(bucket.name())).limit(10));
//        N1qlQuery query = N1qlQuery.simple(select("*").limit(10));
//        N1qlQuery query = N1qlQuery.simple("SELECT * FROM travel-sample LIMIT 10");
        System.out.println(query.statement().toString());
        N1qlQueryResult result = bucket.query(query);
        System.out.println(result.toString());
        return result.allRows().toString();
    }

    @GET
    @Path("{id}")
    public String getAirline(@PathParam("id") String id) {
        return bucket.get(id, RawJsonDocument.class).content();
    }
}
