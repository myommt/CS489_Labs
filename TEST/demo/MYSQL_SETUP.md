# MySQL Database Setup Instructions

## Prerequisites
1. Install MySQL Server on your system
2. Make sure MySQL service is running

## Database Configuration
The application is configured to connect to MySQL with these default settings:

- **Host**: localhost
- **Port**: 3306
- **Database**: employee_department_db (will be created automatically)
- **Username**: root
- **Password**: Abcde12345!

## Setup Steps

### Option 1: Use Default Configuration
If your MySQL root password is "password", no changes needed.

### Option 2: Update Configuration
If your MySQL setup is different, update the `persistence.xml` file:

```xml
<property name="jakarta.persistence.jdbc.url" value="jdbc:mysql://localhost:3306/employee_department_db?useSSL=false&amp;serverTimezone=UTC&amp;createDatabaseIfNotExist=true"/>
<property name="jakarta.persistence.jdbc.user" value="YOUR_USERNAME"/>
<property name="jakarta.persistence.jdbc.password" value="YOUR_PASSWORD"/>
```

### Option 3: Create Database Manually (Optional)
If you prefer to create the database manually:

```sql
CREATE DATABASE employee_department_db;
USE employee_department_db;
```

## Running the Application

1. Make sure MySQL is running
2. Update credentials in persistence.xml if needed
3. Run: `mvn clean compile exec:java`

## Database Tables
The application will automatically create these tables:
- `departments` (id, name, location)
- `employees` (id, first_name, last_name, email, salary, department_id)

## Sample Data
The application automatically creates sample data:
- 3 Departments: IT, HR, Finance
- 4 Employees: John Doe, Jane Smith, Mike Johnson, Sarah Wilson

## Troubleshooting
- Ensure MySQL server is running
- Check username/password in persistence.xml
- Verify MySQL port (default 3306)
- Check firewall settings if connecting remotely