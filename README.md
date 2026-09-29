# Employee Management DevOps Project

A simple Employee Management web application used to demonstrate a complete DevOps CI/CD workflow using Java, Maven, JUnit, Docker, Jenkins, AWS EC2, and Ansible.

## 1. Project Overview

The application provides basic employee management functionality through a web interface.

### Application Features

- View all employees
- Add an employee
- Edit employee details
- Delete an employee
- View employee details
- Search employees in real time
- Display employees in ascending order by Employee ID
- Validate duplicate Employee IDs
- Display employee department and salary information

## 2. Technologies Used

### Application

- Java 21
- Maven
- JUnit 5
- Java HTTP Server
- HTML
- CSS
- JavaScript

### DevOps

- Git
- GitHub
- Jenkins
- Docker
- Ansible
- AWS EC2

## 3. Project Architecture

The project follows this deployment flow:

Developer
    |
    v
GitHub Repository
    |
    v
Jenkins
    |
    +--> Maven Build
    |
    +--> JUnit Tests
    |
    +--> Docker Image Build
    |
    v
Docker Image Transfer
    |
    v
AWS EC2
    |
    v
Ansible Deployment
    |
    v
Docker Container
    |
    v
Employee Management Application

## 4. Project Structure

```text
employee-management/
│
├── .gitignore
├── Dockerfile
├── Jenkinsfile
├── pom.xml
├── README.md
│
├── ansible/
│   ├── deploy.yml
│   └── inventory
│
└── src/
    ├── main/
    │   └── java/
    │       └── com/
    │           └── example/
    │               └── employee/
    │                   ├── Employee.java
    │                   ├── EmployeeManager.java
    │                   ├── Main.java
    │                   └── WebServer.java
    │
    └── test/
        └── java/
            └── com/
                └── example/
                    └── employee/
                        └── EmployeeTest.java
```

## 5. Application Components

### Employee.java

Represents an employee and contains:

- Employee ID
- Name
- Email
- Department
- Salary

### EmployeeManager.java

Provides employee management operations including:

- Add employee
- Find employee by ID
- Get all employees
- Update employee
- Delete employee
- Search employees
- Count employees
- Sort employees by ID

### WebServer.java

Provides the web interface and HTTP endpoints for:

- Dashboard
- Add employee
- Edit employee
- Delete employee
- View employee
- Search employees

### EmployeeTest.java

Contains JUnit tests for validating employee creation and employee information.

## 6. Local Setup

### Prerequisites

Install the following:

- Java 21
- Maven
- Git
- Docker Desktop

Verify the installations:

```bash
java -version
mvn -version
git --version
docker --version
```

### Clone the Repository

```bash
git clone <YOUR_GITHUB_REPOSITORY_URL>
cd employee-management
```

### Build the Application

```bash
mvn clean package
```

This command:

1. Cleans previous build files.
2. Compiles the Java application.
3. Compiles the test code.
4. Runs the JUnit tests.
5. Creates the application JAR file.

### Run Tests

```bash
mvn test
```

### Run the Application Locally

```bash
java -cp target/employee-management-1.0.0.jar com.example.employee.WebServer
```

Open the application in a browser:

```text
http://localhost:8081
```

## 7. Docker

### Build the Docker Image

```bash
docker build -t employee-management:1.0 .
```

### Run the Container

```bash
docker run -d -p 8081:8081 --name employee-app employee-management:1.0
```

The application can then be accessed at:

```text
http://localhost:8081
```

### Check the Running Container

```bash
docker ps
```

### Stop the Container

```bash
docker stop employee-app
```

### Remove the Container

```bash
docker rm employee-app
```

## 8. Jenkins CI/CD

Jenkins is used to automate the build, test, Docker image creation, and deployment process.

The Jenkins pipeline performs the following activities:

1. Checkout the latest source code from GitHub.
2. Build the Java application using Maven.
3. Execute the JUnit tests.
4. Build the Docker image.
5. Save the Docker image.
6. Transfer the Docker image to the AWS EC2 server.
7. Load the Docker image on EC2.
8. Execute the Ansible deployment playbook.
9. Start the application container.

### Jenkins Requirements

Configure the following Jenkins tools/credentials on the target system:

- Maven installation
- Git
- Docker
- SSH credential for the EC2 server
- GitHub access if required by the Jenkins SCM configuration

Credential IDs should be configured in Jenkins rather than storing credentials in the repository.

## 9. AWS EC2

The application is deployed to an AWS EC2 instance.

The EC2 instance is used as the application server and runs:

- Docker
- Ansible
- Employee Management Docker container

The application container exposes port:

```text
8081
```

The EC2 security group should allow only the ports required by the deployment.

For the current demonstration setup:

- SSH (22) is restricted to the administrator's current public IP.
- Application port 8081 is accessible for the application demonstration.
- Unused HTTP port 80 is not required.

## 10. Ansible Deployment

Ansible is used on the EC2 server to automate container deployment.

Inventory:

```ini
[appserver]
localhost ansible_connection=local
```

The deployment playbook:

1. Removes the existing employee application container.
2. Starts a new container using the latest Docker image.
3. Configures the container to restart automatically.
4. Maps port 8081.

Run the playbook manually:

```bash
ansible-playbook -i ansible/inventory ansible/deploy.yml
```

Perform a dry run:

```bash
ansible-playbook -i ansible/inventory ansible/deploy.yml --check
```

## 11. CI/CD Workflow

A typical deployment works as follows:

```text
1. Developer changes source code
             |
             v
2. Commit changes to Git
             |
             v
3. Push changes to GitHub
             |
             v
4. Jenkins starts the pipeline
             |
             v
5. Maven builds the application
             |
             v
6. JUnit tests execute
             |
             v
7. Docker image is created
             |
             v
8. Image is transferred to AWS EC2
             |
             v
9. Ansible deploys the container
             |
             v
10. Application becomes available
```

## 12. Security Considerations

The repository must not contain:

- AWS access keys
- AWS secret keys
- SSH private keys
- Passwords
- API tokens
- Environment files containing secrets

The `.gitignore` file excludes:

```text
target/

*.pem
*.key

.env
.env.*

.idea/
.vscode/
```

Jenkins credentials should be stored using the Jenkins Credentials system instead of being written directly into the Jenkinsfile.

## 13. Recreating the Project on a New System

The project is designed so that it can be recreated on another computer using new accounts and credentials.

### Step 1 — Install Required Software

Install:

- Java 21
- Maven
- Git
- Docker Desktop
- Jenkins
- Ansible where required

### Step 2 — Create a New GitHub Repository

Create a new GitHub repository and push the project source code.

Do not copy personal access tokens or private keys into the repository.

### Step 3 — Configure Jenkins

Install/configure the required Jenkins plugins and tools.

Create new Jenkins credentials for:

- GitHub access, if required
- New AWS EC2 SSH key

The credential IDs can be configured according to the new Jenkins environment.

### Step 4 — Create a New AWS EC2 Instance

Create a new EC2 instance using the required operating system and instance type.

Install:

- Docker
- Ansible
- Required Ansible collections

Create the required security-group rules.

### Step 5 — Update Deployment Configuration

Update only account/environment-specific configuration such as:

- EC2 host/IP
- Jenkins credentials
- SSH configuration

Do not add credentials to source code.

### Step 6 — Run Jenkins

Trigger the Jenkins pipeline and verify:

```text
GitHub
  ↓
Jenkins
  ↓
Maven
  ↓
JUnit
  ↓
Docker
  ↓
AWS EC2
  ↓
Ansible
  ↓
Docker Container
```

### Step 7 — Verify the Application

Open the new EC2 application's URL using the configured application port.

## 14. Troubleshooting

### Maven Build Failure

Run:

```bash
mvn clean package
```

Review the compilation or test error.

### Docker Permission Issue

Ensure the Jenkins service account has permission to communicate with Docker.

### Docker Container Not Running

Check:

```bash
docker ps
```

Check container logs:

```bash
docker logs employee-app
```

### Port Already in Use

Check which process is using port 8081:

```bash
sudo ss -ltnp | grep :8081
```

### Ansible Deployment Failure

Run:

```bash
ansible-playbook -i ansible/inventory ansible/deploy.yml
```

Review the task that reports the failure.

### Jenkins Cannot Connect to EC2

Verify:

- EC2 instance is running.
- EC2 security group allows SSH from the Jenkins machine's current public IP.
- Correct SSH credential is configured in Jenkins.
- Correct EC2 host is configured.
- Private key has not been committed to Git.

## 15. Project Status

The project demonstrates a complete basic DevOps workflow:

- Java application development
- Unit testing
- Maven build
- Git version control
- GitHub source-code management
- Jenkins CI/CD
- Docker containerization
- AWS EC2 deployment
- Ansible-based deployment automation
- Basic cloud security practices

## 16. Important Notes

This project is intended as a learning and demonstration project.

Account-specific values such as:

- GitHub usernames
- AWS account information
- EC2 IP addresses
- SSH private keys
- Jenkins credentials

should be configured separately for each environment and should not be committed to the repository.
