@environment-s3-sftp
Feature: Verify s3 and sftp access

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

  Scenario: Verify access badge bucket
    * eval s3.removeObject(badgeBucketName, '/pictures/smile1.jpg')
    * assert !s3.objectExists(badgeBucketName, '/pictures/smile1.jpg')
    * assert '/pictures/smile1.jpg' == s3.putObject(badgeBucketName, '/pictures/smile1.jpg')
    * assert s3.objectExists(badgeBucketName, '/pictures/smile1.jpg')
    * eval s3.removeObject(badgeBucketName, '/pictures/smile1.jpg')

  Scenario: Verify access printer bucket
    * eval s3.removeObject(printerBucketName, '/pictures/smile1.jpg')
    * assert !s3.objectExists(printerBucketName, '/pictures/smile1.jpg')
    * assert '/pictures/smile1.jpg' == s3.putObject(printerBucketName, '/pictures/smile1.jpg')
    * assert s3.objectExists(printerBucketName, '/pictures/smile1.jpg')
    * eval s3.removeObject(printerBucketName, '/pictures/smile1.jpg')

  Scenario: Verify access sftp server
    * eval ftp.clean()
    * assert ftp.getFileCount() == 2
    * assert ftp.putFile('/pictures/smile1.jpg')
    * assert ftp.getFileCount() == 3