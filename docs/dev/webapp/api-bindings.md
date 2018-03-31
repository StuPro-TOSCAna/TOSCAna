# API bindings

Our server uses [Swagger](https://swagger.io/) to document our REST API. Swagger provides a tool called [swagger-codegen](https://github.com/swagger-api/swagger-codegen).
With swagger-codegen it is possible to auto-generate TypeScript bindings/calls for our TOSCAna REST API.
## Install swagger-codegen

There are several possibilities to install swagger-codegen:

- if your system got a package manager you can check if it is available there
- download the jar like described [here](https://github.com/swagger-api/swagger-codegen#prerequisites)

## Generate the api bindings
### Prepare the api-gen script

The `api-gen` auto-generates the api in the `src/app/api` folder and applies some fixes since the swagger REST API documentation is not correctly mapped to TypeScript classes.

Edit line 4 to match the location of your swagger-codegen executable:
If you installed swagger-codegen with a package manager it might look like:
```
executable=swagger-codegen
```
If you use the jar:
```
executable="java -jar $HOME/.local/bin/swagger-codegen-cli.jar"
```

### Run the script

**Info:** The script assumes you are running the REST API locally, if not you have to change line 5

Simply run:
```
./api-gen
```

### Manual check

Since the auto generation can go wrong it is always a good idea to check if everything went right.




