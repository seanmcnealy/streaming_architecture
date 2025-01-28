## streaming-architecture spring-batch

The queries are based on the [batch](../batch) application, but uses Spring for some new features.

Events are still processed in order. We can have database transactions as we process each event.
For large jobs we probably want to [avoid one large transaction](https://docs.spring.io/spring-batch/reference/common-patterns.html#drivingQueryBasedItemReaders)
for the entire batch.

### Checkpointing

This is especially improved. Spring Batch can pause and start back where it left off.
Though there are steps to restarting a spring-batch program that has failed, the framework 
will save incremental offsets as Events are processed. This version can recover from a 
system failure in the middle of a long batch process with fewer Events
needed to be reprocessed.

### Partitioning

Spring-batch allows some partitioning in the `Job`, but it is not implemented here.

You can run different jobs (with their own job identifiers) that process different 
Events independently. This distributes the load to different Job executors, and possibly
different processes and hardware entirely.
