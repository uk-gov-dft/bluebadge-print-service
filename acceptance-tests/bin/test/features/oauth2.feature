@authentication
Feature: Authenticate with the authorisation service

  Background:
    * url authServerUrl

  Scenario: Obtain access token with client credentials

    * path 'oauth/token'
    * header Authorization = call read('classpath:basic-auth.js') { username: '***REMOVED***',  ***REMOVED*** }
    * form field grant_type = 'client_credentials'
    * method post
    * status 200
    * match $.access_token == '#notnull'
    * match $.token_type == 'bearer'
    * def accessToken = response.access_token