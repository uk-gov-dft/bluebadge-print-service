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
     --data '{"filename" : "filename1","batchType" : "STANDARD","badges" : [ {"localAuthorityShortCode" : "ANGL","badgeNumber" : "AA12BB","party" : {"typeCode" : "PERSON","contact" : {"fullName" : "John First", "buildingStreet" : "Main str.", "line2" : "20", "townCity" : "London","postCode" : "SW1 1AA", "primaryPhoneNumber" : "","secondaryPhoneNumber" : null, "emailAddress" : "john@email.com"}, "person" : {"badgeHolderName" : "John First", "dob" : "1977-03-04", "genderCode" : "MALE"}, "startDate" : "2019-01-02", "expiryDate" : "2021-01-01", "deliverToCode" : "HOME", "deliveryOptionCode" : "STAND", "imageLink" : "s3_bucket/photo1.jpg"}}]}' \
     http://localhost:8880/uk-gov-dft/print-service/1.0/printBatch
```

If you're running outside of docker, there is docker-compose.yml for starting sftp server. 
Run docker-compose up to start sftp.
Create sftp_known_hosts file by running: ssh-keyscan -p 2222 localhost > ~/.ssh/sftp_known_hosts
To connect to sftp: sftp -P 2222 -o UserKnownHostsFile=~/.ssh/sftp_known_hosts foo@localhost

At the moment all connection details for sftp are defaulted to:
  host: localhost
  port: 2222
  user: foo
   ***REMOVED***
  dropbox: /upload
  knownhosts: ~/.ssh/sftp_known_hosts


