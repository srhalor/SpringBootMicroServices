# Output Management System (OMS) - Proposed Architecture Overview

## 1. Overview
This document proposes an improved architecture for the Output Management System (OMS), building on the current implementation. It outlines enhancements in modularity, scalability, and maintainability, and compares multiple architectural solutions for OMS service orchestration and communication. The following sections describe the common functionality, components, and deployment details shared across all solutions, and provide links to the detailed solution-specific documents.

---

## 2. Core Architecture Components

| Component              | Responsibility                                                                                                                         |
|------------------------|----------------------------------------------------------------------------------------------------------------------------------------|
| Oracle API Gateway     | Entry point for all client requests; provides additional security, throttling, and monitoring before forwarding to API Gateway (Nginx) |
| API Gateway            | Nginx Ingress for routing; forwards requests to Service after authentication                                                           |
| Security Service       | Validates JWT; only authenticated requests reach backend services                                                                      |
| Reference Data API     | Centralized access to reference data (allowed sources, enrichment etc.), uses Redis for caching                                        |
| Thunderhead API        | SOAP API (via Oracle API Gateway REST-to-SOAP) for submitting documents, retrieving status, and error details                          |
| OMS Database           | Central storage for requests, statuses, reference data, error tracking etc.                                                            |
| OMS Services           | Core services that orchestrate the request flow, manage processing, and track status                                                   |
| Kafka Event Hub        | Publishes final status updates                                                                                                         |

- **Oracle API Gateway:**
  - Entry point for all client requests.
  - Provides additional security, including authentication and authorization, as well as throttling and monitoring.
  - Forwards requests to the internal API Gateway (Nginx) after enforcing security policies.

- **API Gateway:**
  - Nginx Ingress Controller responsible for routing requests to backend OMS services.
  - Forwards requests to services only after successful authentication by the Security Service.
  - Centralizes API routing and load balancing for OMS microservices.

- **Security Service:**
  - Invokes the OIDM `/oauth2/rest/rest/token` API to validate the JWT token received in each request.
  - In parallel, extracts the payload from the JWT token and validates:
    - The expiry date is greater than the current time (token is not expired).
    - The domain and scope in the payload match the configured allowed values.
  - Only if all validations pass, the request is considered authenticated and authorized.
  - Ensures that only valid, non-expired, and properly scoped tokens are allowed to access backend services.

- **Reference Data API:**
  - Provides a single point of access for all reference data required by OMS services.
  - Supports CRUD operations for managing reference data.
  - Integrates with Redis for caching: on data request, checks Redis cache first; if not found, fetches from the database, returns the data, and updates the cache.
  - Ensures cache consistency by updating or invalidating Redis entries on data changes.
  - Reduces database load and improves response times for frequently accessed reference data.

- **Thunderhead API:**
  - SOAP-based API provided by SmartComm Thunderhead.
  - Offers endpoints for submitting documents, retrieving document status, fetching error details, and other document management operations.
  - Secured by an Oracle API Gateway layer that enforces authentication, authorization, and traffic management policies.
  - Oracle API Gateway implements a REST-to-SOAP policy, allowing OMS services to invoke Thunderhead operations using RESTful calls, with the gateway handling REST-to-SOAP conversion and forwarding.
  - Simplifies integration for OMS services and leverages the security and management features of the Oracle API Gateway.

- **OMS Database:**
  - Serves as the central storage for all OMS-related data, including document requests, statuses, reference data, and error tracking.

- **OMS Services:**
  - Core set of microservices responsible for orchestrating the document request flow, managing processing, and tracking status.
  - Handle business logic for document intake, validation, enrichment, processing, and status management.
  - Interact with external systems (e.g., Thunderhead API, Reference Data API) and internal components (OMS Database, Kafka Event Hub).
  - Designed for modularity and scalability, allowing independent deployment and scaling of individual services.
  - Expose APIs or background jobs as needed for orchestration and integration with other OMS components.
  - The specific OMS Services and their roles are explained in detail within each solution variant's documentation.

- **Kafka Event Hub:**
  - Event streaming platform for publishing and consuming status updates and other events.
  - Enables asynchronous communication between OMS services and downstream consumers.
  - Provides durability, scalability, and at-least-once delivery guarantees for event-driven processing.

---

## 3. Solution Variants for OMS Service Orchestration
- [API Orchestration Solution](./README-api-solution.md): Uses REST APIs for asynchronous orchestration between services.
- [Kafka Event-Driven Solution](./README-kafka-solution.md): Uses Kafka Event Hub for asynchronous, event-driven communication between services.
- [Scheduled Jobs Solution](./README-scheduled-jobs-solution.md): Uses scheduled jobs for background processing and status tracking, with no inter-service APIs.

Each solution document describes its unique orchestration, error handling, and pros/cons in detail.

---

## 4. Security
- **Oracle API Gateway** is the entry point for all client requests, providing additional security, throttling, and monitoring before forwarding to the internal API Gateway (Nginx).
- **API Gateway** uses Nginx Ingress Controller for all API routing.
- For each request, Nginx calls the **Security Service** to validate the JWT token.
- Only if the Security Service confirms authentication, Nginx forwards the request to the backend OMS service.
- This ensures all backend services are protected and only accessible with valid JWTs.

---

## 5. Monitoring & Observability
- **ELK Stack (Elasticsearch, Logstash, Kibana)** is used for monitoring, log aggregation, and alerting.
- All services are instrumented for centralized logging and metrics.
- Additional monitoring is provided by Azure Monitor and Application Insights for real-time performance tracking and alerting.

---

## 6. Deployment
- All modules are Dockerized and deployed via Kubernetes.
- Helm charts are used for deployment and configuration management.
- All configurations are stored in Azure variables for secure management.
- Deployment is managed by Azure DevOps pipelines for CI/CD.

---

## 7. Pros and Cons: Comparison with Other Solutions

| Aspect                   | API Orchestration              | Kafka Event-Driven                 | Scheduled Jobs (This)          |
|--------------------------|--------------------------------|------------------------------------|--------------------------------|
| **Coupling**             | Tighter (API dependencies)     | Looser (event-driven)              | Looser (DB-driven)             |
| **Resilience**           | Recovery logic needed for APIs | Kafka provides built-in resilience | Jobs retry on next schedule    |
| **Latency**              | API/network latency            | Eventual consistency, some lag     | Polling interval, some lag     |
| **Traceability**         | Easier (API call chain)        | Harder (event tracing in Kafka)    | Harder (job logs, DB state)    |
| **Scalability**          | Good, but API bottlenecks      | Excellent, Kafka scales well       | Good, depends on job frequency |
| **Operational Overhead** | Error handling, retries        | Kafka ops, topic management        | Job scheduling, DB ops         |
| **Complexity**           | Moderate                       | Higher (Kafka infra, topics)       | Lower (simple jobs, no APIs)   |
| **Error Handling**       | API retries, error table, UI   | Kafka retries, DLQ, UI             | Job reruns, UI                 |
| **Cost**                 | Moderate                       | Higher (Kafka infra)               | Lower (no Kafka, fewer APIs)   |

**Summary:**
- **API Orchestration** is simple and traceable but tightly coupled and needs explicit recovery logic.
- **Kafka Event-Driven** is highly resilient and scalable but adds operational complexity and cost.
- **Scheduled Jobs** (this solution) are simple and cost-effective but may have higher latency and less real-time processing.
