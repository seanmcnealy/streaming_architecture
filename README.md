## Streaming Architecture

A simple starter application that illustrates different ways to design an application
that does computations on events.

### [common](common/README.md)

Simple model objects and database access.

### [spring-web](spring-web/README.md)

An event-collector application that accepts simple HTTP POST calls to create events.

### [batch](batch/README.md)

A standalone application that reads and processes new events that have been saved
to a database table.

### [spring-batch](spring-batch/README.md)

Using the same simple queries as the `batch` application, harness spring-batch to
give us more features.

### [spring-kafka](spring-kafka/README.md)

Do the same work as `batch`, but partition and distribute and manage completely differently
in a streaming way.
