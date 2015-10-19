package org.couchbase.sample.javaee;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.CouchbaseCluster;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 * @author Arun Gupta
 */
@Singleton
@Startup
public class Database {
    
    CouchbaseCluster cluster;
    Bucket bucket;

    public CouchbaseCluster getCluster() {
        if (null == cluster) {
            cluster = CouchbaseCluster.create("192.168.99.101");
        }
        return cluster;
    }
    
    public Bucket getBucket() {
        if (null == bucket) {
            bucket = getCluster().openBucket("travel-sample");
        }
        return bucket;
    }
}
