# The basic skeleton structure of the project that we initiated is referenced from:     #
#          "https://github.com/shekhargulati/spring-boot-maven-angular-starter"         #
# Based on the POM dependecy existed, following the skeleton mvn strucutre, we build everything upon that.                                              
# VERSION #
The project use the following versions
- Spring Boot v1.5.9
- Angular v5.0.4
- Node v8.9.0
- Yarn v1.3.2

# STRUCTURE #
We have three pom files under the assignment3 folder.
- One of them is under assignment3/
- One of them is under assignment3/backend/
- One of them is under assignment3/frontend/

# HOW TO RUN #
To run the full application:
- Switch to the directory assignment3/
- run "java -jar backend/target/ngboot-app.jar" in the console

To run the backend for development mode
- Switch to the directory assignment3/backend/
- run "mvn spring-boot:run" if you have maven installed
  run "../mvnw spring-boot:run" if you do not have maven installed

To run the frontend for development mode
- Make sure you install "yarn" on the machine
- Switch to assignment3/frontend/
- run "mvn frontend:install-node-and-yarn frontend:yarn" if you have maven installed
  run "../mvnw frontend:install-node-and-yarn frontend:yarn" if you do not have maven installed

After the full application is run in the console, the website is available at "http://localhost:8090/"
The rest of the task should be fairly easy to figure out according to the frontend layout.

# NOTE #
For a better user experience, the following few tips might be useful
- You can search without input website or size, then the default website we use will be "kijiji"
- It could take fairly a long time to get a large number of housing information.
  For less waiting time, please try to input a "small" size, preferrably around 10-15

# FUTURE VERSION #
In the version comming up, we will add color to the analysis chart to differentiate each section.



