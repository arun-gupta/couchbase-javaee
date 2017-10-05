FROM jboss/wildfly:latest

COPY target/airlines.war /opt/jboss/wildfly/standalone/deployments/airlines.war
