# Cache Service Application

This is a Spring Boot application that provides cache management for `Employee` entities using an in-memory cache and Hibernate-backed database persistence. The application is designed to manage cache operations such as adding, removing, and retrieving employee entities, along with automatic cache eviction when the cache size limit is reached.

## Features

- **In-memory Cache:** Utilizes an in-memory cache for storing `Employee` entities.
- **Cache Eviction:** When the cache reaches its maximum size, the least recently used (LRU) entry is evicted and saved to the database.
- **Database Integration:** Uses H2 in-memory database for persisting employee data.
- **Cache Management:** Provides endpoints to add, remove, clear cache, and fetch employee data either from the cache or database.

## Endpoints

### 1. **Add Entity to Cache**

- **URL:** `/add`
- **Method:** `POST`
- **Request Body:**
  - `Employee` entity to be added to the cache.
  - Example:
    ```json
    {
      "id": 1,
      "name": "John Doe",
      "salary": 1000.0
    }
    ```
- **Description:** Adds a new `Employee` entity to the cache. If the cache is full, the least recently used entry will be evicted to the database.
- **Response:**
  - **Status:** 200 OK
  - **Body:**
    ```json
    {
      "message": "Entry added successfully."
    }
    ```

### 2. **Remove Entity from Cache and Database**

- **URL:** `/remove`
- **Method:** `DELETE`
- **Request Body:**
  - `Employee` entity to be removed from both the cache and the database.
  - Example:
    ```json
    {
      "id": 1
    }
    ```
- **Description:** Removes an `Employee` entity from both the cache and the database.
- **Response:**
  - **Status:** 200 OK
  - **Body:**
    ```json
    {
      "message": "Entry removed from cache and DB successfully."
    }
    ```

### 3. **Remove All Entities from Cache and Database**

- **URL:** `/removeAll`
- **Method:** `DELETE`
- **Description:** Removes all `Employee` entities from both the cache and the database.
- **Response:**
  - **Status:** 200 OK
  - **Body:**
    ```json
    {
      "message": "All entries removed from cache and DB successfully."
    }
    ```

### 4. **Get Entity from Cache or Database**

- **URL:** `/get/{id}`
- **Method:** `GET`
- **URL Parameters:**
  - `id` (Long) - The ID of the `Employee` entity to retrieve.
- **Description:** Retrieves an `Employee` entity by its ID. If the entity is not found in the cache, it will be fetched from the database and added to the cache.
- **Response:**
  - **Status:** 200 OK
  - **Body:**
    ```json
    {
      "id": 1,
      "name": "John Doe",
      "salary": 1000.0
    }
    ```

### 5. **Clear All Entities from Cache**

- **URL:** `/clear`
- **Method:** `DELETE`
- **Description:** Clears all entries from the cache without affecting the database.
- **Response:**
  - **Status:** 200 OK
  - **Body:**
    ```json
    {
      "message": "All entries cleared from cache successfully."
    }
    ```

### To view Swagger UI

Run the server and browse to http://localhost:8080/swagger-ui.html

## Technologies Used

* Spring Boot for creating the RESTful application.
* Hibernate for ORM database interaction using JPA repository
* H2 Database for in-memory data storage.
* Slf4j Logging for logging throughout the application.
* Lombok for reducing boilerplate code.

## Running the Application

* Clone the repository using https://github.com/pandi-r/jpmc_repo.git
* Run the application using preferred method (e.g., IDE or command line).
* Access the H2 console at http://localhost:8080/h2-console for direct database interaction.