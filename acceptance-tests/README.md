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

### How to validate a printer provider print batch xml request file using command line
Check this file: 
acceptance-tests/src/test/resources/testPrintBatchXmlFile.sh

### Hot to validate a printer provider print batch xml request file from within karate
Check this file
acceptance-tests/src/test/resources/features/print-batch-ok-validation.feature


### Relevant Articles: 
- [Test a REST API with Java](http://www.baeldung.com/2011/10/13/integration-testing-a-rest-api/)
- [Introduction to WireMock](http://www.baeldung.com/introduction-to-wiremock)
- [REST API Testing with Cucumber](http://www.baeldung.com/cucumber-rest-api-testing)
- [Testing a REST API with JBehave](http://www.baeldung.com/jbehave-rest-testing)
- [REST API Testing with Karate](http://www.baeldung.com/karate-rest-api-testing)

