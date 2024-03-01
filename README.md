# ChessMate: Online Chess Platform
ChessMate is an online chess platform where players can engage in player-versus-player (PvP) matches or compete against AI. The platform supports both rated games and friendly matches, catering to a wide audience ranging from complete beginners to seasoned chess veterans. ChessMate's goal is to make chess more accessible and enjoyable for everyone by eliminating the need for physical boards or in-person opponents. This web-based application is developed with Spring Boot for the backend and Angular for the frontend.

## Architectural Overview
### Technical Stack
- **JDK 21** - Java Development Kit
- **Spring Boot** - Backend framework for building REST API applications
- **Spring Data JPA** -  Provides repository support for JPA data access
- **PostgreSQL** - An open-source relational database
- **Flyway** - Manages database migrations
- **JUnit** - Facilitates unit testing in Java
- **WebSocket** -  Enables real-time, bidirectional communication between clients and servers.
- **Auth0** - Offers authentication and authorization as a service.
- **Angular 17** - Frontend framework to build a SPA applications
- **PrimeNG** - Rich set of UI components for the Angular
- **Bootstrap 5** - CSS library for quickly prototyping layouts

### Design Patterns
- Mode-View-Controller (MVC)
- Repository
- Inversion of Control / Dependency injection

## Getting Started
To set up ChessMate locally, follow these steps:

1. Install SDKs
    - [Download](https://www.oracle.com/java/technologies/downloads) and install the JDK 21.
    - [Download](https://nodejs.org/en/download) and install the Node.js runtime.
    - [Download](https://www.postgresql.org/download) and install PostgreSQL database.

2. Install Angular app NPM packages: navigate to the frontend directory and install dependencies:
    ```shell
    cd ./frontend
    npm install
    ```

3. Modify the database connection strings in Spring Boot's [application.properties](./backend/src/main/resources/application.properties) file. Update the following sections:
    - `spring.datasource.url` - Specify the database server host address with port number and instance name.
    - `spring.datasource.username` - Specify the the database username
    - `spring.datasource.password` - Specify the database user password
    - `spring.flyway.url` - Same values from the `spring.datasource` values for the Flyway migration.
    - `spring.flyway.user`
    - `spring.flyway.password`

4. Run applications:
    - To start the backend application:
    ```shell
    cd ./backend
    ./gradlew bootRun
    ```

    - To launch the frontend application:
    ```shell
    cd ./frontend
    npm run start
    ```

5. Use the following local URLs to access the apps:
    - Backend API: http://localhost:8000
    - Frontend UI: http://localhost:8001


## Roadmap
View the [roadmap gantt chart](./docs/roadmap.pdf) file to explore implemented and planned functionalities
