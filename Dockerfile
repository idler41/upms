FROM idler41/tomcat8-jdk8-log4j2:1.0

ENV CONTEXT_PATH=/

COPY target/*.war $TOMCAT_HOME/webapps/ROOT.war

CMD ["./entrypoint.sh"]