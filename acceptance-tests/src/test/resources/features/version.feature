@version
Feature: Verify incorrect version fails

  Background:
    * url baseUrl
    * def result = callonce read('./oauth2.feature')
    * header Authorization = 'Bearer ' + result.accessToken
    * header Accept = 'application/vnd.bluebadge-api.v0+json, application/json'

  Scenario: Verify bean validation gives common response wrapped error
    Given path 'processed-batches/ValidConfirmation6Badges.xml'
    When method DELETE
    Then status 406