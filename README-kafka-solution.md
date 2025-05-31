# Output Management System (OMS) - Kafka Event-Driven Solution

## 1. Overview
This document describes an alternative architecture for the Output Management System (OMS) where Kafka Event Hub is used for asynchronous communication between services, instead of direct API calls. The main flow is:

Document Request API → Kafka → Document Processing Service → Kafka → Monitoring Service

All other processes, security, and deployment practices remain consistent with the original solution.

---

## 2. Core Architecture Components

| Component                   | Responsibility                                                                                                                                            |
|-----------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------|
| Document Request API        | Receives, validates (using Reference Data API), and stores the request to DB, publishes request processing event to Kafka                                 |
| Document Processing Service | Subscribes to processing event, processes and enriches (using Reference Data API), Thunderhead API calls, updates DB, publishes monitoring event to Kafka |
| Monitoring Service          | Subscribes to monitoring event, polls Thunderhead for status, updates DB, publishes final status to Kafka Event Hub                                       |

---

## 3. System Orchestration Flow
1. **Source System/Client**: Sends a document generation request via API call.
2. **Document Request API**: Receives, validates (using Reference Data API), and stores the request to Database. Then, publishes a request processing event to Kafka.
3. **Document Processing Service**: Subscribes to processing event, processes and enriches the request (using Reference Data API), forwards it to **Thunderhead API** for document generation, updates Thunderhead batch details to Database, then publishes a request monitoring event to Kafka.
4. **Monitoring Service**: Subscribes to monitoring event, polls Thunderhead for the latest status of the document request. Once the document request reaches a terminal state (COMPLETED/FAILED), updates the database and publishes a final status event to **Kafka Event Hub**.

---

## 4. Error Handling & Recovery
- **Kafka Unavailability:** If Kafka is unavailable, the Document Request API will log the error and retry publishing the event. Failed events can be stored in an error table and retried later.
- **Service Unavailability:** If Document Processing Service or Monitoring Service is unavailable, Kafka will retain the events until the service is available to consume them (at-least-once delivery).
- **Admin UI Support:** Admins can manually retrigger error scenarios via the UI by republishing events to the appropriate Kafka topic.

---

## 5. Pros and Cons

### Pros
- **Loose Coupling:** Services communicate asynchronously via Kafka, reducing direct dependencies.
- **Resilience:** Kafka provides durability and at-least-once delivery, ensuring events are not lost if a service is temporarily unavailable.
- **Scalability:** Services can be scaled independently, and Kafka can handle high throughput.
- **Flexibility:** Easy to add new consumers or producers for additional processing or monitoring.
- **Error Recovery:** Failed events can be retried by republishing to Kafka topics.
- **Extensibility:** Modular design allows for easy addition of new services or features.
- **Observability:** Centralized logging and monitoring with ELK Stack.

### Cons
- **Kafka Unavailability Impact:** If Kafka is unavailable, event publishing fails and processing is delayed until Kafka recovers. This can cause a backlog and temporary loss of service progress. (See Error Handling & Recovery section for mitigation.)
- **Operational Complexity:** Requires managing and monitoring Kafka clusters in addition to other infrastructure.
- **Eventual Consistency:** Processing is asynchronous, so there may be a delay between request submission and final status.
- **Debugging:** Tracing the flow of a single request across multiple topics and services can be more challenging.
- **Error Handling:** Requires robust handling of failed event publishing and dead-letter topics.
- **Cost:** Running and maintaining Kafka clusters adds to infrastructure costs.
