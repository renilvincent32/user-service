# user-service

#### This managed service allows you to perform following operations.

- Creating a new user
- Updating an existing user
- Deleting an existing user
- Fetching a list of users, falling under a given age group
- Login user

#### Application code is written using Java 8 and Spring boot framework. The backend database used is Postgres.

## Preconditions

- Maven has to be installed
- Docker has to be installed, and running
- Postman (or similar REST clients) is required

## How to run the application ?

- Checkout the code
- run `mvn clean package` on the root directory of the project, to build the project JAR
- Once the build is successful, run `docker-compose up` on the root directory of the project, to make sure docker containers are up and running
- After this step, the application is running at http://localhost:8080
- Open Postman (or similar tools) and perform various operations mentioned above

## REST APIs

- ### Create user
  - #### URL: http://localhost:8080/api/user
  - #### Method: POST
  - #### Content-type: `application/json`
  - #### Sample request: `{"email" : "user@gmail.com","password": "password", "age" : 29 }`
  - #### Status Code: 201 CREATED
  - #### Validations: 
    - ##### Email should be unique, else exception is thrown (400 BAD REQUEST)
    - ##### Email should be well-formed, else exception is thrown (400 BAD REQUEST)

- ### Update user
  - #### URL: http://localhost:8080/api/user/{email}
  - #### Method: PATCH
  - #### Sample request: `{"email" : "user@gmail.com","password": "newPassword" }`
  - #### Status Code: 200 OK
  - #### Validations
    - ##### Age cannot be patched. If the PATCH request contains `age`, it throws exception (400 BAD REQUEST)

- ### Delete user
  - #### URL: http://localhost:8080/api/user/{email}
  - #### Method: DELETE
  - #### Status code: 204 NO_CONTENT

- ### Fetch users
  - #### URL: http://localhost:8080/api/user?minAge={someValue}&maxAge={someValue}
  - #### Method: GET
  - #### Accept: `application/json`
  - #### Status code: 200 OK

- ### Login user
  - #### URL: http://localhost:8080/api/user/login
  - #### Method: POST
  - #### Content-Type: `application/json`
  - #### Sample request: `{"email" : "user@gmail.com","password": "password" }`
  - #### Status code: 200 OK
  - #### Returns a string indicating whether the login is successful or not

## Unit tests

- All the APIs have been unit-tested at controller level.
- Did not write integration test cases, due to the added complexity. Performed integration tests using Postman only.

## Cleanup

- run `docker-compose down` at the project root directory, to remove both app and postgres docker containers
- run `docker rmi user-app` to remove the docker image for the application

