FROM jboss/wildfly

COPY target/couchbase-javaee.war /opt/jboss/wildfly/standalone/deployments/couchbase-javaee.war
