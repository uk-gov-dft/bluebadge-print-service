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
                  badgeHolderName: 'Scottish Test Org Ltd.'
               }
		    },
		    "startDate" : "2019-01-02",
		    "expiryDate" : "2021-01-01",
		    "deliverToCode" : "COUNCIL",
		    "deliveryOptionCode" : "FAST",
		  }, {
		    "localAuthorityShortCode" : "ANGL",
		    "badgeNumber" : "CC12DD",
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
    * def ftpFileCountAfter = ftp.getFileCount()
    * def s3FileCountAfter = s3.getNumberOfFilesInABucket(printerBucketName)
    * print 'ftpbefore:' + ftpFileCountBefore + ',ftpAfterCount:' + ftpFileCountAfter
    * print 's3before:' + s3FileCountBefore + ',s3AfterCount:' + s3FileCountAfter
    * assert s3FileCountBefore == s3FileCountAfter
    * assert ftpFileCountBefore + 1 == ftpFileCountAfter
