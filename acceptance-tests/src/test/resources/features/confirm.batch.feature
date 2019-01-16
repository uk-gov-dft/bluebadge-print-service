@confirm-batch
Feature: Verify Retrieval of batch processing results

  Background:
    * url baseUrl
    * def result = callonce read('./oauth2.feature')
    * header Authorization = 'Bearer ' + result.accessToken
    * header Content-Type = 'application/json'
    * def S3Utils = Java.type('uk.gov.service.printservice.test.utils.S3Utils')
    * def System = Java.type('java.lang.System')
    * def env = System.getenv('bb_env')
    * def s3 = new S3Utils()
    * def inBucketName = 'uk-gov-dft-' + (env == null ? 'ci' : env) +'-badge-in'

  Scenario: Ok when request to delete a batch that exists
    * eval s3.cleanBucket(inBucketName)
    * eval s3.putObject(inBucketName, '/processedbatchxmlfiles/ValidConfirmation6Badges.xml', 'ValidConfirmation6Badges.xml')
    * assert s3.objectExists(inBucketName, 'ValidConfirmation6Badges.xml')
    Given path 'processed-batches/ValidConfirmation6Badges.xml'
    When method DELETE
    Then status 200
    And assert !s3.objectExists(inBucketName, 'ValidConfirmation6Badges.xml')

  Scenario: Not found when batch does not exist
    Given path 'processed-batches/IdoNotExist.xml'
    When method DELETE
    Then status 404
