# Output Management System (OMS) - API Orchestration Solution

## 1. Overview
This document describes the original architecture for the Output Management System (OMS) using REST APIs for asynchronous communication and orchestration between services. The main flow is:

Document Request API → Document Processing API → Monitoring API

All other processes, security, and deployment practices remain consistent with the original solution.

---

## 2. Core Architecture Components

| Component               | Responsibility                                                                                                    |
|-------------------------|-------------------------------------------------------------------------------------------------------------------|
| Document Request API    | Receives, validates (using Reference Data API), and stores the request to DB, triggers Document Processing API    |
| Document Processing API | Async processing, enriches (using Reference Data API), Thunderhead API calls, updates DB, triggers Monitoring API |
| Monitoring API          | Async processing, polls Thunderhead for status, updates DB, publishes final status to Kafka Event Hub             |

---

## 3. System Orchestration Flow
1. **Source System/Client**: Sends a document generation request via API call.
2. **Document Request API**: Receives, validates (using Reference Data API), and stores the request to Database. Then, invokes **Document Processing API**.
3. **Document Processing API**: Starts async processing and returns. Async process: enriches the request (using Reference Data API), forwards it to **Thunderhead API** for document generation, updates Thunderhead batch details to Database, then invokes **Monitoring API**.
4. **Monitoring API**: Starts async processing and returns. Async process: polls Thunderhead for the latest status of the document request. Once the document request reaches a terminal state (COMPLETED/FAILED), updates the database and publishes a final status event to **Kafka Event Hub**.

---

## 4. Error Handling & Recovery
- **Error Table:** Failed requests are stored with stage, error details, and timestamp.
- **Scheduled Recovery Job:** Periodically retries failed requests by invoking the appropriate service API. On success, removes the error entry.
- **Admin UI Support:** Admins can manually retrigger error scenarios via the UI, providing additional control and flexibility for error recovery.

---

## 5. Pros and Cons

### Pros
- **Simplicity:** Direct REST API orchestration is easy to understand, implement, and debug.
- **Traceability:** Easier to trace the flow of a request through API calls.
- **Fine-grained Error Handling:** Immediate feedback and error handling at each API boundary.
- **Statelessness:** All services are stateless and can be independently scaled.
- **Extensibility:** Modular design allows for easy addition of new services or features.
- **Observability:** Centralized logging and monitoring with ELK Stack.

### Cons
- **Tight Coupling:** Services are more tightly coupled due to direct API dependencies.
- **Resilience:** If a downstream service is unavailable, requests may fail unless recovery logic is implemented (see Error Handling & Recovery section).
- **Latency:** Asynchronous API calls can still add to end-to-end latency.
- **Operational Overhead:** Requires robust error handling and retry logic for failed API calls.
- **Scalability Limits:** High load may cause bottlenecks at API endpoints.
