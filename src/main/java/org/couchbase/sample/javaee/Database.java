package org.couchbase.sample.javaee;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.JsonLongDocument;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.concurrent.TimeUnit;

/**
 * @author Arun Gupta
 */
@Singleton
@Startup
public class Database {

    CouchbaseCluster cluster;
    Bucket bucket;

    @PostConstruct
    public void init() {
        if (!getBucket().exists("airline_sequence")) {
            N1qlQuery query = N1qlQuery.simple("SELECT MAX(id) + 1 as counterInit FROM `travel-sample` where type=\"airline\"");
            N1qlQueryResult result = bucket.query(query);
            if (result.finalSuccess()) {
                long counterInit = result.allRows().get(0).value().getLong("counterInit");
                bucket.insert(JsonLongDocument.create("airline_sequence", counterInit));
            }
        }
    }

    @PreDestroy
    public void stop() {
        bucket.close();
        cluster.disconnect();
    }

    public CouchbaseCluster getCluster() {
        if (null == cluster) {
            String host = System.getProperty("COUCHBASE_URI");
            if (host == null) {
                host = System.getenv("COUCHBASE_URI");
            }
            if (host == null) {
                throw new RuntimeException("Hostname is null");
            }
            System.out.println("env: " + host);
            cluster = CouchbaseCluster.create(host);
        }
        return cluster;
    }

    public Bucket getBucket() {
        while (null == bucket) {
            System.out.println("Trying to connect to the database");
            try {
                bucket = getCluster().openBucket("travel-sample", 2L, TimeUnit.MINUTES);
            } catch (com.couchbase.client.core.CouchbaseException e) {
                System.out.println("travel-sample bucket not ready yet ...");
            }
            try {
                System.out.println("Sleeping for 3 secs ...");
                Thread.sleep(3000);
            } catch (Exception e) {
                System.out.println("Thread sleep Exception: " + e.getMessage());
            }
        }
        System.out.println("Bucket found!");
        return bucket;
    }

    public static String getBucketName() {
        return "travel-sample";
    }
}
