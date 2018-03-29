## HTML Documentation

The HTML documentation can be found on [GitHub Pages](https://stupro-toscana.github.io/TOSCAna/)

## Swagger UI based documentation

If you want to query the API directly using Swagger UI you can launch a TOSCAna instance and visit the following page:
```
http://<BASE_URL>/swagger-ui.html
```

Where `<BASE_URL>` represents the address and port to the TOSCAna transformer instance (e.g. `localhost:8084`)

## Building the HTML based API documentation

In case of API changes the HTMl based documentation will be outdated. To update it you have to regenerate it. This can be done as follows:

We assume you installed `bootprint` and `bootprint-openapi` using the following two commands:
```
npm install -g bootprint
npm install -g bootprint-openapi
```

Yarn can also be used instead:

```
yarn global add bootprint
yarn global add bootprint-openapi
```

 1. Run the `SwaggerAPIDownloadIT` either through your IDE or by running `mvn install -P all`
 2. Navigate to the `server/target` directory
 3. Create a folder called `swagger-out`
 4. Run the command `bootprint openapi swagger.json swagger-out`
 5. Copy the contents of the `swagger-out` folder into the `gh-pages` branch, and push the changes to GitHub
