# DebtMates Backend

## Overview

This repository contains the backend for *DebtMates*, a platform designed to simplify group financial management, including debt tracking and rotational savings. The backend is built using **Spring Boot**, with **PostgreSQL** as the database, **Redis** for caching, **Cloudinary** for image uploads, and **Gemini AI** for intelligent debt assignment. It provides RESTful API endpoints to support user authentication, group management, debt tracking, rotational savings, and personal savings plans.

The backend integrates with the *DebtMates* frontend (maintained in a separate repository https://github.com/Dilusha-Ranasingha/DebtMates-FE) to deliver a seamless user experience for managing group finances.

<img width="782" alt="debtmateswall" src="https://github.com/user-attachments/assets/2319ae30-bf16-4145-9b7a-c21c9f2205e3" />


## Table of Contents

- [Features](#features)
- [Technologies](#technologies)
- [Project Structure](#project-structure)
- [Setup Instructions](#setup-instructions)
- [API Endpoints](#api-endpoints)
- [Environment Configuration](#environment-configuration)
- [Docker Setup](#docker-setup)
- [Contributing](#contributing)
- [License](#license)

## 🌟Features

- **User Authentication**: Supports user registration, login, logout, password reset, and profile management with JWT-based authentication.
- **Admin Management**: SuperAdmin can manage admin users, view user/admin activity logs, and perform administrative tasks.
- **Group Management**: Create, update, and manage groups with members for collaborative financial tracking.
- **Debt Management**: Record and track debts within groups, with intelligent debt assignment powered by Gemini AI.
- **Rotational Savings**: Manage rotational savings groups, including plans, payments, and payment slip uploads via Cloudinary.
- **Personal Savings Plans**: Create and manage individual savings plans with deposit tracking and statistics.
- **Activity Logging**: Tracks user registration and admin login activities for auditing purposes.

## 💻Technologies

- **Spring Boot**: Backend framework for building RESTful APIs.
- **PostgreSQL**: Relational database for persistent data storage.
- **Redis**: In-memory data store for caching and session management.
- **Cloudinary**: Cloud-based image storage for payment slip uploads.
- **Gemini AI**: AI-powered debt assignment for efficient debt management.
- **JWT**: JSON Web Tokens for secure authentication.
- **Spring Security**: Handles authentication and authorization.
- **Spring Data JPA**: Simplifies database operations with Hibernate.
- **Spring Mail**: Sends OTP emails for password reset.
- **Docker**: Containerizes the application and its dependencies.
- **Maven**: Dependency management and build tool.

## 📂Project Structure

```
debtmates-be/
├── src/
│   ├── main/
│   │   ├── java/com/example/debtmatesbe/
│   │   │   ├── controller/         # REST controllers for API endpoints
│   │   │   ├── model/              # Entity classes for database mapping
│   │   │   ├── service/            # Business logic and service classes
│   │   │   ├── repo/               # Repository interfaces for database operations
│   │   │   ├── util/               # Utility classes (e.g., JwtUtil)
│   │   │   ├── dto/                # Data Transfer Objects for API requests/responses
│   │   │   └── exception/          # Custom exception classes
│   │   └── resources/
│   │       ├── application.properties  # Configuration file for Spring Boot
├──── docker-compose.yml            # Docker configuration for services
├── pom.xml                         # Maven dependencies and build configuration
└── README.md                       # Project documentation
```

## 🚀Setup Instructions

### Prerequisites

- **Java 17+**: Ensure JDK is installed.
- **Maven**: For dependency management and building the project.
- **Docker**: For running PostgreSQL, Redis, and pgAdmin containers.
- **Cloudinary Account**: For image upload functionality.
- **Gemini AI API Key**: For intelligent debt assignment.
- **SMTP Server**: For sending OTP emails (e.g., Gmail SMTP).

### Steps

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/Dilusha-Ranasingha/DebtMates-BE.git.git
   cd debtmates-be
   ```

2. **Configure Environment Variables**:
   Update the `application.properties` file with the required configurations:
   - PostgreSQL database credentials (`spring.datasource.url`, `spring.datasource.username`, `spring.datasource.password`).
   - Redis connection details (`spring.data.redis.host`, `spring.data.redis.port`, `spring.data.redis.password`).
   - JWT secret (`jwt.secret`) and expiration time (`jwt.expiration`).
   - Gemini AI API key and URL (`gemini.api.key`, `gemini.api.url`).
   - Cloudinary credentials (`cloudinary.cloud-name`, `cloudinary.api-key`, `cloudinary.api-secret`).
   - SMTP server details for email (`spring.mail.host`, `spring.mail.port`, `spring.mail.username`, `spring.mail.password`).

3. **Build the Project**:
   ```bash
   mvn clean install
   ```

4. **Run the Application**:
   ```bash
   mvn spring-boot:run
   ```
   The application will start on `http://localhost:8080`.

5. **Run with Docker** (optional):
   See [Docker Setup](#docker-setup) for running the application with Docker.

## 🖇️API Endpoints

### Authentication (`/api/auth`)
- **POST /register**: Register a new user (or admin if authorized by SuperAdmin).
- **POST /login**: Authenticate a user and return a JWT token.
- **POST /logout**: Invalidate the user's JWT token.
- **POST /password-reset/request**: Request an OTP for password reset.
- **POST /password-reset/confirm**: Confirm password reset with OTP.

### Admin Management (`/api/admin`)
- **GET /users**: Fetch all users (paginated, admin access only).
- **GET /admins**: Fetch all admins (paginated, SuperAdmin only).
- **GET /users/{id}**: Fetch a user by ID (admin access only).
- **GET /activity/registrations/users**: View user registration activity (admin access only).
- **GET /activity/registrations/all**: View all registration activity (SuperAdmin only).
- **GET /activity/logins/admins**: View admin login activity (SuperAdmin only).
- **PUT /admins/{id}**: Update an admin's details (SuperAdmin only).
- **DELETE /admins/{id}**: Delete an admin (SuperAdmin only).

### Group Management (`/api/groups`)
- **POST /**: Create a new group.
- **PUT /{groupId}**: Update a group.
- **GET /me**: Retrieve the logged-in user’s groups.
- **POST /{groupId}/members**: Add members to a group.
- **GET /{groupId}/members**: Fetch group members.

### Debt Management (`/api`)
- **POST /groups/{groupId}/debts**: Record a debt in a group (uses Gemini AI for intelligent assignment).
- **GET /groups/{groupId}/debts**: Fetch all debts in a group.
- **GET /users/me/debts**: Fetch the logged-in user’s debts.

### Rotational Savings (`/api/rotational`)
- **POST /groups**: Create a rotational savings group.
- **PUT /groups/{groupId}**: Update a rotational savings group.
- **DELETE /groups/{groupId}**: Delete a rotational savings group.
- **POST /groups/{groupId}/members**: Add members to a rotational group.
- **POST /groups/{groupId}/plan**: Add a rotational savings plan.
- **GET /groups**: Fetch the logged-in user’s rotational groups.
- **GET /groups/{groupId}/payments**: Fetch payments for a rotational group.
- **PUT /payments/{paymentId}/slip**: Upload a payment slip (via Cloudinary).
- **GET /payments/{paymentId}/slip**: Retrieve a payment slip URL.
- **GET /groups/{groupId}/members**: Fetch rotational group members.

### Personal Savings Plans (`/api/savings-plans`)
- **POST /**: Create a new savings plan.
- **GET /{id}**: Fetch a savings plan by ID.
- **GET /user/{userId}**: Fetch all savings plans for a user.
- **PUT /{id}**: Update a savings plan.
- **DELETE /{id}**: Delete a savings plan.
- **POST /{id}/deposit**: Record a deposit to a savings plan.
- **GET /stats**: Fetch savings statistics for the logged-in user.

### User Management (`/api/user`)
- **GET /profile**: Fetch the logged-in user’s profile.
- **PUT /profile**: Update the logged-in user’s profile.
- **POST /change-password**: Change the logged-in user’s password.
- **GET /search**: Search users by username or email.

## Environment Configuration

The `application.properties` file contains configurations for:

- **Database**: PostgreSQL connection details.
- **Redis**: Caching and session management.
- **JWT**: Authentication token settings.
- **Gemini AI**: API key and endpoint for debt assignment.
- **Cloudinary**: Image upload configuration.
- **SMTP**: Email server settings for OTPs.
- **OTP**: Expiration time for password reset OTPs.

Example:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/debtmates
spring.datasource.username=test
spring.datasource.password=1234
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.password=test
jwt.secret=your_jwt_secret
jwt.expiration=86400000
gemini.api.key=your_gemini_api_key
gemini.api.url=https://api.gemini.ai/v1
cloudinary.cloud-name=your_cloud_name
cloudinary.api-key=your_api_key
cloudinary.api-secret=your_api_secret
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_email_password
otp.expiration=300000
```

## 🐳Docker Setup

The `docker-compose.yml` file defines services for PostgreSQL, Redis, and pgAdmin. To set up the environment:

1. Ensure Docker is installed.
2. Run the following command:
   ```bash
   docker-compose up -d
   ```
3. Access pgAdmin at `http://localhost:5050` with credentials:
   - Email: `admin@admin.com`
   - Password: `admin`

The PostgreSQL database will be available at `localhost:5432`, and Redis at `localhost:6379`.

## 📑Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository.
2. Create a new branch (`git checkout -b feature/your-feature`).
3. Commit your changes (`git commit -m "Add your feature"`).
4. Push to the branch (`git push origin feature/your-feature`).
5. Open a Pull Request.

Please ensure your code follows the project’s coding standards and includes appropriate tests.

## 🪪License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
