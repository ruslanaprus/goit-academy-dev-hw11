# Use the official Tomcat 10.1.3 image from Docker Hub
FROM tomcat:10.1.24-jdk21

# Copy a custom WAR file into the webapps directory, rename your WAR file to ROOT.war when copying it
COPY build/libs/time-servlet-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

# Expose port 8080 for the Tomcat server
EXPOSE 8080

# Start the Tomcat server
CMD ["catalina.sh", "run"]