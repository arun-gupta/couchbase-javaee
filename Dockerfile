FROM jboss/wildfly:latest

COPY target/couchbase-javaee.war /opt/jboss/wildfly/standalone/deployments/airlines.war
