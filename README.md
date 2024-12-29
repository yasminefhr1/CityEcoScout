# CityScout: A Platform for Exploring Sustainable Locations Worldwide

![image](https://github.com/user-attachments/assets/26bdd20a-c0fe-47d0-b616-ebadca065a9e)

CityScout is an innovative AI-powered mobile platform designed to help users discover and evaluate environmentally responsible locations in their cities and beyond. By leveraging advanced APIs (Google Maps, Street View, Places) and cutting-edge Gemini AI technology, the application provides real-time sustainability metrics and immersive visualization of eco-friendly destinations.


## Table of Contents

- [CityScout: A Platform for Exploring Sustainable Locations Worldwide](#cityscout-a-platform-for-exploring-sustainable-locations-worldwide)
- [Features](#features)
- [Software Architecture](#software-architecture)
- [Docker Configuration](#docker-configuration)
- [Technologies Used](#technologies-used)
  - [Backend](#backend)
  - [Frontend](#frontend)
  - [External Services](#external-services)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Local Development Setup](#local-development-setup)
- [Security](#security)
- [Quality Assurance](#quality-assurance)
- [Video Demo](#video-demo)
- [Contributors](#contributors)
- [Screens](#screens)


## Features

 **Interactive Map Exploration**
 
  - Real-time location search with filters
  - Street View integration
  - Custom place markers for eco-friendly locations

 **AI-Powered Recommendations**
 
  - Gemini AI integration for smart suggestions
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

## Security

- JWT-based authentication
- HTTPS encryption
- API key validation
- Input validation and sanitization
- Role-based access control

## Quality Assurance

SonarQube analysis demonstrates:
- 0% code duplication in backend
- 1.1% code duplication in frontend
- A-grade maintainability
- Zero security hotspots
- Comprehensive test coverage

## Video Demo

Click the link below to watch a demonstration video:


https://github.com/user-attachments/assets/1b1151b1-8684-41d1-9f7e-a61b37ce5923



## Contributors

- FIHRI Yasmine (GitHub: [link](https://github.com/yasminefhr1))
- LAHLYAL Ahmed Moubarak (GitHub: [link](https://github.com/amlmbr))
- Mohamed Lachgar (ResearchGate: [link](https://www.researchgate.net/profile/Mohamed-Lachgar))

## Screens

![image](https://github.com/user-attachments/assets/27734f4e-daeb-4224-98af-85ada53d979a)
![image](https://github.com/user-attachments/assets/4ce8a645-e678-4d93-898f-f4c24100cda4)
![image](https://github.com/user-attachments/assets/640e1d58-ad67-48fc-9882-46d2ae035880)

