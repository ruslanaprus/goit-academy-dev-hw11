# Time Servlet Project

The Time Servlet Project is a Java web application built using Jakarta Servlet API. It allows users to view the current time in different time zones. The application takes a timezone parameter, validates it, and then displays the current time in the specified time zone. If an invalid or missing timezone parameter is provided, the application returns a user-friendly error message.

## Project Structure

The project follows a standard Java web application structure. Here's an overview of the project organization:

```shell
├── 󱧼 src
│   ├──  main
│   │   ├──  java
│   │   │   └──  org
│   │   │       └──  example
│   │   │           ├──  controller
│   │   │           │   ├──  TimeServlet.java
│   │   │           │   └──  TimezoneValidateFilter.java
│   │   │           ├──  service
│   │   │           │   ├──  TimeResponseBuilder.java
│   │   │           │   └──  TimezoneService.java
│   │   │           └──  util
│   │   │               └──  ErrorResponseUtil.java
│   │   ├──  resources
│   │   └──  webapp
│   │       ├──  index.jsp
│   │       └──  WEB-INF
│   │           ├──  views
│   │           │   ├──  error.jsp
│   │           │   └──  time.jsp
│   │           └──  web.xml
│   └──  test
├──  build.gradle
├──  gradle
├──  gradlew
├──  gradlew.bat
└──  settings.gradle
```

## Tools and Technologies

The project utilizes the following tools and technologies:

- **Java 21**: The core language used for development.
- **Jakarta Servlet API**: For handling HTTP requests and responses.
- **JSP (JavaServer Pages)**: For rendering dynamic web pages.
- **Gradle**: Build tool for managing dependencies and building the project.
- **Logback**: For logging application events.
- **JUnit 5**: For unit testing.
- **Mockito**: For mocking objects in unit tests.
- **Apache Commons Text**: For safely handling and escaping input strings.

## Functionality

The application provides the following functionalities:

1. **Display Current Time**: Displays the current time for a given timezone parameter.
2. **Timezone Validation**: Validates the timezone parameter using `TimezoneValidateFilter`. If the timezone is invalid, an error message is displayed.
3. **Error Handling**: If the timezone parameter is missing or incorrect, the user is shown a descriptive error page.
4. **Formatted Time**: Displays the time in the format `yyyy-MM-dd HH:mm:ss` along with the UTC offset.

## Usage

### Prerequisites

- **Java 21** installed on your machine.
- **Gradle** installed or use the provided Gradle wrapper (`gradlew`).
- A **Java servlet container** like Apache Tomcat 10.

### Setup Instructions

1. **Clone the repository**:
```shell
git clone git@github.com:ruslanaprus/goit-academy-dev-hw10.git
cd goit-academy-dev-hw10
```
   
2. **Build the project using Gradle**:

```shell
./gradlew build
```

3. **Deploy the WAR file**:

- The built `.war` file will be located in the `build/libs` directory.
- Deploy the `.war` file to your servlet container (e.g., place it to the `webapps` directory of your Tomcat server).

4. **Start the server**:

- Start your servlet container (e.g., `catalina.sh run` for Tomcat).
- Visit `http://localhost:8080` to access the application.

**Optional: Deploy Using Docker**

Alternatively, you can deploy the application using Docker with the official Tomcat 10 image from Docker Hub. You can modify the Dockerfile to use the latest image version if necessary.

To build the Docker image:

```shell
docker build -t tomcat-time-servlet:1.0 .
```

To run the container:
```shell
docker run -d -p 8080:8080 --name time-servlet-app tomcat-time-servlet:1.0
```

### Using the Application

**View the Current Time**:

1. Navigate to http://localhost:8080/time?timezone=Europe/London. 
2. Replace `Europe/London` with any valid timezone identifier (e.g., `America/New_York` or `UTC+3`). 
3. The application will display the current time in the specified timezone.

**Handling Invalid Timezones**:

If an invalid or missing timezone parameter is provided, the application displays an error page with a 400 status code and a relevant message.

### Example URLs

To view the current time in UTC:
http://localhost:8080/time?timezone=UTC

To view the time in a specific offset:
http://localhost:8080/time?timezone=UTC+3

Invalid timezone example:
http://localhost:8080/time?timezone=InvalidZone