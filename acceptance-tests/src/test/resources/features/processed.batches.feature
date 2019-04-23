@processed-batches
Feature: Verify Retrieval of batch processing results

  Background:
    * url baseUrl
    * def result = callonce read('./oauth2.feature')
    * header Authorization = 'Bearer ' + result.accessToken
    * header Content-Type = 'application/json'
    * header Accept = jsonVersionHeader
    * def S3Utils = Java.type('uk.gov.service.printservice.test.utils.S3Utils')
    * def System = Java.type('java.lang.System')
    * def env = System.getenv('bb_env')
    * def s3 = new S3Utils()
    * def inBucketName = 'uk-gov-dft-' + (env == null ? 'ci' : env) +'-badge-in'

  Scenario: Process a confirmation and rejection and an invalid file
    * eval s3.cleanBucket(inBucketName)
    * eval s3.putObject(inBucketName, '/processedbatchxmlfiles/ValidConfirmation6Badges.xml', 'ValidConfirmation6Badges.xml')
    * eval s3.putObject(inBucketName, '/processedbatchxmlfiles/ValidRejection2Badges.xml', 'ValidRejection2Badges.xml')
    * eval s3.putObject(inBucketName, '/processedbatchxmlfiles/InvalidTruncatedFile.xml', 'InvalidTruncatedFile.xml')
    Given path 'processed-batches'
    When method GET
    Then status 200
      # All 3 files processed, including invalid one.
    And match $.data[*].filename contains 'ValidConfirmation6Badges.xml'
    And match $.data[*].filename contains 'ValidRejection2Badges.xml'
    And match $.data[*].filename contains 'InvalidTruncatedFile.xml'

  Scenario: Confirmation fields populated ok
    * eval s3.cleanBucket(inBucketName)
    * eval s3.putObject(inBucketName, '/processedbatchxmlfiles/ValidConfirmation1Badge.xml', 'ValidConfirmation1Badge.xml')
    Given path 'processed-batches'
    When method GET
    Then status 200
    And match $.data == [{"filename":"ValidConfirmation1Badge.xml","errorMessage":null,"fileType":"CONFIRMATION","processedBadges":[{"cancellation":"NO","dispatchedDate":"2018-02-02T14:24:34Z","badgeNumber":"AAAAAA","errorMessage":null}]}]

  Scenario: Rejection fields populated ok
    * eval s3.cleanBucket(inBucketName)
    * eval s3.putObject(inBucketName, '/processedbatchxmlfiles/ValidRejection1Badge.xml', 'ValidRejection1Badge.xml')
    Given path 'processed-batches'
    When method GET
    Then status 200
    And match $.data == [{"filename":"ValidRejection1Badge.xml", "errorMessage":null, "fileType":"REJECTION", "processedBadges":[{"cancellation":null,"dispatchedDate":null,"badgeNumber":"BBBBBA","errorMessage":": Photograph not found"}]}]

