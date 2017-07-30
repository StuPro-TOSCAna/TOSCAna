# First Draft for the REST API
All calls and requests return data as JSON.
## Data that needs to be provided by the API
- current status of the transformer
  ```
  GET status/
  ```
  **returns:**
  ```json
  {
    "state": "error|transformation|idle",
    "current_transformations": [1,2,3]
  }
  ```
  *state*: current status of the transformer
  *current_transformations:* ids of the active or queued transformations
- the result of the transformation: file/archive
  ```
  GET transformation/{id}
  ```
  **on success returns:**
  ```json
  {
    "id": 1,
    "result": "file"
  }
  ```
- status of the current transformation
  ```
  GET transformation/{id}/status
  ```
  **on success returns:**
  ```json
  {
    "id": 1,
    "status": "inqueue|started|stopped|failed",
    "progress":  "1-100"
  }
  ```
  *status*: returns the current status
  *progress*: returns the progress as percentage from 1-100%
- provide log files for a specific transformation
  ```
  GET transformation/{id}/log
  ```
  **on success returns:**
  ```json
  {
    "id": 1,
    "log": "log"
  }
  ```
  *log:* return the log output as raw string
## Data the REST API should be able to receive
- A single call containing a CSAR - archive and the target platform
  (for some platforms some credentials are also necessary)
  ```
  POST transformation/
  ```
  **sends:**
  ```json
  {
    "platform": "aws",
    "file": "blob"
  }
  ```
  *platform:* target platform in a short version
  *file:* as binary blob

  **on success returns:**
  ```json
  {
    "id": 1
  }
  ```
  **on error returns:**
  ```json
  {
    "message": "Something went wrong.",
    "error_code": 121341,
    "log": "log"
  }
  ```
  *message:* short message including what did not work
  *error_code*: code to easily identify problem
  *log*: log for that action
- If a user wants to abort a transformation
  ```
  DELETE transformation/{id}
  ```
  **on success returns :** nothing if abortion was successful

  **on error returns:**
  ```json
  {
    "message": "Aborting transformation did not work.",
    "error_code": 121341,
    "log": "log"
  }
  ```
  *message:* short message including what did not work
  *error_code*: code to easily identify problem
  *log*: log for that action

## How to process a lot of incoming calls at once?
- 1. Option: Create a queue, they then get transformed one by one
- 2. Option: Create a queue, the transformer does multiple transformations at once
 Problem: It is necessary to find out or define how many transformation the server can do at once, with "big" transformations at the same time the process might be really.

The problems with both options is if a lot of big files are incoming the storage might be to small
=> only accept calls until a limit is reached. That limit might be the number of calls in the queue or a storage limit.
