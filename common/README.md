## streaming-architecture common

### Schema

Includes a schema directory in `resources`, following the [Flyway version convention](https://github.com/flyway/flywaydb.org/blob/gh-pages/documentation/concepts/migrations.md).

### Model

The Event model is very abstract here, containing an `type` and a `message`. But could be anything
in your application. This could be a log message from an application or security firewall, a
receipt from a point of sale device, or an e-commerce sales order.

All fields are nullable, as that's how Java works.

### DAO

The database access is intentionally done in a simplistic way. Just basic prepared statements.
