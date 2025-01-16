# Fullstack Java Project

## Wesley Kissen 3AONC

## Folder structure

- Readme.md
- _architecture_: this folder contains documentation regarding the architecture of your system.
- `docker-compose.yml` : to start the backend (starts all microservices)
- _backend-java_: contains microservices written in java
- _demo-artifacts_: contains images, files, etc that are useful for demo purposes.
- _frontend-web_: contains the Angular webclient

Each folder contains its own specific `.gitignore` file.  
**:warning: complete these files asap, so you don't litter your repository with binary build artifacts!**

## How to setup and run this application

### Backend setup

1. First, navigate to the backend folder where your `docker-compose.yml` file is located.
2. Run the following command to start the backend services: docker-compose up
3. After starting up the Docker containers, initialize the services in this order:
    - Start the ConfigService service first.
    - Then start the Discovery service.
    - Next, start the Gateway microservice.
    - Then start the ReviewService microservice.
    - After that, start the PostService microservice.
    - Finally, start the CommentService microservice.

### Frontend setup

1. Navigate to the folder containing the Dockerfile for the frontend.
2. Build the frontend Docker image by running: docker build -t posts-app .
3. Run the frontend application container: docker run -d -p 8080:80 posts-app

Now both your backend and frontend should be up and running!

:heavy_check_mark:_(COMMENT) Add setup instructions and provide some direction to run the whole  application: frontend to backend._
