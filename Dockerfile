FROM jboss/wildfly

RUN yum -y install maven && yum clean all

RUN mvn clean package

ADD ./src/main/docker/postgresql-9.1-903.jdbc4.jar /opt/jboss/wildfly/standalone/deployments/

ADD ./target/ceps.war /opt/jboss/wildfly/standalone/deployments/

EXPOSE 8080

EXPOSE 8787

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-c", "standalone-full.xml", "-b", "0.0.0.0", "--debug"]