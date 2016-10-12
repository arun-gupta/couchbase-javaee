package org.couchbase.sample.javaee;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * @author Arun Gupta
 */
@ApplicationPath("resources")
public class MyApplication extends Application {

    final Set<Class<?>> classes = new HashSet<>();

    @Override
    public Set<Class<?>> getClasses() {
        classes.add(AirlineResource.class);
        return Collections.unmodifiableSet(classes);
    }

}
