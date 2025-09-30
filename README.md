# 📝 Task Manager (Spring Boot + Angular + MySQL)

A full-stack task manager application built with **Spring Boot (Java)** for the backend, **Angular (TypeScript)** for the frontend, and **MySQL** for database storage.  

## 📦 Features
- ✅ Create, Read, Update, Delete (CRUD) tasks  
- ✅ Filter tasks by status (TODO, IN_PROGRESS, COMPLETED)  
- ✅ Search tasks by title  
- ✅ Set task priority (LOW, MEDIUM, HIGH)  
- ✅ Set due dates  
- ✅ Quick status updates  
- ✅ Responsive design with Bootstrap  
- ✅ Real-time updates  

---

## 🏗️ Project Structure
task-manager/
├── taskmanager-backend/ # Spring Boot + Maven backend
│ ├── src/main/java/com/taskmanager/
│ ├── src/main/resources/application.properties
│ └── pom.xml
├── taskmanager-frontend/ # Angular frontend
│ ├── src/app/
│ ├── angular.json
│ └── package.json
└── README.md

---

## ⚙️ Backend (Spring Boot)
### Prerequisites
- Java 17+  
- Maven 3+  
- MySQL 8+  

### Setup
1. Update `application.properties` with your MySQL credentials:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/taskmanager_db
   spring.datasource.username=root
   spring.datasource.password=your_password
   
Run backend:
cd taskmanager-backend
mvn spring-boot:run
Backend runs on: http://localhost:8080

🎨 Frontend (Angular)
Prerequisites

Node.js 18+
Angular CLI

Setup

Install dependencies:
cd taskmanager-frontend
npm install

Run frontend:
ng serve

Frontend runs on: http://localhost:4200

🧪 Testing Scenarios

Create Task – Add a new task with title, description, priority, due date.
Filter Tasks – View by status (TODO, In Progress, Completed).
Search Tasks – Search by task title in real time.
Update Status – Change task status quickly.
Edit Task – Modify task details.
Delete Task – Remove task from the list.

🚀 Future Improvements

User authentication (JWT)
Multi-user support
Email reminders for due tasks
Cloud deployment (Heroku / Render + Netlify / Vercel)


