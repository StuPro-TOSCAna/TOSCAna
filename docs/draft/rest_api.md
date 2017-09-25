# Alternative draft for the REST api
**Note:** Terms in curly braces like {appName} and {platformName} are meant to be substituted with a proper value.
They must not be used literally.

![rest api as a tree](rest-api.png)

### Current status
##### GET /status
Get the current status of the transformer.

*Returns:* HTTP Response Code: 200 (always)

```json
{
    "_links": {
        "self": { "href": "/status" }
    },
    "status": "idle",
    "available_storage": 1000,
    "total_storage": 10000
}
```
- `_links`: contains links to resources
    - `self`: link to self
- `status`: current status of the transformer, values can be [`idle`, `transforming`, `error`]
- `available_storage`: free hard disk storage (integer, in MB)
- `total_storage`: total hard disk storage (integer, in MB)

### Manage target platforms

##### GET /platforms
Return all target platforms which are available for transforming the CSAR.

*Returns:*
```json
{
    "_links": {
        "self": { "href": "/platforms" },
    },
    "_embedded": {
        "platform": [{
            "_links": {
                "self": { "href": "/platforms/aws" }
            }
         }, {
            "_links": {
                "self": { "href": "/platforms/cloudformation" }
            }
        }]
     }
}
```
- `_links`: contains links to resources
    - `self`: link to self
- `_embedded`: resources contained within

**Note:** It is possible to add platform specific preferences here later.

### Manipulating apps

##### GET /apps
Get a list of all applications.

*Returns*:
```json
{
    "_links": {
        "self": { "href": "/apps" },
        "next": { "href": "/apps?page=2"}
        "findApp": {
            "href": "/apps/{appName}",
            "templated": true
        }
    },
    "_embedded": {
       "app": [{
            "_links": {
                "self": { "href": "/apps/hello-world" },
                "archive": { "href": "/apps/hello-world/archive" },
                "transformations": { "href": "/apps/hello-world/transformations" }
            },
            "name": "hello-world"
        }, {
            "_links": {
                "self": { "href": "/apps/billing-app" },
                "archive": { "href":"/apps/billing-app/archive" },
                "transformations": { "href": "/apps/billing-app/transformations" }
            },
            "name": "billing-app"
        }]
     }
}
```
- `_links`: contains links to resources
    - `self`: link to self
    - `next`: link to the next page of apps
    - `findApp`: template containing the variable "appName", which can be used with the specific app name to get directly to that app
- `_embedded`: resources contained within


##### POST /apps
Create a new application. Returns a link to the new resource.

*Request body:*
```json
{
    "name": "{appName}"
}
```

*Returns*: `201 - created`
```json
{
    "_links": {
        "self": { "href": "/apps/{appName}" },
        "archive": { "href": "/apps/{appName}/archive" },
        "transformations": { "href": "/apps/{appName}/transformations" }
    },
    "name": "{appName}"
}
```
- `_links`: contains links to resources
    - `self`: link to self
    - `archive`: link to CSAR of the application
    - `transformations`: link to transformations of the application
- `name`: name (String) of the application used for representation. Must be unique. Allowed characters: [a-z0-9_-]. Upper case letters are automatically converted to lower case.

*Errors*:
`422` - `name` value already in use by other application

##### DELETE /apps
Delete all applications

##### GET /apps/{appName}
Get the application which name matches {appName}.

*Returns*:
```json
{
    "_links": {
        "self": { "href": "/apps/{appName}" },
        "archive": { "href": "/apps/{appName}/archive" },
        "transformations": { "href": "/apps/{appName}/transformations" }
    },
    "name": "{appName}"
}
```
*Errors*:
`404` - application with given {appName} does not exist

##### PUT /apps/{appName}
Update the application which name matches given {appName}.

*Returns:* Nothing

*Errors*:
`404` - application with given {appName} does not exist
`422` - `name` value already in use by other application

##### DELETE /apps/{appName}
Delete the application which name matches given {appName}

*Returns:* Nothing

*Errors:*
`404` - application with given {appName} does not exist

## Manipulating CSARs
##### PUT /apps/{appName}/archive
Uploads a CSAR.

*Request body*:
Raw CSAR file content

*Returns:* `203`

*Errors:*
`400` - Uploaded file is not a valid CSAR, rejected
`404` - application with given {appName} does not exist
`507` - Insufficient storage

##### DELETE /apps/{appName}/archive
Deletes the CSAR of the application which matches given {appName}.

*Returns:* Nothing

### Managing transformations

##### GET /apps/{appName}/transformations
Returns a list of all ongoing or finished transformations of given application.

*Returns:*
```json
{
    "_links": {
        "self": { "href": "/apps/{appName}/transformations/" },
    },
    "_embedded": {
        "transformation": [{
            "_links": {
                "self": { "href": "/apps/{appName}/transformations/{platformName}" },
            ...
            },
            "status": ...
            ...
        }]
    },
    ..
}
```
- `_links`: contains links to resources
    - `self`: link to self
- `_embedded`: resources contained within

See below for details of the format of a transformation.

##### GET /apps/{appName}/transformations/{platformName}
Returns the transformation of the specifified application which name matches given {platformName}.

*Returns:*
```json
{
    "_links": {
        "self": { "href": "/apps/{appName}/transformations/{platformName}" },
        "platform": { "href": "/platforms/{platformName}" },
        "artifact": { "href": "/apps/{appName}/transformations/{platformName}/artifact" },
        "logs": { "href": "/apps/{appName}/transformations/{platformName}/logs" },
        "properties": { "href": "/apps/{appName}/transformations/{platformName}/properties" }
    },
    "status": "input-required",
    "progress": 0
}
```
- `_links`: contains links to resources
    - `self`: link to self
    - `platform`: link to target platform
    - `artifact`: link to target platform artifact
- `status`: status of the transformation, values can be [`input-required`, `ready`, `queued`, `transforming`, `done`, ` stopped`, `failed`]
    - `input-required`: before the transformation can start, the user has to specify some values
    - `ready`: ready for transformation
    - `queued`: server is currently busy, the transformation is queued and will eventually start
    - `transforming`: transformation is currently ongoing
    - `done`: transformation is successfully finished
    - `canceled`: transformation got canceled by a client
    - `failed`: transformation failed due to an error
- `progress`: progress of the  transformation in percentage (integer, [0-100]). Can only change in the status `transforming`.

##### PUT /apps/{appName}/transformations/{platformName}
Request the transformation of the specified application to the specified platform.
If a transformation has already started for the particular platform, the server will abort and restart the transformation.

*Returns:* `201` Created
(immediately - **Note:** This does not mean that the transformation is finished.)

*Errors:*
`423` - Locked: transformation not ready but in state "input-required"

##### DELETE /apps/{appName}/transformations/{platformName}
Halts the specified transformation.

*Postcondition:* Status of specified transformation is "canceled"

*Errors:*
`404` - transformation doesn't exit (application oder platform does not exist)

### Reading transformation logs
##### GET /apps/{appName}/transformations/{platformName}/logs/
Receive the logs for the specified transformation.

*returns:*
```json
{
    "_links": {
        "self": { "href": "/apps/{appName}/transformations/{platformName}/logs"},
        "next": { "href": "/apps/{appName}/transformations/{platformName}/logs?page=2"},
        "findLog": {
            "href": "/apps/{appName}/transformations/{platformName}/logs{?line},
            "templated": true
    }
    "start": 1,
    "end": 10,
    "logs": [ "line1", "line2", ..., "line10" ]
}
```
- `_links`: contains links to resources
    - `self`: link to self
    - `next`: link to the next page of logs
    - `findLog`: template containing the variable "line", which can be used with a specific line number to get directly to the log with that line
- start: the index of the first log line
- end: the index of the last log line
- logs: array of log lines (order: oldest first)

*Errors:*
`404` - no logs available

*Example*:
1. Client calls GET .../logs?page=2
2. Server answers with
```json
{
    "_links": {
        "self": { "href": "/apps/{appName}/transformations/{platformName}/logs?page=2" },
        "next": { "href": "/apps/{appName}/transformations/{platformName}/logs?page=3" },
        "findLog": {
            "href": "/apps/{appName}/transformations/{platformName}/logs{?id}",
            "templated": true
    }
    "start": 11,
    "end": 20,
    "logs": [ "line11", "line12", ..., "line20" ]
}
```
3. Client calls GET .../logs?page=3
4. etc.

### Downloading platform artifacts
##### GET /apps/{appName}/transformations/{platformName}/artifact
Downloads the deployment artifact for specified platform and application.

*Errors:*
`404` - The artifact does not exist

### Specifying additional user input

If the transformation status changes to `input-required` the transformator needs additonal data from the client in order to perform the transformation.

To get information about required data, call:
```
GET /apps/{appName}/transformations/{platformName}/properties
```
*returns:*
```json
[{
    "Database Password": {
        "type": "string",
        "value": null,
        "valid": false
    },
    "timeout": {
        "type": "integer",
        "value": null,
        "valid": false
    }
}]
```
- `Object Names`: Key which requires a value
- `type`: the value needs to be of this type (must be one of [string, integer, float]
- `value`: In the response, this field needs to be set with a value of wanted type
- `valid`: if false, server rejects value. All key value pairs must be valid in order for the transformation to happen.

*Errors:*
`404` - if the transformation is not found (hence application name or plaform is invalid)

##### PUT /apps/{appName}/transformations/{platformName}/properties
Call this in order to specify the values for required keys. Calling this will automatically trigger a GET call to the same resource as the response (in order to validate the input).

*Request body*:
```json
[{
    "Database Password": "securePassword",
    "timeout": 5
}]
```
*Returns:*
```json
[{
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
}]
```
