## Spring Web Reactive POC

### Goal

This POC demonstrates the Web Reactive support in `Spring 5`.
The application subscribes to a `SSE` stream of `Artifacts`.
A random `Artifact` is generated each time the user calls a `REST` endpoint and is pushed to the subscribed `SSE` stream.
A `ReplayProcessor` is used to implement a broadcaster cache mechanism that sends the events missed when the `SSE` connection was reconnecting.

### Use the sample

Checkout the repository and run the command `mvn spring-boot:run`.

### TODO

The POC needs to be completed with a `Couchbase` access through `spring-data` where `Artifact` objects can be stored.

### Inspiration

The POC is inspired from the [Spring Reactive Playground project of Sébastien Deleuze](https://github.com/sdeleuze/spring-reactive-playground).