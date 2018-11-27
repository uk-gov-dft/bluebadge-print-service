@print-service-print-btch
Feature: Verify Print batch ok

  Background:
    * url baseUrl
    * def result = callonce read('./oauth2.feature')
    * header Authorization = 'Bearer ' + result.accessToken

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
    Then status 200
