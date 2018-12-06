@print-service-print-btch.validation
Feature: Verify Print batch ok validation

  Background:
    * url baseUrl
    * def result = callonce read('./oauth2.feature')
    * header Authorization = 'Bearer ' + result.accessToken
    * def cmdLineUtilsConfig = {}
    * def CommandLineUtils = Java.type('uk.gov.service.bluebadge.test.utils.CommandLineUtils')
    * def cmdLineUtils = new CommandLineUtils(cmdLineUtilsConfig)

  Scenario: Verify valid print batch comparing xml
    * def batch =
    """
      {
        "filename": "1.xml",
        "batchType": "STANDARD",
        "localAuthorities": [
          {
            "laCode": "",
            "laName": "",
            "issuingCountry": "",
            "languageCode": "",
            "clockType": "",
            "phoneNumber": "",
            "emailAddress": "",
            "badges": [
              {
                "badgeIdentifier": "",
                "printedBadgeReference": "",
                "startDate": "",
                "expiryDate": "",
                "dispatchMethodCode": "",
                "fastTrackCode": "",
                "postageCode": "",
                "photo": "",
                "barCodeData": "",
                "name": {
                  "forename": "",
                  "surname": ""
                },
                "letterAddress": {
                  "nameLine": "",
                  "addressLine1": "",
                  "addressLine2": "",
                  "town": "",
                  "country": "",
                  "postcode": ""
                }
              }
            ]
          }
        ]
      }
    """
    Given path 'printBatch'
    And request batch
    When method POST
    Then status 200
    And def actualPrintBatchOutput = read('../actual-print-batch-output.xml')
    And def expectedPrintBatchOutput = read('../expected-print-batch-output.xml')
    And match actualPrintBatchOutput == expectedPrintBatchOutput


  # Examples of using XML
  Scenario: Verify valid print file (matching file as a whole)
    Given def actualPrintBatchOutput = read('../actual-print-batch-output.xml')
    And def expectedPrintBatchOutput = read('../expected-print-batch-output.xml')
    Then match actualPrintBatchOutput == expectedPrintBatchOutput

  Scenario: Verify valid print file (matching properties)
    Given def someXml = read('../actual-print-batch-output.xml')
    Then match someXml/BadgePrintExtract/Batch/Filename == 'BADGEEXTRACT180808120021.xml'
    And match someXml count(/BadgePrintExtract/Batch) == 1

  # Basic test to show xml matching
  Scenario: Verify valid print batch basic xml
    Given def someXml = <name><surname>value</surname></name>
    Then match someXml.name.surname == 'value'

  Scenario: Verify valid print file read simple xml file
    Given def someXmlFile = read('../my-xml.xml')
    Then match someXmlFile == '<name><surname>value</surname></name>'
    Then match someXmlFile.name.surname == 'value'

  Scenario: Verify valid print file read 2 files only difference is whitespace
    Given def someXmlFile = read('../my-xml.xml')
    Given def someXmlFileWhitespaceDifferent = read('../my-xml-whitespace-different.xml')
    Then match someXmlFile == someXmlFileWhitespaceDifferent