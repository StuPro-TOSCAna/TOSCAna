# First Draft for the REST API
> This is only the first draft. Some adaptations are still possible.
## Data that needs to be provided by the API [GET]
- current status of the transformer
  ```
  GET status/
  ```
- the result of the transformation: file/archive
  ```
  GET transformation/{id}
  ```
- status of the current transformation
  ```
  GET transformation/status/{id}
  ```
- provide log files for a specific transformation
  ```
  GET transformation/log/{id}
  ```

## Data the REST API should be able to receive [POST]
- A single call containing a CSAR - archive and the target platform
  (for some platforms some credentials are also necessary)
  ```
  POST transformation/
  ```

## How to process a lot of incoming calls at once?
- 1. Option: Create a queue, they then get transformed one by one
- 2. Option: Create a queue, the transformer does multiple transformations at once
 Problem: It is necessary to find out or define how many transformation the server can do at once, with "big" transformations at the same time the process might be really.

The problems with both options is if a lot of big files are incoming the storage might be to small
=> only accept calls until a limit is reached. That limit might be the number of calls in the queue or a storage limit.
