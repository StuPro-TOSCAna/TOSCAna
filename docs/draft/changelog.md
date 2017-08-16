## Changes made in the alternative version

- /transformation renamed to /transformations (collections should be plural in REST)
- ```GET /transformations``` returns list of all transformations
- platforms:
    - unified the terms `platform` and `capabilities` to `platform`
    - available platforms can be found and managed with URL /platforms. As platforms deserve play a major role in the application, it might be useful to have a dedicated branch for them. Maybe we need to add platform specific options later; this will be the place for it
- **changes the concept of transformations**: 
    - problem with old version: if user wants to transform a specific csar to multiple platforms, he has to upload the same csar several times (a new transformation resource gets created for every platform)
    - solution: new hierarchy (/toscamodels/{id}/csar and /toscamodels/{id}/transformations, dropping /transformations)
        - the same toscamodel can have several transformations
        - this hierarchy is much more taylored to our problem than the old one
- omit the message property of transformation status. If needed, this information should be taken out of the logs
- reworked the process of log transmission, as the old mechanism was neither non-redundant nor gapless
- removed the notes section: whether the server computes the transformations sequentially or in parallel doesn't matter for api design (though i'd opt for the parallel approach)
- the concept of 'questions' was given up (its not a quiz game)
    - instead we now have simple key value pairs.
    - exact phrasing should be a decision of the gui/client
        - e.g. I would not like the gui to ask me some questions in rather weird popup dialogues. I imagine the keys as labels followed by textfields, where user can put in the keys..
    - if transformation is not in state "user-input", do not send error 404. The resource is there. There are simply no key value pairs to fill out. Send an empty array.
#### TODO
- Add missing status responses, especially for errors
- add a graphical representation (tree) of the api
- read requirements again and find mismatches / missing features
- check for inconsistencies in json response/request style or enhance style

