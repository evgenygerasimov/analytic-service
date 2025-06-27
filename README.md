📊 Analytics Microservice
Overview
The Analytics Service is a standalone microservice that collects, stores, and analyzes key user and order activity data from the main e-commerce application. It is designed for real-time tracking and dashboard visualization using modern web technologies.

🔁 Responsibilities
This service is responsible for:

✅ Receiving Kafka events from the main e-commerce app:

Order data (total, items, timestamp)

Page views (URL, session ID, timestamp)

Session data (unique sessions, page counts)

📈 Storing analytical data in a PostgreSQL database

📊 Displaying dashboard charts via a web interface using Chart.js

🔐 Handling authentication context via parsed JWTs for user-specific analytics

🔐 Authentication
Authentication is handled in the main application

Requests to the Analytics Service are proxied by the main app

During proxying:

The JWT token is added to the Authorization header

The Analytics Service:

Parses the JWT

Extracts user information

Sets the Spring Security context accordingly

This allows seamless secure access to the analytics dashboard for authenticated users without repeated logins.

📊 Charts & Dashboards
The service provides a web-based dashboard (via Thymeleaf) that visualizes:

📦 Total number of orders over time

🧾 Total quantity of items ordered per day

👁️ Page views per session

📅 Unique sessions over time

All charts are rendered using Chart.js.

🛠 Technologies Used
Technology	Purpose
Spring Boot	Core framework
Kafka	Event stream for analytics data
PostgreSQL	Analytical data storage
Chart.js	Frontend chart rendering
Spring Security	Auth context from JWT
Thymeleaf	Web-based dashboard interface
Docker Compose	Service orchestration

🔄 Kafka Integration
Kafka listeners are configured to consume topics from the main application, including:

order-events

page-view-events

Each event is parsed, validated, and stored in the appropriate repository for further analysis and visualization.

🔐 Security Recap
JWT token is expected in the Authorization header as Bearer <token>

JWT is parsed using internal secret/public key

The extracted userId and roles are set into the security context

Protected endpoints require authenticated access

📦 Deployment
This service is included in the shared docker-compose.yml used across the project. It runs alongside the main application, Kafka, Redis, and PostgreSQL.
