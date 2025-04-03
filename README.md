# Task API

## Overview

Task API is a Java-based application for managing tasks.\
It uses Spring Boot, Spring Data JPA, Liquibase, and Kafka for task management and messaging.

## Features

- Create, update, delete, and retrieve tasks
- Kafka integration for task creation
- Liquibase for database migrations
- Swagger UI for API documentation

## Technologies

- Java
- Spring Boot
- Spring Data JPA
- Liquibase
- Kafka
- PostgreSQL
- Maven

## Prerequisites

- Java 21 or higher
- Maven 3.6.0 or higher
- PostgreSQL
- Kafka

## Getting Started

### Run the application

Run the application
Use Maven to build and run the application:

```sh
mvn clean install
mvn spring-boot:run
```

### Swagger UI

The application includes Swagger UI for API documentation. You can access it at:

- Swagger UI: [http://localhost:8080/api/v1/swagger](http://localhost:8080/api/v1/swagger)
- API Documentation: [http://localhost:8080/api/v1/api-docs](http://localhost:8080/api/v1/api-docs)

## API Endpoints

### Task Endpoints

| Method | Endpoint                    | Description                       |
|--------|-----------------------------|-----------------------------------|
| GET    | /api/v1/tasks               | Get all tasks                     |
| GET    | /api/v1/tasks/{id}          | Get task by ID                    |
| POST   | /api/v1/tasks               | Create a new task                 |
| PUT    | /api/v1/tasks/{id}          | Update an existing task           |
| DELETE | /api/v1/tasks/{id}          | Delete a task                     |

### Authentication Endpoints

| Method | Endpoint                    | Description                      |
|--------|-----------------------------|----------------------------------|
| POST   | /api/v1/tasks/auth/register | Register a new user              |
| POST   | /api/v1/tasks/auth/login    | Login with username and password |

### Actuator, Metrics and Health Check Endpoints

| Method | Endpoint                       | Description                      |
|--------|--------------------------------|----------------------------------|
| GET    | /api/v1/tasks/actuator         | Get actuator endpoints           |
| GET    | /api/v1/tasks/actuator/health  | Check the health of the service  |
| GET    | /api/v1/tasks/actuator/metrics | Get application metrics          |

## Database

The application uses PostgreSQL as the database.\
The database configuration can be found in the `application.yaml` file.\
Make sure to update the database URL, username, and password according to your local setup.

### Configure database

Update the application.yaml file with your PostgreSQL database credentials:

```yaml
spring:
  datasource:
    url: "jdbc:postgresql://localhost:5432/your_database_name"
    username: "your_username"
    password: "your_password"
```

## Liquibase

Liquibase is used for database migrations.\
The changelogs are located in the `src/main/resources/db/changelog` directory.\

### Run migrations

To run the migrations, you can use the following command:

```sh
mvn liquibase:update
```
This will apply any pending migrations to the database.\
Alternatively, you can run the application, and Liquibase will automatically apply the migrations on startup.

### Execute rollback migration

To rollback the last migration, you can use the following command:

```sh
mvn liquibase:rollback -Dliquibase.rollbackCount=1
```
This will rollback the last migration applied to the database.\
You can specify the number of migrations to rollback by changing the value of `liquibase.rollbackCount`.

## Kafka

The application uses Kafka for messaging.\
Make sure to have a Kafka broker running and update the Kafka configuration in the `application.yaml` file.\
The default configuration uses `localhost:9092` as the Kafka broker address.

### Configure Kafka

The application listens to Kafka messages on the topic specified in the application.yaml file:

```yaml
spring:
  kafka:
    bootstrap-servers:
    topic:
    consumer:
      group-id:
```

#### Using Docker Compose

You can use Docker Compose to run Kafka and Zookeeper locally.\
To start the Kafka container, run the following command in the directory where the docker-compose.yaml file is located:

```sh
docker-compose up -d
```

This will start a Kafka broker and a Zookeeper instance.\
The Kafka broker will be accessible at localhost:9092.\
Update the Kafka configuration in the application.yaml file:

```yaml
spring:
  kafka:
    bootstrap-servers: "localhost:9092"
    topic: "tasks-topic"
    consumer:
      group-id: "tasks-group"
```

Make sure to have the Kafka broker running before starting the application.

## Testing
The application includes unit tests and integration tests.\
You can run the tests using the following command:

```sh
mvn test
```

This will run all the tests in the project.

## License
Feel free to customize this `README.md` file further based on your specific requirements.
