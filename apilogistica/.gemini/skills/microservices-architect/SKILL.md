---
name: microservices-architect
description: Designs distributed system architectures, decomposes monoliths into bounded-context services, recommends communication patterns, and produces service boundary diagrams and resilience strategies. Use when designing distributed systems, decomposing monoliths, or implementing microservices patterns — including service boundaries, DDD, saga patterns, event sourcing, CQRS, service mesh, or distributed tracing.
license: MIT
metadata:
  author: https://github.com/Jeffallan
  version: "1.1.0"
  domain: api-architecture
  triggers: microservices, service mesh, distributed systems, service boundaries, domain-driven design, event sourcing, CQRS, saga pattern, Kubernetes microservices, Istio, distributed tracing
  role: architect
  scope: system-design
  output-format: architecture
  related-skills: devops-engineer, kubernetes-specialist, graphql-architect, architecture-designer, monitoring-expert
---

# Microservices Architect

## Core Workflow

1. **Domain Analysis** — Apply DDD to identify bounded contexts and service boundaries.
2. **Communication Design** — Choose sync/async patterns. Define the **Driving Side** (Input Adapters like REST) and **Driven Side** (Output Adapters like DB/Kafka).
3. **Data Strategy** — Database per service. Ensure Output Ports in the application layer abstract all persistence details.
4. **Resilience** — Circuit breakers, retries, timeouts. Apply these patterns in the **Driven Side** (Output Adapters) to protect the core.
5. **Observability** — Distributed tracing, correlation IDs.
   - *Mandatory:* Propagate `x-correlation-id` in every outbound HTTP call **AND Kafka message headers**.

## Implementation Examples — Distributed Tracing (Kafka)

### Correlation ID in Kafka Header (Spring Kafka)
```java
@Service
public class MessageProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(String topic, Object payload, String correlationId) {
        ProducerRecord<String, Object> record = new ProducerRecord<>(topic, payload);
        record.headers().add("x-correlation-id", correlationId.getBytes());
        kafkaTemplate.send(record);
    }
}
```

## Constraints

### MUST DO
- Apply domain-driven design for service boundaries.
- Define clear **Input Ports** (Driving Side) and **Output Ports** (Driven Side).
- Implement circuit breakers for external calls.
- Add correlation IDs to all requests and messaging headers.
- Use async communication for cross-aggregate operations.
- Design for failure and graceful degradation.

### MUST NOT DO
- Create distributed monoliths.
- Share databases between services.
- Skip distributed tracing implementation across messaging boundaries.


## Output Templates

When designing microservices architecture, provide:
1. Service boundary diagram with bounded contexts
2. Communication patterns (sync/async, protocols)
3. Data ownership and consistency model
4. Resilience patterns for each integration point
5. Deployment and infrastructure requirements

## Knowledge Reference

Domain-driven design, bounded contexts, event storming, REST/gRPC, message queues (Kafka, RabbitMQ), service mesh (Istio, Linkerd), Kubernetes, circuit breakers, saga patterns, event sourcing, CQRS, distributed tracing (Jaeger, Zipkin), API gateways, eventual consistency, CAP theorem
