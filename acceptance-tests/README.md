### API Acceptance tests

#### Start the message service

First you need to start the service by executing following commands

```
gradle bootRun
```

#### How to run api acceptance tests

```
gradle acceptanceTests
```
To run acceptance tests 'bb_env' environment variable should be provided ('dev', 'ci', etc)

To connect to fstp server there should be environment variables provided:
  sftp_host (default 'localhost')
  sftp_port (default '2222')
  sftp_user (default 'foo')
  sftp_pass (default 'pass')
  sftp_folder (default '/upload')


### Relevant Articles: 
- [Test a REST API with Java](http://www.baeldung.com/2011/10/13/integration-testing-a-rest-api/)
- [Introduction to WireMock](http://www.baeldung.com/introduction-to-wiremock)
- [REST API Testing with Cucumber](http://www.baeldung.com/cucumber-rest-api-testing)
- [Testing a REST API with JBehave](http://www.baeldung.com/jbehave-rest-testing)
- [REST API Testing with Karate](http://www.baeldung.com/karate-rest-api-testing)

