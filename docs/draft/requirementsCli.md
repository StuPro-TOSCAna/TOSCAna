# Requirements CLI - draft

## Basics
- parse the commands and call the commands
- call the method to transform a given topology to a custom language (begin / interrupt / select language)
- show current status of transformation
- print logs if wanted
- get status from different components
- show supported languages
- create threads to provide asynchronous operations

## supported commands
| command                | description          | option                        |
|-----------------------|-----------------------|-------------------------------|
| start         | starts the transformation     | -s or -start                            |
| stop        | stops a transformation      |-stop |
| status        | prints some information about a current transformation or default values       |-status |
| verbose        | show logs while transformation      |-v |
| list        | show all available supported languages      |-l |
| help        | prints the man page      |-h or -help |
| default        | prints the set default values      |-default |



## additional
- save and load custom settings
- man page / help
- make it friendly for testing

## questions
- lifecycle operations(deploy, start, stop ... application)?
- catch errors?
- custom style
