# TOSCAna WebApp
> The Readme asumes you operate in the `project_root/app`-folder.
This document contains information about how to develop our web app.

## Prerequisites

- [`yarn`](https://yarnpkg.com) - for package management
- [`angular-cli`](https://cli.angular.io/) - used to build and run our app,  can be installed width `yarn global add @angular/cli`
- a editor with TypeScript support, Visual Studio Code or JetBrains WebStorm are recommended

## SetUp

open a terminal and run `yarn install`, this will install all necessary dependencies

## Develop

Run `ng serve` for a dev server. Navigate to `http://localhost:4200/`. The app will automatically reload if you change any of the source files.

**Important:** replace `basePath` in  `src/app/app.module.ts` with the adress of the REST Api you want to develop against.

## Build

### WebApp only

Run `ng build` to build the project. The build artifacts will be stored in the `dist/` directory. Use the `-prod` flag for a production build.

### WebApp and Server component together

In the project root folder run `mvn install -P build` to build everything toghether. Now you can run the whole stack with `java -jar project_root/server/target/server-1.0-SNAPSHOT.jar`.  The WebApp is reachable under [`http://localhost:8084`](http://localhost:8084) if you use default server settings.

## Generate the api code with `swagger-codegen`

### Prerequisites

- latest Version of [swagger-codegen](https://github.com/swagger-api/swagger-codegen)

### Generate api

1. Change line `4` in the `api-gen` script to the location of your codegen jar file or binary and the url of your REST api
2. run `./api-gen`
