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
import java.lang.Thread;

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
//            cluster = CouchbaseCluster.create("192.168.99.100");
            cluster = CouchbaseCluster.create(System.getenv("COUCHBASE_URI"));
        }
        return cluster;
    }
    
    public Bucket getBucket() {
        while (null == bucket) {
    		System.out.println("Trying to connect to the database");
        	try {
        		bucket = getCluster().openBucket("travel-sample", 2L, TimeUnit.MINUTES);
        	} catch (com.couchbase.client.core.ServiceNotAvailableException e) {
        		System.out.println("Exception: " + e.toString());
        	}
        	try {
        		Thread.sleep(10000);
        	} catch (Exception e) {
        		System.out.println("Thread sleep Exception: " + e.toString());        		
        	}
        }
        
        return bucket;
    }
}
