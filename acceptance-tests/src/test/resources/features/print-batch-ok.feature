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
		  "filename" : "filename1",
		  "batchType" : "STANDARD",
		  "badges" : [ {
		    "localAuthorityShortCode" : "ANGL",
		    "badgeNumber" : "AA12BB",
		    "party" : {
		      "typeCode" : "PERSON",
		      "contact" : {
		        "fullName" : "John First",
		        "buildingStreet" : "Main str.",
		        "line2" : "20",
		        "townCity" : "London",
		        "postCode" : "SW1 1AA",
		        "primaryPhoneNumber" : "",
		        "secondaryPhoneNumber" : null,
		        "emailAddress" : "john@email.com"
		      },
		      "person" : {
		        "badgeHolderName" : "John First",
		        "dob" : "1977-03-04",
		        "genderCode" : "MALE"
		       }
		    },
		    "startDate" : "2019-01-02",
		    "expiryDate" : "2021-01-01",
		    "deliverToCode" : "HOME",
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
		        "badgeHolderName" : "Jane Second",
		        "dob" : "1987-12-08",
		        "genderCode" : "FEMALE"
		      }
		    },
		    "startDate" : "2019-01-02",
		    "expiryDate" : "2021-01-01",
		    "deliverToCode" : "HOME",
		    "deliveryOptionCode" : "STAND",
		  }, {
		    "localAuthorityShortCode" : "GLOCC",
		    "badgeNumber" : "CC12DD",
		    "party" : {
		      "typeCode" : "PERSON",
		      "contact" : {
		        "fullName" : "Michael Third",
		        "buildingStreet" : "century building",
		        "line2" : "flat 5",
		        "townCity" : "Leeds",
		        "postCode" : "LS1 3XX",
		        "primaryPhoneNumber" : "",
		        "secondaryPhoneNumber" : null,
		        "emailAddress" : "mike@email.com"
		      },
		      "person" : {
		        "badgeHolderName" : "Michael Third",
		        "dob" : "1934-02-05",
		        "genderCode" : "MALE"
		      }
		    },
		    "startDate" : "2019-01-02",
		    "expiryDate" : "2021-01-01",
		    "deliverToCode" : "HOME",
		    "deliveryOptionCode" : "STAND",
		  } ]
		}
    """
    * set batch.badges[0].imageLink = "/pictures/smile1.jpg"
    * set batch.badges[1].imageLink = "/pictures/smile2.jpg"
    * set batch.badges[2].imageLink = "/pictures/smile3.jpg"

	* eval s3.putObject(badgeBucketName, '/pictures/smile1.jpg')
    * eval s3.putObject(badgeBucketName, '/pictures/smile2.jpg')
    * eval s3.putObject(badgeBucketName, '/pictures/smile3.jpg')
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
