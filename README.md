# Money Transfer Service

Implementation of the RESTful API for money transfers between accounts.

### Design

The money transfer service represents a server-side application. The separation in layouts lies in basis of the architecture. 
There are two data packages (`me.volart.dao.model` and `me.volart.dto`), it was made intentionally in order to show that we separate client interaction stuff and server things.

The system works with clients who have account with specified currency and amount. 
The amount is *Java long type*. Suppose that the fractional parts of currency is included into the long value.
The currency is a currency code satisfying the ISO 4217.

For more information regarding currency look at [ISO 4217](https://en.wikipedia.org/wiki/ISO_4217)

The system has ability to create, get, delete a client and transfer money between clients.

### RESTful API

Method   | Description                         | URL                        | Request    | Response
---------|-------------------------------------|----------------------------| -----------|------------
POST     |Create client                        | /client                    | Client     | ResponseInfo
GET      |Get client                           | /client/:clientId          | -          | ResponseInfo
DELETE   |Delete client                        | /client/:clientId          | -          | ResponseInfo
POST     |Transfer money from client to another| /client/:clientId/transfer |TransferInfo| ResponseInfo

_:clientId_ - the long value 
    
### Data type examples

**TransferInfo**
```
{  
   "clientId":1,
   "amount":100,
   "currency":"USD"
}
```  

**Client**
```
{
   "id":1,
   "accounts":[
      {
         "amount":100,
         "currency":"RUB"
      }
   ]
} 
```

**ResponseInfo**
```
{
   "message":"text_message",
   "data":{
      "id":1,
      "accounts":[
         {
            "amount":100,
            "currency":"RUB"
         }
      ]
   },
   "statusCode":0
}
```

### Build, run and test

To build the service you should run the command below
`mvn clean package`

To run locally from the project folder
Cope dependencies to the directory `mvn install dependency:copy-dependencies`

Then run `java -cp target/money-transfer-service-1.0-SNAPSHOT.jar:target/dependency/* me.volart.App`

If you want to stop running service press `Control+C`

To pass tests
`mvn test`

In the `me.volart.MoneyTransferServiceIT` class there are integration tests.  