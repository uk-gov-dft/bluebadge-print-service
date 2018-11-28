@print-service-print-batch
Feature: Verify Print batch ok

  Background:
    * url baseUrl
    * def result = callonce read('./oauth2.feature')
    * header Authorization = 'Bearer ' + result.accessToken
    * def S3Utils = Java.type('uk.gov.service.printservice.test.utils.S3Utils')
    * def System = Java.type('java.lang.System')
	* def s3 = new S3Utils()
	* def bucketName = 'uk-gov-dft-' + System.getenv('bb_env') +'-printer' 

  Scenario: Verify valid print batch
    * def batches =
    """
[ {
  "filename" : "filename1",
  "localAuthorities" : [ {
    "laCode" : "ABERD",
    "laName" : "Aberdinshire council",
    "issuingCountry" : "S",
    "languageCode" : "E",
    "clockType" : "STANDARD",
    "phoneNumber" : "07875506745",
    "emailAddress" : "bluebadge@aberdine.gov.uk",
    "badges" : [ {
      "badgeIdentifier" : "AA12BB",
      "printedBadgeReference" : "AA12BB 0 0580X0121",
      "startDate" : "2019-01-02",
      "expiryDate" : "2021-01-01",
      "dispatchMethodCode" : "M",
      "fastTrackCode" : "Y",
      "postageCode" : "SC",
      "photo" : "http://url_to_s3_bucket_photo1",
      "barCodeData" : "80X1220",
      "name" : {
        "forename" : "John",
        "surname" : "First"
      },
      "letterAddress" : {
        "nameLine" : "John First",
        "addressLine1" : "20",
        "addressLine2" : "Main str.",
        "town" : "London",
        "country" : "United Kingdom",
        "postcode" : "SW1 1AA"
      }
    }, {
      "badgeIdentifier" : "AA34BB",
      "printedBadgeReference" : "AA34BB 0 0199Y0121",
      "startDate" : "2019-01-02",
      "expiryDate" : "2021-01-01",
      "dispatchMethodCode" : "C",
      "fastTrackCode" : "N",
      "postageCode" : "SC",
      "photo" : "http://url_to_s3_bucket_photo2",
      "barCodeData" : "Y1220",
      "name" : {
        "forename" : "Jane",
        "surname" : "Second"
      },
      "letterAddress" : {
        "nameLine" : "Jane Second",
        "addressLine1" : "Council",
        "addressLine2" : "government road",
        "town" : "London",
        "country" : "United Kingdom",
        "postcode" : "EC1 2Z"
      }
    } ]
  }, {
    "laCode" : "ANGL",
    "laName" : "Anglesey council",
    "issuingCountry" : "W",
    "languageCode" : "EW",
    "clockType" : "WALET",
    "phoneNumber" : "07875506746",
    "emailAddress" : "bluebadge@anglesey.gov.uk",
    "badges" : [ {
      "badgeIdentifier" : "CC12DD",
      "printedBadgeReference" : "CC12DD 0 0152Y0121",
      "startDate" : "2019-01-02",
      "expiryDate" : "2021-01-01",
      "dispatchMethodCode" : "M",
      "fastTrackCode" : "Y",
      "postageCode" : "SD1",
      "photo" : "http://url_to_s3_bucket_photo3",
      "barCodeData" : "52Y1220",
      "name" : {
        "forename" : "Michael",
        "surname" : "Third"
      },
      "letterAddress" : {
        "nameLine" : "Michael Third",
        "addressLine1" : "flat 5",
        "addressLine2" : "century building",
        "town" : "Leeds",
        "country" : "United Kingdom",
        "postcode" : "LS1 3XX"
      }
    }, {
      "badgeIdentifier" : "CC34DD",
      "printedBadgeReference" : "CC34DD 0 1228Z0121",
      "startDate" : "2019-01-02",
      "expiryDate" : "2021-01-01",
      "dispatchMethodCode" : "M",
      "fastTrackCode" : "Y",
      "postageCode" : "SD1",
      "photo" : "http://url_to_s3_bucket_photo4",
      "barCodeData" : "Z0121",
      "name" : {
        "forename" : "Name",
        "surname" : "Last"
      },
      "letterAddress" : {
        "nameLine" : "Name Last",
        "addressLine1" : "88",
        "addressLine2" : "pleasant walk",
        "town" : "Manchester",
        "country" : "United Kingdom",
        "postcode" : "M4 3AS"
      }
    } ]
  } ]
} ]
    """
    
    * def beforeCount = s3.getNumberOfFilesInABucket(bucketName)
    Given path 'printBatch'
    And request batches
    When method POST
    Then status 200
    * def afterCount = s3.getNumberOfFilesInABucket(bucketName)
    * assert afterCount > beforeCount
    * assert afterCount - 1 == beforeCount
    
