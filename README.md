# Multi-Environment CI/CD Pipeline (Dev → Test → Prod)

This project demonstrates a **complete CI/CD pipeline for a containerized Spring Boot application**, implementing automated build, code quality analysis, Docker image creation, and Kubernetes deployments across multiple environments using **Jenkins, Docker, SonarQube, and Kubernetes**.

---

## Project Architecture

Developer Push → GitHub → Jenkins Pipeline → Build → SonarQube Analysis → Quality Gate → Docker Build → DockerHub Push → Kubernetes Deployment (DEV → TEST → PROD)

---

## Technologies Used

- Java (Spring Boot)
- Maven
- Jenkins
- Docker
- DockerHub
- SonarQube
- Kubernetes
- Kind (Kubernetes in Docker)
- kubectl
- Linux

---

## Project Features

### 1. Continuous Integration (CI)

The Jenkins pipeline automatically performs:

- Source code checkout from GitHub
- Maven build and packaging
- Unit test execution
- Static code analysis using SonarQube
- SonarQube Quality Gate validation

Pipeline stops automatically if the quality gate fails.

---

### 2. Docker Image Build and Push

After successful code analysis:

- Jenkins builds a Docker image of the Spring Boot application.
- Image is tagged with the Jenkins build number.
- Image is pushed to DockerHub.

Example image:

```
agnussk/multi-env-app:<build-number>
```

---

### 3. Kubernetes Multi-Environment Deployment

The application is deployed to three Kubernetes environments:

- **DEV**
- **TEST**
- **PROD**

Each environment has its own:

- Namespace
- Deployment configuration
- Service configuration

Directory structure:

```
k8s/
 ├── dev/
 │    ├── deployment.yaml
 │    └── service.yaml
 ├── test/
 │    ├── deployment.yaml
 │    └── service.yaml
 └── prod/
      ├── deployment.yaml
      └── service.yaml
```

---

### 4. Environment Promotion Workflow

The deployment follows a promotion model:

```
DEV → TEST → PROD
```

Pipeline flow:

1. Deploy automatically to **DEV**
2. Manual approval required for **TEST**
3. Manual approval required for **PROD**

This simulates a real production release workflow.

---

### 5. Kubernetes RBAC Security

To secure deployments, **Kubernetes Role-Based Access Control (RBAC)** was implemented.

Jenkins uses a dedicated ServiceAccount with limited permissions.

RBAC components:

- ServiceAccount
- Role
- RoleBinding

This ensures Jenkins can only deploy applications and does not have full cluster access.

RBAC configuration is stored in:

```
k8s/rbac/
```

---

## Jenkins Pipeline Stages

The Jenkins pipeline performs the following stages:

1. Build Application (Maven)
2. SonarQube Code Analysis
3. SonarQube Quality Gate Validation
4. Docker Image Build
5. Docker Image Push to DockerHub
6. Deploy to DEV
7. Manual Approval for TEST
8. Deploy to TEST
9. Manual Approval for PROD
10. Deploy to PROD

---

## Kubernetes Cluster Setup

The Kubernetes cluster was created locally using **Kind (Kubernetes in Docker)**.

Cluster contains:

- Control Plane Node
- Worker Node

Namespaces used:

```
dev
test
prod
```

---

## Repository Structure

```
multi-env-app/
│
├── src/                     # Spring Boot source code
├── Dockerfile               # Container build configuration
├── Jenkinsfile              # CI/CD pipeline definition
├── pom.xml                  # Maven build configuration
│
├── k8s/
│   ├── dev/
│   ├── test/
│   ├── prod/
│   └── rbac/
│
└── README.md
```

---

## How to Run the Project

### 1. Start Kubernetes Cluster

```
kind create cluster --name multi-env-cluster
```

---

### 2. Create Namespaces

```
kubectl create namespace dev
kubectl create namespace test
kubectl create namespace prod
```

---

### 3. Apply RBAC Configuration

```
kubectl apply -f k8s/rbac/
```

---

### 4. Configure Jenkins

Required Jenkins tools:

- Maven
- Docker
- SonarQube Scanner
- Kubernetes CLI

Add Jenkins credentials:

- DockerHub credentials
- Kubernetes kubeconfig

---

### 5. Run Pipeline

Push code to GitHub:

```
git push
```

Jenkins automatically triggers the pipeline.

---

## Key DevOps Concepts Demonstrated

- CI/CD pipeline automation
- Static code analysis
- Quality gate validation
- Docker containerization
- Container registry integration
- Multi-environment deployment strategy
- Kubernetes deployment management
- Role-Based Access Control (RBAC)
- Secure credential handling in Jenkins

---

## Future Improvements

Possible improvements for production environments:

- Helm charts for Kubernetes deployments
- ConfigMaps and Secrets for environment configuration
- Automated testing stages
- Monitoring using Prometheus and Grafana
- GitOps-based deployments

---

## Author

Agnus sk
DevOps Engineer (Fresher)
