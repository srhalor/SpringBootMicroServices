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
| Thunderhead API      | SOAP API (via Oracle API Gateway REST-to-SOAP) for submitting documents, retrieving status, and error details                          |
| Kafka Event Hub      | Publishes final status updates                                                                                                         |
| OMS Database         | Central storage for requests, statuses, reference data, error tracking                                                                 |
| Document Request API | Authenticates, receives/validates (from DB) document requests, stores, processes/enriches (from DB), calls Thunderhead, updates status |
| Monitoring Service   | Scheduled job; polls Thunderhead for status, updates DB, publishes final status to Kafka                                               |

---

## 3. Thunderhead API Integration
The Thunderhead API is a SOAP-based API provided by SmartComm Thunderhead. <br/>
It offers endpoints for submitting documents, retrieving document status, and fetching error details. <br/>
To enhance security, an Oracle API Gateway layer is placed in front of the Thunderhead API, enforcing authentication, authorization, and traffic management. <br/>
The gateway also implements a REST-to-SOAP policy, allowing OMS services to invoke Thunderhead operations using RESTful calls, with the gateway handling REST-to-SOAP conversion and forwarding.

---

## 4. System Orchestration Flow
1. **Source System/Client**: Sends a document generation request via API call.
2. **Document Request API**: Authenticates, receives, validates, and stores the request, returning the document ID. A background process enriches the request, sends it to the Thunderhead API for document generation, and updates the request status.
3. **Monitoring Service (Scheduled Job)**: Periodically scans the database for requests in processing, polls Thunderhead for the latest status, updates the database, and publishes a final status event to Kafka Event Hub when completed/failed.

---

## 5. Monitoring & Observability
- **ELK Stack (Elasticsearch, Logstash, Kibana)** for monitoring, log aggregation, and alerting.
- All services instrumented for centralized logging and metrics.
- Azure Monitor and Application Insights for real-time performance tracking and alerting.

---

## 6. Deployment
- All modules Dockerized and deployed via Kubernetes.
- Helm charts for deployment and configuration management.
- Configurations stored in Azure variables for secure management.
- Deployment managed by Azure DevOps pipelines for CI/CD.

---

## 7. Pros and Cons

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
