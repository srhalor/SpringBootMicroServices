# Output Management System (OMS) - Kafka Event-Driven Solution

## 1. Overview
This document describes the Kafka event-driven solution for the Output Management System (OMS), focusing on asynchronous orchestration using Kafka Event Hub. All security, gateway, and validation processes are inherited from the overall architecture (see README-proposed-architecture-overview.md).

---

## 2. Kafka Solution Architecture Diagram

See `oms-kafka-solution-overview.puml` for a visual representation of the Kafka event-driven orchestration flow.

---

## 3. Core Components and Flow

- **Client System**
  - Initiates document generation requests.
  - Follows all security, gateway, and validation processes as described in the overall architecture.

- **Document Request API**
  - Receives and validates requests using the Reference Data API.
  - Stores validated requests in the OMS Database.
  - Publishes processing events to Kafka for downstream processing.

- **Kafka Event Hub**
  - Event streaming platform for all orchestration between services.
  - Publishes and consumes processing, monitoring, and final status events.

- **Document Processing Service**
  - Subscribes to processing events from Kafka.
  - Enriches requests using the Reference Data API.
  - Calls the Thunderhead API to generate documents.
  - Updates the OMS Database with processing and batch details.
  - Publishes monitoring events to Kafka for status tracking.

- **Monitoring Service**
  - Subscribes to monitoring events from Kafka.
  - Polls the Thunderhead API for document status.
  - Updates the OMS Database with the latest status.
  - Publishes final status events to Kafka for downstream consumers.

- **Reference Data API**
  - Provides validation and enrichment data for document requests.

- **Thunderhead API**
  - External system for document generation and status retrieval.

- **OMS Database**
  - Central storage for requests, statuses, and batch details.
  - Contains an error table for tracking failed requests and their recovery status.

- **Recovery Scheduled Job**
  - Periodically checks the error table in the OMS Database.
  - Publishes retry events to Kafka for failed requests, allowing reprocessing by the appropriate service.

---

## 4. Orchestration Flow

1. **Client System** sends a document generation request to the Document Request API.
2. **Document Request API** validates the request (using Reference Data API), stores it in the OMS Database, and publishes a processing event to Kafka.
3. **Document Processing Service** subscribes to processing events, enriches the request (using Reference Data API), calls the Thunderhead API to generate the document, updates processing/batch details in the OMS Database, and publishes a monitoring event to Kafka.
4. **Monitoring Service** subscribes to monitoring events, polls the Thunderhead API for status, updates the OMS Database, and publishes the final status to Kafka Event Hub.
5. **Recovery Scheduled Job** periodically checks the error table in the OMS Database and publishes retry events to Kafka for failed requests.

---

## 5. Error Handling & Recovery

- Failed requests are stored with stage, error details, and timestamp in an error table in the OMS Database.
- The Recovery Scheduled Job periodically publishes retry events to Kafka, allowing the appropriate service to reprocess failed requests.
- Admin UI allows manual retriggering of error scenarios for additional control and flexibility.

---

## 6. Pros and Cons (Specific to Kafka Event-Driven Orchestration)

### Pros
- Loose coupling and asynchronous communication between services.
- High resilience and scalability due to Kafka's event streaming capabilities.
- Modular and extensible design for adding new services or consumers.
- Robust error recovery via retry events and error table.

### Cons
- Increased operational complexity due to Kafka infrastructure.
- Eventual consistency and possible delays in processing.
- Requires robust error handling and monitoring for event failures.
- More challenging to trace the flow of a single request across multiple topics and services.

---

For shared details on security, deployment, and monitoring, refer to [README-proposed-architecture-overview.md](README-proposed-architecture-overview.md).
