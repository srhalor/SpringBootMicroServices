# Output Management System (OMS) - Current Solution

## 1. Overview
This document describes the current implementation of the Output Management System (OMS). <br/>
The architecture combines request intake, validation, enrichment, and processing in the Document Request API, while the Monitoring Service operates as a scheduled job to track and update request statuses. <br/>
Security, observability, and deployment practices follow platform standards.

---

## 2. Core Architecture Components

| Component            | Responsibility                                                                                                                         |
|----------------------|----------------------------------------------------------------------------------------------------------------------------------------|
| Oracle API Gateway   | Entry point for all client requests; provides additional security, throttling, and monitoring before forwarding to API                 |
| Document Request API | Authenticates, receives/validates (from DB) document requests, stores, processes/enriches (from DB), calls Thunderhead, updates status |
| OMS Database         | Central storage for requests, statuses, reference data, error tracking                                                                 |
| Thunderhead API      | SOAP API (via Oracle API Gateway REST-to-SOAP) for submitting documents, retrieving status, and error details                          |
| Monitoring Service   | Scheduled job; polls Thunderhead for status, updates DB, publishes final status to Kafka                                               |
| Kafka Event Hub      | Publishes final status updates                                                                                                         |

- **Oracle API Gateway:**
  - Entry point for all client requests, providing security, throttling, and monitoring before forwarding to the Document Request API.

- **Document Request API:**
  - Central entry point for document generation requests.
  - Authenticates request:
    - Validates the JWT token by invoking the OIDM `/oauth2/rest/rest/token` API.
    - In parallel, extracts the payload from the JWT token and validates:
      - The expiry date is greater than the current time (token is not expired).
      - The domain and scope in the payload match the configured allowed values.
  - Validates requests, including reference data checks from the database.
  - Persists validated requests and returns a unique document ID in the response.
  - After persisting the request, starts a backend process to handle further processing.
    - The backend process enriches requests as needed and orchestrates calls to Thunderhead API.
    - The backend process updates request status throughout the process.

- **OMS Database:**
  - Central storage for all OMS data, including requests, statuses, reference data.

- **Thunderhead API:**
  - SOAP-based API provided by SmartComm Thunderhead.
  - Offers endpoints for submitting documents, retrieving document status, fetching error details, and other document management operations.
  - Secured by an Oracle API Gateway layer that enforces authentication, authorization, and traffic management policies.
  - Oracle API Gateway implements a REST-to-SOAP policy, allowing OMS services to invoke Thunderhead operations using RESTful calls, with the gateway handling REST-to-SOAP conversion and forwarding.
  - Simplifies integration for OMS services and leverages the security and management features of the Oracle API Gateway.

- **Monitoring Service:**
    - Periodically scans the database for requests that require status monitoring.
    - Polls the Thunderhead API for the latest status of each document request.
    - Updates the OMS database with the latest status.
    - Publishes final status events (completed/failed) to the Kafka Event Hub for downstream consumers.

- **Kafka Event Hub:**
  - Publishes final status updates for downstream consumers and integrations.

---

## 3. System Orchestration Flow
1. **Source System/Client**: Sends a document generation request via API call.
2. **Document Request API**: Authenticates, receives, validates, and stores the request, returning the document ID. A background process enriches the request, sends it to the Thunderhead API for document generation, and updates the request status.
3. **Monitoring Service (Scheduled Job)**: Periodically scans the database for requests in processing, polls Thunderhead for the latest status, updates the database, and publishes a final status event to Kafka Event Hub when completed/failed.

---

## 6. Security
- **Oracle API Gateway** is the entry point for all client requests, providing additional security, throttling, and monitoring before forwarding to the Document Request API.
- The gateway enforces authentication and authorization for all incoming requests.
- Only authenticated and authorized requests are allowed to reach backend services, ensuring protection of all OMS components.
- Additionally, the Document Request API validates the JWT token by invoking the OIDM `/oauth2/rest/rest/token` API and, validates expiry, domain, and scope against allowed configured values.

---

## 7. Monitoring & Observability
- **ELK Stack (Elasticsearch, Logstash, Kibana)** for monitoring, log aggregation, and alerting.
- All services instrumented for centralized logging and metrics.
- Azure Monitor and Application Insights for real-time performance tracking and alerting.

---

## 8. Deployment
- All modules Dockerized and deployed via Kubernetes.
- Helm charts for deployment and configuration management.
- Configurations stored in Azure variables for secure management.
- Deployment managed by Azure DevOps pipelines for CI/CD.

---

## 9. Pros and Cons

### Pros
- **Simplicity:** Combines request intake, validation, enrichment, and processing in a single service, reducing inter-service dependencies.
- **Authentication:** Document Request API enforces authentication for all incoming requests.
- **Operational Simplicity:** Fewer moving parts and services to manage.
- **Cost-Effective:** No need for additional messaging infrastructure beyond Kafka for status updates.
- **Resilience:** Monitoring Service can retry failed or stuck requests on the next scheduled run.
- **Extensibility:** Modular design allows for easy addition of new features or jobs.
- **Observability:** Centralized logging and monitoring with ELK Stack, Azure Monitor, and Application Insights.

### Cons
- **Latency:** Processing and status updates depend on job scheduling intervals, which may introduce delays.
- **Traceability:** Harder to trace the flow of a request compared to API or event-driven solutions.
- **Scalability:** Scaling is limited by job frequency and database performance.
- **Error Handling:** Relies on job reruns and admin UI for recovery; less real-time feedback.
- **Polling Overhead:** Jobs may poll the database frequently, increasing load.
- **Less Real-Time:** Not suitable for use cases requiring immediate processing or response.
- **Tight Coupling:** Combining multiple responsibilities in a single service can make maintenance and scaling more challenging as requirements grow.
