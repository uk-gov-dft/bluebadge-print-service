@print-service-print-batch
Feature: Verify Print batch ok

  Background:
    * url baseUrl
    * def result = callonce read('./oauth2.feature')
    * header Authorization = 'Bearer ' + result.accessToken
    * def S3Utils = Java.type('uk.gov.service.printservice.test.utils.S3Utils')
    * def SFTPUtils = Java.type('uk.gov.service.printservice.test.utils.SFTPUtils')
    * def System = Java.type('java.lang.System')
    * def env = System.getenv('bb_env')
    * def s3 = new S3Utils()
    * def ftp = new SFTPUtils()
    * def printerBucketName = 'uk-gov-dft-' + (env == null ? 'ci' : env) +'-printer'
    * def badgeBucketName = 'uk-gov-dft-' + (env == null ? 'ci' : env) +'-badge'
    * def sleep =
      """
      function(seconds){
        for(i = 0; i <= seconds; i++)
        {
          java.lang.Thread.sleep(1000);
          karate.log(i);
        }
      }
      """
    * def validateXmlPrintBatchRequest =
    """
    function(script) {
      var XMLValidator = Java.type('uk.gov.service.printservice.test.utils.XMLValidator');
      var xmlValidator = new XMLValidator();
      return xmlValidator.validate(script, 'BadgePrintExtract.xsd');
    }
    """

  Scenario: Verify valid print batch
    * def batch =
    """
		{
		  "filename" : "BATCHEXTRACT_TEST_20190111105700",
		  "batchType" : "STANDARD",
		  "badges" : [ {
		    "localAuthorityShortCode" : "ANGL",
		    "badgeNumber" : "AA12BB",
		    "party" : {
		      "typeCode" : "PERSON",
		      "contact" : {
		        "fullName" : "John Firsts Contact Name",
		        "buildingStreet" : "Main str.",
		        "line2" : "20",
		        "townCity" : "London",
		        "postCode" : "SW1 1AA",
		        "primaryPhoneNumber" : "",
		        "secondaryPhoneNumber" : null,
		        "emailAddress" : "john@email.com"
		      },
		      "person" : {
		        "badgeHolderName" : "John J First",
		        "dob" : "1977-03-04",
		        "genderCode" : "MALE"
		       }
		    },
		    "startDate" : "2019-01-02",
		    "expiryDate" : "2021-01-01",
		    "deliverToCode" : "COUNCIL",
		    "deliveryOptionCode" : "STAND",

		  }, {
		    "localAuthorityShortCode" : "ANGL",
		    "badgeNumber" : "AA34BB",
		    "party" : {
		      "typeCode" : "PERSON",
		      "contact" : {
		        "fullName" : "Jane Second",
		        "buildingStreet" : "government road",
		        "line2" : "Council",
		        "townCity" : "London",
		        "postCode" : "EC1 2Z",
		        "primaryPhoneNumber" : "",
		        "secondaryPhoneNumber" : null,
		        "emailAddress" : "jane@email.com"
		      },
		      "person" : {
		        "badgeHolderName" : "Somebody With An exceptionally Long Name",
		        "dob" : "1987-12-08",
		        "genderCode" : "FEMALE"
		      }
		    },
		    "startDate" : "2019-01-02",
		    "expiryDate" : "2021-01-01",
		    "deliverToCode" : "HOME",
		    "deliveryOptionCode" : "STAND",
		  }, {
		    "localAuthorityShortCode" : "ABERD",
		    "badgeNumber" : "CC12DD",
		    "party" : {
		      "typeCode" : "ORG",
		      "contact" : {
		        "fullName" : "Mr Deliver LA",
		        "buildingStreet" : "Business St",
		        "line2" : "flat 5",
		        "townCity" : "Leeds",
		        "postCode" : "LS1 3XX",
		        "primaryPhoneNumber" : "",
		        "secondaryPhoneNumber" : null,
		        "emailAddress" : "mike@email.com"
		      },
		      organisation: {
                  badgeHolderName: 'Scottish Test Org Ltd & Co'
               }
		    },
		    "startDate" : "2019-01-02",
		    "expiryDate" : "2021-01-01",
		    "deliverToCode" : "COUNCIL",
		    "deliveryOptionCode" : "FAST",
		  }, {
		    "localAuthorityShortCode" : "ANGL",
		    "badgeNumber" : "CC12DE",
		    "party" : {
		      "typeCode" : "ORG",
		      "contact" : {
		        "fullName" : "Mr Deliver Home",
		        "buildingStreet" : "century building",
		        "line2" : "flat 5",
		        "townCity" : "Leeds",
		        "postCode" : "LS1 3XX",
		        "primaryPhoneNumber" : "",
		        "secondaryPhoneNumber" : null,
		        "emailAddress" : "mike@email.com"
		      },
		      organisation: {
                  badgeHolderName: 'Welsh Test Org Ltd & co > < & > <'
               }
		    },
		    "startDate" : "2019-01-02",
		    "expiryDate" : "2021-01-01",
		    "deliverToCode" : "HOME",
		    "deliveryOptionCode" : "FAST",
		  } ]
		}
    """
    * set batch.badges[0].imageLink = "pictures/smile1.jpg"
    * set batch.badges[1].imageLink = "pictures/smile2.jpg"
    * set batch.badges[2].imageLink = ""

    * eval s3.putObject(badgeBucketName, '/pictures/smile1.jpg', 'pictures/smile1.jpg')
    * eval s3.putObject(badgeBucketName, '/pictures/smile2.jpg', 'pictures/smile2.jpg')

    * eval ftp.clean()
    * def ftpFileCountBefore = ftp.getFileCount()
    * eval s3.cleanBucket(printerBucketName)
    * def s3FileCountBefore = s3.getNumberOfFilesInABucket(printerBucketName)
    Given path 'printBatch'
    And request batch
    When method POST
    Then status 200
    # Processing is async
    * eval sleep(10)
    * def ftpFileCountAfter = ftp.getFileCount()
    * def s3FileCountAfter = s3.getNumberOfFilesInABucket(printerBucketName)
    * print 'ftpbefore:' + ftpFileCountBefore + ',ftpAfterCount:' + ftpFileCountAfter
    * print 's3before:' + s3FileCountBefore + ',s3AfterCount:' + s3FileCountAfter
    # If successful, any new file in s3 should have been cleaned up
    * assert s3FileCountBefore == s3FileCountAfter
    # and 1 file should have been sent by sftp
    * assert ftpFileCountBefore + 1 == ftpFileCountAfter
    # Validate xml file using xsd schema
    * def getFileResult = ftp.getFile('BATCHEXTRACT_TEST_20190111105700.xml')
    * assert getFileResult == true
    * def isXmlValidFile = validateXmlPrintBatchRequest('BATCHEXTRACT_TEST_20190111105700.xml')
    * print 'isXmlValidFile=' + isXmlValidFile
    * match isXmlValidFile == true
    # Validate xml file contains all the badges
    * def batchExtractFileXml = read('../../../../build/resources/test/BATCHEXTRACT_TEST_20190111105700.xml')
    * match batchExtractFileXml/BadgePrintExtract/Batch/Filename == 'BATCHEXTRACT_TEST_20190111105700.xml'
    * match batchExtractFileXml/BadgePrintExtract/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[*]/BadgeIdentifier contains only ['AA12BB', 'AA34BB', 'CC12DD', 'CC12DE']


  Scenario: Verify print batch with dodgy badges
    * def batch =
    """
		{
		  "filename" : "BATCHEXTRACT_TEST_20190111105702",
		  "batchType" : "STANDARD",
		  "badges" : [ {
		    "localAuthorityShortCode" : "ANGL",
		    "badgeNumber" : "AA12BB",
		    "party" : {
		      "typeCode" : "PERSON",
		      "contact" : {
		        "fullName" : "John Firsts Contact Name",
		        "buildingStreet" : "Main str.",
		        "line2" : "20",
		        "townCity" : "London",
		        "postCode" : "SW1 1AA",
		        "primaryPhoneNumber" : "",
		        "secondaryPhoneNumber" : null,
		        "emailAddress" : "john@email.com"
		      },
		      "person" : null
		    },
		    "startDate" : "2019-01-02",
		    "expiryDate" : "2021-01-01",
		    "deliverToCode" : "COUNCIL",
		    "deliveryOptionCode" : "STAND",

		  }, {
		    "localAuthorityShortCode" : "ANGL",
		    "badgeNumber" : "AA34BB",
		    "party" : {
		      "typeCode" : "PERSON",
		      "contact" : {
		        "fullName" : "Jane Second",
		        "buildingStreet" : "government road",
		        "line2" : "Council",
		        "townCity" : "London",
		        "postCode" : "EC1 2Z",
		        "primaryPhoneNumber" : "",
		        "secondaryPhoneNumber" : null,
		        "emailAddress" : "jane@email.com"
		      },
		      "person" : {
		        "badgeHolderName" : "Somebody With An exceptionally Long Name",
		        "dob" : "1987-12-08",
		        "genderCode" : "FEMALE"
		      }
		    },
		    "startDate" : "2019-01-02",
		    "expiryDate" : "2021-01-01",
		    "deliverToCode" : "HOME",
		    "deliveryOptionCode" : "STAND",
		  }, {
		    "localAuthorityShortCode" : "ABERD",
		    "badgeNumber" : "CC12DD",
		    "party" : {
		      "typeCode" : "ORG",
		      "contact" : {
		        "fullName" : "Mr Deliver LA",
		        "buildingStreet" : "Business St",
		        "line2" : "flat 5",
		        "townCity" : "Leeds",
		        "postCode" : "LS1 3XX",
		        "primaryPhoneNumber" : "",
		        "secondaryPhoneNumber" : null,
		        "emailAddress" : "mike@email.com"
		      },
		      organisation: {
                  badgeHolderName: 'Scottish Test Org Ltd.'
               }
		    },
		    "startDate" : "2019-01-02",
		    "expiryDate" : "2021-01-01",
		    "deliverToCode" : "COUNCIL",
		    "deliveryOptionCode" : "FAST",
		  }, {
		    "localAuthorityShortCode" : "ANGL",
		    "badgeNumber" : "CC12DE",
		    "party" : {
		      "typeCode" : "ORG",
		      "contact" : {
		        "fullName" : "Mr Deliver Home",
		        "buildingStreet" : "century building",
		        "line2" : "flat 5",
		        "townCity" : "Leeds",
		        "postCode" : "LS1 3XX",
		        "primaryPhoneNumber" : "",
		        "secondaryPhoneNumber" : null,
		        "emailAddress" : "mike@email.com"
		      },
		      organisation: {
                  badgeHolderName: 'Welsh Test Org Ltd.'
               }
		    },
		    "startDate" : "2019-01-02",
		    "expiryDate" : "2021-01-01",
		    "deliverToCode" : "HOME",
		    "deliveryOptionCode" : "FAST",
		  } ]
		}
    """
    * set batch.badges[0].imageLink = "pictures/smile1.jpg"
    * set batch.badges[1].imageLink = "pictures/smile2.jpg"
    * set batch.badges[2].imageLink = ""

    * eval s3.putObject(badgeBucketName, '/pictures/smile1.jpg', 'pictures/smile1.jpg')
    * eval s3.putObject(badgeBucketName, '/pictures/smile2.jpg', 'pictures/smile2.jpg')

    * eval ftp.clean()
    * def ftpFileCountBefore = ftp.getFileCount()
    * eval s3.cleanBucket(printerBucketName)
    * def s3FileCountBefore = s3.getNumberOfFilesInABucket(printerBucketName)
    Given path 'printBatch'
    And request batch
    When method POST
    Then status 200
    # Processing is async
    * eval sleep(10)
    * def ftpFileCountAfter = ftp.getFileCount()
    * def s3FileCountAfter = s3.getNumberOfFilesInABucket(printerBucketName)
    * print 'ftpbefore:' + ftpFileCountBefore + ',ftpAfterCount:' + ftpFileCountAfter
    * print 's3before:' + s3FileCountBefore + ',s3AfterCount:' + s3FileCountAfter
    # If successful, any new file in s3 should have been cleaned up
    * assert s3FileCountBefore == s3FileCountAfter
    # and 1 file should have been sent by sftp
    * assert ftpFileCountBefore + 1 == ftpFileCountAfter
    # Validate xml file using xsd schema
    * def getFileResult = ftp.getFile('BATCHEXTRACT_TEST_20190111105702.xml')
    * assert getFileResult == true
    * def isXmlValidFile = validateXmlPrintBatchRequest('BATCHEXTRACT_TEST_20190111105702.xml')
    * print 'isXmlValidFile=' + isXmlValidFile
    * match isXmlValidFile == true
    # Validate xml file contains only non dodgy the badges
    * def batchExtractFileXml = read('../../../../build/resources/test/BATCHEXTRACT_TEST_20190111105702.xml')
    * match batchExtractFileXml/BadgePrintExtract/Batch/Filename == 'BATCHEXTRACT_TEST_20190111105702.xml'
    * match batchExtractFileXml/BadgePrintExtract/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[*]/BadgeIdentifier contains only ['AA34BB','CC12DD','CC12DE']
