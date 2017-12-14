FROM jboss/wildfly

ADD ./target/ceps.war /opt/jboss/wildfly/standalone/deployments/

EXPOSE 8080

EXPOSE 8787

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-c", "standalone-full.xml", "-b", "0.0.0.0", "--debug"]