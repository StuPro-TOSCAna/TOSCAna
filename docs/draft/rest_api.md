# First Draft for the REST API
All calls and requests return data as JSON.
## General Calls
### Get current status of the transformer
```
GET status/
```
**returns:**
  
HTTP Response Code: 200 (always)
  
```json
{
  "state": "idle",
  "current_transformations": [1,2,3],
  "available_storage": 1000,
  "total_storage": 10000,
  "capabilities": ["aws", "kubernetes", "openstack"]
}
```
- *state*: current status of the transformer, values can either be `idle`, `transforming` or `error`
- *current_transformations:* ids of the active or queued transformations (Array of unsigned integers)
- *available_storage*: represents the free hard disk space in megabytes (Unsigned integer)
- *total_storage*: represents the total storage of the hard disk (Unsigned integer)
- *capabilities*: represents a list of the plugins loaded, i.e. the platforms which are supported by this instance of the transformer

## Transformation specific calls

***Error Note***: Every Call that requires a id will return a Error 404 if the id cannot be found.

### Retrieve the result of the transformation

```
GET transformation/{id}
```
  
*Parameters*:
- **id**: The id of the transformation for which the result should be retrieved.
  
**on success returns:**
  
HTTP Response Code: 200
```json
{
  "id": 1,
  "result": "/results/transform_1.zip"
}
```
- *id*: The id of the transformation (unsigned Integer)
- *result*: the URL path (on the HTTP server) to download the result archive. If the transformation is still in progress or has errored this value will be null

### Getting the status of a transformation
```
GET transformation/{id}/status
```

*Parameters*:
- **id**: The id of the transformation for which the result should be retrieved.

**on success returns:**
  
HTTP Response Code: 200
```json
{
  "id": 1,
  "status": "done",
  "progress":  "100",
  "message": "Tranformation was sucessful."
}
```
- *status*: returns the current status. Possible values are `user-input`, `inqueue`, `started`, `stopped`, `failed` or `done`
- *progress*: returns the progress as percentage from 0-100% (Unsigned Integer, range 0  to 100)
- *message*: String message to quickly inform the user about whats happening.

### Retrieve Logs for a transformation
```
GET transformation/{id}/log/{linecount}
```
 
*Parameters*:
- *id*: The id of the transformation for which the logs should be retrieved
- *linecount*: (Optional, 50 by default) The Maximum linecount to return. if this parameter is ``all`` all lines of the log will be sent

**on success returns:**

This call returns the `linecount` last lines of the log. 

HTTP Response Code: 200
  
```json
{
  "id": 1,
  "linecount": 50,
  "log": ["Line 1","Line 2","Line 3"]
}
```
*log:* return the log output as raw string

### Retrieving user-input questions

If the transformation status changes to `user-input` the transformator needs additonal data from the user in order to perform the transformation.
The Questions the user has to answer for a specific transformation can be retieved by calling
```
GET /transformation/{id}/questions
```

if the transformation is in the `user-input` state the following will be returned.

```json
{
  "questions": [
    {
      "id": 1,
      "question": "Please enter the API token for AWS:",
      "input_type": "string"
    },
    {
      "id": 2,
      "question": "Please enter the password for the MySQL root account:",
      "input_type": "password"
    }
  ]
}
```
- **questions**: the questions array contains all the questions the user has to answer.
    - **id**: represents a unique identifier within the question request (Unsigned integer)
    - **question**: English text of the question (String)
    - **input_type**: The type of input requested. (Supported values ``password``, `string`, `uinteger`, `integer` and `float`)

if the transformation is not in the ``user-input`` state a Error 404 will be returned

### Submitting user-input questions

### Submitting a Cloud Service Archive for transformation

A single call containing a CSAR - archive and the target platform
(for some platforms some credentials are also necessary, these will probably be supplied by user Input)
```
POST transformation/{platform}
```

**parameters**:
-  **platform**: the platform on which the application should be transformed. (Supported platforms can be retrieved using a status request (capabilities))

**sends:**
Binary Blob of the Cloud Service Archive,

**on success returns:**
  
HTTP Response Code: 200
  
```json
{
  "id": 1
}
```
**on error returns:**
  
HTTP Error Code: 500
  
```json
{
  "message": "Something went wrong.",
  "error_code": 121341,
  "log": "log"
}
```
- *message:* short message including what did not work
- *error_code*: code to easily identify problem
- *log*: log for that action

### Aborting a transformation
```
DELETE transformation/{id}
```
**on success returns :** nothing if abortion was successful (HTTP Response Code 200)

**on error returns:**

HTTP Response Code: 500

```json
{
  "message": "Aborting transformation did not work.",
  "error_code": 121341,
  "log": "log"
}
```
- *message:* short message including what did not work
- *error_code*: code to easily identify problem
- *log*: log for that action

# Notes

## How to process a lot of incoming calls at once?
- 1. Option: Create a queue, they then get transformed one by one
- 2. Option: Create a queue, the transformer does multiple transformations at once
 Problem: It is necessary to find out or define how many transformation the server can do at once, with "big" transformations at the same time the process might be really.

The problems with both options is if a lot of big files are incoming the storage might be to small
=> only accept calls until a limit is reached. That limit might be the number of calls in the queue or a storage limit.
