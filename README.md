1. docker run -d -p 9411:9411 openzipkin/zipkin
2. sbt run
3. open http://localhost:8080/hello and refresh several times
4. check tracing http://localhost:9411
5. notice how the same trace id keeps being logged for all requests