## streaming-architecture spring-web

Generates schema on startup 

Collects events POSTed over HTTP

Saves the Event to the database as a new unprocessed event. Also writes to a Kafka topic.

### Example sending event

`curl --request POST localhost:8081 --header 'Content-Type: application/json' --data '{"type": "test", "message": "test6"}'`
