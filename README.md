# CHISoftwareTestTask

This is a small contact book backend application built with Spring Boot and MariaDB.
## Getting Started
### Github
Clone the repository:
```bash
git clone https://github.com/Titan43/InForceTestTask.git
```
Configure the MariaDB connection:

Open the ```src/main/resources/application.properties``` file.

Modify the database connection properties according to your MariaDB server configuration.
By default, the application connects to MariaDB using the following properties:

```
Database URL: jdbc:mysql://localhost:3306/contactbook
Username: root
Password: 3425
```
You can modify these properties in the application.properties file.

Build the application:
```
cd backend-app
mvn clean install
```
### Docker
Alternatively you can use docker image from docker hub:
```
docker pull t1tan45/test-task-image:latest
```
Configure DB the same way as in the GitHub variant inside ```application.properties```.

Run the image using external config:
```
docker run -v <path to folder with application.properties>:/app/config -p 8080:8080 <imageID>
```

## API has the following endpoints:

### User
- ```POST /auth```: Authenticate a user and generate an access token.

- ```POST /register```: Register a new user. Takes ```application/json``` as body.

- ```DELETE /delete```: Delete a user by token.

### Contact

- ```POST /contacts```: Create a new contact for a user. Takes ```form-data``` as body. ```form-data``` includes "image" and "contact" parts  

- ```GET /contacts```: Get all contacts for a user.

- ```DELETE /contacts/{contactName}```: Delete a contact by its name.

- ```PUT /contacts/{contactName}```: Update a contact by its name. Takes ```form-data``` as body. ```form-data``` includes "image" and "contact" parts.

## Entities used in API:
### User:
- id
- login
- password
### Contact:
- id
- name
- phones(1:n)
- emails(1:n)
- user(n:1)
  
