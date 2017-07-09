# money_transfer

This is a simple RESTful web-app emulating money transferring service

## Technologies/frameworks used:

Framework | Purpose | URL
------------ | ------------- | -------------
Guice | Bean wiring | https://github.com/google/guice/wiki/GettingStarted
Spark | Content in the second column | http://sparkjava.com
Log4j | Logging | https://logging.apache.org/log4j/2.x/
GSON | JSON marshalling | https://github.com/google/gson

> this dumb table above is 99% to check out this new GitHub markdown language and 1% to show that I like documenting projects


## Running the application

` java com.barthezzko.server.Server `

## Rest method overview API
TBD: after implementing

## TBD (to implement)
* Integration tests with running service
* Multi-threaded testing (add a delay paramaterisation to TransferServiceImpl) <- to check locking mechanism
* User -> User
* Edge cases

## REST Methods
RequestType | Method | Params 
------------ | ------------ | ------------- 
GET | /serverStatus | NONE
POST | /client/add | String clientName
POST | /account/add | String clientId, String{RUR, EUR, USD} currency
POST | /transfer/acc2acc | String sourceAcc, String destAcc, BigDecimal amount
POST | /transfer/cli2cli | String sourceClient, String destClient, BigDecimal amount, String{RUR, EUR, USD} currency
POST | /account/topup | String destAccount, BigDecimal amount
GET | /account/:accountId | String accountId
GET | /client/:clientId | String clientId

## Testing approache

Unit tests:
* FXTest
* KeyGeneratorTest

IntegrationTest
* ServerIntegrationTest
