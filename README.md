# 💸 DebtMates Backend

## 📌 Overview
DebtMates is a financial management system that facilitates debt tracking, rotational savings plans, personal savings plans, and user management. This repository contains the backend implementation of DebtMates, built with Spring Boot, using PostgreSQL as the database, Cloudinary for image storage, and the Gemini API for debt calculation algorithms.

## 🚀 Features
- **Debt Management**: Core feature to calculate and settle debts among group members using the Gemini API for algorithmic computation in JSON format.
- **Rotational Savings Plan Management**: Manage group savings plans with image uploads stored in Cloudinary (image URLs saved in PostgreSQL).
- **Personal Savings Plan Management**: Support for individual savings plans.
- **User Management**: Features include user registration, login, email verification, password reset, and admin user management.
- **Secure API**: Implements JWT authentication, security configurations, and global exception handling.

## 🛠️ Technologies Used
- **Spring Boot**: Framework for building the backend application (version 3.4.3).
- **PostgreSQL**: Database for storing user data, debts, groups, and savings plans.
- **Cloudinary**: Cloud storage for uploading and managing images for rotational plans.
- **Gemini API**: Used for debt calculation algorithms via Google AI Studio.
- **Redis**: For caching and session management.
- **JWT**: For secure authentication and authorization (using `io.jsonwebtoken`).
- **Spring Mail**: For sending email verification and password reset OTPs (configured with Gmail SMTP).
- **Lombok**: To reduce boilerplate code (version 1.18.34).

## 📂 Folder Structure
- `src/main/java/com/example/debtmatesbe/`: Main package.
  - `config/`: Configuration classes (e.g., `GlobalExceptionHandler`, `JwtAuthenticationFilter`, `SecurityConfig`).
  - `controller/`: REST controllers (e.g., `DebtController`, `RotationalController`, `AuthController`).
  - `dto/`: Data Transfer Objects (e.g., `DebtResponse`, `RotationalGroupResponse`, `AddMembersRequest`).
  - `model/`: Entity models (e.g., `DebtDetails`, `Group`, `RotationalPlan`).
  - `repo/`: Repositories for database operations (e.g., `DebtDetailsRepository`, `UserRepository`).
  - `service/`: Business logic services (e.g., `DebtService`, `RotationalService`, `EmailService`).
- `src/main/resources/`: Configuration files (e.g., `application.properties`).

## 📦 Setup Instructions
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/Dilusha-Ranasingha/DebtMates-BE.git
   cd DebtMates-BE
   ```
2. **Set Up Docker Services**:
   - Ensure Docker and Docker Compose are installed.
   - Start PostgreSQL and Redis using the provided `docker-compose.yml`:
     ```bash
     docker-compose up -d
     ```
   - PostgreSQL will run on `localhost:5432` with database `debtMateDB`, username `test`, and password `1234`.
   - Redis will run on `localhost:6379` with password `test`.
   - (Optional) Access pgAdmin at `http://localhost:5050` (email: `admin@admin.com`, password: `admin`).
3. **Install Dependencies**:
   - Ensure Maven is installed.
   - Run:
     ```bash
     mvn install
     ```
4. **Environment Variables**:
   - Update `src/main/resources/application.properties` with your configurations:
     ```
     spring.datasource.url=jdbc:postgresql://localhost:5432/debtMateDB
     spring.datasource.username=test
     spring.datasource.password=1234

     spring.data.redis.host=localhost
     spring.data.redis.port=6379
     spring.data.redis.password=test

     jwt.secret=<your-jwt-secret>
     jwt.expiration=86400000

     gemini.api.key=<your-gemini-api-key>
     gemini.api.url=https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent

     spring.mail.host=smtp.gmail.com
     spring.mail.port=587
     spring.mail.username=<your-email>
     spring.mail.password=<your-email-password>
     spring.mail.properties.mail.smtp.auth=true
     spring.mail.properties.mail.smtp.starttls.enable=true

     otp.expiration=300000

     cloudinary.cloud-name=<your-cloudinary-cloud-name>
     cloudinary.api-key=<your-cloudinary-api-key>
     cloudinary.api-secret=<your-cloudinary-api-secret>
     ```
5. **Run the Application**:
   ```bash
   mvn spring-boot:run
   ```
   The backend will run on `http://localhost:8080`.

## 🔐 API Endpoints
- **Authentication**:
  - `POST /api/auth/register`: Register a new user (admin registration by SuperAdmin only).
  - `POST /api/auth/login`: Log in a user and return a JWT token.
  - `POST /api/auth/logout`: Invalidate the JWT token.
  - `POST /api/auth/password-reset/request`: Request a password reset OTP.
  - `POST /api/auth/password-reset/confirm`: Confirm password reset with OTP.
- **Debt Management**:
  - `POST /api/groups/{groupId}/debts`: Record a debt in a group.
  - `GET /api/groups/{groupId}/debts`: Fetch debts for a group.
  - `GET /api/users/me/debts`: Fetch debts for the logged-in user.
- **Rotational Plans**:
  - `POST /api/rotational/groups`: Create a rotational group.
  - `PUT /api/rotational/groups/{groupId}`: Edit a rotational group.
  - `DELETE /api/rotational/groups/{groupId}`: Delete a rotational group.
  - `POST /api/rotational/groups/{groupId}/members`: Add members to a rotational group.
  - `POST /api/rotational/groups/{groupId}/plan`: Add a plan to a rotational group.
  - `GET /api/rotational/groups`: Fetch user’s rotational groups.
  - `GET /api/rotational/groups/{groupId}/payments`: Fetch payments for a rotational group.
  - `PUT /api/rotational/payments/{paymentId}/slip`: Upload a payment slip (via Cloudinary).
  - `GET /api/rotational/payments/{paymentId}/slip`: Fetch the payment slip URL.
- **Group Management**:
  - `POST /api/groups`: Create a new group.
  - `PUT /api/groups/{groupId}`: Update a group.
  - `GET /api/groups/me`: Fetch user’s groups.
  - `POST /api/groups/{groupId}/members`: Add members to a group.
  - `GET /api/groups/{groupId}/members`: Fetch group members.
- **User Management**:
  - `GET /api/user/profile`: Fetch user profile.
  - `PUT /api/user/profile`: Update user profile.
  - `POST /api/user/change-password`: Change user password.
  - `GET /api/user/search`: Search users by username or email.
- **Admin Management**:
  - `GET /api/admin/users`: Fetch all users (paginated, admin access required).
  - `GET /api/admin/admins`: Fetch all admins (paginated, SuperAdmin access required).
  - `PUT /api/admin/users/{id}`: Update a user’s details (admin access required).
  - `DELETE /api/admin/users/{id}`: Delete a user (admin access required, SuperAdmin for deleting admins).

## 🙌 Contributing
1. Fork the repository.
2. Create a new branch (`git checkout -b feature-branch`).
3. Commit your changes (`git commit -m "Add feature"`).
4. Push to the branch (`git push origin feature-branch`).
5. Open a Pull Request.

## 📄 License
This project is licensed under the MIT License.

## 🧰Languages and Tools
<p align="left">
    <img src="https://skillicons.dev/icons?i=spring,git,docker,postman,postgres" />
</p>
