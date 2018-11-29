# DFT BLUE BADGE BETA - PRINT-SERVICE
Print Service manages interactions with the Print Provider 

## Getting Started in few minutes
From command line:
```
git clone git@github.com:uk-gov-dft/print-service.git
cd print-service
gradle wrapper
./gradlew build
./gradlew bootRun
```


## PLAY WITH THE API

Base url:
http://localhost:8880/uk-gov-dft/print-service/1.0

* Post batch for printing:
```
curl --header "Content-Type: application/json" \
     --request POST \
     --data '[{"filename" : "filename1", "localAuthorities" : [ {"laCode" : "ABERD", "laName" : "Aberdinshire council","issuingCountry" : "S","languageCode" : "E","clockType" : "STANDARD","phoneNumber" : "07875506745","emailAddress" : "bluebadge@aberdine.gov.uk","badges" : [{"badgeIdentifier" : "AA12BB","printedBadgeReference" : "AA12BB 0 0580X0121","startDate" : "2019-01-02","expiryDate" : "2021-01-01","dispatchMethodCode" : "M","fastTrackCode" : "Y","postageCode" : "SC","photo" : "http://url_to_s3_bucket_photo1","barCodeData" : "80X1220","name" : {"forename" : "John","surname" : "First"},"letterAddress" : {"nameLine" : "John First","addressLine1" : "20","addressLine2" : "Main str.","town" : "London","country" : "United Kingdom","postcode" : "SW1 1AA"}]}]}]' \
     http://localhost:8880/uk-gov-dft/print-service/1.0/printBatch
```