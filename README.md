# CityEcoScout: A Platform for Exploring Sustainable Locations Worldwide

![a  Login Screen](https://github.com/user-attachments/assets/63109ac6-7841-4ab8-98cc-d5df7ae90498)


CityScout is an innovative mobile platform designed to help users discover and evaluate environmentally responsible locations in their cities and beyond. By leveraging advanced APIs (Google Maps, Street View, Places) and cutting-edge Gemini AI technology, the application provides real-time sustainability metrics and immersive visualization of eco-friendly destinations.


## Table of Contents

- [CityScout: A Platform for Exploring Sustainable Locations Worldwide](#cityscout-a-platform-for-exploring-sustainable-locations-worldwide)
- [Features](#features)
- [Software Architecture](#software-architecture)
- [Backend Project Structure](#backend-project-structure)
- [Docker Configuration](#docker-configuration)
- [Technologies Used](#technologies-used)
  - [Backend](#backend)
  - [Frontend](#frontend)
  - [External Services](#external-services)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Local Development Setup](#local-development-setup)
- [Quality Assurance](#quality-assurance)
- [Video Demo](#video-demo)
- [Contributors](#contributors)


## Features

![image](https://github.com/user-attachments/assets/a085ad81-496a-478a-bb9f-e1744da79b82)

 **Interactive Map Exploration**
 
  - Real-time location search with filters
  - Street View integration
  - Custom place markers for eco-friendly locations

 **AI-Powered Assistant**
 
  - Gemini AI integration 
  - Multi-language support
  - Image recognition capabilities

 **Community Features**
 
  - User posts and reviews
  - Event sharing
  - Sustainability tips

 **Sustainable Place Discovery**
 
  - Eco-friendly location filtering
  - Sustainability ratings
  - Environmental impact metrics

 **Secure Authentication**
 
  - JWT-based authentication
  - Role-based access control
  - Secure password management



## Software Architecture

The application follows a modern, scalable architecture:

![image](https://github.com/user-attachments/assets/e428a714-e292-45ee-8b29-e29fd60d52ab)


## Backend Project Structure

The backend code follows a modular and organized structure, leveraging the power of Spring Boot for building a robust and scalable application.

1. **com.ensa.CityScout**
   - **Main Application Class:** `CityScoutApplication.java` serves as the entry point for the Spring Boot application. It includes the main method to start the application.
   
2. **com.ensa.CityScout.controller**
   - **Controller Classes:** This package contains classes responsible for handling incoming HTTP requests. Each controller class is dedicated to a specific feature or entity, exposing RESTful endpoints. These classes interact with the services to process requests and return appropriate responses.

3. **com.ensa.CityScout.entity**
   - **Entity Classes:** This package includes classes representing data entities in the application. These classes are annotated with JPA annotations, defining the structure of the database tables. Each entity typically corresponds to a table in the MySQL database.

4. **com.ensa.CityScout.repository**
   - **Repository Interfaces:** This package contains interfaces that extend Spring Data JPA repositories. These interfaces provide methods for basic CRUD operations and are used by services to interact with the database.

5. **com.ensa.CityScout.service**
   - **Service Classes:** The service layer contains business logic. It acts as a bridge between the controllers and the repositories.

6. **com.ensa.CityScout.dto**
   - **Data Transfer Objects:** This package contains DTOs that are used to transfer data between different layers of the application.

7. **com.ensa.CityScout.config**
   - **Configuration Classes:** These classes define application-level configurations, including security, CORS, and custom beans.

8. **com.ensa.CityScout.security.oauth2**
   - **Security Implementation:** This package manages authentication and authorization using OAuth2 and JWT.

9. **com.ensa.CityScout.util**
   - **Utility Classes:** This package contains helper classes or methods that are reused throughout the application.

## Docker Configuration

```yaml
version: '3.8'

services:
  backend:
    build:
      context: ./CityScoutBACK
      dockerfile: Dockerfile
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/cityscout?createDatabaseIfNotExist=true
      - SPRING_DATASOURCE_USERNAME=root
      - SPRING_DATASOURCE_PASSWORD=
      - SPRING_JPA_HIBERNATE_DDL_AUTO=update
      - JWT_SECRET=moubarak_secure_key
    depends_on:
      - db
    networks:
      - cityscout-network

  db:
    image: mysql:8.0
    environment:
      - MYSQL_DATABASE=cityscout
      - MYSQL_ALLOW_EMPTY_PASSWORD=yes
      - MYSQL_ROOT_HOST=%
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
    networks:
      - cityscout-network
    command: --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci

volumes:
  mysql_data:

networks:
  cityscout-network:
    driver: bridge
```

## Technologies Used

### Backend
- **Framework:** Spring Boot 
- **Database:** MySQL 
- **Security:** Spring Security with JWT

### Frontend
- **Framework:** Jetpack Compose
- **Language:** Kotlin
- **Architecture:** MVVM
- **Navigation:** Compose Navigation
- **HTTP Client:** Retrofit
- **Maps:** Google Maps SDK

### External Services
- Google Maps Platform APIs
- Gemini AI API (Version 1.5 Flash)
- Street View API
- Places API

## Getting Started

### Prerequisites
- JDK 21 or higher
- Android Studio 
- Docker and Docker Compose
- MySQL
- Google Maps API key
- Gemini AI API key

### Local Development Setup

1. **Clone the Repository**
```bash
git clone https://github.com/yasminefhr1/CityScout_MobileApp.git
cd CityScout
```

2. **Environment Configuration**
```bash
# Create .env file in project root
cp .env.example .env
# Add your API keys and configuration
```

3. **Backend Setup**
```bash
cd backend
./mvnw clean install
./mvnw spring-boot:run
```

4. **Frontend Setup**
```bash
# Open the project in Android Studio
# Add your API keys 
# Sync Gradle files
# Run the application
```

5. **Docker Deployment**
```bash
docker-compose up -d
```

6. ## Chatbot Setup and Execution

To run the chatbot module based on Flask:
 **Navigate to the directory containing the `chatbot.py` file:**
   ```bash
   cd pythonCodes
   python chatbot.py
The chatbot will be available locally at http://127.0.0.1:5000 or on another port as configured in the chatbot.py code.
   ```


## Quality Assurance

SonarQube analysis demonstrates:
- 0% code duplication in backend
- 1.1% code duplication in frontend
- A-grade maintainability
- Comprehensive test coverage

## Video Demo

Click the link below to watch a demonstration video:


https://github.com/user-attachments/assets/1b1151b1-8684-41d1-9f7e-a61b37ce5923



## Contributors

- FIHRI Yasmine (GitHub: [link](https://github.com/yasminefhr1))
- LAHLYAL Ahmed Moubarak (GitHub: [link](https://github.com/amlmbr))
- Mohamed Lachgar (ResearchGate: [link](https://www.researchgate.net/profile/Mohamed-Lachgar))


