## streaming-architecture batch

A simple program that reads new Events from a database and does something with each Event.

Events are read in order and their processing is done at-least-once.

### Checkpointing

Uses a table named `batch_processed` to save the highest seen identifier. This value is saved
at the end of a successful run. It's then read at the beginning of the next run.

This means there are no periodic incremental checkpoints.
