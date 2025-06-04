# Output Management System (OMS) - Scheduled Jobs Solution

## 1. Overview
This document describes the scheduled jobs solution for the Output Management System (OMS), where orchestration is performed by background jobs rather than APIs or event-driven mechanisms. All security, gateway, and validation processes are inherited from the overall architecture (see README-proposed-architecture-overview.md).

---

## 2. Scheduled Jobs Solution Architecture Diagram

See `oms-scheduled-jobs-solution-overview.puml` for a visual representation of the scheduled jobs orchestration flow.

---

## 3. Core Components and Flow

- **Client System**
  - Initiates document generation requests.
  - Follows all security, gateway, and validation processes as described in the overall architecture.

- **Document Request API**
  - Receives and validates requests using the Reference Data API.
  - Stores validated requests in the OMS Database.

- **Document Processing Job**
  - Scheduled job that periodically scans the OMS Database for new requests.
  - Enriches requests by fetching additional data from the Reference Data API.
  - Calls the Thunderhead API to generate documents.
  - Updates the OMS Database with processing and batch details.

- **Monitoring Job**
  - Scheduled job that periodically scans the OMS Database for requests to monitor.
  - Polls the Thunderhead API for the latest status.
  - Updates the OMS Database with the latest status.
  - Publishes final status events to the Kafka Event Hub for downstream consumers.

- **Reference Data API**
  - Provides validation and enrichment data for document requests.

- **Thunderhead API**
  - External system for document generation and status retrieval.

- **OMS Database**
  - Central storage for requests, statuses, and batch details.

- **Kafka Event Hub**
  - Publishes final status events for downstream processing.

---

## 4. Orchestration Flow

1. **Client System** sends a document generation request to the Document Request API.
2. **Document Request API** validates the request (using Reference Data API) and stores it in the OMS Database.
3. **Document Processing Job** periodically scans the OMS Database for new requests, enriches the request (using Reference Data API), calls the Thunderhead API to generate the document, and updates processing/batch details in the OMS Database.
4. **Monitoring Job** periodically scans the OMS Database for requests to monitor, polls the Thunderhead API for status, updates the OMS Database, and publishes the final status to Kafka Event Hub.

---

## 5. Error Handling & Recovery

- Failed requests are tracked in the OMS Database with error details and timestamps.
- Scheduled jobs automatically retry failed or stuck requests on subsequent runs.
- Admin UI allows manual retriggering of error scenarios for additional control and flexibility.

---

## 6. Pros and Cons (Specific to Scheduled Jobs Solution)

### Pros
- Simplicity: No inter-service APIs; jobs are easy to implement and maintain.
- Cost-Effective: No need for Kafka or additional messaging infrastructure for orchestration.
- Loose Coupling: Services interact via database state, reducing direct dependencies.
- Resilience: Jobs can retry failed or stuck requests on the next scheduled run.
- Operational Simplicity: Fewer moving parts, easier to operate and monitor.
- Extensibility: Modular design allows for easy addition of new jobs or features.

### Cons
- Latency: Processing and status updates depend on job scheduling intervals, which may introduce delays.
- Traceability: Harder to trace the flow of a request compared to API or event-driven solutions.
- Scalability: Scaling is limited by job frequency and database performance.
- Error Handling: Relies on job reruns and admin UI for recovery; less real-time feedback.
- Polling Overhead: Jobs may poll the database frequently, increasing load.
- Less Real-Time: Not suitable for use cases requiring immediate processing or response.

---

For shared details on security, deployment, and monitoring, refer to [README-proposed-architecture-overview.md](README-proposed-architecture-overview.md).
