# Pattern Finder
Finding a 6x1 (3px white - 3px red) stripe pattern on provided image.

## Build Tools
App was written using JDK 11. 
To build the app, you need Maven. 
Use: `mvn clean package spring-boot:repackage` to build the app. Then issue `java -jar target/Pattern.Finder-0.0.1-SNAPSHOT.jar`  in the same directory to run it. 

## Using
After starting the app, it should be available at http://localhost:8080 , where you can choose a file to upload. 

## Frameworks and libraries
To create the project I used Spring Boot with Web dependency. No additional frameworks and libraries are included, I used pure Java and Spring Boot. Tests are written in JUnit 5 (which are part of Spring Boot Test).

## Javadoc
You should be able to generate a Javadoc by running
 `mvn javadoc:javadoc` in main folder. Generated javadoc should be present at `/target/site/apidocs/index.html`

## Endpoints
App provides only one endpoint: `/rotate` which accepts POST requests with MultipartFile. You should not be able to upload anything that isn’t PNG image. 
Possible outcomes:
	- Image rotated according to the instructions,
	- 204 No Content - when no pattern or no image
	- 400 Bad Request - when more than 1 pattern or file is not PNG
All of the above can be checked in browser’s tools.