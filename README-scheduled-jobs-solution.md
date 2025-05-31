# Output Management System (OMS) - Scheduled Jobs Solution

## 1. Overview
This document describes an alternative architecture for the Output Management System (OMS) where the Document Processing Service and Monitoring Service are implemented as scheduled jobs, rather than exposed as APIs or using Event Hub.

All other processes, security, and deployment practices remain consistent with the original solution.

---

## 2. Core Architecture Components

| Component                   | Responsibility                                                                                                            |
|-----------------------------|---------------------------------------------------------------------------------------------------------------------------|
| Document Request API        | Receives, validates (using Reference Data API), and stores the request to DB                                              |
| Document Processing Service | Runs as a scheduled job, processes and enriches the request (using Reference Data API), Thunderhead API calls, updates DB |
| Monitoring Service          | Runs as a scheduled job, polls Thunderhead for status, updates DB, publishes final status to Kafka Event Hub              |

---

## 3. System Orchestration Flow
1. **Source System/Client**: Sends a document generation request via API call.
2. **Document Request API**: Receives, validates (using Reference Data API), and stores the request to Database.
3. **Document Processing Service (Scheduled Job)**: Periodically scans the database for new requests, processes and enriches the request (using Reference Data API), forwards it to **Thunderhead API** for document generation, and updates Thunderhead batch details to Database.
4. **Monitoring Service (Scheduled Job)**: Periodically scans the database for status monitoring, polls Thunderhead for the latest status of the document request. Once the document request reaches a terminal state (COMPLETED/FAILED), updates the database and publishes a final status event to **Kafka Event Hub**.

---

## 4. Pros and Cons

### Pros
- **Simplicity:** No inter-service APIs; jobs are easy to implement and maintain.
- **Cost-Effective:** No need for Kafka or additional messaging infrastructure.
- **Loose Coupling:** Services interact via database state, reducing direct dependencies.
- **Resilience:** Jobs can retry failed or stuck requests on the next scheduled run.
- **Operational Simplicity:** Fewer moving parts, easier to operate and monitor.
- **Extensibility:** Modular design allows for easy addition of new jobs or features.
- **Observability:** Centralized logging and monitoring with ELK Stack.

### Cons
- **Latency:** Processing and status updates depend on job scheduling intervals, which may introduce delays.
- **Traceability:** Harder to trace the flow of a request compared to API or event-driven solutions.
- **Scalability:** Scaling is limited by job frequency and database performance.
- **Error Handling:** Relies on job reruns and admin UI for recovery; less real-time feedback.
- **Polling Overhead:** Jobs may poll the database frequently, increasing load.
- **Less Real-Time:** Not suitable for use cases requiring immediate processing or response.

