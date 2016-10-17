## Spring Web Reactive POC

### Goal

This POC demonstrates the Web Reactive support in `Spring 5`.
The application subscribes to a `SSE` stream of `Artifacts`.
A random `Artifact` is persisted to `Couchbase` each time the user calls a `REST` endpoint and is pushed to the subscribed `SSE` stream.
A `ReplayProcessor` is used to implement a broadcaster cache mechanism that sends the events missed when the `SSE` connection was reconnecting.

### Use the sample

You need to install first `Couchbase` server on your local machine, create a bucket with name `artifacts` and create an index with the following command:

    CREATE PRIMARY INDEX ON `artifacts`

Then checkout the repository and run the command `mvn spring-boot:run`.

### Inspiration

The POC is inspired from the [Spring Reactive Playground project of Sébastien Deleuze](https://github.com/sdeleuze/spring-reactive-playground).