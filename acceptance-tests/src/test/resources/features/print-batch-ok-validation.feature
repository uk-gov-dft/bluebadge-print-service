@print-service-print-btch
Feature: Verify Print batch ok validation

  Background:
    * url baseUrl
    * def result = callonce read('./oauth2.feature')
    * header Authorization = 'Bearer ' + result.accessToken
    #* def cmdLineUtilsConfig = {}
      #* def DbUtils = Java.type('uk.gov.service.bluebadge.test.utils.DbUtils')
    #* def CommandLineUtils = Java.type('uk.gov.service.bluebadge.test.utils.CommandLineUtils')
    #* def cmdLineUtils = new CommandLineUtils(cmdLineUtilsConfig)
    * def executeShellScript =
    """
    function(script) {
      var CommandLineUtils = Java.type('uk.gov.service.bluebadge.test.utils.CommandLineUtils')
      var cmdLineUtilsConfig = {}
      var cmdLineUtils = new CommandLineUtils(cmdLineUtilsConfig);
      return cmdLineUtils.runScript(script);
    }
    """

  Scenario: Verify valid print batch
    * def batches =
    """
    [
      {
        "filename": "1.xml",
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
    ]
    """
    Given path 'printBatch'
    And request batches
    When method POST
    #Then status 200
    And def actualPrintBatchOutput = read('../actual-print-batch-output.xml')
    #And def isValid = call cmdLineUtils.runScript('testPrintBatchXmlFile.sh')
    #And print 'isValid=' + isValid
  #, 'actual-print-batch-output.xml');
    #And match isValid == true
    And def expectedPrintBatchOutput = read('../expected-print-batch-output.xml')
    And match actualPrintBatchOutput == expectedPrintBatchOutput
    * def myfunction =
    """
    function(s) {
      return "mys";
    }
    """

    #And def isValid2 =  executeShellScript('../testPrintBatchXmlFile.sh')
    And def isValid2 =  executeShellScript('pwd')
    And print "isValid2=" + isValid2
    And match isValid2 == true
    And def myresult = myfunction('hola')
    And print "myresult=" + myresult
    And match myresult == "mys"


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
    Given def someXml = <miguel><gil>value</gil></miguel>
    Then match someXml.miguel.gil == 'value'

  Scenario: Verify valid print file read simple xml file
    Given def someXmlFile = read('../my-xml.xml')
    Then match someXmlFile == '<miguel><gil>value</gil></miguel>'
    Then match someXmlFile.miguel.gil == 'value'

  Scenario: Verify valid print file read 2 files only difference is whitespace
    Given def someXmlFile = read('../my-xml.xml')
    Given def someXmlFileWhitespaceDifferent = read('../my-xml-whitespace-different.xml')
    Then match someXmlFile == someXmlFileWhitespaceDifferent
