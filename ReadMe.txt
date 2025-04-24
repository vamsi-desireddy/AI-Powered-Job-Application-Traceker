# 🧠 AI-Powered Job Application Tracker

The **AI-Powered Job Application Tracker** streamlines job application management by automatically reading emails, extracting key details using NLP, and storing them in a structured database. It helps users track application statuses, improve resumes based on job descriptions, and stay organized throughout the job search journey.

---

## 🚀 Features

- 📥 Automatically reads job-related emails from your inbox.
- 🧠 Uses Hugging Face + PyTorch models to extract:
  - Company name
  - Role applied for
  - Date of application
  - Application status
  - Days since last update
- 🗂️ Stores extracted data in a MySQL database.
- 🌐 REST API built with Spring Boot (Java).
- 💻 Frontend-ready (Bootstrap) for UI extension.
- 🔐 Future-ready for cloud deployment.

---

## 🛠 Tech Stack

| Layer         | Technology                     |
|---------------|--------------------------------|
| NLP Extraction| Python, Hugging Face, PyTorch |
| Backend API   | Java, Spring Boot             |
| Database      | MySQL                          |
| Frontend UI   | Bootstrap (optional extension) |
| Email Reading | Python IMAP                    |

---

## 📦 Project Structure

ai-job-tracker/
│
├── AI/                    	       # Python AI/NLP module
│   ├── localFLAN.py                   # Uses Hugging Face FLAN-T5 for role/status extraction
│   ├── gmail_reader.py                # Connects to Gmail via IMAP, reads unread emails
│   ├── AIanalysis.py                  # Main script coordinating NLP and API calls
│   ├── email_utils.py                 # Helper functions for email filtering, parsing
│   ├── json_builder.py                # Builds structured JSON from extracted info
│   └── .env                           # Secure storage for Gmail app password
│    
├── Backend/                           # Java Spring Boot backend
│   ├── src/main/java/com/tracker/
│   │   ├── controller/                # JobApplicationController.java
│   │   ├── model/                     # JobApplication.java
│   │   ├── repository/                # JobApplicationRepository.java
│   │   └── service/                   # (Optional: JobService.java)
│   ├── src/main/resources/
│   │   └── application.properties     # DB + Mail config
│   └── pom.xml                        # Maven build config
│
├── Frontend/                          # React frontend
│   ├── public/
│   ├── src/
│   │   ├── components/                # JobList.jsx, Header.jsx
│   │   └── App.jsx                    # Main component
│   ├── index.html
│   └── package.json
│
├── requirements.txt                  # dependencies
├── README.md                         # Project description & setup instructions





---------------------------------------------------------------------------
Phase 1: Email Reader + NLP (Python)
✅ Requirements
	Python 3.9+

	PyTorch

	Transformers

	imaplib, email

	mysql-connector-python

▶️ Steps
	Create and activate a virtual environment:

		python -m venv venv
		venv\Scripts\activate
Install dependencies:

		pip install torch transformers mysql-connector-python
		Set email credentials and MySQL config inside main.py.

Run:

	python main.py
	python pipeline.py

☕ Phase 2: Spring Boot Backend (Java)
✅ Requirements
	JDK 17+

	Maven

	MySQL running locally

▶️ Steps
Go to the backend directory:

	cd Backend
Run:

	mvn spring-boot:run
Access API:

	http://localhost:8080/api/applications
🧪 API Testing with Postman
🔹 GET /api/applications
	URL: http://localhost:8080/api/applications

Response: List of job applications.

🔹 POST /api/applications
	URL: http://localhost:8080/api/applications

Body (JSON):

json
{
  "companyName": "Google",
  "roleApplied": "Software Engineer",
  "applicationDate": "2025-04-10",
  "status": "Submitted",
  "lastUpdated": "2025-04-12"
}.


🎯 Phase 3 Goal
Create a simple web-based UI to:

	View all job applications

	Add a new application

	Refresh the list

tools required

	React 		UI structure + styling
	JavaScript	Fetch API calls to backend
	VS Code	Editor for HTML/JS
	Spring Boot API	Already running backend


--------------------------------------end-------------------------------------------