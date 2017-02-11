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
        long travelSampleCount = 31591;

        N1qlQuery query = N1qlQuery.simple("SELECT count(*) as count FROM `travel-sample`");
        N1qlQueryResult result = null;
        long count = 0;
        while (result == null || count != travelSampleCount) {
            try {
                result = getBucket().query(query);
                if (result.finalSuccess()) {
                    count = result.allRows().get(0).value().getLong("count");
                } else {
                    System.out.println("Travel sample bucket not ready ...");
                }
            } catch (com.couchbase.client.core.ServiceNotAvailableException ex) {
                System.out.println("Query service not up ...");
            }
            try {
                System.out.println("Sleeping for 3 secs (waiting for Query service or bucket to be loaded) ...");
                Thread.sleep(3000);
            } catch (Exception e) {
                System.out.println("Thread sleep Exception: " + e.getMessage());
            }
        }
        System.out.println(count + " number of JSON documents in bucket.");

        query = N1qlQuery.simple("SELECT MAX(id) + 1 as counterInit FROM `travel-sample` where type=\"airline\"");
        result = getBucket().query(query);
        while (!result.finalSuccess()) {
            try {
                System.out.println("Sleeping for 3 secs (waiting for indexes) ...");
                Thread.sleep(3000);
            } catch (Exception e) {
                System.out.println("Thread sleep Exception: " + e.getMessage());
            }
            result = getBucket().query(query);
        }

        long counterInit = result.allRows().get(0).value().getLong("counterInit");
        bucket.insert(JsonLongDocument.create("airline_sequence", counterInit));
    }

    @PreDestroy
    public void stop() {
        bucket.close();
        cluster.disconnect();
    }

    public CouchbaseCluster getCluster() {
        if (null != cluster) {
            return cluster;
        }

        String host = System.getProperty("COUCHBASE_URI");
        if (host == null) {
            host = System.getenv("COUCHBASE_URI");
        }
        if (host == null) {
            throw new RuntimeException("Hostname is null");
        }
        System.out.println("env: " + host);
        cluster = CouchbaseCluster.create(host);
        return cluster;
    }

    public Bucket getBucket() {
        if (null != bucket) {
            return bucket;
        }

        while (null == bucket) {
            System.out.println("Trying to connect to the database");
            try {
                bucket = getCluster().openBucket("travel-sample", 2L, TimeUnit.MINUTES);
            } catch (Exception e) {
                System.out.println("travel-sample bucket not ready yet ...");
            }
            try {
                System.out.println("Sleeping for 3 secs (waiting for travel-sample bucket) ...");
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
