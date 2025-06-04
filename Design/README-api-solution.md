# Output Management System (OMS) - API Orchestration Solution

## 1. Overview
This document describes the API orchestration solution for the Output Management System (OMS), focusing on the unique service-to-service flow and responsibilities of this approach. All security, gateway, and validation processes are inherited from the overall architecture (see README-proposed-architecture-overview.md).

---

## 2. API Solution Architecture Diagram

See `oms-api-solution-overview.puml` for a visual representation of the API orchestration flow.

---

## 3. Core Components and Flow

- **Client System**
  - Initiates document generation requests.
  - Follows all security, gateway, and validation processes as described in the overall architecture.

- **Document Request API**
  - Entry point for document generation requests.
  - Validates requests using the Reference Data API.
  - Stores validated requests in the OMS Database.
  - Triggers the Document Processing API for further processing.

- **Document Processing API**
  - Handles asynchronous processing of document requests.
  - Enriches requests by fetching additional data from the Reference Data API.
  - Calls the Thunderhead API to generate documents.
  - Updates the OMS Database with processing and batch details.
  - Triggers the Monitoring API to begin status tracking.

- **Monitoring API**
  - Handles asynchronous monitoring of document status.
  - Periodically polls the Thunderhead API for the latest status.
  - Updates the OMS Database with the latest status.
  - Publishes final status events to the Kafka Event Hub for downstream consumers.

- **Reference Data API**
  - Provides validation and enrichment data for document requests.

- **Thunderhead API**
  - External system for document generation and status retrieval.

- **OMS Database**
  - Central storage for requests, statuses, and batch details.
  - Contains an error table for tracking failed requests and their recovery status.

- **Kafka Event Hub**
  - Publishes final status events for downstream processing.

- **Recovery Scheduled Job**
  - Periodically checks the error table in the OMS Database.
  - Retries failed requests by invoking the Document Processing API or Monitoring API, depending on the error stage.

---

## 4. Orchestration Flow

1. **Client System** sends a document generation request to the Document Request API.
2. **Document Request API** validates the request (using Reference Data API), stores it in the OMS Database, and triggers the Document Processing API.
3. **Document Processing API** enriches the request (using Reference Data API), calls the Thunderhead API to generate the document, updates processing/batch details in the OMS Database, and triggers the Monitoring API.
4. **Monitoring API** polls the Thunderhead API for status, updates the OMS Database, and publishes the final status to Kafka Event Hub.
5. **Recovery Scheduled Job** periodically checks the error table in the OMS Database and retries failed requests by invoking the appropriate API (Document Processing or Monitoring).

---

## 5. Error Handling & Recovery

- Failed requests are stored with stage, error details, and timestamp in an error table in the OMS Database.
- The Recovery Scheduled Job periodically retries failed requests by invoking the appropriate service API. On success, the error entry is removed.
- Admin UI allows manual retriggering of error scenarios for additional control and flexibility.

---

## 6. Pros and Cons (Specific to API Orchestration)

### Pros
- Simplicity and traceability of direct REST API orchestration.
- Fine-grained error handling and immediate feedback at each API boundary.
- Stateless, independently scalable services.
- Modular and extensible design.

### Cons
- Tighter coupling due to direct API dependencies.
- Requires robust error handling and retry logic for failed API calls.
- Potential for bottlenecks at API endpoints under high load.

---

For shared details on security, deployment, and monitoring, refer to [README-proposed-architecture-overview.md](README-proposed-architecture-overview.md).
