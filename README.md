# Employee Management System (EMS)

A full-stack web application for managing employee records, departments, positions, attendance, and leave requests.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| **Frontend** | React 18, Vite, Tailwind CSS, React Router, Axios |
| **Backend** | Java 17, Spring Boot 3.4, Spring Data JPA, Caffeine Cache |
| **Database** | DB2 for IBM i (PUB400.COM), Library: NGIRI4001 |
| **JDBC Driver** | JTOpen jt400 v21.0.6 |
| **Hosting** | Azure App Service (Free Tier) |

## Features

- **Dashboard** - Overview with employee counts, department stats, pending leave requests
- **Employees** - Full CRUD with search, filter by department/status, detail view with phones/dependents/salary/history
- **Departments** - Manage departments with location and manager assignment
- **Positions** - Job titles with salary ranges, linked to departments
- **Attendance** - Daily clock in/out tracking
- **Leave Types** - Configure vacation, sick, personal leave policies
- **Leave Requests** - Submit, approve, reject leave requests

## Database Schema

10 tables with full relational integrity:

```
DEPARTMENTS ←── POSITIONS
     ↑               ↑
     └──── EMPLOYEES ─┘
              ↑
    ┌─────────┼──────────┬──────────┬──────────┐
EMPPHONENB  SALARIES  DEPENDENTS  EMPHIST  ATTENDANCE
                                              
LEAVETYPES ←── LEAVEREQS (also FK to EMPLOYEES)
```

## Running Locally

### Prerequisites
- Java 17+
- Node.js 18+
- Access to PUB400.COM (DB2 for IBM i)

### Backend
```bash
cd backend
# Set your DB2 credentials (required - not stored in code)
set DB2_USER=your_pub400_username
set DB2_PASS=your_pub400_password
# Run with Maven Wrapper
mvnw.cmd spring-boot:run
# Or use start.bat (will prompt for credentials if not set)
start.bat
```
Backend starts at http://localhost:8080

### Frontend
```bash
cd frontend
npm install
npm run dev
```
Frontend starts at http://localhost:5173 (proxies API calls to :8080)

## Performance Optimizations

- **JOIN FETCH** queries to eliminate N+1 problem
- **Caffeine cache** with 10-minute TTL for all read operations
- **Cache warmup** on startup for instant first-page loads
- **GZIP compression** for API responses
- **Single SQL dashboard** query with subqueries

## Project Structure

```
EmpMgmtSystem/
├── sql/                          # Database creation scripts
│   └── create_tables.sql         # 10 tables, indexes, constraints
├── backend/                      # Spring Boot REST API
│   ├── src/main/java/com/empmgmt/
│   │   ├── config/               # CORS, Cache, SPA routing
│   │   ├── controller/           # REST endpoints
│   │   ├── model/                # JPA entities
│   │   ├── repository/           # Database queries
│   │   └── service/              # Business logic + caching
│   └── src/main/resources/
│       └── application.properties
└── frontend/                     # React SPA
    ├── src/
    │   ├── api/                  # Axios client + endpoint functions
    │   └── components/           # UI components
    │       ├── common/           # DataTable, Modal, FormField
    │       ├── dashboard/        # Dashboard page
    │       ├── employees/        # Employee CRUD pages
    │       ├── departments/      # Department management
    │       ├── positions/        # Position management
    │       ├── attendance/       # Attendance tracking
    │       ├── leave/            # Leave types & requests
    │       └── layout/           # Sidebar, Header, Layout
    └── vite.config.js            # Dev server + API proxy
```

## License

Private project.
