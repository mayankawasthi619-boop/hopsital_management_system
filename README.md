# Hospital Management System

A full-stack, comprehensive Hospital Management System designed to streamline healthcare operations. This application features a robust Java Spring Boot backend and a responsive React frontend, providing an intuitive interface for managing hospital resources, patients, doctors, and user interactions.

## 🚀 Features

- **User Authentication & Authorization**: Secure login and registration using JWT (JSON Web Tokens) and Spring Security.
- **Patient Management**: Manage patient records, history, and details.
- **Doctor Management**: Manage doctor profiles, schedules, and appointments.
- **Appointment Scheduling**: Easy-to-use interface to book appointments with available doctors.
- **Payment Integration**: Integrated with Razorpay for secure and seamless online transactions.
- **PDF Report Generation**: Automated generation of medical reports and invoices using iTextPDF.
- **Responsive Dashboard**: Admin, Doctor, and Patient dashboards built with React and Bootstrap.

## 🛠️ Technology Stack

**Backend**
- Java 17
- Spring Boot 3.x
  - Spring Security
  - Spring Data JPA
  - Spring Web
- JWT (JSON Web Tokens) for authentication
- Maven
- MySQL Database
- Razorpay Payment Gateway API
- iTextPDF

**Frontend**
- React 18
- Vite
- React Bootstrap 5
- React Router DOM
- Axios
- JWT Decode

**Infrastructure**
- Docker & Docker Compose

## 🐳 Running with Docker

The easiest way to get the application up and running is by using Docker Compose. It will spin up the MySQL database, the Spring Boot backend, and the React frontend.

1. Make sure you have [Docker](https://docs.docker.com/get-docker/) and [Docker Compose](https://docs.docker.com/compose/install/) installed.
2. Clone the repository.
3. Open a terminal in the root directory of the project.
4. Run the following command:

```bash
docker-compose up --build
```

- **Frontend** will be available at: `http://localhost:3000`
- **Backend API** will be available at: `http://localhost:8080`
- **MySQL Database** will be running on port `3306`

## 💻 Manual Setup

### 1. Database Setup
- Install MySQL and start the service.
- Create a database named `hms_db`.
- Ensure the connection details in `backend/src/main/resources/application.properties` (or environment variables) match your local MySQL configuration (username/password).

### 2. Backend Setup
1. Navigate to the `backend` directory:
   ```bash
   cd backend
   ```
2. Build and run the Spring Boot application using Maven:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```
   The backend will start on `http://localhost:8080`.

### 3. Frontend Setup
1. Navigate to the `frontend` directory:
   ```bash
   cd frontend
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Start the Vite development server:
   ```bash
   npm run dev
   ```
   The frontend will be accessible at `http://localhost:5173` (or port specified by Vite).

## ☁️ Deployment

This project is configured and ready for cloud deployment. It can be easily deployed to platforms like Docker Hub, Render, Railway, or GitHub Pages. 
- The frontend is already set up to be deployed on GitHub Pages using `npm run deploy` via `gh-pages`.
- The backend can be containerized using the provided `Dockerfile`.

## 📜 License

This project is open-source and available under the [MIT License](LICENSE).
