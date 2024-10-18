# Cattivity Time Servlet Project

Cattivity Time is a Java servlet-based web application that displays the current time and cat activity based on a user's selected time zone. The application allows users to view time in different time zones and see a related cat image and activity message. It also stores the last selected time zone using cookies, providing a seamless user experience on subsequent visits.

## Project Structure

The project is organized into the following packages and components:

- **org.example.controller**: Contains servlet and filter classes that handle HTTP requests, validate time zones, and render time-related pages.

    - `TimeServlet`: A servlet that handles GET requests to `/time`, processes time zone information, and renders the time page with the appropriate cat image and activity.
    - `TimezoneValidateFilter`: A filter that validates time zone parameters and retrieves time zone information from cookies.
- **org.example.listener**: Contains the listener to initialize the template engine.

    - `TemplateEngineInitializer`: Sets up the Thymeleaf template engine for rendering HTML templates.
- **org.example.service**: Contains service classes that handle time formatting, time zone management, and cookie handling.

    - `TimeResponseBuilder`: Provides methods to format the current time and retrieve relevant cat images and activity messages.
    - `TimezoneService`: Manages conversion of time zone parameters into valid `ZoneId` objects.
    - `TimezoneCookieService`: Handles storing and retrieving time zone data using cookies.
- **org.example.util**: Contains utility classes for rendering HTML pages.

    - `ThymeleafRenderer`: Renders Thymeleaf templates into HTTP responses, including handling error pages.
- **webapp/WEB-INF/templates**: Stores the Thymeleaf templates for the HTML pages.

    - `time.html`: The main template for displaying the time, cat image, and activity.
    - `error.html`: Template used for displaying error messages.

## Tools and Technologies

The project utilizes the following tools and technologies:

- **Java 21**: The core programming language used for the project.
- **Jakarta Servlet API**:  For handling HTTP requests and responses.
- **Thymeleaf**: For rendering dynamic HTML content.
- **SLF4J and Logback**: For logging information and debugging.
- **JUnit 5**: For unit testing.
- **Mockito**: For mocking objects in unit tests.
- **JUnit & Mockito**: For writing and executing unit tests.
- **Gradle**: Used for dependency management and building the project as a WAR (Web Application Archive) file.

## Functionality

The application provides the following functionalities:

1. **Display Current Time**: Displays the current time for a given timezone parameter. If no time zone is provided, the application defaults to UTC.
2. **Time Zone Validation**: Validates the timezone parameter using `TimezoneValidateFilter`, retrieves the time zone from cookies if available. If the timezone is invalid, an error message is displayed.
3. **Display Cat Activities**: Based on the time of day, displays different cat images and corresponding activity messages.
4. **Cookie Storage**: Stores the last selected time zone in a cookie.
5. **Error Handling**: If the timezone parameter is missing or incorrect, the user is shown a descriptive error page.
6. **Formatted Time**: Displays the time in the format `yyyy-MM-dd HH:mm:ss` along with the UTC offset.

## Usage Instructions

### Prerequisites

- **Java 21** installed on your machine.
- **Gradle** installed or use the provided Gradle wrapper (`gradlew`).
- A **Java servlet container** like Apache Tomcat 10.

### Setup Instructions

1. **Clone the repository**:
```shell
git clone git@github.com:ruslanaprus/goit-academy-dev-hw11.git
cd goit-academy-dev-hw11
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

1. Visit the main page http://localhost:8080/ to see a greeting and a button to check the cat activities at the current time:
2. Click the `Show Cattivity` button or directly access the time page: http://localhost:8080/time
3. Enter your time zone in the text field and click button `Get Time`. Optionally, you can specify a time zone in the query parameter: http://localhost:8080/time?timezone=Europe/London
4. This will display the current time for the specified time zone along with a corresponding cat image and cattivity message.

The application will store your last selected time zone in a cookie. On subsequent visits, it will automatically display the time for the previously selected time zone, if you won't specify the time zone.

**Handling Invalid Timezones**:

If an invalid or missing timezone parameter is provided, the application displays an error page with a 400 status code and a relevant message.

### Example URLs

To view the current time in UTC:
http://localhost:8080/time?timezone=UTC

To view the time in a specific offset:
http://localhost:8080/time?timezone=UTC+3

Invalid timezone example:
http://localhost:8080/time?timezone=InvalidZone