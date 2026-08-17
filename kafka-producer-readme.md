# Kafka Producer Configuration — Full Reference

## Request Flow

```
producer.send(record)
        │
        ▼
┌─────────────────────────────────────────────────────────┐
│ 1. ACCEPT INTO LOCAL BUFFER                                │
│    max.block.ms = 60000 (60s)                               │
│    buffer.memory = 33554432 (32MB)                           │
│    Blocks here if buffer.memory is full, or topic metadata    │
│    isn't cached yet (metadata.max.age.ms = 300000 controls    │
│    how often metadata refreshes)                               │
│    → expiry throws TimeoutException DIRECTLY to caller         │
└─────────────────────────────────────────────────────────┘
        │ accepted
        ▼
┌─────────────────────────────────────────────────────────┐
│ 2. BATCH FORMATION (per partition)                          │
│    linger.ms = 0                                             │
│    batch.size = 16384 bytes (16KB)                            │
│    compression.type = none (options: gzip, snappy, lz4, zstd) │
│    Sent when EITHER linger.ms elapses OR batch.size reached    │
└─────────────────────────────────────────────────────────┘
        │ batch ready
        ▼
┌─────────────────────────────────────────────────────────┐
│ 3. VALIDATION                                                │
│    max.request.size = 1048576 (1MB)                           │
│    Hard cap — RecordTooLargeException if exceeded,             │
│    no network call attempted. Must be coordinated with          │
│    broker's message.max.bytes.                                  │
└─────────────────────────────────────────────────────────┘
        │ within limit
        ▼
┌─────────────────────────────────────────────────────────┐
│ 4. NETWORK CONNECTION (if not already open)                  │
│    socket.connection.setup.timeout.ms = 10000 (10s)            │
│    socket.connection.setup.timeout.max.ms = 30000 (30s)         │
│    On drop/failure, reconnect governed by:                      │
│      reconnect.backoff.ms = 50                                   │
│      reconnect.backoff.max.ms = 1000                              │
│      (exponential backoff with ceiling)                            │
└─────────────────────────────────────────────────────────┘
        │ connected
        ▼
┌─────────────────────────────────────────────────────────┐
│ 5. SEND REQUEST, AWAIT BROKER RESPONSE                        │
│    request.timeout.ms = 30000 (30s)                             │
│    acks = all (alias: -1)                                        │
│      → requires min.insync.replicas (broker/topic config,         │
│        no producer default — commonly set to 2) to be              │
│        meaningful; below that ISR count, leader rejects writes      │
│    max.in.flight.requests.per.connection = 5                         │
│      (hard-capped at 5 when enable.idempotence=true)                  │
│    → on failure: retriable error, goes to step 6                        │
└─────────────────────────────────────────────────────────┘
        │
   ┌────┴────┐
   │ success │ failure
   ▼         ▼
 DONE   ┌─────────────────────────────────────────────────┐
        │ 6. RETRY                                            │
        │    retries = 2147483647 (Integer.MAX_VALUE by         │
        │      default when idempotence enabled)                  │
        │    retry.backoff.ms = 100                                 │
        │    enable.idempotence = true                                │
        │      → PID + per-partition sequence numbers ensure           │
        │        broker rejects/dedupes duplicate retries correctly     │
        │      → requires acks=all                                       │
        └─────────────────────────────────────────────────┘
                    │
                    ▼
        ┌─────────────────────────────────────────────────┐
        │ 7. OVERALL BUDGET CHECK                              │
        │    delivery.timeout.ms = 120000 (120s)                 │
        │    Constraint: delivery.timeout.ms >= linger.ms +        │
        │                request.timeout.ms (enforced at startup)   │
        │    → exceeded across all retries: record permanently       │
        │      fails, surfaced via the returned Future/callback        │
        └─────────────────────────────────────────────────┘

[Transactional producer — wraps steps 1-7]
┌─────────────────────────────────────────────────────────┐
│ transactional.id = <none by default — must be set explicitly>│
│ transaction.timeout.ms = 60000 (60s)                            │
│ Entire beginTransaction() ... commitTransaction() span must      │
│ finish within this window, or the broker's transaction             │
│ coordinator forcibly aborts it. Requires enable.idempotence=true.    │
└─────────────────────────────────────────────────────────┘
```

## Quick Reference Table

| Step | Config | Default | Governs |
|---|---|---|---|
| 1 | `max.block.ms` | 60000 (60s) | Local buffer/metadata admission wait |
| 1 | `buffer.memory` | 33554432 (32MB) | Total memory for unsent records |
| 1 | `metadata.max.age.ms` | 300000 (5min) | Forced metadata refresh interval |
| 2 | `linger.ms` | 0 | Batching delay |
| 2 | `batch.size` | 16384 (16KB) | Batching threshold (per partition) |
| 2 | `compression.type` | none | Batch compression algorithm |
| 3 | `max.request.size` | 1048576 (1MB) | Hard per-request size ceiling |
| 4 | `socket.connection.setup.timeout.ms` | 10000 (10s) | Initial TCP connect wait |
| 4 | `socket.connection.setup.timeout.max.ms` | 30000 (30s) | Max TCP connect wait after backoff growth |
| 4 | `reconnect.backoff.ms` | 50 | Initial reconnect delay |
| 4 | `reconnect.backoff.max.ms` | 1000 (1s) | Max reconnect delay |
| 5 | `request.timeout.ms` | 30000 (30s) | Per-request broker response wait |
| 5 | `acks` | all (-1) | Durability level (0 / 1 / all) |
| 5 | `min.insync.replicas` | (broker/topic config, no producer default) | Min replicas required for acks=all to succeed |
| 5 | `max.in.flight.requests.per.connection` | 5 | Concurrent unacknowledged requests (capped at 5 w/ idempotence) |
| 6 | `retries` | Integer.MAX_VALUE (w/ idempotence) | Retry attempt count |
| 6 | `retry.backoff.ms` | 100 | Delay between retry attempts |
| 6 | `enable.idempotence` | true (since Kafka 3.0) | Dedupe on retry via PID + sequence numbers |
| 7 | `delivery.timeout.ms` | 120000 (120s) | End-to-end budget across all retries |
| — | `transactional.id` | none (must be set explicitly) | Enables transactional producer |
| — | `transaction.timeout.ms` | 60000 (60s) | Max transaction lifespan before forced abort |

## Key Relationships & Constraints

- **`acks=all` requires `enable.idempotence=true`** to be meaningful for exactly-once semantics; the reverse is also true — idempotence requires `acks=all`.
- **`enable.idempotence=true` caps `max.in.flight.requests.per.connection` at 5** — Kafka refuses to start above this.
- **`delivery.timeout.ms >= linger.ms + request.timeout.ms`** is enforced at startup — the overall budget must fit at least one full attempt cycle.
- **A transactional producer requires idempotence** — `transactional.id` set implies `enable.idempotence=true`.
- **`max.request.size` should be ≤ broker's `message.max.bytes`** — otherwise the broker rejects requests the producer itself considers valid.

## Drawbacks / Cons / Edge Cases

1. **These settings form a dependency chain, not independent knobs.** Raising `request.timeout.ms` without raising `delivery.timeout.ms` reduces the number of retry attempts that fit in the same overall budget, rather than adding more patience.
2. **Kafka-level tuning doesn't protect against application-level thread blocking.** A perfectly-tuned producer can still cause request-thread pool exhaustion if application code calls `.get()` synchronously on the returned `Future` without its own bounded timeout.
3. **`acks=all` + high `min.insync.replicas` trades availability for durability.** E.g., replication factor 3 with `min.insync.replicas=3` means any single replica outage makes the topic unwritable. `min.insync.replicas=2` with replication factor 3 is the common balanced setup.
4. **Backoff settings too aggressive (too low) can worsen an outage** by hammering a struggling/recovering broker with rapid retries/reconnects right when it needs breathing room — this is exactly what exponential backoff with a ceiling is designed to prevent.
5. **`batch.size`/`linger.ms` tuned for throughput increase per-message latency.** Not ideal for latency-sensitive synchronous paths; better suited to high-throughput, latency-tolerant pipelines (e.g., analytics/logging).
6. **Large payloads should generally avoid raising `max.request.size` fleet-wide.** Common pattern: store large blobs in S3/blob storage, put a reference in the Kafka message instead.
7. **Best practice for production tuning:** model the actual worst-case scenario (broker fully down for N seconds) end-to-end through the whole chain above, rather than tuning each setting against an isolated benchmark — the numbers only make sense together.
