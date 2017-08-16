# Alternative draft for the REST api
All calls and requests return data as JSON.
### Current status
##### GET /status
Get the current status of the transformer.
*returns:* HTTP Response Code: 200 (always)

```json
{
    "status": "idle",
    "available_storage": 1000,
    "total_storage": 10000,
}
```
- *status*: current status of the transformer, values can either be `idle`, `transforming` or `error`
- *available_storage*: free hard disk storage in MB (integer)
- *total_storage*: total storage of the hard disk (integer, in MB)

### Manage target platforms

##### GET /platforms
Returns all target platforms which are available for transforming the CSAR.

*returns:*
```json
[ 
    "aws": {
        "href"="/platforms/aws"
    },
    "cloudformation": {
        "href"="/platforms/cloudformation"
    }
]
```
NOTE: Later it is possible to add platform specific preferences here

### Manipulating TOSCA Models

##### GET /toscamodels
Get a list of all toscamodels.  

*returns*:
```json
[
    {
        "href":"/toscamodels/1",
        "id":1,
        "name":"Hello World"
    },
    {
        "href":/toscamodels/2",
        "id":2,
        "name":"Billing App"
]
```
##### POST /toscamodels
Create a new TOSCA model. Returns a link to the new resource.
*Request body:*  
{
    "name"={appName}
}
- name: name (String) of the application used for representation. must be unique

*returns*: 
```json
{
    "href":/toscamodels/1
    "id":1
    "name"={appName}
}
```

*ERRORS*:  
422 - "name" value already in use by other toscamodel

##### DELETE /toscamodels
Delete all TOSCA models

##### GET /toscamodels/{id}
Get the TOSCA model which ID matches {id}.

*returns*:
```json
{
    "href":/toscamodels/1
    "id": 1,
    "name"="AppName"
}
```
*ERRORS*:  
404 - TOSCA model with given {id} does not exist

##### PUT /toscamodels/{id}
Update the TOSCA model which ID matches given {id}.

*returns:* Nothing
*ERRORS*:  
404 - TOSCA model with given {id} does not exist
422 - "name" value already in use by other toscamodel

##### DELETE /toscamodels/{id}
Delete the TOSCA model which ID matches given {id}

*returns:* Nothing

*ERRORS:*  
404 - TOSCA model with given {id} does not exist

## Manipulating CSARs
##### PUT /toscamodels/{id}/csar
Uploads a CSAR.
*Required request body*:
Raw csar file content

*returns:* 203

*ERRORS:*  
400 - Uploaded file is not a valid CSAR, rejected  
404 - TOSCA model with given {id} does not exist  
507 - Insufficient storage  

##### DELETE /toscamodels/{id}/csar
Deletes the csar of the toscamodel which matches given {id}.

*returns:* Nothing

### Managing transformations

##### GET /toscamodels/{id}/transformations
Returns a list of all ongoing or finished transformations of given TOSCA model.

*returns:*
```json
[
    "{platform}": {
        "href": "/toscamodels/{id}/transformations/{platform}",
        "status": ...
        ...

    },
..
]
```
See below for details of the format of a transformation.

##### GET /toscamodels/{id}/transformations/{platform}
Returns the transformation of the specifified TOSCA model which name matches given {platform}.

*returns:*
```json
{
    "href":"/toscamodels/{id}/transformations/{platform}"
    "platform": {
        "href": "/platforms/{platform}"
    },
    "artifact": {
        "href": "/toscamodels/{id}/transformations/{platform}/artifact"
    },
    "status":"user-input",
    "progress":0
}
```
- href: link to self
- platform: link to target platform
- artifact: link to target platform artifact
- status: ["user-input","ready","queued","transforming","done","stopped","failed"]
    - user-input: before transformation can start, user has to specify some values
    - ready: ready for transformation
    - queued: server is currently busy, transformation is queued and will eventually start
    - transforming: transformation is currently ongoing
    - done: transformation is successfully finished
    - canceled: transformation got canceled by a client
    - failed: transformation failed due to an error
- progress: int, [0-100], progresss of transformation in percentage. Can only change in status "transforming"

##### PUT /toscamodels/{id}/transformations/{platform}
Request the transformation of the specified TOSCA model to the specified platform.
If already started a transformation to the particular platform, server will abort and restart transformation.

*returns:* 201 Created (immediately - note this does not mean
that the transformation is finished)
*ERRORS*:  
423 - Locked: transformation not ready but in state "user-input"

##### DELETE /toscamodels/{id}/transformations/{platform}
Halts the specified transformation.

*Postcondition:* Status of specified transformation is "canceled"

*ERRORS:*  
404 - transformation doesn't exit (TOSCA model oder platform does not exist)

### Reading transformation logs
##### GET /toscamodels/{id}/transformations/{platform}/logs/
Receive the logs for specified transformation. All logs starting with the {start}nth to the most recent log are transfered.

*Request body:*
```json
{
    "start":0
}
```
- start - index of first log to receive

*returns:*
```json
{
    "end":53,
    "logs":["line1","line2",...]
}
```
- end: the index of the last log line
- logs: array of log lines (order: oldest first)

*ERRORS:*  
400 - start index out of bounds
404 - no logs available

*EXAMPLE*:
1. Client calls GET .../logs?start=0
2. Server answers with
```json
{
    "end":3,
    "logs:["line1","line2","line3","line4"]
}
```
3. Client calls GET .../logs?start=4
4. etc
### Downloading platform artifacts
##### GET /toscamodels/{id}/transformations/{platform}/artifact
Downloads the deployment artifact for specified platform and TOSCA model.

*ERRORS:*  
404 - The artifact does not exist

### Specifying additional user-input

If the transformation status changes to `user-input` the transformator needs additonal data from the client in order to perform the transformation.

To get information about required data, call:
```
GET /toscamodel/{id}/transformations/{platform}/user-input
```
*returns:*  
```json
[
    "Database Password": {
        "type":"string",
        "value":null,
        "valid":false
    },
    "timeout": {
        "type":"integer",
        "value":null,
        "valid":false
    }
]
```
- Object Names: Key which requires a value
- type: the value needs to be of this type (must be one of [string,uinteger,integer,float]
- value: In the response, this field needs to be set with a value of wanted type
- valid: if false, server rejects value. All key value pairs must be valid in order for the transformation to happen.

*ERRORS:*  
404 - if the transformation is not found (hence TOSCA model id or plaform is invalid)

##### PUT /toscamodel/{id}/transformations/{platform}/user-input
Call this in order to specify the values for required keys. Calling this will automatically trigger an GET call to the same resource as response (in order to validate input).

*Request body*:
```json
[
    "Database Password":"securePassword",
    "timeout":5
]
```
*returns:*
```json
[
    "Database Password": {
        "type":"string",
        "value":"securePassword",
        "valid":true
    },
    "timeout": {
        "type":"integer",
        "value":5,
        "valid":true
    }
]
```

