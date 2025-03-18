# DebtMates-BE (Spring Boot)

Debt management backend API integration for DebtMates-FE.

## Project Overview
This is the Spring Boot backend for DebtMates, providing REST APIs to support user management, debt tracking, rotational savings, and personal savings features.

## Getting Started

### Prerequisites
- Java 17+
- Maven (or Gradle)
- Git
- IDE (e.g., IntelliJ IDEA)

### Installation
1. Clone the repo: `git clone https://github.com/Dilusha-Ranasingha/DebtMates-BE.git`
2. Navigate to the project directory: `cd DebtMates-BE`
3. Install Dependencies: `mvn install`
4. Run the application: `mvn spring-boot:run`
5. Access the app: `http://localhost:8080`


## Branching
- **`main`**: Stable, production-ready code.
- **`dev`**: Development integration branch.
- **Feature Branches**: Create under `dev` (e.g., `feature/user-management`).
  - Example: `git checkout -b feature/<feature-name>`

### Workflow
1. Checkout `dev`: `git checkout dev`
2. Create branch: `git checkout -b feature/<feature-name>`
3. Work on your feature.
4. Push changes: `git add .`, `git commit -m "feat: Implemented <feature>"`, `git push origin feature/<feature-name>`
5. Submit a PR to `dev`.

## Team Functions and Issues
- **Dhanithya - User Management** 🧰  
  - **Issue**: [Implement User Management APIs](#)  
- **Dilusha - Debt Management** 💸  
  - **Issue**: [Implement Debt Management APIs](#)  
- **Chalani - Rotational Savings Plan** 🔄  
  - **Issue**: [Implement Rotational Savings APIs](#)  
- **Nethmi - Personal Savings Plan Management** 📚  
  - **Issue**: [Implement Personal Savings APIs](#)

## Technologies
- **Spring Boot**: Backend framework
- **Spring Data JPA**: Database access
- **H2/MySQL**: Database (H2 for dev)
- **Maven/Gradle**: Build tool
- **Git**: Version control

## Contributing
Follow the setup and branching instructions. Check your assigned GitHub Issue for tasks.
