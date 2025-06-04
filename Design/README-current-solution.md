# Output Management System (OMS) - Current Solution

## 1. Overview
This document describes the current implementation of the Output Management System (OMS). The architecture combines request intake, validation, enrichment, and processing in the Document Request API, while the Monitoring Service operates as a scheduled job to track and update request statuses. Security, observability, and deployment practices follow platform standards.

---

## 2. Current Solution Architecture Diagram

See `oms-current-solution-overview.puml` for a visual representation of the current solution architecture.

---

## 3. Core Architecture Components

| Component              | Responsibility                                                                                                                         |
|------------------------|----------------------------------------------------------------------------------------------------------------------------------------|
| Oracle API Gateway     | Entry point for all client requests; provides security, throttling, and monitoring before forwarding to Document Request API            |
| Document Request API   | Authenticates, receives/validates requests, stores, processes/enriches, calls Thunderhead, updates status and reference data           |
| OMS Database           | Central storage for requests, statuses, reference data, errors                                                                         |
| Thunderhead API        | SOAP API (via Oracle API Gateway REST-to-SOAP) for submitting documents, retrieving status, and error details                          |
| Monitoring Service     | Scheduled job; polls Thunderhead for status, updates DB, publishes final status to Kafka                                               |
| Kafka Event Hub        | Publishes final status updates                                                                                                         |

- **Oracle API Gateway:**
  - Entry point for all client requests.
  - Provides security, throttling, and monitoring before forwarding to the Document Request API.

- **Document Request API:**
  - Authenticates and validates requests (including JWT and reference data checks).
  - Handles enrichment and processing of requests.
  - Calls Thunderhead API for document submission and status retrieval.
  - Updates OMS Database with requests, statuses, reference data, and errors.

- **OMS Database:**
  - Central storage for all OMS data, including requests, statuses, reference data, and errors.

- **Thunderhead API:**
  - SOAP-based API (SmartComm) for document submission and status retrieval.
  - Accessed via Oracle API Gateway (REST-to-SOAP conversion).

- **Monitoring Service:**
  - Scheduled job that polls Thunderhead API for status updates.
  - Updates OMS Database with the latest status.
  - Publishes final status events to Kafka Event Hub.

- **Kafka Event Hub:**
  - Publishes final status events for downstream consumers.

---

## 4. Orchestration Flow

1. **Client** sends a document generation request to the Oracle API Gateway.
2. **Oracle API Gateway** forwards the request (after security, throttling, monitoring) to the Document Request API.
3. **Document Request API** authenticates, validates, enriches, and processes the request, storing all relevant data in the OMS Database and interacting with Thunderhead API as needed.
4. **Monitoring Service** (scheduled job) polls Thunderhead API for status, updates the OMS Database, and publishes final status to Kafka Event Hub.

---

## 5. Error Handling & Recovery

- Errors encountered during request processing or status updates are stored in the OMS Database.
- Monitoring Service ensures status is kept up to date and publishes final status events.
- Manual intervention or admin tools may be used for error recovery and retriggering failed requests.

---

## 6. Limitations of the Current Solution

- Tight coupling of validation, enrichment, and processing logic in a single API.
- Limited scalability and modularity due to monolithic service boundaries.
- Error handling and recovery are less automated compared to proposed solutions.
- No event-driven orchestration for intermediate steps; only final status is published to Kafka.

---

For details on proposed improvements, see [README-proposed-architecture-overview.md](README-proposed-architecture-overview.md).
